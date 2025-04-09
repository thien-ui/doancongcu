package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Truy vấn đơn hàng theo người dùng

    List<Order> findByUserId(Long userId);
    List<Order> findByUser(User user);
    // Lấy danh sách đơn hàng theo trạng thái
    List<Order> findByStatus(String status);
}