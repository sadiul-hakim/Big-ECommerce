package org.shopme.site.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    private static final int PAGE_SIZE = 35;

    public Optional<Category> findById(int id) {
        Optional<Category> p = repository.findById(id);
        if (p.isEmpty()) {
            log.warn("CategoryService.findById :: Product does not exists with id : {}", id);
            return p;
        }

        Category category = p.get();
        List<Category> children = repository.findAllByParent(category.getId());
        category.setChildren(children);

        return Optional.of(category);
    }

    public PaginationResult searchCategory(String text, int pageNumber) {
        try {
            Page<Category> page = repository.findAllByNameContaining(text, PageRequest.of(pageNumber, 1000));
            PaginationResult result = PageUtil.prepareResult(page);

            var records = page.getContent();
            List<Object> data = new ArrayList<>();
            records.forEach(d -> {

                List<Category> categories = repository.findAllByParent(d.getId());
                d.setChildren(categories);
                data.add(d);
            });

            result.setData(data);
            return result;
        } catch (Exception ex) {
            log.error("CategoryService.searchCategory :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public List<Category> findAllIn(List<Integer> ids) {
        List<Category> categories = repository.findAllByIdIn(ids);

        for (Category category : categories) {
            List<Category> children = repository.findAllByParent(category.getId());
            category.setChildren(children);
        }

        return categories;
    }

    public Optional<Category> findByName(String name) {
        Optional<Category> p = repository.findByName(name);
        if (p.isEmpty()) {
            log.warn("CategoryService.findById :: Product does not exists with name : {}", name);
            return p;
        }

        Category category = p.get();
        List<Category> children = repository.findAllByParent(category.getId());
        category.setChildren(children);

        return Optional.of(category);
    }

    public List<Category> findAll() {
        List<Category> categories = repository.findAll();
        for (Category category : categories) {
            List<Category> children = repository.findAllByParent(category.getId());
            category.setChildren(children);
        }

        return categories;
    }

    public List<Category> findAllByEnabled(boolean enabled) {
        List<Category> categories = repository.findAllByEnabled(enabled);
        for (Category category : categories) {
            List<Category> children = repository.findAllByParent(category.getId());
            category.setChildren(children);
        }

        return categories;
    }

    public PaginationResult findAllPaginated(int pageNumber) {

        try {

            // Find the page
            Page<Category> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            PaginationResult result = PageUtil.prepareResult(page);

            // Put the children
            var records = page.getContent();
            List<Object> data = new ArrayList<>();
            records.forEach(d -> {

                List<Category> allChildren = repository.findAllByParent(d.getId());
                d.setChildren(allChildren);
                data.add(d);
            });

            result.setData(data);
            return result;
        } catch (Exception ex) {
            log.error("CategoryService.findAllPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }
}
