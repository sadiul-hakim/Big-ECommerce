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

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private static final int PAGE_SIZE = 10;

    public JpaResult save(Category category) {

        try {
            // Check unique ness
            Optional<Category> existingCategory = findByNameAndAlias(category.getName(), category.getAlias());
            if (existingCategory.isPresent()) {
                log.warn("Category with name {} and alias {} already exists.", category.getName(), category.getAlias());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "Category with name " + category.getName() + " and alias " + category.getAlias() + " already exists.");
            }

            var savedCategory = repository.save(category);

            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Category : " + savedCategory.getName());
        } catch (Exception ex) {
            log.error("CategoryService.save :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not save Category : " + category.getName());
        }
    }

    public JpaResult update(Category category) {
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Category : " + category.getName());
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        Page<Category> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));

        var records = page.getContent();
        List<Object> data = new ArrayList<>();
        records.forEach(d -> {
            List<Category> allChildren = repository.findAllByParent(d.getId());
            d.setChildren(allChildren);
            data.add(d);
        });

        PaginationResult result = PageUtil.prepareResult(page);
        result.setData(data);
        return result;
    }

    public PaginationResult searchCategory(String text, int pageNum) {
        var page = repository.findAllByNameContainingOrAliasContaining(text, text, PageRequest.of(pageNum, 10_000));
        return PageUtil.prepareResult(page);
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Optional<Category> findById(int id) {
        return repository.findById(id);
    }

    public Optional<Category> findByNameAndAlias(String name, String alias) {
        return repository.findByNameAndAlias(name, alias);
    }

    public List<Category> findAllEnabled() {
        return repository.findAllByEnabled(true);
    }

    public List<Category> findAllDisabled() {
        return repository.findAllByEnabled(false);
    }

    public JpaResult delete(int id) {

        try {
            // Todo: check for children

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
                    .append(",");

            // TODO:

//            if (category.getParent() != null && category.getParent().getId() != 0) {
//                data.append(category.getParent().getName())
//                        .append(",");
//            } else {
//                data.append("-")
//                        .append(",");
//            }
            data.append(category.isEnabled())
                    .append(",")
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
