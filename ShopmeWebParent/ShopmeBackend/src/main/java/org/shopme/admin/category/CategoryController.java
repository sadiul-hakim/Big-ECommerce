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

    @GetMapping
    public ModelAndView usersPage(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var categoryResult = service.findAllPaginated(page);
        model.addObject("categoryResult", categoryResult);

        model.addObject("tableUrl", pageUrl);
        model.addObject("savedSuccessfully", false);
        model.addObject("savingCategory", false);
        model.addObject("deletedSuccessfully", false);
        model.addObject("deletingCategory", false);
        model.addObject("updatingCategory", false);
        model.addObject("message", "");

        model.setViewName("categories");

        return model;
    }

    @PostMapping("/save")
    public ModelAndView save(
            @ModelAttribute Category category,
            @RequestParam boolean updating,
            ModelAndView model
    ) {

        var result = updating ? service.update(category) : service.save(category);
        model.addObject("savedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("savingCategory", true);
        model.addObject("message", result.message());

        model.addObject("tableUrl", pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            var categories = service.findAll();
            model.addObject("category", category);
            model.addObject("categories", categories);
            model.setViewName("create_category");
        } else {
            var categoryResult = service.findAllPaginated(0);
            model.addObject("categoryResult", categoryResult);
            model.setViewName("categories");
        }
        return model;
    }

    @GetMapping("/update_page/{categoryId}")
    public ModelAndView updatePage(@PathVariable int categoryId, ModelAndView model) {

        var category = service.findById(categoryId);
        if (category.isEmpty()) {
            var categories = service.findAllPaginated(0);
            model.addObject("tableUrl", pageUrl);
            model.addObject("categories", categories);
            model.setViewName("categories");

            model.addObject("updatingCategory", true);
            model.addObject("message", "Category does not exists!");
            return model;
        }

        var categories = service.findAll();
        model.addObject("category", category.get());
        model.addObject("categories", categories);
        model.setViewName("create_category");
        model.addObject("updatingCategory", true);

        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable int id, ModelAndView model) {
        var result = service.delete(id);
        model.addObject("deletedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("deletingCategory", true);
        model.addObject("tableUrl", pageUrl);
        model.addObject("message", result.message());

        var categories = service.findAllPaginated(0);
        model.addObject("categoryResult", categories);
        model.setViewName("categories");
        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var categoryResult = service.searchCategory(text, page);
        model.addObject("categoryResult", categoryResult);

        model.addObject("tableUrl", pageUrl);
        model.setViewName("categories");

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

        modelAndView.addObject("category", new Category());
        modelAndView.addObject("savedSuccessfully", false);
        modelAndView.addObject("savingCategory", false);
        modelAndView.addObject("updatingCategory", false);
        modelAndView.addObject("message", "");
        modelAndView.addObject("categories", categories);

        modelAndView.setViewName("create_category");

        return modelAndView;
    }

    @GetMapping("/tree-view/{categoryId}")
    @ResponseBody
    public List<String> treeView(@PathVariable int categoryId) {
        return service.childrenText(categoryId);
    }
}
