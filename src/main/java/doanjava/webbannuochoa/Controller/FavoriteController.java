package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.FavoriteService;
import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.Service.UserService;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ProductService productService;

    // Hiển thị danh sách sản phẩm yêu thích
    @GetMapping
    public String getFavoriteList(Model model) {
        List<Product> favoriteProducts = favoriteService.getFavoriteProducts();
        model.addAttribute("favoriteProducts", favoriteProducts);
        return "/favorites/favorite-list"; // Trả về view hiển thị danh sách yêu thích
    }

    // Thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add")
    public String addFavorite(@RequestParam Long productId, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));
            favoriteService.addToFavorites(product);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm vào danh sách yêu thích!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/favorites"; // Chuyển hướng đến danh sách sản phẩm
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    @PostMapping("/remove")
    public String removeFavorite(@RequestParam Long productId, RedirectAttributes redirectAttributes) {
        favoriteService.removeFromFavorites(productId);
        redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi danh sách yêu thích!");
        return "redirect:/favorites"; // Chuyển hướng lại danh sách yêu thích
    }



}
