package products.mapper;

import model.Color;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ColorMapper {
    @Select("select * from colors where id=#{colorId}")
    Color findColor(Integer colorId);

    @Select("select * from colors order by id")
    List<Color> findAll();
}
