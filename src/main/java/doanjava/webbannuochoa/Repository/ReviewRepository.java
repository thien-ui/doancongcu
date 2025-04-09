package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    // Phương thức lấy tất cả các đánh giá (dành cho ADMIN)
    List<Review> findAll();

    // Lấy một đánh giá theo ID
    Optional<Review> findById(Long id);
}
