package doanjava.webbannuochoa.Repository;

import doanjava.webbannuochoa.models.Favorite;
import doanjava.webbannuochoa.models.Product;
import doanjava.webbannuochoa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndProduct(User user, Product product);

    void deleteByUserAndProductId(User user, Long productId);
    List<Favorite> findByUserAndProductIsHiddenFalse(User user);
}
