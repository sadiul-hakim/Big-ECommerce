package org.shopme.admin.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Order;
import org.shopme.common.enumeration.OrderStatus;
import org.shopme.common.enumeration.PaymentMethod;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${app.table.page.size:35}")
    private int PAGE_SIZE;

    private final OrderRepository repository;

    public PaginationResult findAll(int pageNumber) {
        Page<Order> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        PaymentMethod method = PaymentMethod.findByName(text);
        OrderStatus status = OrderStatus.findByName(text);
        Page<Order> page = repository.searchOrders(
                text, method, status, PageRequest.of(pageNumber, PAGE_SIZE)
        );
        return PageUtil.prepareResult(page);
    }

    public Order findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public JpaResult delete(long id) {
        try {
            repository.deleteById(id);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted order.");
        } catch (Exception ex) {
            log.error("Deleting order, error {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Failed to delete order.");
        }
    }
}
