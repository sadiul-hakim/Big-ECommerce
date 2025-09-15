package org.shopme.site.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.shopme.site.security.CustomUserDetailsService;
import org.shopme.site.util.AuthenticatedUserUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String username = AuthenticatedUserUtil.getOAuthValue(user, AuthenticatedUserUtil.EMAIL);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String contextPath = request.getContextPath();

        if (!StringUtils.hasText(userDetails.getUsername())) {

            // Clear security context and session if user is not registered
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Redirect to login page with error and message
            String errorMessage = URLEncoder.encode("Account not registered. Please sign up first.", StandardCharsets.UTF_8);
            response.sendRedirect(contextPath + "/loginPage?loginError=true&message=" + errorMessage);
            return;
        }

        // Authenticate user with UserDetails
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // Store the context in the session (important, otherwise it won't persist after redirect)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // Redirect to home page
        response.sendRedirect(contextPath + "/");
    }
}
