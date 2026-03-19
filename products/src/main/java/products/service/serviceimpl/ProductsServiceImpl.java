package products.service.serviceimpl;

import com.github.pagehelper.Page;
import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import products.mapper.*;
import products.service.ProductsService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import products.mapper.ColorMapper;
import products.mapper.ProductVariantMapper;
import products.mapper.ProductsMapper;

import java.math.BigDecimal;

@Service
@Slf4j
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private ColorMapper colorMapper;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    @Autowired
    private SizeMapper sizeMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SizeCategoryMapper sizeCategoryMapper;

    @Override
    public List<Products> productsList() {
        return productsMapper.productsList();
    }

    @Override
    public Products productsDetail(Long id) {
        Products product = productsMapper.productsDetail(id);

        // 检查imageUrl是否为空，如果为空则尝试获取主图
        if (product != null) {
            if (product.getImage_url() == null) {
                try {
                    // 直接使用Mapper方法获取主图
                    ProductImage primaryImage = productImageMapper.findByProductIdAndIsPrimaryTrue(product.getId());

                    // 如果找不到主图，则尝试获取第一张图片
                    if (primaryImage == null) {
                        List<ProductImage> orderedImages = productImageMapper
                                .findByProductIdOrderByDisplayOrderAsc(product.getId());
                        if (orderedImages != null && !orderedImages.isEmpty()) {
                            primaryImage = orderedImages.get(0);
                        }
                    }

                    // 设置商品主图
                    if (primaryImage != null) {
                        product.setImage_url(primaryImage.getImage_url());
                    } else {
                        log.warn("未找到商品ID: {}的任何图片", product.getId());
                    }
                } catch (Exception e) {
                    log.error("获取商品图片时发生错误: {}", e.getMessage(), e);
                }
            }
        } else {
            log.error("未找到ID为{}的商品", id);
        }

        return product;
    }

    @Override
    public ProductVariantDTO getProductVariant(Long productId, Integer colorId, Integer sizeId) {
        ProductVariant variant = productVariantMapper.findByProductIdAndColorIdAndSizeId(productId, colorId, sizeId);
        if (variant == null) {
            return null;
        }
        return convertToVariantDTO(variant);
    }

    @Override
    public List<ProductVariantDTO> getProductVariants(Long productId) {
        List<ProductVariant> variants = productVariantMapper.findByProductId(productId);
        return variants.stream().map(this::convertToVariantDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductVariantDTO> getProductVariantsByColor(Long productId, Integer colorId) {
        List<ProductVariant> variants = productVariantMapper.findByProductIdAndColorId(productId, colorId);
        return variants.stream().map(this::convertToVariantDTO).collect(Collectors.toList());
    }

    @Override
    public List<Color> getProductColors(Long productId) {
        return productVariantMapper.findDistinctColorsByProductId(productId);
    }

    @Override
    public List<Color> getAllColors() {
        return colorMapper.findAll();
    }

    @Override
    public List<Size> getProductSizes(Long productId) {
        return productVariantMapper.findDistinctSizesByProductId(productId);
    }

    @Override
    public List<Size> getProductSizesByColor(Long productId, Integer colorId) {
        return productVariantMapper.findDistinctSizesByProductIdAndColorId(productId, colorId);
    }

    @Override
    public List<ProductImage> getProductImages(Long productId) {
        return productImageMapper.findByProductId(productId);
    }

    @Override
    public List<ProductImage> getProductImagesByColor(Long productId, Integer colorId) {
        return productImageMapper.findByProductIdAndColorId(productId, colorId);
    }

    private ProductVariantDTO convertToVariantDTO(ProductVariant variant) {
        ProductVariantDTO dto = new ProductVariantDTO();
        dto.set_variant_id(variant.get_variant_id());
        dto.set_product_id(variant.get_product_id());
        dto.set_color_id(variant.get_color_id());
        dto.set_color_name(variant.get_color() != null ? variant.get_color().getName() : null);
        dto.set_color_code(variant.get_color() != null ? variant.get_color().getCode() : null);
        dto.set_size_id(variant.get_size_id());
        dto.set_size_name(variant.get_size() != null ? variant.get_size().getSize_name() : null);
        dto.set_size_code(variant.get_size() != null ? variant.get_size().getSize_code() : null);
        dto.set_size_value(variant.get_size() != null ? variant.get_size().getSize_value() : null);
        dto.set_sku_code(variant.get_sku_code());
        dto.set_stock_quantity(variant.get_stock_quantity());
        dto.set_price(variant.get_price());
        dto.set_image_url(variant.get_image_url());
        dto.set_status(variant.get_status());
        return dto;
    }

    @Override
    public PageBeam GetProductList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<Products> products = productsMapper.getproductsList();
        PageBeam pageBeam = new PageBeam();
        pageBeam.setTotal(products.getTotal());
        pageBeam.setRows(products.getResult());
        return pageBeam;
    }

    @Override
    public void updateProduct(Products product, Integer productid) {
        productsMapper.updateProduct(product.getCategory(), product.getCategoryId(), product.getImage_url(),
                product.getName(), product.getPrice(), product.getStatus(), productid);
    }

    @Override
    public void deleteproduct(Integer productid) {
        productsMapper.deleteProduct(productid);
    }

    @Override
    public void updateProductStatus(Integer productid, Integer status) {
        productsMapper.updateProductStatus(status, productid);
    }

    @Override
    public Long createProduct(Products product) {
        productsMapper.addProduct(product);
        return product.getId();
    }

    @Override
    public List<Category> getCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public List<SizeCategory> getSizeCategories() {
        return sizeCategoryMapper.findAll();
    }

    @Override
    public List<Size> getSizesByCategory(Integer categoryId) {
        return sizeMapper.findByCategoryId(categoryId);
    }

    @Override
    public void saveProductVariants(Long productId, List<ProductVariantDTO> variantDTOs) {
        log.info("保存商品变体: productId={}, 变体数量={}", productId, variantDTOs.size());

        // 输出接收到的变体数据用于调试
        for (int i = 0; i < variantDTOs.size(); i++) {
            ProductVariantDTO dto = variantDTOs.get(i);
            log.info("接收到的变体 #{}: color_id={}, size_id={}, sku_code={}, stock_quantity={}",
                    i, dto.get_color_id(), dto.get_size_id(), dto.get_sku_code(), dto.get_stock_quantity());
        }

        // 过滤掉无效的变体
        List<ProductVariantDTO> validVariants = variantDTOs.stream()
                .filter(dto -> dto.get_color_id() != null && dto.get_size_id() != null)
                .collect(Collectors.toList());

        if (validVariants.isEmpty()) {
            log.warn("没有有效的变体数据，跳过保存操作");
            return; // 如果没有有效变体，直接返回
        }

        log.info("有效变体数量: {}", validVariants.size());

        // 先删除该商品的所有现有变体
        productVariantMapper.deleteByProductId(productId);

        // 保存新的变体
        for (ProductVariantDTO dto : validVariants) {
            ProductVariant variant = new ProductVariant();
            variant.set_product_id(productId);
            variant.set_color_id(dto.get_color_id());
            variant.set_size_id(dto.get_size_id());
            variant.set_sku_code(dto.get_sku_code() != null ? dto.get_sku_code()
                    : productId + "-" + dto.get_color_id() + "-" + dto.get_size_id());
            variant.set_stock_quantity(dto.get_stock_quantity() != null ? dto.get_stock_quantity() : 0);
            variant.set_price(dto.get_price() != null ? dto.get_price() : new BigDecimal("0"));
            variant.set_status(dto.get_status() != null ? dto.get_status() : 1);

            log.debug("保存变体前的数据检查: 商品ID={}, 颜色ID={}, 尺码ID={}, SKU={}, 库存={}",
                    variant.get_product_id(), variant.get_color_id(), variant.get_size_id(),
                    variant.get_sku_code(), variant.get_stock_quantity());

            productVariantMapper.save(variant);
            log.debug("变体保存成功: 商品ID={}, 颜色ID={}, 尺码ID={}",
                    productId, dto.get_color_id(), dto.get_size_id());
        }

        // 更新商品总库存
        int totalStock = validVariants.stream()
                .mapToInt(dto -> dto.get_stock_quantity() != null ? dto.get_stock_quantity() : 0)
                .sum();

        // 更新商品表中的库存字段
        productsMapper.updateStock(productId, totalStock);

        log.info("商品变体保存完成: productId={}, 总库存={}", productId, totalStock);
    }

    @Override
    public boolean decreaseStock(Long productId, Integer colorId, Integer sizeId, Integer quantity) {
        log.info("减少商品库存: productId={}, colorId={}, sizeId={}, quantity={}", productId, colorId, sizeId, quantity);

        try {
            // 查找对应的商品变体
            ProductVariant variant = productVariantMapper.findByProductIdAndColorIdAndSizeId(productId, colorId,
                    sizeId);
            if (variant == null) {
                log.error("未找到商品变体: productId={}, colorId={}, sizeId={}", productId, colorId, sizeId);
                return false;
            }

            // 检查库存是否足够
            if (variant.get_stock_quantity() < quantity) {
                log.error("库存不足: 当前库存={}, 需要数量={}", variant.get_stock_quantity(), quantity);
                return false;
            }

            // 减少库存
            int newStock = variant.get_stock_quantity() - quantity;
            variant.set_stock_quantity(newStock);
            productVariantMapper.updateStockQuantity(variant.get_variant_id(), newStock);
            log.info("库存更新成功: variantId={}, 新库存={}", variant.get_variant_id(), newStock);

            // 更新商品总库存
            int totalStock = productVariantMapper.sumStockByProductId(productId);
            productsMapper.updateStock(productId, totalStock);
            log.info("商品总库存更新成功: productId={}, 总库存={}", productId, totalStock);

            return true;
        } catch (Exception e) {
            log.error("减少库存失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
