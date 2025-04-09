package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.CartService;
import doanjava.webbannuochoa.Service.OrderService;
import doanjava.webbannuochoa.models.CartItem;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class ThanhtoanController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        double total = cartService.calculateTotal();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "/cart/checkout";
    }



    @PostMapping("/submit")
    public String submitOrder(@RequestParam("customerName") String customerName,
                              @RequestParam("phone") String phone,
                              @RequestParam("address") String address,
                              @RequestParam("note") String note) {
        // Lấy danh sách sản phẩm trong giỏ hàng
        List<CartItem> cartItems = cartService.getCartItems();

        // Kiểm tra nếu giỏ hàng rỗng
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Quay về giỏ hàng nếu rỗng
        }

        // Tính tổng tiền đơn hàng
        int totalAmount = cartItems.stream()
                .mapToInt(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();

        // Tạo đối tượng Order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setStatus("PENDING"); // Đặt trạng thái đơn hàng là "PENDING"
        order.setAmount(totalAmount); // Gán tổng tiền đơn hàng

        // Lưu đơn hàng
        orderService.saveOrder(order);

        // Lưu chi tiết đơn hàng
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order); // Gắn đơn hàng cho chi tiết
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice()); // Lưu giá tại thời điểm mua
            orderService.saveOrderDetail(orderDetail); // Lưu chi tiết đơn hàng vào cơ sở dữ liệu
        }

        // Xóa giỏ hàng sau khi hoàn tất đặt hàng
        cartService.clearCart();

        // Chuyển hướng đến trang thanh toán hoặc trang xác nhận đơn hàng
        return "redirect:/create-payment-link";
    }




}
