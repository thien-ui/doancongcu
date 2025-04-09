package doanjava.webbannuochoa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Gắn với người dùng (nếu cần)
    @Column(name = "user_id", nullable = false)
    private Long userId; // Null nếu không đăng nhập

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private boolean isCompleted = false; // Đánh dấu giỏ hàng đã thanh toán hay chưa

    // Tính tổng giá trị giỏ hàng
    public int getTotalPrice() {
        return items.stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
