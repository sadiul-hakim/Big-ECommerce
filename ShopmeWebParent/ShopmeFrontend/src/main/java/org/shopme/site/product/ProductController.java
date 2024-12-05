package org.shopme.site.product;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Brand;
import org.shopme.common.entity.Category;
import org.shopme.common.entity.Product;
import org.shopme.common.exception.NotFoundException;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.site.brand.BrandService;
import org.shopme.site.category.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;
    private final CategoryService categoryService;
    private final BrandService brandService;

    @GetMapping("/categories/{categoryName}")
    public ModelAndView viewByCategory(@PathVariable String categoryName,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam String pageEntity,
                                       ModelAndView model) {
        if (!StringUtils.hasText(categoryName)) {
            throw new RuntimeException("Invalid category!");
        }

        Optional<Category> category = categoryService.findByName(categoryName);
        if (category.isEmpty()) {
            throw new RuntimeException("Category does not exist with is name " + categoryName);
        }
        PaginationResult result = service.findAllByCategoryPaginated(categoryName, page);

        model.addObject("pageEntity", pageEntity);
        model.addObject("entity", category.get());
        model.addObject("result", result);
        model.setViewName("product_by_entity");

        return model;
    }

    @GetMapping("/brands/{brandName}")
    public ModelAndView viewByBrand(@PathVariable String brandName,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam String pageEntity,
                                    ModelAndView model) {
        if (!StringUtils.hasText(brandName)) {
            throw new RuntimeException("Invalid brand!");
        }

        Optional<Brand> brand = brandService.findByName(brandName);
        if (brand.isEmpty()) {
            throw new RuntimeException("Brand does not exist with is name " + brandName);
        }
        PaginationResult result = service.findAllByBrandPaginated(brandName, page);

        model.addObject("pageEntity", pageEntity);
        model.addObject("entity", brand.get());
        model.addObject("result", result);
        model.setViewName("product_by_entity");

        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchProduct(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String entity,
            @RequestParam String pageEntity,
            ModelAndView model
    ) {
        PaginationResult result = service.search(text, page);

        Optional<Category> category = categoryService.findByName(entity);
        category.ifPresent(value -> model.addObject("entity", value));

        Optional<Brand> brand = brandService.findByName(entity);
        brand.ifPresent(value -> model.addObject("entity", value));

        model.addObject("pageEntity", pageEntity);
        model.addObject("result", result);
        model.setViewName("product_by_entity");

        return model;
    }

    @GetMapping("/view_page/{id}")
    public ModelAndView viewPage(@PathVariable int id, ModelAndView model) {
        Optional<Product> product = service.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Product is not found with id " + id);
        }

        model.addObject("product", product.get());
        model.setViewName("view_product");

        return model;
    }
}
