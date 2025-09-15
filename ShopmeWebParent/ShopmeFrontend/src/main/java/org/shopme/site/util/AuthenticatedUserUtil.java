package org.shopme.site.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AuthenticatedUserUtil {
    private AuthenticatedUserUtil() {
    }

    public final static String NAME = "name";
    public final static String VERIFIED = "email_verified";
    public final static String ISS = "iss";
    public final static String SUB = "sub";
    public final static String NONCE = "nonce";
    public final static String PICTURE = "picture";
    public final static String EMAIL = "email";
    public final static String FAMILY_NAME = "family_name";

    public static String getOAuthValue(OAuth2User user, String key) {

        try {

            return user.getAttribute(key);
        } catch (Exception ex) {
            return "";
        }
    }
}
