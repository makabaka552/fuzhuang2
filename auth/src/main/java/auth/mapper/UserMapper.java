package auth.mapper;


import model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //插入用户
    @Insert("insert into user(password,username) values (#{password},#{username})")
    void insertUser(User user);
    //根据用户名查询
    @Select("select * from user where username=#{username}")
    User findUserByName(String username);

    @Select("select * from user where username=#{username} and password=#{password} ")
    User login(String username, String password);
}
