package doanjava.webbannuochoa.security;

import doanjava.webbannuochoa.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final User user;

    // Constructor
    public CustomUserDetails(User user) { // Đổi tên lớp và constructor
        this.user = user; // Gán giá trị cho biến user
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities(); // Giả sử User của bạn có phương thức này
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cập nhật logic nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cập nhật logic nếu cần
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Cập nhật logic nếu cần
    }

    @Override
    public boolean isEnabled() {
        return true; // Cập nhật logic nếu cần
    }

    public User getUser () {
        return user; // Trả về đối tượng User
    }
}