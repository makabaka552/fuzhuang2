package cart.service.impl;

import cart.mapper.*;
import cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

        @Autowired
        private CartMapper cartMapper;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private ProductMapper productMapper;

        @Autowired
        private ProductVariantMapper productVariantMapper;

        @Override
        public void addCartItem(String username, Long productId, Integer variantId, Integer quantity) {
                User user = userMapper.findByUsername(username);

                Products product = productMapper.findById(productId)
                                .orElseThrow(() -> new RuntimeException("商品不存在"));

                ProductVariant variant = productVariantMapper.findById(variantId)
                                .orElseThrow(() -> new RuntimeException("商品变体不存在"));

                if (variant.getStockQuantity() < quantity) {
                        throw new RuntimeException("商品库存不足");
                }

                CartItem cartItem = cartMapper.findByUserAndProductAndVariantId(user, product, variantId);
                if (cartItem != null) {
                        // 已存在，数量累加
                        int newQuantity = cartItem.getQuantity() + quantity;
                        if (newQuantity > variant.getStockQuantity()) {
                                throw new RuntimeException("商品库存不足");
                        }
                        cartItem.setQuantity(newQuantity);
                } else {
                        // 不存在，新增
                        cartItem = new CartItem();
                        cartItem.setUser(user);
                        cartItem.setProduct(product);
                        cartItem.setQuantity(quantity);
                        cartItem.setVariantId(variantId);

                        // 设置尺码和款式信息
                        if (variant.getSize() != null) {
                                cartItem.setSizeId(variant.getSizeId());
                        }

                        // 直接设置colorId
                        cartItem.setColorId(variant.getColorId());

                        // 设置style
                        if (variant.getColor() != null) {
                                cartItem.setStyle(variant.getColor().getName());
                        }

                        cartItem.setImageUrl(variant.getImageUrl());
                        cartItem.setSkuCode(variant.getSkuCode());
                        cartItem.setCategory(product.getCategory());
                        cartItem.setSubCategory(product.getSubCategory());
                }
                cartMapper.save(cartItem);
        }

        @Override
        public List<CartItemDTO> GetCartItem(String username) {
                User user = userMapper.findByUsername(username);
                List<CartItem> cartItems = cartMapper.findByUser(user);
                return cartItems.stream().map(this::convertToDTO).collect(Collectors.toList());
        }

        @Override
        public boolean checkStock(String username) {
                User user = userMapper.findByUsername(username);
                List<CartItem> cartItems = cartMapper.findByUser(user);

                // 检查每个购物车项的库存是否足够
                return cartItems.stream().allMatch(item -> {
                        // 如果有变体ID，检查变体库存
                        if (item.getVariantId() != null) {
                                ProductVariant variant = productVariantMapper.findById(item.getVariantId())
                                                .orElse(null);
                                if (variant != null) {
                                        return item.getQuantity() <= variant.getStockQuantity();
                                }
                        }
                        // 否则检查商品库存
                        return item.getQuantity() <= item.getProducts().getStock();
                });
        }

        @Override
        public void UpdateCartItem(String username, Long itemId, Integer quantity) {
                User user = userMapper.findByUsername(username);
                CartItem cartItem = cartMapper.findById(itemId)
                                .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
                if (!cartItem.getUser().getUsername().equals(username)) {
                        throw new RuntimeException("无权操作此购物车商品");
                }

                // 检查库存是否足够
                if (cartItem.getVariantId() != null) {
                        // 如果有变体ID，检查变体库存
                        ProductVariant variant = productVariantMapper.findById(cartItem.getVariantId()).orElse(null);
                        if (variant != null && quantity > variant.getStockQuantity()) {
                                throw new RuntimeException("商品库存不足");
                        }
                } else if (quantity > cartItem.getProducts().getStock()) {
                        throw new RuntimeException("商品库存不足");
                }

                cartItem.setQuantity(quantity);
                cartMapper.save(cartItem);
        }

        @Override
        public void DeleteCartItem(String username, Long itemId) {
                User user = userMapper.findByUsername(username);
                CartItem cartItem = cartMapper.findById(itemId)
                                .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
                if (!cartItem.getUser().getUsername().equals(username)) {
                        throw new RuntimeException("无权操作此购物车商品");
                }
                cartMapper.deleteById(itemId);
        }

        private CartItemDTO convertToDTO(CartItem cartItem) {
                CartItemDTO dto = new CartItemDTO();
                dto.setId(cartItem.getId());
                dto.setProductId(cartItem.getProducts().getId());
                dto.setName(cartItem.getProducts().getName());
                dto.setPrice(cartItem.getProducts().getPrice());
                dto.setQuantity(cartItem.getQuantity());

                // 设置变体相关信息
                if (cartItem.getVariantId() != null) {
                        dto.setVariantId(cartItem.getVariantId());
                        ProductVariant variant = productVariantMapper.findById(cartItem.getVariantId()).orElse(null);
                        if (variant != null && variant.getSize() != null) {
                                dto.setSizeId(variant.getSize().getSizeId());
                                dto.setSizeName(variant.getSize().getSizeName());
                        } else {
                                dto.setSizeId(null);
                                dto.setSizeName(null);
                        }
                        dto.setSkuCode(cartItem.getSkuCode());
                        // 使用变体的库存
                        if (variant != null) {
                                dto.setStock(variant.getStockQuantity());
                                dto.setPrice(cartItem.getProducts().getPrice());
                                dto.setColorId(variant.getColorId());
                                // 设置颜色信息
                                if (variant.getColor() != null) {
                                        dto.setColorName(variant.getColor().getName());
                                        dto.setColorCode(variant.getColor().getCode());
                                }
                        } else {
                                dto.setStock(cartItem.getProducts().getStock());
                        }
                } else {
                        // 兼容旧数据
                        dto.setStock(cartItem.getProducts().getStock());
                }

                // 设置图片
                String imageUrl = cartItem.getImageUrl() != null ? cartItem.getImageUrl()
                                : cartItem.getProducts().getImageUrl();
                dto.setImage(imageUrl);

                // 设置分类信息
                dto.setCategory(cartItem.getCategory() != null ? cartItem.getCategory()
                                : cartItem.getProducts().getCategory());
                dto.setSubCategory(cartItem.getSubCategory() != null ? cartItem.getSubCategory()
                                : cartItem.getProducts().getSubCategory());

                dto.setStyle(cartItem.getStyle());
                dto.setSpecs(null);

                return dto;
        }
}
