package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.ReviewService;
import doanjava.webbannuochoa.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    @Autowired
    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Hiển thị danh sách tất cả các đánh giá (ADMIN)
    @GetMapping("/admin/reviews")
    public String viewAllReviews(Model model) {
        // Lấy tất cả các đánh giá
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "/admin/reviews"; // Tạo trang HTML tương ứng
    }

    // Xóa đánh giá
    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        // Xóa đánh giá theo ID
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews"; // Sau khi xóa, quay lại trang quản lý đánh giá
    }

    // Phản hồi đánh giá (ví dụ, ADMIN có thể thêm phản hồi)
    @PostMapping("/admin/reviews/respond/{id}")
    public String respondToReview(@PathVariable Long id, String response) {
        // Lấy đánh giá theo ID
        Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + id));

        // Thêm phản hồi vào đánh giá
        review.setResponse(response);  // Giả sử Review có trường response để lưu phản hồi
        reviewService.saveReview(review); // Lưu lại phản hồi

        return "redirect:/admin/reviews"; // Quay lại trang quản lý đánh giá
    }
}
