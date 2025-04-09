package doanjava.webbannuochoa.type;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestBody {
  @NotBlank(message = "Tên sản phẩm không được để trống")
  private String productName;

  @NotBlank(message = "Mô tả không được để trống")
  private String description;

  @NotBlank(message = "URL thành công không được để trống")
  private String returnUrl;

  @NotBlank(message = "URL hủy không được để trống")
  private String cancelUrl;

  @NotNull(message = "Giá không được để trống")
  private Integer price;

  @NotBlank(message = "Tên khách hàng không được để trống")
  private String customerName;

  @NotBlank(message = "Số điện thoại không được để trống")
  private String phone;

  @NotBlank(message = "Địa chỉ không được để trống")
  private String address;

  private String note;
}