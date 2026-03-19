package favorites.mapper;

import model.Favorite;
import model.Products;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteMapper extends JpaRepository<Favorite,Long> {


    List<Favorite> findByUser(User user);

    Favorite findByUserAndProduct(User user, Products product);
}
