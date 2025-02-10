package org.backend.user.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.backend.user.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String hostUrl(HttpServletRequest request){
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null) {
            host = request.getHeader("Host");  // Fallback to normal Host header
        }
        // Ensure HTTPS if needed (use X-Forwarded-Proto if behind a proxy)
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null) {
            scheme = request.getScheme();  // Fallback to request scheme (http/https)
        }
        // Construct full redirect URL using the gateway's host
        return scheme + "://" + host ;
    }

    public static Long getCurrentUserId(){
        CustomUserDetails currentUser= (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUsersID();
    }
}
