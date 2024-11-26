package org.shopme.admin.category;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/categories/search", "/categories",
            "/categories/export-csv", "/categories/create-page");

    private static final String PAGINATION_RESULT = "categoryResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "categories";
    private static final String POJO_NAME = "category";
    private static final String CREATE_PAGE = "create_category";
    private static final String UPDATING_CONDITION = "updatingCategory";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingCategory";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingCategory";
    private static final String MESSAGE = "message";

    @GetMapping
    public ModelAndView page(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var categoryResult = service.findAllPaginated(page);
        model.addObject(PAGINATION_RESULT, categoryResult);

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
            @ModelAttribute Category category,
            @RequestParam boolean updating,
            ModelAndView model
    ) {

        var result = updating ? service.update(category) : service.save(category);
        model.addObject(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(SAVING_CONDITION, true);
        model.addObject(MESSAGE, result.message());

        model.addObject(TABLE_URL, pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            var categories = service.findAll();
            model.addObject(POJO_NAME, category);
            model.addObject(PAGE, categories);
            model.setViewName(CREATE_PAGE);
        } else {
            var categoryResult = service.findAllPaginated(0);
            model.addObject(PAGINATION_RESULT, categoryResult);
            model.setViewName(PAGE);
        }
        return model;
    }

    @GetMapping("/update_page/{categoryId}")
    public ModelAndView updatePage(@PathVariable int categoryId, ModelAndView model) {

        var category = service.findById(categoryId);
        if (category.isEmpty()) {
            var categories = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGE, categories);
            model.setViewName(PAGE);

            model.addObject(UPDATING_CONDITION, true);
            model.addObject(MESSAGE, "Category does not exists!");
            return model;
        }

        var categories = service.findAll();
        model.addObject(POJO_NAME, category.get());
        model.addObject(PAGE, categories);
        model.setViewName(CREATE_PAGE);
        model.addObject(UPDATING_CONDITION, true);

        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteCategory(@PathVariable int id, ModelAndView model) {
        var result = service.delete(id);
        model.addObject(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject(DELETING_CONDITION, true);
        model.addObject(TABLE_URL, pageUrl);
        model.addObject(MESSAGE, result.message());

        var categories = service.findAllPaginated(0);
        model.addObject(PAGINATION_RESULT, categories);
        model.setViewName(PAGE);
        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchCategories(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var categoryResult = service.searchCategory(text, page);
        model.addObject(PAGINATION_RESULT, categoryResult);

        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/create-page")
    public ModelAndView createPage(ModelAndView modelAndView) {
        List<Category> categories = service.findAll();

        modelAndView.addObject(POJO_NAME, new Category());
        modelAndView.addObject(SAVED_CONDITION, false);
        modelAndView.addObject(SAVING_CONDITION, false);
        modelAndView.addObject(UPDATING_CONDITION, false);
        modelAndView.addObject(MESSAGE, "");
        modelAndView.addObject(PAGE, categories);

        modelAndView.setViewName(CREATE_PAGE);

        return modelAndView;
    }

    @GetMapping("/tree-view/{categoryId}")
    @ResponseBody
    public List<String> treeView(@PathVariable int categoryId) {
        return service.childrenText(categoryId);
    }
}
