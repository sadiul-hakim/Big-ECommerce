package org.shopme.admin.order;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.setting.SettingService;
import org.shopme.common.entity.Setting;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.CurrencySettingBag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String RESULT = "result";
    private static final String PAGE = "orders";

    private final OrderService service;
    private final SettingService settingService;

    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {
        PaginationResult result = service.findAll(page);
        model.addAttribute(RESULT, result);
        return PAGE;
    }

    @GetMapping("/search")
    public String page(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        PaginationResult result = service.search(text, page);
        model.addAttribute(RESULT, result);
        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();
        for (Setting setting : currencySettingBag.getSettings()) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        return PAGE;
    }
}
