package doanjava.webbannuochoa.Controller;

import doanjava.webbannuochoa.Service.UserService;
import doanjava.webbannuochoa.models.User;
import doanjava.webbannuochoa.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;




    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vàomodel
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @NotNull BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register";
        }

        // Kiểm tra trùng lặp thông tin (username, email, phone)
        try {
            userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // Thông báo lỗi nếu username, email hoặc phone đã tồn tại
            return "users/register";
        }

        return "redirect:/login"; // Chuyển hướng người dùng tới trang đăng nhập
    }



}