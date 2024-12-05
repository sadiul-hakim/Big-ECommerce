package org.shopme.site.category;

import org.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(String name);

    List<Category> findAllByEnabled(boolean enabled);

    List<Category> findAllByParent(int parent);

    List<Category> findAllByIdIn(List<Integer> ids);

    Page<Category> findAllByNameContaining(String text, Pageable pageable);
}
