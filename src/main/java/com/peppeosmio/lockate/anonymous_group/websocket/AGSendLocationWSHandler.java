package com.peppeosmio.lockate.anonymous_group.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peppeosmio.lockate.anonymous_group.dto.AGLocationSaveRequestDto;
import com.peppeosmio.lockate.anonymous_group.exceptions.AGNotFoundException;
import com.peppeosmio.lockate.anonymous_group.service.AnonymousGroupService;
import com.peppeosmio.lockate.common.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class AGSendLocationWSHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AnonymousGroupService anonymousGroupService;

    public AGSendLocationWSHandler(
            ObjectMapper objectMapper, AnonymousGroupService anonymousGroupService) {
        this.objectMapper = objectMapper;
        this.anonymousGroupService = anonymousGroupService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws IOException {
        try {
            var sessionAttributes = session.getAttributes();
            var anonymousGroupId = (UUID) sessionAttributes.get("anonymousGroupId");
            var authentication = (Authentication) sessionAttributes.get("authentication");
            var dto = objectMapper.readValue(message.getPayload(), AGLocationSaveRequestDto.class);
            anonymousGroupService.saveLocation(anonymousGroupId, authentication, dto);
        } catch (AGNotFoundException | UnauthorizedException | JsonProcessingException e) {
            session.close(CloseStatus.BAD_DATA);
        } catch (Exception e) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}
