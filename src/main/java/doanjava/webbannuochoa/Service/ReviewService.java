package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.ReviewRepository;
import doanjava.webbannuochoa.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Lưu một đánh giá
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    // Lấy tất cả đánh giá của một sản phẩm
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // Lấy tất cả các đánh giá (dành cho ADMIN)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Lấy một đánh giá theo ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Xóa một đánh giá
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
