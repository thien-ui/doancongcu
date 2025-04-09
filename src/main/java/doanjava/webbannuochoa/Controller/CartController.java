package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.CartService;
import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.models.CartItem;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @GetMapping
    public String showCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems(); // Lấy danh sách sản phẩm trong giỏ hàng của người dùng hiện tại
        int total = cartService.calculateTotal(); // Tính tổng tiền chỉ của người dùng hiện tại

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total); // Hiển thị tổng tiền chính xác

        return "/cart/cart"; // Trả về view giỏ hàng
    }



    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));
        cartService.addToCart(product, quantity);
        return "redirect:/cart"; // Chuyển hướng đến danh sách sản phẩm

    }



    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(cartItemId); // Xóa sản phẩm khỏi giỏ hàng
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm khỏi giỏ hàng!");
        }
        return "redirect:/cart";
    }



    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart(); // Gọi phương thức clearCart từ service
        return "redirect:/cart"; // Chuyển hướng về trang giỏ hàng
    }

}
