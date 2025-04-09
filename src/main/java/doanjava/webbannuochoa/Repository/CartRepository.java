package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findFirstByOrderByIdDesc();
    // Tìm giỏ hàng mới nhất (nếu cần logic này)
    Optional<Cart> findByUserIdAndIsCompletedFalse(Long userId);
}
