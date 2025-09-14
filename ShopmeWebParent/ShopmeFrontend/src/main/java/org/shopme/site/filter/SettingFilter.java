package org.shopme.site.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Setting;
import org.shopme.common.util.GeneralSettingBag;
import org.shopme.common.util.NumberFormatter;
import org.shopme.common.util.SettingBag;
import org.shopme.site.setting.SettingService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettingFilter implements Filter {
    private final SettingService service;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURL().toString();
        if (url.contains("/css") || url.contains("/js") || url.contains("/images") || url.contains("/font") || url.contains("/image"))
            chain.doFilter(request, response);

        GeneralSettingBag generalSetting = service.getGeneralSettingBag();

        String DECIMAL_POINT_TYPE = "";
        String THOUSAND_POINT_TYPE = "";
        int DECIMAL_DIGITS = 1;

        for (Setting setting : generalSetting.getSettings()) {
            req.setAttribute(setting.getKey(), setting.getValue());

            switch (setting.getKey()) {
                case SettingBag.DECIMAL_POINT_TYPE -> DECIMAL_POINT_TYPE = setting.getValue();
                case SettingBag.THOUSAND_POINT_TYPE -> THOUSAND_POINT_TYPE = setting.getValue();
                case SettingBag.DECIMAL_DIGITS -> DECIMAL_DIGITS = Integer.parseInt(setting.getValue());
            }
        }

        DECIMAL_POINT_TYPE = DECIMAL_POINT_TYPE.equals("COMMA") ? "," : ".";
        THOUSAND_POINT_TYPE = THOUSAND_POINT_TYPE.equals("COMMA") ? "," : ".";

        NumberFormatter formatter = new NumberFormatter(THOUSAND_POINT_TYPE, DECIMAL_POINT_TYPE, DECIMAL_DIGITS);
        req.setAttribute("numberFormater", formatter);

        chain.doFilter(request, response);
    }
}
