package org.shopme.site.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository repository;

    public List<Setting> getGeneralSetting() {

        return repository.findAllByCategoryIn(List.of(SettingCategory.GENERAL, SettingCategory.CURRENCY));
    }
}
