package cart.controller;

import cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cart.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 添加商品变体到购物车
     * 这是推荐的添加购物车方法，支持商品变体
     */
    @PostMapping("/add-variant")
    public Result addCartItemWithVariant(@RequestBody AddCartVariantRequest request, HttpServletRequest httprequest) {
        String token = httprequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        cartService.addCartItem(username, request.getProductId(), request.getVariantId(), request.getQuantity());
        return Result.success("已成功添加到购物车中");
    }

    // 获取购物车列表
    @GetMapping("/list")
    public Result GetCartItem(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        List<CartItemDTO> cartItemDTOList = cartService.GetCartItem(username);
        return Result.success(cartItemDTOList);
    }

    // 检查库存
    @PostMapping("/check-stock")
    public Result checkStock(HttpServletRequest httpRequest) {
        // 从请求头中手动解析token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        boolean hasStock = cartService.checkStock(username);
        if (hasStock) {
            return Result.success();
        } else {
            return Result.failure("部分商品库存不足");
        }
    }

    // 更新购物车商品数量
    @PutMapping("/items/{itemId}")
    public Result UpdateCartItem(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest quantity,
            HttpServletRequest httpRequest) {
        // 从请求头中手动解析token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        cartService.UpdateCartItem(username, itemId, quantity.getQuantity());
        return Result.success("已成功更新购物车");
    }

    // 删除购物车商品
    @DeleteMapping("/items/{itemId}")
    public Result DeleteCartItem(@PathVariable Long itemId, HttpServletRequest httpRequest) {
        // 从请求头中手动解析token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        cartService.DeleteCartItem(username, itemId);
        return Result.success();
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public Result ClearCartItem(@RequestBody ClearCartRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = JwtUtil.getUsernameJwt(token);
        List<Long> itemIds = request.getItemIds();
        for (int i = 0; i < itemIds.size(); i++) {
            Long id = itemIds.get(i);
            cartService.DeleteCartItem(username, id);
        }
        return Result.success();
    }
}
