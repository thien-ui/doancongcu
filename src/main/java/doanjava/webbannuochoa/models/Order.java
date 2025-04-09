package doanjava.webbannuochoa.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng tạo đơn hàng




    @Column(name = "customer_name", nullable = false)
    private String customerName; // Tên khách hàng

    @Column(name = "phone", nullable = false)
    private String phone; // Số điện thoại khách hàng

    @Column(name = "address", nullable = false)
    private String address; // Địa chỉ giao hàng

    @Column(name = "note")
    private String note; // Ghi chú đơn hàng

    @Column(name = "status", nullable = false)
    private String status; // Trạng thái đơn hàng (e.g., "PAID", "PENDING")

    @Column(name = "amount", nullable = false)
    private int amount; // Tổng số tiền của đơn hàng

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate; // Ngày tạo đơn hàng

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails; // Danh sách chi tiết đơn hàng
}
