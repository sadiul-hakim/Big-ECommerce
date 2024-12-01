package org.shopme.admin.product;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.brand.BrandService;
import org.shopme.admin.category.CategoryService;
import org.shopme.common.entity.Brand;
import org.shopme.common.entity.Category;
import org.shopme.common.entity.Product;
import org.shopme.common.exception.NotFoundException;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final TableUrlPojo pageUrl = new TableUrlPojo("/products/search", "/products",
            "/products/export-csv", "/products/create-page");

    private static final String PAGINATION_RESULT = "productResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "products";
    private static final String POJO_NAME = "product";
    private static final String CREATE_PAGE = "create_product";
    private static final String UPDATING_CONDITION = "updatingProduct";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingProduct";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingProduct";
    private static final String MESSAGE = "message";

    private final CategoryService categoryService;
    private final ProductService service;
    private final BrandService brandService;

    @GetMapping
    public ModelAndView page(@RequestParam(defaultValue = "0") int page, ModelAndView model) {

        var productResult = service.findAllPaginated(page);
        model.addObject(PAGINATION_RESULT, productResult);

        model.addObject(TABLE_URL, pageUrl);
        model.addObject(SAVED_CONDITION, false);
        model.addObject(SAVING_CONDITION, false);
        model.addObject(DELETED_CONDITION, false);
        model.addObject(DELETING_CONDITION, false);
        model.addObject(UPDATING_CONDITION, false);
        model.addObject(MESSAGE, "");

        model.setViewName(PAGE);

        return model;
    }

    @PostMapping("/save")
    public ModelAndView save(
            @ModelAttribute Product product,
            @RequestParam boolean updating,
            ModelAndView model,
            @RequestParam MultipartFile firstImage,
            @RequestParam MultipartFile secondImage,
            @RequestParam MultipartFile thirdImage,
            @RequestParam MultipartFile fourthImage
    ) {

        var result = updating ? service.update(product) : service.save(product);
        service.handleFiles(result.entityId(), firstImage, secondImage, thirdImage, fourthImage);

        model.addObject(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(SAVING_CONDITION, true);
        model.addObject(MESSAGE, result.message());
        model.addObject(TABLE_URL, pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            model.addObject(POJO_NAME, product);
            model.setViewName(CREATE_PAGE);

            List<Category> categories = categoryService.findAll();
            model.addObject("categories", categories);

            List<Brand> brands = brandService.findAll();
            model.addObject("brands", brands);
        } else {
            var productResult = service.findAllPaginated(0);
            model.addObject(PAGINATION_RESULT, productResult);
            model.setViewName(PAGE);
        }
        return model;
    }

    @GetMapping("/update_page/{productId}")
    public ModelAndView updatePage(@PathVariable int productId, ModelAndView model) {

        var product = service.findById(productId);
        if (product.isEmpty()) {
            var brands = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGINATION_RESULT, brands);
            model.setViewName(PAGE);

            model.addObject(UPDATING_CONDITION, true);
            model.addObject(MESSAGE, "Product does not exists!");
            return model;
        }

        List<Category> categories = categoryService.findAll();
        model.addObject("categories", categories);

        List<Brand> brands = brandService.findAll();
        model.addObject("brands", brands);

        model.addObject(POJO_NAME, product.get());
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, true);
        return model;
    }

    @GetMapping("/product-details-page/{id}")
    public ModelAndView productDetailsPage(@PathVariable int id, ModelAndView model) {
        Optional<Product> product = service.findById(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Product is not found with id {}" + id);
        }

        model.addObject("product", product.get());
        model.setViewName("product_details");
        return model;
    }

    @GetMapping("/add-details")
    public ModelAndView addDetails(@RequestParam String key, @RequestParam String value,
                                   @RequestParam int productId, ModelAndView model) {
        JpaResult jpaResult = service.addDetails(key, value, productId);
        model.addObject("product", jpaResult.entity());
        model.addObject("detailsOperation", true);
        model.addObject(MESSAGE, "Detail " + key + " is added successfully.");
        model.setViewName("product_details");

        return model;
    }

    @GetMapping("/remove-details")
    public ModelAndView removeDetails(@RequestParam String key,
                                      @RequestParam int productId, ModelAndView model) {
        JpaResult jpaResult = service.removeDetails(key, productId);
        model.addObject("product", jpaResult.entity());
        model.addObject("detailsOperation", true);
        model.addObject(MESSAGE, "Detail " + key + " is removed successfully.");
        model.setViewName("product_details");

        return model;
    }

    @GetMapping("/create-page")
    public ModelAndView createPage(ModelAndView model) {

        model.addObject(POJO_NAME, new Product());
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, false);

        var categories = categoryService.findAll();
        model.addObject("categories", categories);

        List<Brand> brands = brandService.findAll();
        model.addObject("brands", brands);

        return model;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var brandResult = service.search(text, page);
        model.addObject(PAGINATION_RESULT, brandResult);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable int id, ModelAndView model) {

        var result = service.delete(id);
        model.addObject(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(DELETING_CONDITION, true);
        model.addObject(TABLE_URL, pageUrl);
        model.addObject(MESSAGE, result.message());

        var products = service.findAllPaginated(0);
        model.addObject(PAGINATION_RESULT, products);
        model.setViewName(PAGE);
        return model;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/view_page/{id}")
    public ModelAndView viewPage(@PathVariable int id, ModelAndView model) {
        Optional<Product> product = service.findById(id);
        if (product.isEmpty()) {
        }

        model.addObject("product", product.get());
        model.setViewName("view_product");

        return model;
    }
}
