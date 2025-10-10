package com.peppeosmio.lockate.anonymous_group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.peppeosmio.lockate.anonymous_group.dto.*;
import com.peppeosmio.lockate.anonymous_group.exceptions.Base64Exception;
import com.peppeosmio.lockate.anonymous_group.exceptions.AGNotFoundException;
import com.peppeosmio.lockate.common.exceptions.NotFoundException;
import com.peppeosmio.lockate.common.exceptions.UnauthorizedException;
import com.peppeosmio.lockate.anonymous_group.service.AnonymousGroupService;
import com.peppeosmio.lockate.srp.InvalidSrpSessionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/anonymous-groups")
@Validated
public class AnonymousGroupController {

    private final AnonymousGroupService anonymousGroupService;

    public AnonymousGroupController(AnonymousGroupService anonymousGroupService) {
        this.anonymousGroupService = anonymousGroupService;
    }

    @GetMapping("/{anonymousGroupId}/members")
    @ResponseStatus(HttpStatus.OK)
    AGGetMembersResponseDto getAGMembers(@PathVariable UUID anonymousGroupId,
                                         Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        return anonymousGroupService.getMembers(anonymousGroupId, authentication);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    AGCreateResDto createAnonymousGroup(@RequestBody AGCreateReqDto dto)
            throws Base64Exception {
        return anonymousGroupService.createAnonymousGroup(dto);
    }

    @GetMapping("/{anonymousGroupId}/members/auth/srp/info")
    @ResponseStatus(HttpStatus.OK)
    AGGetMemberPasswordSrpInfoResDto getMemberSrpInfo(
            @PathVariable UUID anonymousGroupId) throws AGNotFoundException {
        return anonymousGroupService.getMemberSrpInfo(anonymousGroupId);
    }

    @PostMapping("/{anonymousGroupId}/members/auth/srp/start")
    AGMemberAuthStartResponseDto memberAuthStart(@PathVariable UUID anonymousGroupId,
                                                 @RequestBody
                                                 AGMemberAuthStartRequestDto dto)
            throws UnauthorizedException, Base64Exception, AGNotFoundException,
            InvalidSrpSessionException {
        return anonymousGroupService.startMemberSrpAuth(anonymousGroupId, dto);
    }

    @PostMapping("/{anonymousGroupId}/members/auth/srp/verify")
    @ResponseStatus(HttpStatus.OK)
    AGMemberAuthVerifyResponseDto memberAuthVerify(@PathVariable UUID anonymousGroupId,
                                                   @RequestBody
                                                   AGMemberAuthVerifyRequestDto dto)
            throws UnauthorizedException, NotFoundException, InvalidSrpSessionException,
            Base64Exception {
        return anonymousGroupService.verifyMemberSrpAuth(anonymousGroupId, dto);
    }

    @GetMapping("/{anonymousGroupId}/members/auth/verify")
    @ResponseStatus(HttpStatus.OK)
    void verifyMemberAuth(@PathVariable UUID anonymousGroupId,
                          Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        anonymousGroupService.verifyMemberAuth(anonymousGroupId, authentication);
    }

    @PostMapping("/{anonymousGroupId}/members/leave")
    @ResponseStatus(HttpStatus.OK)
    void memberLeave(@PathVariable UUID anonymousGroupId, Authentication authentication)
            throws AGNotFoundException, UnauthorizedException {
        anonymousGroupService.memberLogout(anonymousGroupId, authentication);
    }

    @GetMapping("/{anonymousGroupId}/members/count")
    @ResponseStatus(HttpStatus.OK)
    AGGetMembersCountDto getMembersCount(@PathVariable UUID anonymousGroupId,
                                         Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        return anonymousGroupService.getMembersCount(anonymousGroupId, authentication);
    }

    @PostMapping("/{anonymousGroupId}/locations")
    @ResponseStatus(HttpStatus.CREATED)
    void saveAnonymousLocation(@PathVariable UUID anonymousGroupId,
                               @RequestBody AGLocationSaveRequestDto dto,
                               Authentication authentication)
            throws UnauthorizedException, AGNotFoundException, JsonProcessingException {
        anonymousGroupService.saveLocation(anonymousGroupId, authentication, dto, null);
    }

    @PostMapping("/{anonymousGroupId}/admin/auth/token")
    @ResponseStatus(HttpStatus.CREATED)
    AGAdminTokenResDto getAdminToken(@PathVariable UUID anonymousGroupId,
                                     @RequestBody AGAdminTokenReqDto dto)
            throws UnauthorizedException, AGNotFoundException {
        return anonymousGroupService.getAdminToken(anonymousGroupId, dto);
    }

    @GetMapping("/{anonymousGroupId}/admin/auth/verify")
    @ResponseStatus(HttpStatus.OK)
    void verifyAdminAuth(@PathVariable UUID anonymousGroupId,
                         Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        anonymousGroupService.verifyAdminAuth(anonymousGroupId, authentication);
    }

    @DeleteMapping("/{anonymousGroupId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAnonymousGroup(@PathVariable UUID anonymousGroupId, Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        anonymousGroupService.deleteAnonymousGroup(anonymousGroupId, authentication);
    }

    @GetMapping("/{anonymousGroupId}/locations")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter streamLocations(@PathVariable UUID anonymousGroupId,
                                      Authentication authentication)
            throws UnauthorizedException, AGNotFoundException {
        SseEmitter emitter = new SseEmitter(0L);
        var unsubscribe =
                anonymousGroupService.streamLocations(anonymousGroupId, (location) -> {
                    try {
                        emitter.send(
                                SseEmitter.event().name("location").data(location));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }, authentication);

        emitter.onCompletion(unsubscribe);
        emitter.onError((error) -> {
            unsubscribe.run();
            emitter.complete();
        });
        emitter.onTimeout(() -> {
            unsubscribe.run();
            emitter.complete();
        });

        return emitter;
    }
}
