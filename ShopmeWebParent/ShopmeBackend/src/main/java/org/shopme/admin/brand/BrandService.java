package org.shopme.admin.brand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.admin.category.CategoryService;
import org.shopme.admin.util.FileUtil;
import org.shopme.common.entity.Brand;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {
    private static final int PAGE_SIZE = 10;
    private static final String FILE_PATH = "/image/brand/";
    private static final String DEFAULT_PHOTO_NAME = "brand_logo.png";

    private final BrandRepository repository;
    private final CategoryService categoryService;

    public JpaResult save(Brand brand, MultipartFile file) {

        try {

            // Handle photo upload
            if (file == null || Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                brand.setLogo(DEFAULT_PHOTO_NAME);
            } else {
                var filePath = FileUtil.uploadFile(file, FILE_PATH);
                if (!StringUtils.hasText(filePath)) {
                    log.warn("BrandService.save :: Could not upload file!");
                    brand.setLogo(DEFAULT_PHOTO_NAME);
                } else {
                    brand.setLogo(filePath);
                }
            }

            Optional<Brand> existingBrand = findByName(brand.getName());
            if (existingBrand.isPresent()) {
                return new JpaResult(JpaResultType.NOT_UNIQUE, "Brand with name " + brand.getName() + " already exists!");
            }

            Brand savedBrand = repository.save(brand);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved brand " + savedBrand.getName());
        } catch (Exception ex) {
            log.error("BrandService.save :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update brand: " + brand.getName() + ". Please try again!");
        }
    }

    public JpaResult updateBrand(Brand brand, MultipartFile file) {

        try {
            Optional<Brand> existingBrandOptional = findByName(brand.getName());
            if (existingBrandOptional.isEmpty()) {
                return new JpaResult(JpaResultType.FAILED, "Brand " + brand.getName() + " does not exist!");
            }

            Brand existingBrand = existingBrandOptional.get();
            if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
                var filePath = FileUtil.uploadFile(file, FILE_PATH);
                if (!StringUtils.hasText(filePath)) {
                    log.warn("BrandService.update :: Could not upload file!");
                } else {
                    FileUtil.deleteFile(FILE_PATH, existingBrand.getLogo());
                    existingBrand.setLogo(filePath);
                }
            }

            existingBrand.setName(brand.getName());
            existingBrand.setCategories(brand.getCategories());
            Brand save = repository.save(existingBrand);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + save.getName());
        } catch (Exception ex) {
            log.error("BrandService.updateBrand :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update brand: " + brand.getName() + ". Please try again!");
        }
    }

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

    public PaginationResult searchCategory(String text, int pageNumber) {
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

    public JpaResult delete(int id) {
        try {
            repository.deleteById(id);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted brand.");
        } catch (Exception ex) {
            log.error("BrandService.delete :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not delete brand : " + id);
        }
    }

    public List<Brand> findAll() {
        List<Brand> brands = repository.findAll();
        for (Brand brand : brands) {
            List<Category> categories = categoryService.findAllIn(brand.getCategories());
            brand.setCategoryList(categories);
        }

        return brands;
    }

    public byte[] csvData() {
        var brands = findAll();
        StringBuilder data = new StringBuilder("Id,Name,Logo,Categories\n");
        for (var category : brands) {
            data.append(category.getId())
                    .append(",")
                    .append(category.getName())
                    .append(",")
                    .append(category.getLogo())
                    .append(",");

            if (category.getCategoryList().isEmpty()) {
                data.append("-")
                        .append(",");
            } else {
                StringJoiner joiner = new StringJoiner(";", "[", "]");
                for (Category c : category.getCategoryList()) {
                    joiner.add(c.getName());
                }
                data.append(joiner)
                        .append(",");
            }
            data.append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
