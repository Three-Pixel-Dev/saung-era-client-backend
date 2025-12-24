package org.threepixeldev.saungeraclient.security.exceptions.dto;

import java.util.Map;

public record ErrorResponse(int code, String message, Map<String, String> meta) {
}
