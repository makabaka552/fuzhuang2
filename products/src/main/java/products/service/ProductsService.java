package products.service;

import com.github.pagehelper.Page;
import model.*;

import java.util.List;

public interface ProductsService {
    List<Products> productsList();

    Products productsDetail(Long id);

    ProductVariantDTO getProductVariant(Long productId, Integer colorId, Integer sizeId);

    List<ProductVariantDTO> getProductVariants(Long productId);

    List<ProductVariantDTO> getProductVariantsByColor(Long productId, Integer colorId);

    List<Color> getProductColors(Long productId);

    List<Color> getAllColors();

    List<Size> getProductSizes(Long productId);

    List<Size> getProductSizesByColor(Long productId, Integer colorId);

    List<ProductImage> getProductImages(Long productId);

    List<ProductImage> getProductImagesByColor(Long productId, Integer colorId);

    PageBeam GetProductList(Integer page, Integer pageSize);

    void updateProduct(Products product, Integer productid);

    void deleteproduct(Integer productid);

    void updateProductStatus(Integer productid, Integer status);

    Long createProduct(Products product);

    List<Category> getCategories();

    List<SizeCategory> getSizeCategories();

    List<Size> getSizesByCategory(Integer categoryId);

    void saveProductVariants(Long productId, List<ProductVariantDTO> variants);

    boolean decreaseStock(Long productId, Integer colorId, Integer sizeId, Integer quantity);
}
