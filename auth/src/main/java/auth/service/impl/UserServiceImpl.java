package auth.service.impl;

import auth.mapper.UserMapper;
import auth.service.UserService;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public User findByUsername(String username) {
        return (User) userMapper.findUserByName(username);
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.findUserByPhone(phone);
    }

    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public User loginByPhone(String phone, String password) {
        return userMapper.loginByPhone(phone, password);
    }

    @Override
    public User adminlogin(String username, String password) {
        User admin = userMapper.login(username,password);
        if(!password.equals(admin.getPassword())){
            throw new RuntimeException("用户名或者密码出错");
        }

        if (!"admin".equals(admin.getRole())) {
            System.out.println("用户身份不是管理员" + admin.getRole());
            throw new RuntimeException("该用户不是管理员");
        }
        return admin;
    }

    @Override
    public void updatePasswordByPhone(String phone, String password) {
        userMapper.updatePasswordByPhone(phone, password);
    }
}
