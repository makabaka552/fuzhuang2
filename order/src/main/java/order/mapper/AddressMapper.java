package order.mapper;

import model.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressMapper extends JpaRepository<Addresses,Long> {
}
