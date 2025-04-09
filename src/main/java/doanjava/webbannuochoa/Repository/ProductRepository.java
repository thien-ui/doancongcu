package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Lấy sản phẩm không bị ẩn và phân trang
    Page<Product> findByIsHiddenFalse(Pageable pageable);

    // Lấy sản phẩm theo categoryId không bị ẩn và phân trang
    Page<Product> findByCategoryIdAndIsHiddenFalse(Long categoryId, Pageable pageable);

    // Lấy sản phẩm theo brandId không bị ẩn và phân trang
    Page<Product> findByBrandIdAndIsHiddenFalse(Long brandId, Pageable pageable);

    // Tìm kiếm sản phẩm theo tên không bị ẩn và phân trang
    Page<Product> findByNameContainingIgnoreCaseAndIsHiddenFalse(String keyword, Pageable pageable);

    List<Product> findByCategoryId(Long categoryId); // Tìm sản phẩm theo danh mục

    List<Product> findByBrandId(Long brandId); // Tìm sản phẩm theo thương hiệu

    // Lấy các sản phẩm cùng thương hiệu, ngoại trừ sản phẩm hiện tại
    List<Product> findByBrandIdAndIdNot(Long brandId, Long productId);

    @Query("SELECT p FROM Product p WHERE " +
            "(:price IS NULL OR " +
            " (:comparisonType = 'greater' AND p.price > :price) OR " +
            " (:comparisonType = 'equal' AND p.price = :price) OR " +
            " (:comparisonType = 'less' AND p.price < :price)) AND " +
            "(:brandNames IS NULL OR p.brand.name IN :brandNames) AND " +
            "(:categoryNames IS NULL OR p.category.name IN :categoryNames) AND " +
            "p.isHidden = false")
    List<Product> findByCriteria(@Param("price") Double price,
                                 @Param("comparisonType") String comparisonType,
                                 @Param("brandNames") List<String> brandNames,
                                 @Param("categoryNames") List<String> categoryNames);


}


