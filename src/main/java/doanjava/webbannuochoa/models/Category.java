package doanjava.webbannuochoa.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên là bắt buộc")
    private String name;

    // Giữ nguyên kiểu boolean cho isDeleted
    private boolean isDeleted = false;

    // Các phương thức getter và setter cho isDeleted
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted; // Sử dụng this để rõ ràng hơn
    }

    @Column(name = "image_url")
    private String imgUrl; // Đường dẫn lưu hình ảnh


}
