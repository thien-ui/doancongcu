package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.CartService;
import doanjava.webbannuochoa.Service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
@ControllerAdvice

public class GlobalControllerAdvice {
    @Autowired
    private CartService cartService;

    @Autowired
    private FavoriteService favoriteService;


    // Số lượng sản phẩm yêu thích
    @ModelAttribute("favoriteCount")
    public int getFavoriteCount() {
        return favoriteService.getFavoriteProducts().size(); // Lấy số lượng sản phẩm yêu thích
    }
}

