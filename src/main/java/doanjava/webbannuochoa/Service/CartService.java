package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.CartItemRepository;
import doanjava.webbannuochoa.Repository.CartRepository;
import doanjava.webbannuochoa.Repository.ProductRepository;
import doanjava.webbannuochoa.models.Cart;
import doanjava.webbannuochoa.models.CartItem;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SessionScope
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng của người dùng hiện tại.
     */
    public List<CartItem> getCartItems() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Optional<Cart> cartOptional = cartRepository.findByUserIdAndIsCompletedFalse(currentUser.getId());
            return cartOptional.map(cart -> cart.getItems().stream()
                            .filter(cartItem -> !cartItem.getProduct().isHidden()) // Lọc sản phẩm không bị ẩn
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
        }
        return new ArrayList<>();
    }


    /**
     * Thêm sản phẩm vào giỏ hàng.
     */
    public void addToCart(Product product, int quantity) {
        Cart cart = getOrCreateCart();
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng theo ID CartItem.
     */
    @Transactional

    public void removeFromCart(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new IllegalArgumentException("CartItem với ID " + cartItemId + " không tồn tại.");
        }
    }

    /**
     * Làm trống giỏ hàng của người dùng hiện tại.
     */
    public void clearCart() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Optional<Cart> cartOptional = cartRepository.findByUserIdAndIsCompletedFalse(currentUser.getId());
            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                cartItemRepository.deleteAll(cart.getItems());
                cart.getItems().clear();
                cartRepository.save(cart);
            }
        }
    }

    /**
     * Tính tổng giá trị giỏ hàng của người dùng hiện tại.
     */
    public int calculateTotal() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Optional<Cart> cartOptional = cartRepository.findByUserIdAndIsCompletedFalse(currentUser.getId());
            return cartOptional.map(cart ->
                    cart.getItems().stream()
                            .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                            .sum()
            ).orElse(0);
        }
        return 0;
    }

    /**
     * Lấy người dùng hiện tại từ Spring Security.
     */
    private User getCurrentUser() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        return null;
    }

    /**
     * Lấy hoặc tạo mới giỏ hàng.
     */
    private Cart getOrCreateCart() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return cartRepository.findByUserIdAndIsCompletedFalse(currentUser.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUserId(currentUser.getId());
                        return cartRepository.save(newCart);
                    });
        } else {
            // Trường hợp không có người dùng, tạo giỏ hàng ẩn danh
            return cartRepository.findFirstByOrderByIdDesc().orElseGet(() -> cartRepository.save(new Cart()));
        }
    }
}
