package com.thales.bcb.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@UtilityClass
public class SecurityUtil {

    public UUID getClientIdFromToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = authentication.getName();;
        return UUID.fromString(clientId);
    }
}
