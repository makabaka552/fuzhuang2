package cart.mapper;

import model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartMapper extends JpaRepository<CartItem, Long> {

    // 根据用户、产品和变体ID查询购物车项
    CartItem findByUserAndProductAndVariantId(User user, Products product, Integer variantId);

    List<CartItem> findByUser(User user);
}
