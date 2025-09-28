package org.shopme.site.currency;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Currency;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository repository;

    public Currency findById(long id) {
        return repository.findById(id).orElse(null);
    }
}
