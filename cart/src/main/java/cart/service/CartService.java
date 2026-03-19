package cart.service;

import model.CartItemDTO;

import java.util.List;

public interface CartService {

    void addCartItem(String username, Long productId, Integer variantId, Integer quantity);

    List<CartItemDTO> GetCartItem(String username);

    boolean checkStock(String username);

    void UpdateCartItem(String username, Long itemId, Integer quantity);

    void DeleteCartItem(String username, Long itemId);

}
