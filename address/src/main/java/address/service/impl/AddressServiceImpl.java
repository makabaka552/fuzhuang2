package address.service.impl;

import address.mapper.AddressMapper;
import address.mapper.AddressMapper2;
import address.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import model.Addresses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressServiceImpl  implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private AddressMapper2 addressMapper2;


    @Override
    public List<Addresses> GetAddresses() {
        return addressMapper2.findAll();
    }

    @Override
    public void CreateAddresses(Addresses addresses) {
        addressMapper.cresteaddress(addresses);
    }

    @Override
    public Addresses FindAddresses(Long id) {
        return addressMapper.searchaddress(id);
    }

    @Override
    public void UpdateAddresses(Long id, Addresses addresses) {
        if (addresses.getIsDefault()){
            addressMapper.updatefaultaddress();
        }
        addressMapper.updateaddress(id,addresses);
    }

    @Override
    public void DeleteAddresses(Long id) {
        addressMapper.deleteaddress(id);
    }

    @Override
    public void SetDefaultAddress(Long id) {
        addressMapper.updatefaultaddress();
        addressMapper.setdefaultaddress(id);
    }

    @Override
    public Addresses GetDefaultAddress() {
        return addressMapper.getdefaultaddress();
    }
}
