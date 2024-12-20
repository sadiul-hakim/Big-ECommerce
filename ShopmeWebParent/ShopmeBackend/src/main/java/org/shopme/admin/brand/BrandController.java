package org.shopme.admin.brand;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.category.CategoryService;
import org.shopme.common.entity.Brand;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService service;
    private final CategoryService categoryService;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/brands/search", "/brands",
            "/brands/export-csv", "/brands/create-page");

    private static final String PAGINATION_RESULT = "brandResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "brands";
    private static final String POJO_NAME = "brand";
    private static final String CREATE_PAGE = "create_brand";
    private static final String UPDATING_CONDITION = "updatingBrand";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingBrand";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingBrand";
    private static final String MESSAGE = "message";

    @GetMapping
    public ModelAndView brandsPage(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var brandResult = service.findAllPaginated(page);
        model.addObject(PAGINATION_RESULT, brandResult);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var brandResult = service.searchCategory(text, page);
        model.addObject(PAGINATION_RESULT, brandResult);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/create-page")
    public ModelAndView createPage(ModelAndView model) {

        var categories = categoryService.findAll();
        model.addObject(POJO_NAME, new Brand());
        model.addObject("categories", categories);
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, false);

        return model;
    }

    @PostMapping("/save")
    public ModelAndView save(
            @ModelAttribute Brand brand,
            @RequestParam boolean updating,
            @RequestParam MultipartFile file,
            ModelAndView model
    ) {

        var result = updating ? service.updateBrand(brand, file) : service.save(brand, file);
        model.addObject(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(SAVING_CONDITION, true);
        model.addObject(MESSAGE, result.message());
        model.addObject(TABLE_URL, pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            model.addObject(POJO_NAME, brand);
            model.setViewName(CREATE_PAGE);
            List<Category> categories = categoryService.findAll();
            model.addObject("categories", categories);
        } else {
            var brandResult = service.findAllPaginated(0);
            model.addObject(PAGINATION_RESULT, brandResult);
            model.setViewName(PAGE);
        }
        return model;
    }

    @GetMapping("/update_page/{brandId}")
    public ModelAndView updatePage(@PathVariable int brandId, ModelAndView model) {

        var brand = service.findById(brandId);
        List<Category> categories = categoryService.findAll();
        if (brand.isEmpty()) {
            var brands = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGINATION_RESULT, brands);
            model.setViewName(PAGE);

            model.addObject(UPDATING_CONDITION, true);
            model.addObject(MESSAGE, "Brand does not exists!");
            return model;
        }

        model.addObject("categories", categories);
        model.addObject(POJO_NAME, brand.get());
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, true);
        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteBrand(@PathVariable int id, ModelAndView model) {

        var result = service.delete(id);
        model.addObject(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(DELETING_CONDITION, true);
        model.addObject(TABLE_URL, pageUrl);
        model.addObject(MESSAGE, result.message());

        var brands = service.findAllPaginated(0);
        model.addObject(PAGINATION_RESULT, brands);
        model.setViewName(PAGE);
        return model;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=brands.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
