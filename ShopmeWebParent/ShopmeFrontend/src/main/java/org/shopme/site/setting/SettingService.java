package org.shopme.site.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.shopme.common.util.GeneralSettingBag;
import org.shopme.common.util.MailServerSettingBag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository repository;

    public GeneralSettingBag getGeneralSettingBag() {
        List<Setting> settings = repository.findAllByCategoryIn(List.of(SettingCategory.GENERAL, SettingCategory.CURRENCY));
        return new GeneralSettingBag(settings);
    }

    public MailServerSettingBag getMailServerSettingBag() {
        List<Setting> settings = repository.findAllByCategoryIn(List.of(SettingCategory.MAIL_SERVICE, SettingCategory.MAIL_TEMPLATES));
        return new MailServerSettingBag(settings);
    }
}
