package org.shopme.site.util;

import jakarta.servlet.http.HttpServletRequest;
import org.shopme.common.util.MailServerSettingBag;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public final class AppUtility {
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_START_TLS_ENABLED = "mail.smtp.starttls.enable";
//    private static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";

    private AppUtility() {
    }

    public static String getSiteUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(MailServerSettingBag settingBag) {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(settingBag.getHost());
        mailSender.setPort(settingBag.getPort());
        mailSender.setUsername(settingBag.getUsername());
        mailSender.setPassword(settingBag.getPassword());

        Properties properties = new Properties();
        properties.put(MAIL_SMTP_AUTH, settingBag.isSmtpAuth());
        properties.put(MAIL_SMTP_START_TLS_ENABLED, settingBag.isSmtpSecured());
//        properties.put(MAIL_SMTP_SSL_TRUST, settingBag.getHost());

        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
