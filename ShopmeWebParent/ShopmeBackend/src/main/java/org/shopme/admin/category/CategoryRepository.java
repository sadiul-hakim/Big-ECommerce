package org.shopme.admin.category;

import org.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByNameAndAlias(String name, String alias);

    Optional<Category> findByName(String name);

    Optional<Category> findByAlias(String alias);

    List<Category> findAllByEnabled(boolean enabled);

    Page<Category> findAllByNameContainingOrAliasContaining(String name, String alias, Pageable page);

    List<Category> findAllByParent(int parent);
}
