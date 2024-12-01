package org.shopme.admin.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.admin.util.FileUtil;
import org.shopme.common.entity.Product;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PAGE_SIZE = 10;
    private static final String FILE_PATH = "/image/product/";

    private final ProductRepository repository;

    public JpaResult save(Product product) {
        try {

            // Check uniqueness
            Optional<Product> existingProduct = repository.findByName(product.getName());
            if (existingProduct.isPresent()) {
                log.warn("Product with name {} already exists.", product.getName());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "product with name " + product.getName() + " already exists.");
            }

            Optional<Product> existingProductByAlias = repository.findByAlias(product.getAlias());
            if (existingProductByAlias.isPresent()) {
                log.warn("Product with alias {} already exists.", product.getAlias());
                return new JpaResult(JpaResultType.NOT_UNIQUE,
                        "Product with alias " + product.getAlias() + " already exists.");
            }

            product.setCreatedTime(LocalDateTime.now());
            product.setUpdatedTime(LocalDateTime.now());

            Product savedProduct = repository.save(product);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Product : " + savedProduct.getName(), savedProduct.getId());
        } catch (Exception ex) {
            log.error("ProductService.save :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not save Product : " + product.getName());
        }
    }

    public JpaResult update(Product product) {
        Optional<Product> optionalProduct = findById(product.getId());
        if (optionalProduct.isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "Could not find product : " + product.getName());
        }
        Product existingProduct = optionalProduct.get();
        product.setUpdatedTime(LocalDateTime.now());

        String[] files = new String[4];
        for (int i = 0; i < 4; i++) {
            String existingFile = existingProduct.getFiles()[i];
            String newFile = product.getFiles()[i];

            if (newFile != null) {
                files[i] = newFile;
            } else {
                files[i] = existingFile;
            }
        }

        product.setFiles(files);
        Product save = repository.save(product);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Product : " + product.getName(), save.getId());
    }

    public JpaResult addDetails(String key, String value, int productId) {
        try {

            Optional<Product> product = findById(productId);
            if (product.isEmpty())
                return new JpaResult(JpaResultType.NOT_FOUND, "Product with id " + productId + " is not found!");

            Product p = product.get();
            p.getDetails().put(key.trim(), value.trim());
            repository.save(p);

            return new JpaResult(JpaResultType.SUCCESSFUL, key + " is added in details of product {}" + productId, 0, p);
        } catch (Exception ex) {
            log.error("ProductService.addDetails() :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, ex.getMessage());
        }
    }

    public JpaResult removeDetails(String key, int productId) {
        try {

            Optional<Product> product = findById(productId);
            if (product.isEmpty())
                return new JpaResult(JpaResultType.NOT_FOUND, "Product with id " + productId + " is not found!");

            Product p = product.get();
            p.getDetails().remove(key.trim());
            repository.save(p);
            return new JpaResult(JpaResultType.SUCCESSFUL, key + " is removed from details of product {}" + productId, 0, p);
        } catch (Exception ex) {
            log.error("ProductService.removeDetails() :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, ex.getMessage());
        }
    }

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
            Page<Product> page = repository.findAllByNameContainingOrAliasContainingOrShortDescriptionContainingOrFullDescriptionContaining(text, text, text, text, PageRequest.of(pageNumber, 100));

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

    public Optional<Product> findByAlias(String alias) {
        return repository.findByAlias(alias);
    }

    public Optional<Product> findByNameAndAlias(String name, String alias) {
        return repository.findByNameAndAlias(name, alias);
    }

    public JpaResult delete(int id) {
        try {
            Optional<Product> product = findById(id);
            if (product.isEmpty())
                return new JpaResult(JpaResultType.SUCCESSFUL, "Product not found!");

            Product p = product.get();
            for (String file : p.getFiles()) {
                if (!StringUtils.hasText(file))
                    continue;

                FileUtil.deleteFile(FILE_PATH + id, file);
            }

            repository.delete(p);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted product.");
        } catch (Exception ex) {
            log.error("ProductService.delete :: {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Could not delete product : " + id);
        }
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public void handleFiles(int productId, MultipartFile... files) {

        try {
            Optional<Product> product = findById(productId);
            if (product.isEmpty())
                return;

            String fullPath = FILE_PATH + productId;
            File path = new File("classpath:static/" + fullPath);
            if (!path.exists()) {
                boolean dir = path.mkdirs();
                log.info("{} is created", dir);
            }

            Product p = product.get();
            String[] filesArr = new String[4];
            for (int i = 0; i < files.length; i++) {
                var file = files[i];
                if (file == null || !StringUtils.hasText(file.getOriginalFilename())) {
                    filesArr[i] = p.getFiles()[i];
                    continue;
                }

                var filePath = FileUtil.uploadFile(file, FILE_PATH);
                if (!StringUtils.hasText(filePath)) {
                    log.warn("UserService.update :: Could not upload file!");
                } else {
                    filesArr[i] = filePath;
                    FileUtil.deleteFile(fullPath, p.getFiles()[i]);
                }
            }

            p.setFiles(filesArr);
            update(p);
        } catch (Exception ex) {
            log.error("ProductService.handleFiles :: {}", ex.getMessage());
        }
    }

    public byte[] csvData() {

        var formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var products = findAll();
        StringBuilder data = new StringBuilder("Id,Name,Alias,product,Brand,shortDescription,fullDescription,enabled," +
                "inStock,cost,price,discountPrice,length,weight,width,height,createdTime,updatedTime\n");
        for (var product : products) {
            data.append(product.getId())
                    .append(",")
                    .append(product.getName())
                    .append(",")
                    .append(product.getAlias())
                    .append(",")
                    .append(product.getCategory())
                    .append(",")
                    .append(product.getBrand())
                    .append(",")
                    .append(product.getShortDescription())
                    .append(",")
                    .append(product.getFullDescription())
                    .append(",")
                    .append(product.isEnabled())
                    .append(",")
                    .append(product.isInStock())
                    .append(",")
                    .append(product.getCost())
                    .append(",")
                    .append(product.getPrice())
                    .append(",")
                    .append(product.getDiscountPrice())
                    .append(",")
                    .append(product.getLength())
                    .append(",")
                    .append(product.getWeight())
                    .append(",")
                    .append(product.getWidth())
                    .append(",")
                    .append(product.getHeight())
                    .append(",");
            if (product.getCreatedTime() != null) {
                data.append(product.getCreatedTime().format(formater))
                        .append(",");
            }

            if (product.getUpdatedTime() != null) {
                data.append(product.getUpdatedTime().format(formater));
            }

            data.append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
