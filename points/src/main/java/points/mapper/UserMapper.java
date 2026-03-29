package points.mapper;

import model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from user where username=#{username}")
    User findByUsername(String username);

    @Select("select * from user where id=#{id}")
    User findById(Long id);

    @Update("update user set points=#{points} where id=#{userId}")
    void updatePoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Update("update user set points = points + #{amount} where id=#{userId}")
    int addPoints(@Param("userId") Long userId, @Param("amount") Integer amount);

    @Update("update user set points = points - #{amount} where id=#{userId} and points >= #{amount}")
    int deductPoints(@Param("userId") Long userId, @Param("amount") Integer amount);
}
