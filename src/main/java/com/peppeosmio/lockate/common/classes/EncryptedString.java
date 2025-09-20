package com.peppeosmio.lockate.common.classes;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

import java.util.Base64;

public record EncryptedString(byte[] cipherText, byte[] iv, byte[] authTag,
                              byte[] salt) {

}
