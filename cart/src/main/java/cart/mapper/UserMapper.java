package cart.mapper;


import model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMapper extends JpaRepository<User,Long>  {
    @Select("select * from user where username=#{username}")
    User findByUsername(String username);
}
