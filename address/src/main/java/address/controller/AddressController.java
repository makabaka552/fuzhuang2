package address.controller;

import address.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import model.Addresses;
import model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("")
    public Result GetAllAddresses(){
        List<Addresses> e = addressService.GetAddresses();
        return Result.success(e);
    }

    @PostMapping("/create")
    public Result CreateAddress(@RequestBody Addresses addresses){
        addressService.CreateAddresses(addresses);
        return Result.success("添加成功，已成成功添加地址");
    }

    @GetMapping("/{id}")
    public Result GetAddresses(@PathVariable Long id){
        Addresses e = addressService.FindAddresses(id);
        return Result.success(e);
    }
    @PutMapping("/update/{id}")
    public Result UpdateAddress(@PathVariable Long id,@RequestBody Addresses addresses){
        log.info("{}",addresses);
        addressService.UpdateAddresses(id,addresses);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result DeleteAddress(@PathVariable Long id){
        addressService.DeleteAddresses(id);
        return Result.success("地址删除成功");

    }

    @PutMapping("/{id}")
    public Result SetDefaultAddress(@PathVariable Long id){
        log.info("{}",id);
        addressService.SetDefaultAddress(id);
        return Result.success("默认地址设置成功");
    }

    @GetMapping("/default")
    public Result GetDefaultAddress(){
        Addresses addresses = addressService.GetDefaultAddress();
        return Result.success(addresses);
    }
}




















