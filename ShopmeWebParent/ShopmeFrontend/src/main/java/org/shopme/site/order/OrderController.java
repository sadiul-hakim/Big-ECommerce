package org.shopme.site.order;

import lombok.RequiredArgsConstructor;
import org.shopme.common.enumeration.PaymentMethod;
import org.shopme.common.util.JpaResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/place/{method}")
    public String checkout(@PathVariable PaymentMethod method, Model model) {
        JpaResult jpaResult = orderService.placeOrderOfCurrentUser(method);
        model.addAttribute("successful", true);
        model.addAttribute("orderId", jpaResult.entityId());
        return "order_successful_page";
    }
}
