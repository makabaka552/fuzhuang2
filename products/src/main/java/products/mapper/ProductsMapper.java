package products.mapper;

import com.github.pagehelper.Page;
import model.Products;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductsMapper {
        @Select("select * from products")
        List<Products> productsList();

        @Select("select * from products where id=#{id}")
        Products productsDetail(Long id);

        @Select("select * from products")
        Page<Products> getproductsList();

        @Delete("delete from products where id=#{productid}")
        void deleteProduct(Integer productid);

        @Update("update products set status=#{status} where id=#{productid}")
        void updateProductStatus(Integer status, Integer productid);

        @Insert("insert into products (category_id, name, price, image_url, category, status, stock)"
                        + "values (#{categoryId}, #{name}, #{price}, #{imageUrl}, #{category}, #{status}, #{stock}) ")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void addProduct(Products product);

        @Update("update products set category=#{category}, category_id=#{categoryId}, image_url=#{imageUrl}, name=#{name}, price=#{price}, status=#{status} where id=#{productid}")
        void updateProduct(String category, Long categoryId, String imageUrl, String name, BigDecimal price,
                        Integer status, Integer productid);

        @Update("update products set stock=#{stock} where id=#{productId}")
        void updateStock(Long productId, Integer stock);
}
