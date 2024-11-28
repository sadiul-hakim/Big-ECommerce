package org.shopme.admin.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Product;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PAGE_SIZE = 10;
    private final ProductRepository repository;

    public PaginationResult findAllPaginated(int pageNumber){
        try{
            Page<Product> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            return PageUtil.prepareResult(page);
        }catch (Exception ex){
            log.error("ProductService.findAllPaginated :: {}",ex.getMessage());
            return new PaginationResult();
        }
    }
}
