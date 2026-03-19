package address.service;

import model.Addresses;

import java.util.List;

public interface AddressService {


    List<Addresses> GetAddresses();

    void CreateAddresses(Addresses addresses);

    Addresses FindAddresses(Long id);

    void UpdateAddresses(Long id, Addresses addresses);

    void DeleteAddresses(Long id);

    void SetDefaultAddress(Long id);

    Addresses GetDefaultAddress();
}
