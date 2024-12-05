package org.shopme.site.product;

import org.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);

    Page<Product> findAllByCategory(String category, Pageable pageable);

    Page<Product> findAllByBrand(String brand, Pageable pageable);

    Page<Product> findAllByCategoryAndBrand(String category, String brand, Pageable pageable);

    List<Product> findAllByEnabled(boolean enabled);

    Page<Product> findAllByNameContainingOrAliasContainingOrShortDescriptionContainingOrFullDescriptionContainingOrCategoryContainingOrBrandContaining(String name, String alias, String shortDesc, String fullDsc, String category, String brand, Pageable page);
}
