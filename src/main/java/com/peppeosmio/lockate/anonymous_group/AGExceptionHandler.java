package com.peppeosmio.lockate.anonymous_group;

import com.peppeosmio.lockate.anonymous_group.exceptions.AGNotFoundException;
import com.peppeosmio.lockate.anonymous_group.exceptions.Base64Exception;
import com.peppeosmio.lockate.common.dto.ErrorResponseDto;
import com.peppeosmio.lockate.srp.InvalidSrpSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AGExceptionHandler {
    @ExceptionHandler(Base64Exception.class)
    public ResponseEntity<ErrorResponseDto> handleBase64(Base64Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("invalid_base64"));
    }

    @ExceptionHandler(InvalidSrpSessionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSrpSession(
            InvalidSrpSessionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("invalid_srp_session"));
    }

    @ExceptionHandler(AGNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAGNotFound(AGNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("ag_not_found"));
    }
}
