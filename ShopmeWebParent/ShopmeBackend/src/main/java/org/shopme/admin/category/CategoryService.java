package org.shopme.admin.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private static final int PAGE_SIZE = 10;

    public JpaResult save(Category category) {

        try {

            // Check unique ness
            Optional<Category> existingCategoryByName = repository.findByName(category.getName());
            if (existingCategoryByName.isPresent()) {
                log.warn("Category with name {} already exists.", category.getName());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "Category with name " + category.getName() + " already exists.");
            }

            Optional<Category> existingCategoryByAlias = repository.findByAlias(category.getAlias());
            if (existingCategoryByAlias.isPresent()) {
                log.warn("Category with alias {} already exists.", category.getAlias());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "Category with alias " + category.getAlias() + " already exists.");
            }

            var savedCategory = repository.save(category);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Category : " + savedCategory.getName());
        } catch (Exception ex) {
            log.error("CategoryService.save :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not save Category : " + category.getName());
        }
    }

    public JpaResult update(Category category) {

        if (findById(category.getId()).isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "Could not find category : " + category.getName());
        }
        repository.save(category);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Category : " + category.getName());
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

    public List<Category> findAllIn(List<Integer> ids) {
        List<Category> categories = repository.findAllByIdIn(ids);

        for (Category category : categories) {
            List<Category> children = repository.findAllByParent(category.getId());
            category.setChildren(children);
        }

        return categories;
    }

    public PaginationResult searchCategory(String text, int pageNum) {

        try {
            var page = repository.findAllByNameContainingOrAliasContaining(text, text, PageRequest.of(pageNum, 10_000));
            PaginationResult searchResult = PageUtil.prepareResult(page);

            // Put the children
            var records = page.getContent();
            List<Object> data = new ArrayList<>();
            records.forEach(d -> {

                List<Category> allChildren = repository.findAllByParent(d.getId());
                d.setChildren(allChildren);
                data.add(d);
            });

            searchResult.setData(data);
            return searchResult;

        } catch (Exception ex) {
            log.error("CategoryService.searchCategory :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public List<Category> findAll() {
        List<Category> categories = repository.findAll();
        for (Category category : categories) {
            List<Category> children = repository.findAllByParent(category.getId());
            category.setChildren(children);
        }

        return categories;
    }

    public Optional<Category> findById(int id) {
        return repository.findById(id);
    }

    public JpaResult delete(int id) {

        try {
            List<Category> children = repository.findAllByParent(id);
            if (!children.isEmpty()) {
                return new JpaResult(JpaResultType.NOT_ALLOWED, "The category has children categories!");
            }

            repository.deleteById(id);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted category.");
        } catch (Exception ex) {
            log.error("CategoryService.delete :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not delete Category : " + id);
        }
    }

    public byte[] csvData() {
        var categories = findAll();
        StringBuilder data = new StringBuilder("Id,Name,Alias,Parent,Children,Enabled\n");
        for (var category : categories) {
            data.append(category.getId())
                    .append(",")
                    .append(category.getName())
                    .append(",")
                    .append(category.getAlias())
                    .append(",")
                    .append(category.getParent())
                    .append(",");

            if (category.getChildren().isEmpty()) {
                data.append("-")
                        .append(",");
            } else {
                StringJoiner joiner = new StringJoiner(";", "[", "]");
                for (Category child : category.getChildren()) {
                    joiner.add(child.getName());
                }
                data.append(joiner)
                        .append(",");
            }
            data.append(category.isEnabled())
                    .append(",")
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }

    public List<String> childrenText(int categoryId) {
        Optional<Category> category = findById(categoryId);
        if (category.isEmpty()) {
            return new ArrayList<>();
        }

        List<Category> children = repository.findAllByParent(categoryId);

        List<String> text = new ArrayList<>();
        text.add(category.get().getName());

        for (Category child : children) {
            text.add("-> " + child.getName());
        }

        return text;
    }
}
