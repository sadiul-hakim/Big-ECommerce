package org.shopme.admin.setting;

import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {
    List<Setting> findAllByCategory(SettingCategory category);
    List<Setting> findAllByCategoryIn(List<SettingCategory> categories);
}
