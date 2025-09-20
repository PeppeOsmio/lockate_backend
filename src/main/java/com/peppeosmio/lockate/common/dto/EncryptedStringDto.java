package com.peppeosmio.lockate.common.dto;

import com.peppeosmio.lockate.common.classes.EncryptedString;
import jakarta.validation.constraints.NotBlank;

import java.util.Base64;

public record EncryptedStringDto(@NotBlank String cipherText, @NotBlank String iv,
                                 @NotBlank String authTag, @NotBlank String salt) {
    public EncryptedString toEncryptedString() {
        var decoder = Base64.getDecoder();
        return new EncryptedString(decoder.decode(cipherText), decoder.decode(iv),
                decoder.decode(authTag), decoder.decode(salt));
    }

    public static EncryptedStringDto fromEncryptedString(
            EncryptedString encryptedString) {
        var encoder = Base64.getEncoder();
        return new EncryptedStringDto(
                encoder.encodeToString(encryptedString.cipherText()),
                encoder.encodeToString(encryptedString.iv()),
                encoder.encodeToString(encryptedString.authTag()),
                encoder.encodeToString(encryptedString.salt()));
    }
}
