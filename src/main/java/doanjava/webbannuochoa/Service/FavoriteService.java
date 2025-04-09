package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.FavoriteRepository;
import doanjava.webbannuochoa.Repository.ProductRepository;
import doanjava.webbannuochoa.models.Favorite;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductService productService;

    // Lấy danh sách sản phẩm yêu thích của người dùng hiện tại
    public List<Product> getFavoriteProducts() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        return favoriteRepository.findByUserAndProductIsHiddenFalse(currentUser).stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
    }



    // Thêm sản phẩm vào danh sách yêu thích
    public void addToFavorites(Product product) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để thêm sản phẩm vào danh sách yêu thích!");
        }
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndProduct(currentUser, product);
        if (existingFavorite.isPresent()) {
            throw new IllegalArgumentException("Sản phẩm đã có trong danh sách yêu thích!");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(currentUser);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    @Transactional
    // Xóa sản phẩm khỏi danh sách yêu thích
    public void removeFromFavorites(Long productId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để xóa sản phẩm khỏi danh sách yêu thích!");
        }
        favoriteRepository.deleteByUserAndProductId(currentUser, productId);
    }

    // Lấy người dùng hiện tại từ Spring Security
    private User getCurrentUser() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        return null;
    }
}


