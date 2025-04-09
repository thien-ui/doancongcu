package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.OrderService;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrderAdminController {

    @Autowired
    private OrderService orderService;

    // Lấy danh sách tất cả đơn hàng và hiển thị lên giao diện
    @GetMapping
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders"; // Tên file HTML: orders.html
    }

    // Lấy thông tin chi tiết của một đơn hàng và hiển thị chi tiết
    @GetMapping("/{id}/details")
    public String getOrderDetails(@PathVariable Long id, Model model) {
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(id);
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "admin/order-details"; // Tên file HTML: order-details.html
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") String status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/admin/orders"; // Quay lại danh sách đơn hàng sau khi cập nhật
    }

    // Xóa đơn hàng
    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/orders"; // Quay lại danh sách đơn hàng sau khi xóa
    }
}
