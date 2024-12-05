package org.shopme.site.category;

import lombok.RequiredArgsConstructor;
import org.shopme.common.pojo.PaginationResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ModelAndView viewVCategories(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        PaginationResult result = categoryService.findAllPaginated(page);
        model.addObject("result", result);
        model.addObject("searchResult", false);
        model.setViewName("categories");
        return model;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam String text, ModelAndView model) {
        PaginationResult result = categoryService.searchCategory(text, 0);
        model.addObject("result", result);
        model.addObject("searchResult", true);
        model.setViewName("categories");
        return model;
    }
}
