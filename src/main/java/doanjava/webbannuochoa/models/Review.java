package doanjava.webbannuochoa.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String content;

    @Min(1)
    @Max(5)
    private int rating; // Đánh giá từ 1 đến 5

    private LocalDateTime createdAt;

    public Review(Product product, User user, String content, int rating) {
        this.product = product;
        this.user = user;
        this.content = content;
        this.rating = rating;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter và Setter


    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt != null ? createdAt.format(formatter) : "";
    }
    // Thêm trường phản hồi từ ADMIN
    private String response;  // Phản hồi của ADMIN (mới thêm)
}
