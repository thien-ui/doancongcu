package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.ProductService;
import doanjava.webbannuochoa.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home() {
        // Chuyển hướng đến trang /products
        return "redirect:/products";
    }
    // Thêm phương thức contact cho trang liên hệ
    @GetMapping("/contact")
    public String contact() {
        // Trả về view contact.html
        return "home/contact";  // Tên của file contact.html trong thư mục templates
    }


}
