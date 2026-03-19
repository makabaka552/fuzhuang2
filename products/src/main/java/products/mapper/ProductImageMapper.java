package products.mapper;

import model.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper {
    @Select("SELECT * FROM product_images WHERE product_id = #{productId}")
    List<ProductImage> findByProductId(Long productId);

    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND color_id = #{colorId}")
    List<ProductImage> findByProductIdAndColorId(Long productId, Integer colorId);

    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_primary = 1 LIMIT 1")
    ProductImage findByProductIdAndIsPrimaryTrue(Long productId);

    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY display_order ASC")
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
}