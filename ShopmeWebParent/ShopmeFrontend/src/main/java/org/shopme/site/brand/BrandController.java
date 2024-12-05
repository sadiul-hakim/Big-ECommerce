package org.shopme.site.brand;

import lombok.RequiredArgsConstructor;
import org.shopme.common.pojo.PaginationResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ModelAndView viewVCategories(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        PaginationResult result = brandService.findAllPaginated(page);
        model.addObject("result", result);
        model.addObject("searchResult", false);
        model.setViewName("brands");
        return model;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam String text, ModelAndView model) {
        PaginationResult result = brandService.searchBrand(text, 0);
        model.addObject("result", result);
        model.addObject("searchResult", true);
        model.setViewName("brands");
        return model;
    }
}
