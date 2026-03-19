package products.controller;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import products.mapper.ProductVariantMapper;
import products.service.ProductsService;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    @GetMapping("/list")
    public Result getProducts() {
        log.info("获取商品列表");
        List<Products> products = productsService.productsList();
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result getProduct(@PathVariable Long id) {
        log.info("获取商品详情: productId={}", id);
        Products products = productsService.productsDetail(id);

        if (products == null) {
            log.error("未找到ID为{}的商品", id);
        }

        return Result.success(products);
    }

    /**
     * 获取商品库存/变体信息
     * 兼容旧版API，实际返回商品变体信息
     */
    @GetMapping("/stock/{id}")
    public Result getProductStock(@PathVariable Long id, @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) Integer sizeId) {
        log.info("获取商品变体信息: productId={}, colorId={}, sizeId={}", id, colorId, sizeId);

        if (sizeId != null) {
            // 如果提供了sizeId，则获取特定的变体
            ProductVariantDTO variant = productsService.getProductVariant(id, colorId, sizeId);
            return Result.success(variant);
        } else {
            // 如果只提供了colorId，则获取该颜色的所有变体
            List<ProductVariantDTO> variants = productsService.getProductVariantsByColor(id, colorId);
            return Result.success(variants);
        }
    }

    /**
     * 获取商品所有变体
     */
    @GetMapping("/variants/{id}")
    public Result getProductVariants(@PathVariable Long id) {
        log.info("获取商品所有变体: productId={}", id);
        List<ProductVariantDTO> variants = productsService.getProductVariants(id);
        return Result.success(variants);
    }

    /**
     * 获取商品变体详情
     */
    @GetMapping("/variant/{productId}")
    public Result getProductVariant(
            @PathVariable Long productId,
            @RequestParam Integer colorId,
            @RequestParam Integer sizeId) {
        log.info("获取商品变体详情: productId={}, colorId={}, sizeId={}", productId, colorId, sizeId);
        ProductVariantDTO variant = productsService.getProductVariant(productId, colorId, sizeId);
        if (variant == null) {
            log.warn("未找到指定的商品变体");
        }
        return Result.success(variant);
    }

    /**
     * 获取商品所有颜色
     */
    @GetMapping("/colors/{productId}")
    public Result getProductColors(@PathVariable Long productId) {
        log.info("获取商品所有颜色: productId={}", productId);
        List<Color> colors = productsService.getProductColors(productId);
        return Result.success(colors);
    }

    /**
     * 获取所有可用颜色
     */
    @GetMapping("/admin/colors")
    public Result getAllColors() {
        log.info("获取所有可用颜色");
        List<Color> colors = productsService.getAllColors();
        return Result.success(colors);
    }

    /**
     * 获取商品所有尺码
     */
    @GetMapping("/sizes/{productId}")
    public Result getProductSizes(@PathVariable Long productId) {
        log.info("获取商品所有尺码: productId={}", productId);
        List<Size> sizes = productsService.getProductSizes(productId);
        return Result.success(sizes);
    }

    /**
     * 获取商品特定颜色的所有尺码
     */
    @GetMapping("/sizes/{productId}/{colorId}")
    public Result getProductSizesByColor(
            @PathVariable Long productId,
            @PathVariable Integer colorId) {
        log.info("获取商品特定颜色的所有尺码: productId={}, colorId={}", productId, colorId);
        List<Size> sizes = productsService.getProductSizesByColor(productId, colorId);
        return Result.success(sizes);
    }

    /**
     * 获取商品所有图片
     */
    @GetMapping("/images/{productId}")
    public Result getProductImages(@PathVariable Long productId) {
        log.info("获取商品所有图片: productId={}", productId);
        List<ProductImage> images = productsService.getProductImages(productId);
        return Result.success(images);
    }

    /**
     * 获取商品特定颜色的所有图片
     */
    @GetMapping("/images/{productId}/{colorId}")
    public Result getProductImagesByColor(
            @PathVariable Long productId,
            @PathVariable Integer colorId) {
        log.info("获取商品特定颜色的所有图片: productId={}, colorId={}", productId, colorId);
        List<ProductImage> images = productsService.getProductImagesByColor(productId, colorId);
        return Result.success(images);
    }

    @GetMapping("/admin/list")
    public Result getProductList(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBeam pageBeam = productsService.GetProductList(page, pageSize);
        return Result.success(pageBeam);
    }

    @PutMapping("/admin/{productid}/update")
    public Result updateProduct(@RequestBody Products product, @PathVariable Integer productid) {
        productsService.updateProduct(product, productid);
        return Result.success("更新商品成功");
    }

    @DeleteMapping("/admin/{productid}/delete")
    public Result deleteProduct(@PathVariable Integer productid) {
        productsService.deleteproduct(productid);
        return Result.success("删除商品成功");

    }

    @PatchMapping("/admin/{productid}/status")
    public Result updateProductStatus(@PathVariable Integer productid, @RequestBody StatusUpdateRequest status) {
        productsService.updateProductStatus(productid, status.getStatus());
        return Result.success("更新商品状态成功");
    }

    @PostMapping("/admin/create")
    public Result createProduct(@RequestBody Products product) {
        Long productId = productsService.createProduct(product);
        log.info("成功创建商品，ID: {}", productId);
        return Result.success(productId);
    }

    /**
     * 获取商品分类列表
     */
    @GetMapping("/admin/categories/list")
    public Result getCategories() {
        log.info("获取商品分类列表");
        List<Category> categories = productsService.getCategories();
        return Result.success(categories);
    }

    /**
     * 获取尺码分类列表
     */
    @GetMapping("/admin/size-categories")
    public Result getSizeCategories() {
        log.info("获取尺码分类列表");
        List<SizeCategory> sizeCategories = productsService.getSizeCategories();
        return Result.success(sizeCategories);
    }

    /**
     * 获取特定分类下的尺码列表
     */
    @GetMapping("/admin/sizes/category/{categoryId}")
    public Result getSizesByCategory(@PathVariable Integer categoryId) {
        log.info("获取特定分类下的尺码列表: categoryId={}", categoryId);
        List<Size> sizes = productsService.getSizesByCategory(categoryId);
        return Result.success(sizes);
    }

    /**
     * 保存商品变体
     */
    @PostMapping("/admin/{productId}/variants")
    public Result saveProductVariants(
            @PathVariable Long productId,
            @RequestBody List<ProductVariantDTO> variants) {
        log.info("保存商品变体: productId={}, variants数量={}", productId, variants.size());

        // 检查接收到的变体数据
        boolean hasInvalidData = false;
        for (int i = 0; i < variants.size(); i++) {
            ProductVariantDTO variant = variants.get(i);
            log.info("变体 #{} 详情: color_id={}, size_id={}, sku_code={}, stock_quantity={}",
                    i, variant.get_color_id(), variant.get_size_id(),
                    variant.get_sku_code(), variant.get_stock_quantity());

            if (variant.get_color_id() == null || variant.get_size_id() == null) {
                log.warn("接收到无效的变体数据 #{}: color_id={}, size_id={}",
                        i, variant.get_color_id(), variant.get_size_id());
                hasInvalidData = true;
            }
        }

        if (hasInvalidData) {
            return Result.error("变体数据不完整，请检查颜色和尺码信息");
        }

        try {
            productsService.saveProductVariants(productId, variants);
            return Result.success("保存商品变体成功");
        } catch (Exception e) {
            log.error("保存商品变体失败", e);
            return Result.error("保存商品变体失败: " + e.getMessage());
        }
    }

    /**
     * 减少商品库存
     */
    @PostMapping("/decrease-stock")
    public Result decreaseStock(@RequestBody Map<String, Object> request) {
        log.info("收到减少库存请求: {}", request);

        try {
            // 获取请求参数
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer colorId = Integer.valueOf(request.get("colorId").toString());
            Integer sizeId = Integer.valueOf(request.get("sizeId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());

            // 调用服务减少库存
            boolean success = productsService.decreaseStock(productId, colorId, sizeId, quantity);

            if (success) {
                return Result.success("库存减少成功");
            } else {
                return Result.failure("库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("减少库存失败: {}", e.getMessage(), e);
            return Result.failure("减少库存失败: " + e.getMessage());
        }
    }

    /**
     * 通过变体ID获取变体详情
     */
    @GetMapping("/variant-detail/{variantId}")
    public Result getVariantDetail(@PathVariable Integer variantId) {
        log.info("通过变体ID获取变体详情: variantId={}", variantId);

        try {
            ProductVariant variant = productVariantMapper.findById(variantId);

            if (variant == null) {
                log.warn("未找到指定的商品变体: variantId={}", variantId);
                return Result.failure("未找到指定的商品变体");
            }

            // 构建变体详情响应
            Map<String, Object> variantDetail = new HashMap<>();
            variantDetail.put("variantId", variant.get_variant_id());
            variantDetail.put("productId", variant.get_product_id());
            variantDetail.put("colorId", variant.get_color_id());
            variantDetail.put("sizeId", variant.get_size_id());
            variantDetail.put("stockQuantity", variant.get_stock_quantity());
            variantDetail.put("price", variant.get_price());
            variantDetail.put("status", variant.get_status());

            return Result.success(variantDetail);
        } catch (Exception e) {
            log.error("获取变体详情失败: {}", e.getMessage(), e);
            return Result.failure("获取变体详情失败: " + e.getMessage());
        }
    }
}
