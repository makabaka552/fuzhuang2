package products.mapper;

import model.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BrandMapper {
    @Select("SELECT * FROM brands WHERE brand_id = #{brandId}")
    Brand findById(Integer brandId);
}