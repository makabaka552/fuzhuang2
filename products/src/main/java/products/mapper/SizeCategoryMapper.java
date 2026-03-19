package products.mapper;

import model.SizeCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;

@Mapper
public interface SizeCategoryMapper {
    @Select("SELECT * FROM size_categories WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description")
    })
    SizeCategory findById(Integer id);

    @Select("SELECT * FROM size_categories ORDER BY id")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description")
    })
    List<SizeCategory> findAll();
}