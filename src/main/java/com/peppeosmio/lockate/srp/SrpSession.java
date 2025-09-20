package com.peppeosmio.lockate.srp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record SrpSession(String A, String b, String B, LocalDateTime createdAt) {
}
