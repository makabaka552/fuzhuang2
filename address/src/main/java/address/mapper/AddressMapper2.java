package address.mapper;

import model.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressMapper2 extends JpaRepository<Addresses,Long> {

}
