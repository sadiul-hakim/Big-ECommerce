package org.shopme.site.brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Brand;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.PageUtil;
import org.shopme.site.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {
    private static final int PAGE_SIZE = 35;
    private final BrandRepository repository;
    private final CategoryService categoryService;

    public PaginationResult findAllPaginated(int pageNumber) {
        try {
            Page<Brand> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            PaginationResult result = PageUtil.prepareResult(page);

            var records = page.getContent();
            List<Object> data = new ArrayList<>();
            records.forEach(d -> {

                List<Category> categories = categoryService.findAllIn(d.getCategories());
                d.setCategoryList(categories);
                data.add(d);
            });

            result.setData(data);
            return result;
        } catch (Exception ex) {
            log.error("Brand.findAllPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public PaginationResult searchBrand(String text, int pageNumber) {
        try {
            Page<Brand> page = repository.findAllByNameContaining(text, PageRequest.of(pageNumber, PAGE_SIZE));
            PaginationResult result = PageUtil.prepareResult(page);

            var records = page.getContent();
            List<Object> data = new ArrayList<>();
            records.forEach(d -> {

                List<Category> categories = categoryService.findAllIn(d.getCategories());
                d.setCategoryList(categories);
                data.add(d);
            });

            result.setData(data);
            return result;
        } catch (Exception ex) {
            log.error("Brand.searchCategory :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public Optional<Brand> findById(int id) {
        Optional<Brand> brandOptional = repository.findById(id);
        if (brandOptional.isEmpty()) {
            return brandOptional;
        }

        Brand brand = brandOptional.get();
        List<Category> categories = categoryService.findAllIn(brand.getCategories());
        brand.setCategoryList(categories);

        return Optional.of(brand);
    }

    public Optional<Brand> findByName(String name) {
        Optional<Brand> brandOptional = repository.findByName(name);

        if (brandOptional.isEmpty()) {
            return brandOptional;
        }

        Brand brand = brandOptional.get();
        List<Category> categories = categoryService.findAllIn(brand.getCategories());
        brand.setCategoryList(categories);

        return Optional.of(brand);
    }
}
