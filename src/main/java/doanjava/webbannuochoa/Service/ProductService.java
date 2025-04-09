package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.OrderRepository;
import doanjava.webbannuochoa.Repository.ProductRepository;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.Product;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Sử dụng Lombok để tạo constructor injection
@Transactional
public class ProductService {


    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Page<Product> getVisibleProducts(Pageable pageable) {
        return productRepository.findByIsHiddenFalse(pageable);
    }

    public Page<Product> getProductsByCategoryIdPaged(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsHiddenFalse(categoryId, pageable);
    }

    public Page<Product> getProductsByBrandIdPaged(Long brandId, Pageable pageable) {
        return productRepository.findByBrandIdAndIsHiddenFalse(brandId, pageable);
    }

    public Page<Product> searchProductsByName(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsHiddenFalse(keyword, pageable);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProductsPaged(Pageable pageable) {
        return productRepository.findByIsHiddenFalse(pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));

        // Cập nhật các thuộc tính khác của sản phẩm
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());

        existingProduct.setCategory(product.getCategory());
        existingProduct.setBrand(product.getBrand());

        // Cập nhật hình ảnh mới (nếu có)
        if (product.getImgUrl() != null && !product.getImgUrl().isEmpty()) {
            existingProduct.setImgUrl(product.getImgUrl());
        }

        return productRepository.save(existingProduct);
    }



    public void deleteProductById(Long productId) {
        // Xóa sản phẩm và các OrderDetail liên quan
        productRepository.deleteById(productId);

    }

    // Phương thức để lấy sản phẩm liên quan đến thương hiệu
    public List<Product> getRelatedProductsByBrand(Long brandId, Long productId) {
        // Tìm các sản phẩm cùng thương hiệu, ngoại trừ sản phẩm hiện tại
        return productRepository.findByBrandIdAndIdNot(brandId, productId);
    }
    // ProductService
    public List<Product> filterProductsByCriteria(Double price, String comparisonType, List<String> brandNames, List<String> categoryNames) {
        return productRepository.findByCriteria(
                price,
                comparisonType != null ? comparisonType : "equal", // Nếu không có giá trị, mặc định là "equal"
                (brandNames != null && !brandNames.isEmpty()) ? brandNames : null,
                (categoryNames != null && !categoryNames.isEmpty()) ? categoryNames : null
        );
    }





}