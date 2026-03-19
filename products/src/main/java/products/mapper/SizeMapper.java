package products.mapper;

import model.Size;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.One;

import java.util.List;

@Mapper
public interface SizeMapper {
    @Select("SELECT * FROM sizes WHERE size_id = #{sizeId}")
    @Results({
            @Result(property = "sizeId", column = "size_id"),
            @Result(property = "sizeCategoryId", column = "size_category_id"),
            @Result(property = "sizeName", column = "size_name"),
            @Result(property = "sizeCode", column = "size_code"),
            @Result(property = "sizeValue", column = "size_value"),
            @Result(property = "sizeOrder", column = "size_order"),
            @Result(property = "sizeCategory", column = "size_category_id", one = @One(select = "products.mapper.SizeCategoryMapper.findById"))
    })
    Size findById(Integer sizeId);

    @Select("SELECT * FROM sizes WHERE size_category_id = #{categoryId} ORDER BY size_order")
    @Results({
            @Result(property = "sizeId", column = "size_id"),
            @Result(property = "sizeCategoryId", column = "size_category_id"),
            @Result(property = "sizeName", column = "size_name"),
            @Result(property = "sizeCode", column = "size_code"),
            @Result(property = "sizeValue", column = "size_value"),
            @Result(property = "sizeOrder", column = "size_order"),
            @Result(property = "sizeCategory", column = "size_category_id", one = @One(select = "products.mapper.SizeCategoryMapper.findById"))
    })
    List<Size> findByCategoryId(Integer categoryId);
}