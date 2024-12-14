package org.shopme.admin.currency;

import org.shopme.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}
