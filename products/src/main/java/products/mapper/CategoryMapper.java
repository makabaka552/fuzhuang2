package products.mapper;

import model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Select("SELECT * FROM categories")
    @Results({
            @Result(property = "categoryId", column = "category_id"),
            @Result(property = "categoryName", column = "category_name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "level", column = "level"),
            @Result(property = "sortOrder", column = "sort_order")
    })
    List<Category> findAll();

    @Select("SELECT * FROM categories WHERE category_id = #{categoryId}")
    @Results({
            @Result(property = "categoryId", column = "category_id"),
            @Result(property = "categoryName", column = "category_name"),
            @Result(property = "parentId", column = "parent_id"),
            @Result(property = "level", column = "level"),
            @Result(property = "sortOrder", column = "sort_order")
    })
    Category findById(Integer categoryId);
}