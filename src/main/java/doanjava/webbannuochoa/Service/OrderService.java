package doanjava.webbannuochoa.Service;

import doanjava.webbannuochoa.Repository.OrderDetailRepository;
import doanjava.webbannuochoa.Repository.OrderRepository;
import doanjava.webbannuochoa.models.Order;
import doanjava.webbannuochoa.models.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Lưu đơn hàng
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    // Lưu chi tiết đơn hàng
    public void saveOrderDetail(OrderDetail orderDetail) {
        orderDetailRepository.save(orderDetail);
    }

    // Lấy danh sách chi tiết đơn hàng theo orderId
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    // Lấy danh sách đơn hàng theo userId
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Lấy tất cả đơn hàng (cho admin)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy đơn hàng theo trạng thái (cho admin)
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    // Lấy đơn hàng theo ID
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // Cập nhật trạng thái đơn hàng
    public Order updateOrderStatus(Long orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with ID: " + orderId);
    }

    // Xóa đơn hàng theo ID//xóa đơn hàng riêng lẻ
    public void deleteOrderById(Long orderId) {
        // Xóa chi tiết đơn hàng trước (nếu có)
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        if (orderDetails != null && !orderDetails.isEmpty()) {
            orderDetailRepository.deleteAll(orderDetails);
        }
        // Sau đó xóa đơn hàng
        orderRepository.deleteById(orderId);
    }
}
