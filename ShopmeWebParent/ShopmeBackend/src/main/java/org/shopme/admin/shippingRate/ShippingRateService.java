package org.shopme.admin.shippingRate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.ShippingRate;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingRateService {

    @Value("${app.table.page.size:35}")
    private int PAGE_SIZE;

    private final ShippingRateRepository repository;


    public JpaResult save(ShippingRate shippingRate) {

        try {

            Optional<ShippingRate> existingCategoryByName = repository.findByCountryAndState(shippingRate.getCountry(),
                    shippingRate.getState());
            if (existingCategoryByName.isPresent()) {
                log.warn("ShippingRate with country {} and state {} already exists.", shippingRate.getCountry(),
                        shippingRate.getState());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "Category with name same country and state already exists.");
            }

            repository.save(shippingRate);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved the rate.");
        } catch (Exception ex) {
            log.error("ShippingRateService.save :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not save the rate.");
        }
    }

    public JpaResult update(ShippingRate shippingRate) {

        if (findById(shippingRate.getId()).isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "Could not find the rate.");
        }
        repository.save(shippingRate);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved the rate.");
    }

    public Optional<ShippingRate> findById(int id) {
        return repository.findById(id);
    }

    public JpaResult delete(int id) {

        try {
            repository.deleteById(id);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted the rate.");
        } catch (Exception ex) {
            log.error("ShippingRateService.delete :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not delete the rate Category : " + id);
        }
    }

    public PaginationResult findAllPaginated(int pageNumber) {

        try {

            Page<ShippingRate> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ShippingRateService.findAllPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public PaginationResult search(String text, int pageNum) {

        try {
            var page = repository.findAllByCountryContainingIgnoreCaseAndStateContainingIgnoreCase(text, text, PageRequest.of(pageNum, PAGE_SIZE));
            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ShippingRateService.search :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public byte[] csvData() {

        var rates = repository.findAll();
        StringBuilder data = new StringBuilder("Id,Country,State,Rate,Delivery Days,COD supported\n");
        for (var rate : rates) {
            data.append(rate.getId())
                    .append(",")
                    .append(rate.getCountry())
                    .append(",")
                    .append(rate.getState())
                    .append(",")
                    .append(rate.getRate())
                    .append(",")
                    .append(rate.getDays())
                    .append(",")
                    .append(rate.isCodSupported() ? "Yes" : "No")
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
