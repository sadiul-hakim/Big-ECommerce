package org.shopme.admin.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.currency.CurrencyRepository;
import org.shopme.admin.service.CacheService;
import org.shopme.common.entity.Currency;
import org.shopme.common.util.*;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.springframework.cache.annotation.Cacheable;
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

    private static final String GENERIC_SETTING_CACHE = "SettingService.getGeneralSettingBag";
    private static final String CURRENCY_SETTING_CACHE = "SettingService.getCurrencySettingBag";

    private final SettingRepository repository;
    private final CurrencyRepository currencyRepository;
    private final CacheService cacheService;

    public List<Setting> findAll() {
        return repository.findAll();
    }

    @Cacheable(GENERIC_SETTING_CACHE)
    public GeneralSettingBag getGeneralSettingBag() {
        List<Setting> settings = repository.findAllByCategoryIn(List.of(SettingCategory.GENERAL, SettingCategory.CURRENCY));
        return new GeneralSettingBag(settings);
    }

    @Cacheable(CURRENCY_SETTING_CACHE)
    public CurrencySettingBag getCurrencySettingBag() {
        List<Setting> settings = repository.findAllByCategoryIn(List.of(SettingCategory.CURRENCY));
        return new CurrencySettingBag(settings);
    }

    public MailServerSettingBag getMailServerSettingBag() {
        List<Setting> settings = repository.findAllByCategoryIn(List.of(SettingCategory.MAIL_SERVICE, SettingCategory.MAIL_TEMPLATES));
        return new MailServerSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings) {
        repository.saveAll(settings);
        clearCache();
    }

    public void saveTemplate(
            String CUSTOMER_VERIFIED_SUBJECT,
            String CUSTOMER_VERIFIED_CONTENT,
            String ORDER_CONFIRMATION_SUBJECT,
            String ORDER_CONFIRMATION_CONTENT,
            String FORGOT_PASSWORD_SUBJECT,
            String FORGOT_PASSWORD_CONTENT

    ) {
        List<Setting> mailServerSetting = repository.findAllByCategory(SettingCategory.MAIL_TEMPLATES);
        MailServerSettingBag bag = new MailServerSettingBag(mailServerSetting);

        bag.updateMailTemplate(SettingBag.CUSTOMER_VERIFIED_SUBJECT, CUSTOMER_VERIFIED_SUBJECT);
        bag.updateMailTemplate(SettingBag.CUSTOMER_VERIFIED_CONTENT, CUSTOMER_VERIFIED_CONTENT);
        bag.updateMailTemplate(SettingBag.ORDER_CONFIRMATION_SUBJECT, ORDER_CONFIRMATION_SUBJECT);
        bag.updateMailTemplate(SettingBag.ORDER_CONFIRMATION_CONTENT, ORDER_CONFIRMATION_CONTENT);
        bag.updateMailTemplate(SettingBag.FORGOT_PASSWORD_SUBJECT, FORGOT_PASSWORD_SUBJECT);
        bag.updateMailTemplate(SettingBag.FORGOT_PASSWORD_CONTENT, FORGOT_PASSWORD_CONTENT);

        List<Setting> settings = bag.getSettings();
        saveAll(settings);
        clearCache();
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
        MailServerSettingBag bag = new MailServerSettingBag(mailServerSetting);
        bag.updateMailService(SettingBag.MAIL_HOST, MAIL_HOST);
        bag.updateMailService(SettingBag.MAIL_PORT, MAIL_PORT);
        bag.updateMailService(SettingBag.MAIL_USERNAME, MAIL_USERNAME);
        bag.updateMailService(SettingBag.MAIL_PASSWORD, MAIL_PASSWORD);
        bag.updateMailService(SettingBag.MAIL_FROM, MAIL_FROM);
        bag.updateMailService(SettingBag.MAIL_SENDER_NAME, MAIL_SENDER_NAME);
        bag.updateMailService(SettingBag.SMTP_AUTH, String.valueOf(SMTP_AUTH));
        bag.updateMailService(SettingBag.SMTP_SECURED, String.valueOf(SMTP_SECURED));

        List<Setting> settings = bag.getSettings();
        saveAll(settings);
        clearCache();
    }

    public void saveGeneralSettings(MultipartFile SITE_LOGO,
                                    String SITE_NAME,
                                    String COPYRIGHT,
                                    String THOUSAND_POINT_TYPE,
                                    String CURRENCY_SYMBOL_POSITION,
                                    String DECIMAL_POINT_TYPE,
                                    String CURRENCY_ID,
                                    String DECIMAL_DIGITS) {

        GeneralSettingBag settingBag = getGeneralSettingBag();

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
        clearCache();
    }

    private void clearCache() {
        cacheService.clearCache(GENERIC_SETTING_CACHE);
        cacheService.clearCache(CURRENCY_SETTING_CACHE);
    }
}
