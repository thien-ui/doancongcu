package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.BrandRepository;
import doanjava.webbannuochoa.Repository.ProductRepository;
import doanjava.webbannuochoa.models.Brand;
import doanjava.webbannuochoa.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * Retrieve all brands from the database.
     *
     * @return a list of brands
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * Retrieve a brand by its id.
     *
     * @param id the id of the brand to retrieve
     * @return an Optional containing the found brand or empty if not found
     */
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    /**
     * Add a new brand to the database.
     *
     * @param brand the brand to add
     */
    public void addBrand(Brand brand) {
        brandRepository.save(brand);
    }

    /**
     * Update an existing brand.
     *
     * @param brand the brand with updated information
     */
    public void updateBrand(Brand brand) {
        Brand existingBrand = brandRepository.findById(brand.getId())
                .orElseThrow(() -> new IllegalStateException("Brand with ID " +
                        brand.getId() + " does not exist."));
        existingBrand.setName(brand.getName());

        // Cập nhật hình ảnh mới (nếu có)
        if (brand.getImgUrl() != null && !brand.getImgUrl().isEmpty()) {
            existingBrand.setImgUrl(brand.getImgUrl());
        }
        brandRepository.save(existingBrand);
    }

    /**
     * Hide all products associated with the brand and mark the brand as deleted.
     *
     * @param id the id of the brand to delete
     */
    public void deleteBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Brand with ID " + id + " does not exist."));

        // Ẩn tất cả sản phẩm thuộc thương hiệu
        List<Product> products = productRepository.findByBrandId(id);
        for (Product product : products) {
            product.setHidden(true); // Đặt isHidden thành true
            productRepository.save(product);
        }

        // Đánh dấu thương hiệu là đã xóa
        brand.setDeleted(true); // Đặt isDeleted thành true
        brandRepository.save(brand);
    }

    /**
     * Restore products associated with a brand.
     *
     * @param brandId the id of the brand to restore products for
     */
    public void restoreProductsByBrandId(Long brandId) {
        List<Product> products = productRepository.findByBrandId(brandId);
        for (Product product : products) {
            product.setHidden(false); // Đặt isHidden thành false để khôi phục sản phẩm
            productRepository.save(product);
        }
    }

    /**
     * Restore a deleted brand and its associated products.
     *
     * @param id the id of the brand to restore
     */
    public void restoreBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand with ID " + id + " does not exist."));

        // Kiểm tra nếu thương hiệu đã bị xóa
        if (!brand.isDeleted()) {
            throw new IllegalStateException("Brand with ID " + id + " is not deleted.");
        }

        // Khôi phục thương hiệu
        brand.setDeleted(false); // Đặt isDeleted thành false
        brandRepository.save(brand);

        // Khôi phục các sản phẩm liên quan
        restoreProductsByBrandId(id);
    }
}
