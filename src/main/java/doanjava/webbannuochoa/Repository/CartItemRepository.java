package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Cart;
import doanjava.webbannuochoa.models.CartItem;
import doanjava.webbannuochoa.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);


}