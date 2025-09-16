package org.shopme.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@AllArgsConstructor
public class SettingBag {

    public static final String CURRENCY_ID = "CURRENCY_ID";
    public static final String CURRENCY_SYMBOL = "CURRENCY_SYMBOL";
    public static final String CURRENCY_SYMBOL_POSITION = "CURRENCY_POSITION";
    public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    public static final String DECIMAL_POINT_TYPE = "DECIMAL_POINT_TYPE";

    public static final String COPYRIGHT = "COPYRIGHT";
    public static final String SITE_LOGO = "SITE_LOGO";
    public static final String SITE_NAME = "SITE_NAME";
    public static final String THOUSAND_POINT_TYPE = "THOUSAND_POINT_TYPE";

    public static final String MAIL_HOST = "MAIL_HOST";
    public static final String MAIL_PORT = "MAIL_PORT";
    public static final String MAIL_USERNAME = "MAIL_USERNAME";
    public static final String MAIL_PASSWORD = "MAIL_PASSWORD";
    public static final String MAIL_FROM = "MAIL_FROM";
    public static final String SMTP_AUTH = "SMTP_AUTH";
    public static final String SMTP_SECURED = "SMTP_SECURED";
    public static final String MAIL_SENDER_NAME = "MAIL_SENDER_NAME";
    public static final String CUSTOMER_VERIFIED_SUBJECT = "CUSTOMER_VERIFIED_SUBJECT";
    public static final String CUSTOMER_VERIFIED_CONTENT = "CUSTOMER_VERIFIED_CONTENT";
    public static final String FORGOT_PASSWORD_SUBJECT = "FORGOT_PASSWORD_SUBJECT";
    public static final String FORGOT_PASSWORD_CONTENT = "FORGOT_PASSWORD_CONTENT";
    public static final String ORDER_CONFIRMATION_SUBJECT = "CUSTOMER_VERIFIED_SUBJECT";
    public static final String ORDER_CONFIRMATION_CONTENT = "CUSTOMER_VERIFIED_CONTENT";

    private final List<Setting> settings;

    public Setting get(String key) {
        int index = settings.indexOf(new Setting(key));
        if (index >= 0) {
            return settings.get(index);
        }
        return null;
    }

    public Setting getOrDefault(String key) {
        int index = settings.indexOf(new Setting(key));
        if (index >= 0) {
            return settings.get(index);
        }
        return new Setting(key);
    }

    public String getValue(String key) {

        Setting setting = get(key);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public void update(String key, String value, SettingCategory defaultCategory) {
        Setting setting = getOrDefault(key);

        if (!StringUtils.hasText(setting.getValue())) {
            setting.setCategory(defaultCategory);
            settings.add(setting);
        }

        if (value != null) {
            setting.setValue(value);
        }
    }

}
