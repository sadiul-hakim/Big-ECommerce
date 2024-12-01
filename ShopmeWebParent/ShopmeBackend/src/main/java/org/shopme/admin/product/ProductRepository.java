package org.shopme.admin.product;

import org.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);

    Optional<Product> findByAlias(String alias);

    Optional<Product> findByNameAndAlias(String name, String alias);

    List<Product> findAllByCategory(String category);

    List<Product> findAllByBrand(String brand);

    List<Product> findAllByCategoryAndBrand(String category, String brand);

    Page<Product> findAllByNameContainingOrAliasContainingOrShortDescriptionContainingOrFullDescriptionContaining(String name, String alias, String shortDesc, String fullDsc, Pageable page);

    List<Product> findAllByEnabled(boolean enabled);

    List<Product> findAllByIdIn(List<Integer> ids);
}
