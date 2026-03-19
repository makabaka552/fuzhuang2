package products.mapper;

import model.Color;
import model.ProductVariant;
import model.Size;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductVariantMapper {
        @Select("SELECT * FROM product_variants WHERE product_id = #{productId}")
        @Results({
                        @Result(property = "variantId", column = "variant_id"),
                        @Result(property = "productId", column = "product_id"),
                        @Result(property = "colorId", column = "color_id"),
                        @Result(property = "sizeId", column = "size_id"),
                        @Result(property = "color", column = "color_id", one = @One(select = "products.mapper.ColorMapper.findColor")),
                        @Result(property = "size", column = "size_id", one = @One(select = "products.mapper.SizeMapper.findById"))
        })
        List<ProductVariant> findByProductId(Long productId);

        @Select("SELECT * FROM product_variants WHERE product_id = #{productId} AND color_id = #{colorId}")
        @Results({
                        @Result(property = "variantId", column = "variant_id"),
                        @Result(property = "productId", column = "product_id"),
                        @Result(property = "colorId", column = "color_id"),
                        @Result(property = "sizeId", column = "size_id"),
                        @Result(property = "color", column = "color_id", one = @One(select = "products.mapper.ColorMapper.findColor")),
                        @Result(property = "size", column = "size_id", one = @One(select = "products.mapper.SizeMapper.findById"))
        })
        List<ProductVariant> findByProductIdAndColorId(Long productId, Integer colorId);

        @Select("SELECT * FROM product_variants WHERE product_id = #{productId} AND color_id = #{colorId} AND size_id = #{sizeId}")
        @Results({
                        @Result(property = "variantId", column = "variant_id"),
                        @Result(property = "productId", column = "product_id"),
                        @Result(property = "colorId", column = "color_id"),
                        @Result(property = "sizeId", column = "size_id"),
                        @Result(property = "color", column = "color_id", one = @One(select = "products.mapper.ColorMapper.findColor")),
                        @Result(property = "size", column = "size_id", one = @One(select = "products.mapper.SizeMapper.findById"))
        })
        ProductVariant findByProductIdAndColorIdAndSizeId(Long productId, Integer colorId, Integer sizeId);

        @Select("SELECT DISTINCT c.* FROM product_variants pv JOIN colors c ON pv.color_id = c.id WHERE pv.product_id = #{productId}")
        List<Color> findDistinctColorsByProductId(Long productId);

        @Select("SELECT DISTINCT s.* FROM product_variants pv JOIN sizes s ON pv.size_id = s.size_id WHERE pv.product_id = #{productId}")
        List<Size> findDistinctSizesByProductId(Long productId);

        @Select("SELECT DISTINCT s.* FROM product_variants pv JOIN sizes s ON pv.size_id = s.size_id WHERE pv.product_id = #{productId} AND pv.color_id = #{colorId}")
        List<Size> findDistinctSizesByProductIdAndColorId(Long productId, Integer colorId);

        @Delete("DELETE FROM product_variants WHERE product_id = #{productId}")
        void deleteByProductId(Long productId);

        @Insert("INSERT INTO product_variants (product_id, color_id, size_id, sku_code, stock_quantity, price, status) "
                        +
                        "VALUES (#{_product_id}, #{_color_id}, #{_size_id}, #{_sku_code}, #{_stock_quantity}, #{_price}, #{_status})")
        @Options(useGeneratedKeys = true, keyProperty = "_variant_id", keyColumn = "variant_id")
        void save(ProductVariant variant);

        @Update("UPDATE product_variants SET stock_quantity = #{stockQuantity} WHERE variant_id = #{variantId}")
        void updateStockQuantity(Integer variantId, Integer stockQuantity);

        @Select("SELECT SUM(stock_quantity) FROM product_variants WHERE product_id = #{productId}")
        Integer sumStockByProductId(Long productId);

        @Select("SELECT * FROM product_variants WHERE variant_id = #{variantId}")
        @Results({
                        @Result(property = "_variant_id", column = "variant_id"),
                        @Result(property = "_product_id", column = "product_id"),
                        @Result(property = "_color_id", column = "color_id"),
                        @Result(property = "_size_id", column = "size_id"),
                        @Result(property = "_stock_quantity", column = "stock_quantity"),
                        @Result(property = "_color", column = "color_id", one = @One(select = "products.mapper.ColorMapper.findColor")),
                        @Result(property = "_size", column = "size_id", one = @One(select = "products.mapper.SizeMapper.findById"))
        })
        ProductVariant findById(Integer variantId);
}