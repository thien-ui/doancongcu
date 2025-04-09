package doanjava.webbannuochoa.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Liên kết với đơn hàng

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Liên kết với sản phẩm

    @Column(name = "quantity", nullable = false)
    private int quantity; // Số lượng sản phẩm

    @Column(name = "price", nullable = false)
    private int price; // Giá tại thời điểm mua
}