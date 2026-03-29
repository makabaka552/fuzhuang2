package auth.mapper;


import model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Insert("insert into user(password,username,phone,role,points) values (#{password},#{username},#{phone},#{role},#{points})")
    void insertUser(User user);

    @Select("select * from user where username=#{username}")
    User findUserByName(String username);

    @Select("select * from user where phone=#{phone}")
    User findUserByPhone(String phone);

    @Select("select * from user where username=#{username} and password=#{password}")
    User login(String username, String password);

    @Select("select * from user where phone=#{phone} and password=#{password}")
    User loginByPhone(String phone, String password);

    @Update("update user set password=#{password} where phone=#{phone}")
    void updatePasswordByPhone(String phone, String password);
}
