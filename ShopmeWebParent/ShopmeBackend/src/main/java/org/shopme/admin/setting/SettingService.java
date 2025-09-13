package org.shopme.admin.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.currency.CurrencyRepository;
import org.shopme.common.entity.Currency;
import org.shopme.common.util.FileUtil;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.shopme.common.util.GeneralSettingBag;
import org.shopme.common.util.SettingBag;
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

    public void saveMailServerSettings(
            String MAIL_HOST,
            String MAIL_PORT,
            String MAIL_USERNAME,
            String MAIL_PASSWORD,
            String MAIL_FROM,
            String MAIL_SENDER_NAME,
            boolean SMTP_AUTH,
            boolean SMTP_SECURED
    ) {

        List<Setting> mailServerSetting = repository.findAllByCategory(SettingCategory.MAIL_SERVICE);
        GeneralSettingBag bag = new GeneralSettingBag(mailServerSetting);
        if (StringUtils.hasText(MAIL_HOST)) {
            bag.update(SettingBag.MAIL_HOST, MAIL_HOST, SettingCategory.MAIL_SERVICE);
        }

        if (StringUtils.hasText(MAIL_PORT)) {
            bag.update(SettingBag.MAIL_PORT, MAIL_PORT, SettingCategory.MAIL_SERVICE);
        }

        if (StringUtils.hasText(MAIL_USERNAME)) {
            bag.update(SettingBag.MAIL_USERNAME, MAIL_USERNAME, SettingCategory.MAIL_SERVICE);
        }

        if (StringUtils.hasText(MAIL_PASSWORD)) {
            bag.update(SettingBag.MAIL_PASSWORD, MAIL_PASSWORD, SettingCategory.MAIL_SERVICE);
        }

        if (StringUtils.hasText(MAIL_FROM)) {
            bag.update(SettingBag.MAIL_FROM, MAIL_FROM, SettingCategory.MAIL_SERVICE);
        }

        if (StringUtils.hasText(MAIL_SENDER_NAME)) {
            bag.update(SettingBag.MAIL_SENDER_NAME, MAIL_SENDER_NAME, SettingCategory.MAIL_SERVICE);
        }

        if (SMTP_AUTH) {
            bag.update(SettingBag.SMTP_AUTH, "Yes", SettingCategory.MAIL_SERVICE);
        } else {
            bag.update(SettingBag.SMTP_AUTH, "No", SettingCategory.MAIL_SERVICE);
        }

        if (SMTP_SECURED) {
            bag.update(SettingBag.SMTP_SECURED, "Yes", SettingCategory.MAIL_SERVICE);
        } else {
            bag.update(SettingBag.SMTP_SECURED, "No", SettingCategory.MAIL_SERVICE);
        }

        List<Setting> settings = bag.getSettings();
        saveAll(settings);
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
            settingBag.update(GeneralSettingBag.SITE_NAME, SITE_NAME, SettingCategory.GENERAL);
        }

        if (StringUtils.hasText(COPYRIGHT)) {
            settingBag.update(GeneralSettingBag.COPYRIGHT, COPYRIGHT, SettingCategory.GENERAL);
        }

        if (StringUtils.hasText(THOUSAND_POINT_TYPE)) {
            settingBag.update(GeneralSettingBag.THOUSAND_POINT_TYPE, THOUSAND_POINT_TYPE, SettingCategory.CURRENCY);
        }

        if (StringUtils.hasText(CURRENCY_SYMBOL_POSITION)) {
            settingBag.update(GeneralSettingBag.CURRENCY_SYMBOL_POSITION, CURRENCY_SYMBOL_POSITION, SettingCategory.CURRENCY);
        }

        if (StringUtils.hasText(DECIMAL_DIGITS)) {
            settingBag.update(GeneralSettingBag.DECIMAL_DIGITS, DECIMAL_DIGITS, SettingCategory.CURRENCY);
        }

        if (StringUtils.hasText(DECIMAL_POINT_TYPE)) {
            settingBag.update(GeneralSettingBag.DECIMAL_POINT_TYPE, DECIMAL_POINT_TYPE, SettingCategory.CURRENCY);
        }

        if (StringUtils.hasText(CURRENCY_ID) && Integer.parseInt(CURRENCY_ID) > 0) {
            Optional<Currency> currencyOptional = currencyRepository.findById(Integer.parseInt(CURRENCY_ID));
            currencyOptional.ifPresent(currency -> settingBag.updateCurrencySymbol(currency.getSymbol()));
            settingBag.update(GeneralSettingBag.CURRENCY_ID, CURRENCY_ID, SettingCategory.CURRENCY);
        }

        List<Setting> settings = settingBag.getSettings();
        saveAll(settings);
    }
}
