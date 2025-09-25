package org.shopme.admin.order;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.setting.SettingService;
import org.shopme.common.entity.Order;
import org.shopme.common.entity.Setting;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.CurrencySettingBag;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.NumberFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String RESULT = "result";
    private static final String PAGE = "orders";
    private static final String ERROR = "error";
    private static final String DELETING_ORDER = "deletingOrder";
    private static final String DELETED_ORDER = "deletedOrder";
    private static final String MESSAGE = "message";
    private static final String TABLE_URL = "tableUrl";

    private final TableUrlPojo pageUrl = new TableUrlPojo("/orders/search", "/orders",
            "/orders/export-csv", "/orders/create-page");

    private final OrderService service;
    private final SettingService settingService;

    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {
        PaginationResult result = service.findAll(page);
        model.addAttribute(RESULT, result);
        model.addAttribute(TABLE_URL, pageUrl);
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
        model.addAttribute(TABLE_URL, pageUrl);

        return PAGE;
    }

    @GetMapping(value = "/{id}/details")
    public String getOrderDetails(@PathVariable long id, Model mav, RedirectAttributes redirectAttributes) {
        Order order = service.findById(id);
        if (order == null) {
            redirectAttributes.addFlashAttribute(ERROR, true);
            redirectAttributes.addFlashAttribute(MESSAGE, "Failed to load details modal.");
            return "redirect:/" + PAGE;
        }

        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();
        for (Setting setting : currencySettingBag.getSettings()) {
            mav.addAttribute(setting.getKey(), setting.getValue());
        }

        NumberFormatter numberFormatter = NumberFormatter.generate(currencySettingBag);
        mav.addAttribute("numberFormatter", numberFormatter);

        mav.addAttribute("order", order);
        return "fragment/order_modal :: modalContent";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        JpaResult res = service.delete(id);
        redirectAttributes.addFlashAttribute(DELETING_ORDER, true);
        redirectAttributes.addFlashAttribute(DELETED_ORDER, res.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(MESSAGE, res.message());

        return "redirect:/" + PAGE;
    }
}
