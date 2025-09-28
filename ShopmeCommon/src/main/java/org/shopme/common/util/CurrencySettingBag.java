package org.shopme.common.util;

import org.shopme.common.entity.Setting;

import java.util.List;

public class CurrencySettingBag extends SettingBag {
    public CurrencySettingBag(List<Setting> settings) {
        super(settings);
    }

    public String getCurrencyId() {
        Setting setting = get(SettingBag.CURRENCY_ID);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public String getCurrencySymbol() {
        Setting setting = get(SettingBag.CURRENCY_SYMBOL);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }
}
