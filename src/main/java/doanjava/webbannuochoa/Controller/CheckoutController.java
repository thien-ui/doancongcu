package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.CartService;
import doanjava.webbannuochoa.Service.OrderService;
import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.models.*;
import doanjava.webbannuochoa.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {
    private final PayOS payOS;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService; // Thêm OrderService để tạo đơn hàng
    @Autowired
    private CartService cartService;   // Thêm CartService để xóa giỏ hàng
    public CheckoutController(PayOS payOS) {
        this.payOS = payOS;
    }

    @RequestMapping(value = "/")
    public String Index() {
        return "create-payment-link"; // Trang tạo link thanh toán
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String paymentSuccess(HttpServletRequest request, Model model) {
        // Lấy thông tin từ session
        String customerName = (String) request.getSession().getAttribute("customerName");
        String phone = (String) request.getSession().getAttribute("phone");
        String address = (String) request.getSession().getAttribute("address");
        String note = (String) request.getSession().getAttribute("note");

        // Kiểm tra nếu thông tin bị thiếu
        if (customerName == null || phone == null || address == null) {
            model.addAttribute("message", "Thông tin thanh toán bị thiếu.");
            return "error"; // Trang lỗi
        }

        // Tính tổng tiền từ giỏ hàng
        List<CartItem> cartItems = cartService.getCartItems();
        int totalAmount = cartItems.stream()
                .mapToInt(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();

        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            model.addAttribute("message", "Người dùng không hợp lệ. Vui lòng đăng nhập lại.");
            return "error"; // Trang lỗi
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();

        // Lưu đơn hàng
        Order order = new Order();
        order.setUser(currentUser); // Gán người dùng hiện tại vào đơn hàng
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note != null ? note : "");
        order.setStatus("Đã thanh toán");
        order.setAmount(totalAmount);
        order.setCreatedDate(new Date());
        orderService.saveOrder(order);

        // Lưu chi tiết đơn hàng
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice());
            orderService.saveOrderDetail(orderDetail);
        }

        // Xóa giỏ hàng
        cartService.clearCart();

        // Thêm đơn hàng vào model
        model.addAttribute("order", order);

        return "success"; // Trang thành công
    }





    @RequestMapping(value = "/cancel")
    public String Cancel() {
        return "cancel"; // Trang thất bại
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create-payment-link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void checkout(HttpServletRequest request, HttpServletResponse httpServletResponse,
                         @RequestParam("customerName") String customerName,
                         @RequestParam("phone") String phone,
                         @RequestParam("address") String address,
                         @RequestParam(value = "note", required = false) String note,
                         @RequestParam("productIds") List<Long> productIds,
                         @RequestParam("quantities") List<Integer> quantities) {
        try {
            final String baseUrl = getBaseUrl(request);
            final String description = "Thanh toán đơn hàng";
            final String returnUrl = baseUrl + "/success"; // Trả về success khi thanh toán thành công
            final String cancelUrl = baseUrl + "/cancel"; // Trả về cancel nếu thanh toán bị hủy

            // Tính tổng số tiền
            int totalAmount = 0;
            List<ItemData> items = new ArrayList<>();

            for (int i = 0; i < productIds.size(); i++) {
                Long productId = productIds.get(i);
                Integer quantity = quantities.get(i);
                Optional<Product> product = productService.getProductById(productId); // Lấy thông tin sản phẩm
                int price = product.get().getPrice();
                totalAmount += price * quantity;

                // Tạo item cho PayOS
                ItemData item = ItemData.builder()
                        .name(product.get().getName())
                        .price(price)
                        .quantity(quantity)
                        .build();
                items.add(item);
            }

            // Gen order code
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            // Gửi thông tin thanh toán tới PayOS
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(totalAmount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .items(items) // Gửi tất cả sản phẩm
                    .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            String checkoutUrl = data.getCheckoutUrl();

            // Lưu thông tin tạm thời để dùng trong success
            request.getSession().setAttribute("customerName", customerName);
            request.getSession().setAttribute("phone", phone);
            request.getSession().setAttribute("address", address);
            request.getSession().setAttribute("note", note);

            // Chuyển hướng tới PayOS để thanh toán
            httpServletResponse.sendRedirect(checkoutUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String url = scheme + "://" + serverName;
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url += ":" + serverPort;
        }
        url += contextPath;
        return url;
    }





}

