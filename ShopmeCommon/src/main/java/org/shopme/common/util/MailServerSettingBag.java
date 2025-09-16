package org.shopme.common.util;

import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.springframework.util.StringUtils;

import java.util.List;

public class MailServerSettingBag extends SettingBag {

    public MailServerSettingBag(List<Setting> settings) {
        super(settings);
    }

    public String getHost() {
        return getValue(MAIL_HOST);
    }

    public int getPort() {
        String port = getValue(MAIL_PORT);

        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public String getUsername() {
        return getValue(MAIL_USERNAME);
    }

    public String getPassword() {
        return getValue(MAIL_PASSWORD);
    }

    public String getFromAddress() {
        return getValue(MAIL_FROM);
    }

    public String isSmtpAuth() {
        return getValue(SMTP_AUTH);
    }

    public String isSmtpSecured() {
        return getValue(SMTP_SECURED);
    }

    public String getSenderName() {
        return getValue(MAIL_SENDER_NAME);
    }

    public String getCustomerVerifiedSubject() {
        return getValue(CUSTOMER_VERIFIED_SUBJECT);
    }

    public String getForgotPasswordSubject() {
        return getValue(FORGOT_PASSWORD_SUBJECT);
    }

    public String getForgotPasswordContent() {
        return getValue(FORGOT_PASSWORD_CONTENT);
    }

    public String getCustomerVerifiedContent() {
        return getValue(CUSTOMER_VERIFIED_CONTENT);
    }

    public String getOrderConfirmationSubject() {
        return getValue(ORDER_CONFIRMATION_SUBJECT);
    }

    public String getOrderConfirmationContent() {
        return getValue(ORDER_CONFIRMATION_CONTENT);
    }

    public void updateMailTemplate(String key, String value) {

        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            update(key, value, SettingCategory.MAIL_TEMPLATES);
        }
    }

    public void updateMailService(String key, String value) {

        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            update(key, value, SettingCategory.MAIL_SERVICE);
        }
    }
}
