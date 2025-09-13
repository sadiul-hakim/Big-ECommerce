package org.shopme.admin.category;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Category;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingCategory";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingCategory";
    private static final String MESSAGE = "message";

    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {
        var categoryResult = service.findAllPaginated(page);
        model.addAttribute(PAGINATION_RESULT, categoryResult);
        model.addAttribute(TABLE_URL, pageUrl);
        return PAGE;
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Category category,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        var result = category.getId() != 0 ? service.update(category) : service.save(category);
        redirectAttributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
        redirectAttributes.addFlashAttribute(MESSAGE, result.message());

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            model.addAttribute(SAVING_CONDITION, true);
            model.addAttribute(MESSAGE, result.message());
            var categories = service.findAll();
            model.addAttribute(POJO_NAME, category);
            model.addAttribute(PAGE, categories);
            return CREATE_PAGE;
        } else {
            return "redirect:/" + PAGE;
        }
    }

    @GetMapping("/update_page/{categoryId}")
    public ModelAndView updatePage(@PathVariable int categoryId, ModelAndView model) {

        var category = service.findById(categoryId);
        if (category.isEmpty()) {
            var categories = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGE, categories);
            model.setViewName(PAGE);

            model.addObject(MESSAGE, "Category does not exists!");
            return model;
        }

        var categories = service.findAll();
        model.addObject(POJO_NAME, category.get());
        model.addObject(PAGE, categories);
        model.setViewName(CREATE_PAGE);

        return model;
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes model) {
        var result = service.delete(id);
        model.addFlashAttribute(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addFlashAttribute(DELETING_CONDITION, true);
        model.addFlashAttribute(MESSAGE, result.message());
        return "redirect:/" + PAGE;
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
