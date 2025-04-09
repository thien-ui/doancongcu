package doanjava.webbannuochoa.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/403")
    public String accessDenied() {
        return "access-denied"; // Trả về trang access-denied.html
    }
}
