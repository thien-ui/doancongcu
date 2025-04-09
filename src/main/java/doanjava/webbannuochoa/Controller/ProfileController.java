package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Repository.OrderRepository;
import doanjava.webbannuochoa.Service.OrderService;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.OrderDetail;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    // Hiển thị trang hồ sơ người dùng
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = customUserDetails.getUser();

            // Lấy danh sách đơn hàng của người dùng
            List<Order> orders = orderRepository.findByUser(currentUser);

            // Đưa thông tin người dùng và đơn hàng vào model
            model.addAttribute("user", currentUser);
            model.addAttribute("orders", orders);
        } else {
            return "redirect:/login";
        }

        return "users/profile";
    }

    // Lấy thông tin chi tiết của một đơn hàng và hiển thị chi tiết
    @GetMapping("/profile/{id}/details")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(id);
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "users/order-details"; // Tên file HTML: order-details.html
    }

}
