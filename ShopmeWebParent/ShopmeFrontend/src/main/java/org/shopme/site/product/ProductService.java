package org.shopme.site.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Product;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PAGE_SIZE = 35;

    private final ProductRepository repository;

    public PaginationResult findAllPaginated(int pageNumber) {
        try {
            Page<Product> page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
            List<Product> products = page.getContent();
            for (Product product : products) {
                String[] files = product.getFiles();
                for (String file : files) {
                    if (StringUtils.hasText(file)) {
                        product.setImage(file);
                        break;
                    }
                }
            }

            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ProductService.findAllPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public PaginationResult search(String text, int pageNumber) {
        try {
            Page<Product> page = repository.findAllByNameContainingOrAliasContainingOrShortDescriptionContainingOrFullDescriptionContainingOrCategoryContainingOrBrandContaining(text, text, text, text,text,text, PageRequest.of(pageNumber, 100));

            List<Product> products = page.getContent();
            for (Product product : products) {
                String[] files = product.getFiles();
                for (String file : files) {
                    if (StringUtils.hasText(file)) {
                        product.setImage(file);
                        break;
                    }
                }
            }

            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ProductService.search :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public PaginationResult findAllByCategoryPaginated(String category, int pageNumber) {
        try {
            Page<Product> page = repository.findAllByCategory(category, PageRequest.of(pageNumber, PAGE_SIZE));
            List<Product> products = page.getContent();
            for (Product product : products) {
                String[] files = product.getFiles();
                for (String file : files) {
                    if (StringUtils.hasText(file)) {
                        product.setImage(file);
                        break;
                    }
                }
            }

            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ProductService.findAllByCategoryPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public PaginationResult findAllByBrandPaginated(String brand, int pageNumber) {
        try {
            Page<Product> page = repository.findAllByBrand(brand, PageRequest.of(pageNumber, PAGE_SIZE));
            List<Product> products = page.getContent();
            for (Product product : products) {
                String[] files = product.getFiles();
                for (String file : files) {
                    if (StringUtils.hasText(file)) {
                        product.setImage(file);
                        break;
                    }
                }
            }

            return PageUtil.prepareResult(page);
        } catch (Exception ex) {
            log.error("ProductService.findAllByBrandPaginated :: {}", ex.getMessage());
            return new PaginationResult();
        }
    }

    public Optional<Product> findById(int id) {

        Optional<Product> product = repository.findById(id);
        product.ifPresent(p -> {
            String[] files = p.getFiles();
            for (String file : files) {
                if (StringUtils.hasText(file)) {
                    p.setImage(file);
                    break;
                }
            }
        });

        return product;
    }

    public Optional<Product> findByName(String name) {
        return repository.findByName(name);
    }
}
