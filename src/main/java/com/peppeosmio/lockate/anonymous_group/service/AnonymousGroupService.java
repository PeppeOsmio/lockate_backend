package com.peppeosmio.lockate.anonymous_group.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peppeosmio.lockate.anonymous_group.classes.AGMemberEntityWithToken;
import com.peppeosmio.lockate.anonymous_group.dto.*;
import com.peppeosmio.lockate.anonymous_group.entity.AGAdminTokenEntity;
import com.peppeosmio.lockate.anonymous_group.entity.AnonymousGroupEntity;
import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;
import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import com.peppeosmio.lockate.anonymous_group.exceptions.*;
import com.peppeosmio.lockate.anonymous_group.repository.AGAdminTokenRepository;
import com.peppeosmio.lockate.anonymous_group.repository.AnonymousGroupRepository;
import com.peppeosmio.lockate.anonymous_group.repository.AGMemberRepository;
import com.peppeosmio.lockate.anonymous_group.repository.AGLocationRepository;
import com.peppeosmio.lockate.anonymous_group.security.AGAdminAuthentication;
import com.peppeosmio.lockate.anonymous_group.security.AGMemberAuthentication;
import com.peppeosmio.lockate.anonymous_group.service.result.AGAdminAuthResult;
import com.peppeosmio.lockate.anonymous_group.service.result.AGMemberAuthResult;
import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedDataDto;
import com.peppeosmio.lockate.common.exceptions.NotFoundException;
import com.peppeosmio.lockate.common.exceptions.UnauthorizedException;
import com.peppeosmio.lockate.redis.RedisService;
import com.peppeosmio.lockate.srp.InvalidSrpSessionException;
import com.peppeosmio.lockate.srp.SrpService;
import com.peppeosmio.lockate.utils.TTLMap;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
public class AnonymousGroupService {
    private final AnonymousGroupRepository anonymousGroupRepository;
    private final AGMemberRepository agMemberRepository;
    private final AGAdminTokenRepository agAdminTokenRepository;
    private final AGLocationRepository agLocationRepository;
    private final RedisService redisService;
    private final SrpService srpService;
    private final ObjectMapper objectMapper;
    private final TTLMap<UUID, LocalDateTime> lastSavedLocationsCache =
            new TTLMap<>(Duration.ofMinutes(5));

    public AnonymousGroupService(
            AnonymousGroupRepository anonymousGroupRepository,
            AGMemberRepository agMemberRepository,
            AGAdminTokenRepository agAdminTokenRepository,
            AGLocationRepository agLocationRepository,
            RedisService redisService,
            SrpService srpService,
            ObjectMapper objectMapper) {
        this.agMemberRepository = agMemberRepository;
        this.anonymousGroupRepository = anonymousGroupRepository;
        this.agAdminTokenRepository = agAdminTokenRepository;
        this.agLocationRepository = agLocationRepository;
        this.redisService = redisService;
        this.srpService = srpService;
        this.objectMapper = objectMapper;
    }

    private static String getRedisAGLocationChannel(UUID anonymousGroupId) {
        return "ag-" + anonymousGroupId.toString();
    }

    @Transactional
    public AGMemberAuthResult verifyMemberAuth(UUID anonymousGroupId, Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        var agEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        if (!(authentication instanceof AGMemberAuthentication)) {
            throw new UnauthorizedException();
        }
        var authMemberEntity =
                agMemberRepository
                        .findById(((AGMemberAuthentication) authentication).getAGMemberId())
                        .orElseThrow(UnauthorizedException::new);
        if (!agEntity.getId().equals(authMemberEntity.getAnonymousGroupId())) {
            throw new UnauthorizedException();
        }
        return new AGMemberAuthResult(
                agEntity, authMemberEntity, (AGMemberAuthentication) authentication);
    }

    @Transactional
    private AGMemberEntityWithToken createMember(
            EncryptedDataDto encryptedUserNameDto, AnonymousGroupEntity anonymousGroupEntity) {
        var secureRandom = new SecureRandom();
        var token = new byte[32];
        secureRandom.nextBytes(token);
        var entity =
                agMemberRepository.save(
                        AGMemberEntity.fromBase64Fields(
                                encryptedUserNameDto, token, anonymousGroupEntity));
        return new AGMemberEntityWithToken(entity, token);
    }

    @Transactional
    private List<AGMemberDto> listMembers(AnonymousGroupEntity agEntity) {
        var agMemberEntities = agEntity.getAgMemberEntities();
        var agLocationEntities =
                agLocationRepository.findLatestLocationsPerMember(agEntity.getId());
        var lastLocationRecordsMap = new HashMap<UUID, LocationRecordDto>();
        agLocationEntities.forEach(
                (entity) ->
                        lastLocationRecordsMap.put(
                                entity.getAgMemberId(), LocationRecordDto.fromEntity(entity)));
        return agMemberEntities.stream()
                .map(
                        (entity) -> {
                            var lastLocationRecord = lastLocationRecordsMap.get(entity.getId());
                            return AGMemberDto.fromEntity(entity, lastLocationRecord);
                        })
                .toList();
    }

    @Transactional
    public AGGetMembersResponseDto getMembers(UUID anonymousGroupId, Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        var result = verifyMemberAuth(anonymousGroupId, authentication);
        var agEntity = result.anonymousGroupEntity();
        return new AGGetMembersResponseDto(listMembers(agEntity));
    }

    @Transactional
    public AGGetMembersCountDto getMembersCount(
            UUID anonymousGroupId, Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        verifyMemberAuth(anonymousGroupId, authentication);
        return new AGGetMembersCountDto(
                agMemberRepository.countByAnonymousGroupId(anonymousGroupId));
    }

    @Transactional
    public AGCreateResDto createAnonymousGroup(AGCreateReqDto dto) throws Base64Exception {
        var agEntity =
                anonymousGroupRepository.save(
                        AnonymousGroupEntity.fromBase64Fields(
                                dto.encryptedGroupName(),
                                dto.memberPasswordSrpVerifier(),
                                dto.memberPasswordSrpSalt(),
                                new BCryptPasswordEncoder().encode(dto.adminPassword()),
                                dto.keySalt()));
        var agMemberEntityWithToken = createMember(dto.encryptedMemberName(), agEntity);
        return new AGCreateResDto(
                AnonymousGroupDto.fromEntity(agEntity),
                AGMemberWithTokenDto.fromEntityWithToken(agMemberEntityWithToken));
    }

    public AGGetMemberPasswordSrpInfoResDto getMemberSrpInfo(UUID anonymousGroupId)
            throws AGNotFoundException {
        var anonymousGroupEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        var encoder = Base64.getEncoder();
        return new AGGetMemberPasswordSrpInfoResDto(
                EncryptedDataDto.fromEncryptedString(
                        new EncryptedString(
                                anonymousGroupEntity.getNameCipher(),
                                anonymousGroupEntity.getNameIv())),
                encoder.encodeToString(anonymousGroupEntity.getMemberPasswordSrpSalt()),
                encoder.encodeToString(anonymousGroupEntity.getKeySalt()));
    }

    @Transactional
    public AGMemberAuthStartResponseDto startMemberSrpAuth(
            UUID anonymousGroupId, AGMemberAuthStartRequestDto dto)
            throws Base64Exception,
                    UnauthorizedException,
                    AGNotFoundException,
                    InvalidSrpSessionException {
        var anonymousGroupEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        try {
            var decoder = Base64.getDecoder();
            var srpSessionResult =
                    srpService.startSrp(
                            new BigInteger(decoder.decode(dto.A())),
                            new BigInteger(anonymousGroupEntity.getMemberPasswordSrpVerifier()));
            return new AGMemberAuthStartResponseDto(
                    srpSessionResult.sessionId(), srpSessionResult.srpSession().B());
        } catch (CryptoException e) {
            e.printStackTrace();
            throw new UnauthorizedException();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new Base64Exception();
        }
    }

    @Transactional
    public AGMemberAuthVerifyResponseDto verifyMemberSrpAuth(
            UUID anonymousGroupId, AGMemberAuthVerifyRequestDto dto)
            throws UnauthorizedException,
                    NotFoundException,
                    InvalidSrpSessionException,
                    Base64Exception {
        var agEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        var decoder = Base64.getDecoder();
        var isValid = false;
        try {
            isValid =
                    srpService.verifySrp(
                            dto.srpSessionId(),
                            new BigInteger(agEntity.getMemberPasswordSrpVerifier()),
                            new BigInteger(decoder.decode(dto.M1())));

        } catch (CryptoException e) {
            e.printStackTrace();
            throw new UnauthorizedException();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new Base64Exception();
        }
        if (isValid) {
            var agMemberEntityWithToken = createMember(dto.encryptedMemberName(), agEntity);
            var members = listMembers(agEntity);
            return new AGMemberAuthVerifyResponseDto(
                    AnonymousGroupDto.fromEntity(agEntity),
                    AGMemberWithTokenDto.fromEntityWithToken(agMemberEntityWithToken),
                    members);
        } else {
            throw new UnauthorizedException();
        }
    }

    @Transactional
    public void memberLogout(UUID anonymousGroupId, Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        var authResult = verifyMemberAuth(anonymousGroupId, authentication);
        agMemberRepository.deleteById(authResult.agMemberEntity().getId());
    }

    public AGMemberAuthentication authMember(UUID agMemberId, String memberToken)
            throws UnauthorizedException {
        var agMemberEntity =
                agMemberRepository.findById(agMemberId).orElseThrow(UnauthorizedException::new);
        var decoder = Base64.getDecoder();
        if (!BCrypt.checkpw(decoder.decode(memberToken), agMemberEntity.getTokenHash())) {
            throw new UnauthorizedException();
        }
        return new AGMemberAuthentication(agMemberId);
    }

    @Transactional
    public void saveLocation(
            UUID anonymousGroupId, Authentication authentication, AGLocationSaveRequestDto dto)
            throws AGNotFoundException, UnauthorizedException, JsonProcessingException {
        var result = verifyMemberAuth(anonymousGroupId, authentication);
        var agEntity = result.anonymousGroupEntity();
        var agMemberEntity = result.agMemberEntity();
        var timestamp = LocalDateTime.now(ZoneOffset.UTC);
        var locationUpdate =
                new LocationUpdateDto(
                        new LocationRecordDto(dto.encryptedLocation(), timestamp),
                        agMemberEntity.getId());
        var messageJson = objectMapper.writeValueAsString(locationUpdate);
        redisService.publish(getRedisAGLocationChannel(anonymousGroupId), messageJson);
        LocalDateTime lastSavedLocationTimeStamp =
                lastSavedLocationsCache.get(agMemberEntity.getId()).orElse(null);
        if (lastSavedLocationTimeStamp == null) {
            var lastSavedLocation =
                    agLocationRepository
                            .findFirstByAgMemberEntityOrderByTimestampDescIdDesc(agMemberEntity)
                            .orElse(null);
            if (lastSavedLocation != null) {
                lastSavedLocationTimeStamp = lastSavedLocation.getTimestamp();
            }
        }
        var shouldSave = false;
        if(lastSavedLocationTimeStamp == null) {
            shouldSave = true;
        } else {
            shouldSave = Duration.between(lastSavedLocationTimeStamp, timestamp).toMillis() >= 30000L;
        }
        if (shouldSave) {
            var agLocationEntity =
                    agLocationRepository.save(
                            AGMemberLocationEntity.fromBase64Fields(
                                    dto.encryptedLocation(), agMemberEntity, timestamp)
                            );
            agMemberEntity.setLastSeen(agLocationEntity.getTimestamp());
            agMemberRepository.save(agMemberEntity);
            lastSavedLocationsCache.put(agMemberEntity.getId(), timestamp);
        }
    }

    public Runnable streamLocations(
            UUID anonymousGroupId,
            Consumer<LocationUpdateDto> onLocation,
            Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        var authenticatedAGMemberId =
                verifyMemberAuth(anonymousGroupId, authentication).agMemberEntity().getId();
        var channel = getRedisAGLocationChannel(anonymousGroupId);
        var messageListener =
                redisService.subscribe(
                        channel,
                        (message) -> {
                            try {
                                var agLocationUpdate =
                                        objectMapper.readValue(message, LocationUpdateDto.class);
                                if (!agLocationUpdate.memberId().equals(authenticatedAGMemberId)) {
                                    onLocation.accept(agLocationUpdate);
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
        return () -> redisService.unsubscribe(channel, messageListener);
    }

    @Transactional
    public AGAdminTokenResDto getAdminToken(UUID anonymousGroupId, AGAdminTokenReqDto dto)
            throws AGNotFoundException, UnauthorizedException {
        var agEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        if (!BCrypt.checkpw(dto.adminPassword(), agEntity.getAdminPasswordHash())) {
            throw new UnauthorizedException();
        }
        var now = LocalDateTime.now(ZoneOffset.UTC);
        var token =
                agAdminTokenRepository.save(
                        new AGAdminTokenEntity(now, now.plusDays(7L), agEntity));
        var encoder = Base64.getEncoder();
        return new AGAdminTokenResDto(encoder.encodeToString(token.getToken()));
    }

    @Transactional
    public AGAdminAuthentication authAdmin(String adminToken) throws UnauthorizedException {
        var decoder = Base64.getDecoder();
        if (!agAdminTokenRepository.existsById(decoder.decode(adminToken))) {
            throw new UnauthorizedException();
        }
        return new AGAdminAuthentication(adminToken);
    }

    @Transactional
    public AGAdminAuthResult verifyAdminAuth(UUID anonymousGroupId, Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        var agEntity =
                anonymousGroupRepository
                        .findById(anonymousGroupId)
                        .orElseThrow(() -> new AGNotFoundException(anonymousGroupId));
        if (!(authentication instanceof AGAdminAuthentication)) {
            throw new UnauthorizedException();
        }
        var decoder = Base64.getDecoder();
        var agAdminToken =
                agAdminTokenRepository
                        .findById(
                                decoder.decode(
                                        ((AGAdminAuthentication) authentication).getAGAdminToken()))
                        .orElseThrow(UnauthorizedException::new);

        return new AGAdminAuthResult(
                agEntity, agAdminToken, (AGAdminAuthentication) authentication);
    }

    @Transactional
    public void deleteAnonymousGroup(UUID anonymousGroupId, Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        verifyAdminAuth(anonymousGroupId, authentication);
        anonymousGroupRepository.deleteAnonymousGroup(anonymousGroupId);
    }
}
