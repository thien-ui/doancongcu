package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.CategoryRepository;
import doanjava.webbannuochoa.Repository.ProductRepository;
import doanjava.webbannuochoa.models.Category;
import doanjava.webbannuochoa.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Retrieve all categories from the database.
     *
     * @return a list of categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieve a category by its id.
     *
     * @param id the id of the category to retrieve
     * @return an Optional containing the found category or empty if not found
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Add a new category to the database.
     *
     * @param category the category to add
     */
    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    /**
     * Update an existing category.
     *
     * @param category the category with updated information
     */
    public void updateCategory(Category category) {
        // Tìm danh mục hiện tại trong cơ sở dữ liệu
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category with ID " +
                        category.getId() + " does not exist."));

        // Cập nhật tên danh mục
        existingCategory.setName(category.getName());

        // Cập nhật hình ảnh mới (nếu có)
        if (category.getImgUrl() != null && !category.getImgUrl().isEmpty()) {
            existingCategory.setImgUrl(category.getImgUrl());
        }

        // Lưu cập nhật vào cơ sở dữ liệu
        categoryRepository.save(existingCategory);
    }


    /**
     * Hide all products associated with the category and mark the category as deleted.
     *
     * @param id the id of the category to delete
     */
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Category with ID " + id + " does not exist."));

        // Ẩn tất cả sản phẩm thuộc danh mục
        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setHidden(true); // Đặt isHidden thành true
            productRepository.save(product);
        }

        // Đánh dấu danh mục là đã xóa
        category.setDeleted(true); // Đặt isDeleted thành true
        categoryRepository.save(category);
    }

    /**
     * Restore products associated with a category.
     *
     * @param categoryId the id of the category to restore products for
     */
    public void restoreProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        for (Product product : products) {
            product.setHidden(false); // Đặt isHidden thành false để khôi phục sản phẩm
            productRepository.save(product);
        }
    }

    /**
     * Restore a deleted category and its associated products.
     *
     * @param id the id of the category to restore
     */
    public void restoreCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " does not exist."));

        // Kiểm tra nếu danh mục đã bị xóa
        if (!category.isDeleted()) {
            throw new IllegalStateException("Category with ID " + id + " is not deleted.");
        }

        // Khôi phục danh mục
        category.setDeleted(false); // Đặt isDeleted thành false
        categoryRepository.save(category);

        // Khôi phục các sản phẩm liên quan
        restoreProductsByCategoryId(id);
    }
}
