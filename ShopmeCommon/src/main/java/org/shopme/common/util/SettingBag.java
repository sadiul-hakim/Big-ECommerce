package org.shopme.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.shopme.common.entity.Setting;

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

    private final List<Setting> settings;

    public Setting get(String key) {
        int index = settings.indexOf(new Setting(key));
        if (index >= 0) {
            return settings.get(index);
        }
        return null;
    }

    public String getValue(String key) {

        Setting setting = get(key);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);

        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

}
