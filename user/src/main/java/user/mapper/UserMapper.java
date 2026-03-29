package user.mapper;

import com.github.pagehelper.Page;
import model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from user where username=#{username}")
    User FindUser(String username);

    @Select("select * from user where id=#{id}")
    User findById(Long id);

    @Select("select * from user")
    Page<User> userList();

    @Insert("insert into user(password,username,role,phone,points) values (#{password},#{username},#{role},#{phone},#{points})")
    void createUser(User user);

    @Delete("delete from user where id=#{userid}")
    void deleteUser(Integer userid);

    @Update("update user set username=#{username},role=#{role},phone=#{phone},points=#{points} where id=#{userId}")
    void update(String username, String role, String phone,Integer points, Integer userId);
}
