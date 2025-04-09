package doanjava.webbannuochoa.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebhookData {
    private Long orderCode;             // Mã đơn hàng
    private int amount;                 // Tổng số tiền thanh toán
    private String description;         // Nội dung giao dịch
    private String accountNumber;       // Số tài khoản của kênh thanh toán
    private String reference;           // Mã tham chiếu giao dịch
    private String transactionDateTime; // Thời gian thực hiện giao dịch
    private String currency;            // Đơn vị tiền tệ
    private String paymentLinkId;       // ID của link thanh toán
    private String code;                // Mã trạng thái thanh toán
    private String desc;                // Mô tả về trạng thái thanh toán
    private String counterAccountBankId;  // Mã ngân hàng đối ứng (nếu có)
    private String counterAccountBankName; // Tên ngân hàng đối ứng (nếu có)
    private String counterAccountName;    // Tên chủ tài khoản đối ứng (nếu có)
    private String counterAccountNumber;  // Số tài khoản đối ứng (nếu có)
    private String virtualAccountName;    // Tên chủ tài khoản ảo (nếu có)
    private String virtualAccountNumber;  // Số tài khoản ảo (nếu có)
}
