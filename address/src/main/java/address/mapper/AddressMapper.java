package address.mapper;

import model.Addresses;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Mapper
public interface AddressMapper  {



    @Insert("INSERT INTO addresses (name, phone, address,is_default)"+"values (#{name}, #{phone},#{address},#{isDefault, jdbcType=BOOLEAN})")
    void cresteaddress(Addresses addresses);

    @Select("select * from addresses where id = #{id}")
    Addresses searchaddress(Long id);

    @Update("update addresses set name = #{addr.name}, phone = #{addr.phone}, address = #{addr.address},is_default = #{addr.isDefault, jdbcType=BOOLEAN} where id=#{id}")
    void updateaddress( @Param("id")Long id,  @Param("addr") Addresses addresses);

    @Delete("delete from addresses where id = #{id}")
    void deleteaddress(Long id);


    @Update("update addresses set is_default=0")
    void updatefaultaddress();

    @Update("update addresses set is_default=1 where id=#{id}")
    void setdefaultaddress(Long id);

    @Select("select * from addresses where is_default=1")
    Addresses getdefaultaddress();


}
