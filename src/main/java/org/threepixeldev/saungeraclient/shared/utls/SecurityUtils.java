package org.threepixeldev.saungeraclient.shared.utls;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.threepixeldev.saungeraclient.security.dto.CustomUserPrincipal;
import org.threepixeldev.saungeraclient.security.exceptions.UnauthorizedException;

import lombok.NoArgsConstructor;
@NoArgsConstructor
public class SecurityUtils {

    public static CustomUserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && 
            authentication.getPrincipal() instanceof CustomUserPrincipal principal) {
            return principal;
        }
        
        throw new UnauthorizedException("User is not authenticated");
    }

    public static Long getUserId() {
        return getPrincipal().userId();
    }
    
    public static String getIdentifier() {
        return getPrincipal().identifier();
    }
}
