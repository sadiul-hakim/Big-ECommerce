package org.shopme.admin.product;

import lombok.RequiredArgsConstructor;
import org.shopme.common.pojo.TableUrlPojo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;
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
}
