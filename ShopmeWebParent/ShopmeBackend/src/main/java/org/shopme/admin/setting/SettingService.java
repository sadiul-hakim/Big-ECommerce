package org.shopme.admin.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.currency.CurrencyRepository;
import org.shopme.common.entity.Currency;
import org.shopme.common.util.FileUtil;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.shopme.common.util.GeneralSettingBag;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingService {
    private static final String FILE_PATH = "/logo/";
    private static final String DEFAULT_PHOTO_NAME = "logo.webp";

    private final SettingRepository repository;
    private final CurrencyRepository currencyRepository;

    public List<Setting> findAll() {
        return repository.findAll();
    }

    public GeneralSettingBag getGeneralSetting() {
        List<Setting> generalSettings = repository.findAllByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = repository.findAllByCategory(SettingCategory.CURRENCY);
        generalSettings.addAll(currencySettings);

        return new GeneralSettingBag(generalSettings);
    }

    public void saveAll(Iterable<Setting> settings) {
        repository.saveAll(settings);
    }

    public void saveGeneralSettings(MultipartFile SITE_LOGO,
                                    String SITE_NAME,
                                    String COPYRIGHT,
                                    String THOUSAND_POINT_TYPE,
                                    String CURRENCY_SYMBOL_POSITION,
                                    String DECIMAL_POINT_TYPE,
                                    String CURRENCY_ID,
                                    String DECIMAL_DIGITS) {

        GeneralSettingBag settingBag = getGeneralSetting();

        // Handle site logo
        if (SITE_LOGO != null && StringUtils.hasText(SITE_LOGO.getOriginalFilename())) {
            var filePath = FileUtil.uploadFile(SITE_LOGO, FILE_PATH);
            if (!StringUtils.hasText(filePath)) {
                settingBag.updateSiteLogo(DEFAULT_PHOTO_NAME);
            } else {
                Setting logo = settingBag.get(GeneralSettingBag.SITE_LOGO);
                FileUtil.deleteFile(FILE_PATH, logo.getValue());
                settingBag.updateSiteLogo(filePath);
            }
        }

        if (StringUtils.hasText(SITE_NAME)) {
            settingBag.update(GeneralSettingBag.SITE_NAME, SITE_NAME);
        }

        if (StringUtils.hasText(COPYRIGHT)) {
            settingBag.update(GeneralSettingBag.COPYRIGHT, COPYRIGHT);
        }

        if (StringUtils.hasText(THOUSAND_POINT_TYPE)) {
            settingBag.update(GeneralSettingBag.THOUSAND_POINT_TYPE, THOUSAND_POINT_TYPE);
        }

        if (StringUtils.hasText(CURRENCY_SYMBOL_POSITION)) {
            settingBag.update(GeneralSettingBag.CURRENCY_SYMBOL_POSITION, CURRENCY_SYMBOL_POSITION);
        }

        if (StringUtils.hasText(DECIMAL_DIGITS)) {
            settingBag.update(GeneralSettingBag.DECIMAL_DIGITS, DECIMAL_DIGITS);
        }

        if (StringUtils.hasText(DECIMAL_POINT_TYPE)) {
            settingBag.update(GeneralSettingBag.DECIMAL_POINT_TYPE, DECIMAL_POINT_TYPE);
        }

        if (StringUtils.hasText(CURRENCY_ID) && Integer.parseInt(CURRENCY_ID) > 0) {
            Optional<Currency> currencyOptional = currencyRepository.findById(Integer.parseInt(CURRENCY_ID));
            currencyOptional.ifPresent(currency -> settingBag.updateCurrencySymbol(currency.getSymbol()));
            settingBag.update(GeneralSettingBag.CURRENCY_ID, CURRENCY_ID);
        }

        List<Setting> settings = settingBag.getSettings();
        saveAll(settings);
    }
}
