package doanjava.webbannuochoa;

import doanjava.webbannuochoa.Service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Đánh dấu lớp này là một lớp cấu hình cho Spring Context.
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security.
@RequiredArgsConstructor // Lombok tự động tạo constructor có tham số cho tất cả các trường final.
public class SecurityConfig {

    private final UserService userService; // Tiêm UserService vào lớp cấu hình này.

    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Trả về instance của UserService đã được tiêm
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Bean mã hóa mật khẩu sử dụng BCrypt.
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider(); // Tạo nhà cung cấp xác thực.
        auth.setUserDetailsService(userDetailsService()); // Thiết lập dịch vụ chi tiết người dùng.
        auth.setPasswordEncoder(passwordEncoder()); // Thiết lập cơ chế mã hóa mật khẩu.
        return auth; // Trả về nhà cung cấp xác thực.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // API mới để vô hiệu hóa CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll() // Các trang và tài nguyên không cần xác thực
                        .requestMatchers("/products/edit/**", "/products/add", "/products/delete",
                                "/categories/edit/**", "/categories/add", "/categories/delete",
                                "/brands/edit/**", "/brands/add", "/brands/delete","/admin/orders/**", "/admin/order-details/**", "/admin/reviews/**")
                        .hasAuthority("ADMIN") // Chỉ cho phép ADMIN
                        .requestMatchers("/reviews/add", "/favorites/**", "/cart/**").authenticated() // Yêu cầu đăng nhập
                        .anyRequest().permitAll() // Các yêu cầu khác
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/products")
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("hutech")
                        .rememberMeCookieName("hutech")
                        .tokenValiditySeconds(24 * 60 * 60) // Thời gian nhớ đăng nhập.
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403") // Trang báo lỗi khi truy cập không được phép.
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(-1) // Giới hạn số phiên đăng nhập.
                        .expiredUrl("/login") // Trang khi phiên hết hạn.
                )

                .httpBasic(httpBasic -> httpBasic
                        .realmName("hutech") // Tên miền cho xác thực cơ bản.
                )
                .build(); // Xây dựng và trả về chuỗi lọc bảo mật.

    }
}