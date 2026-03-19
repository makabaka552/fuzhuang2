package user.service.serviceimpl;

import com.github.pagehelper.PageHelper;
import model.PageBeam;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;
import user.mapper.UserMapper;
import user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User FindUser(String username) {
        return userMapper.FindUser(username);
    }

    @Override
    public PageBeam GetUserList(Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        Page<User> p1 = userMapper.userList();
        PageBeam pageBeam = new PageBeam();
        pageBeam.setTotal(p1.getTotal());
        pageBeam.setRows(p1.getResult());
        return pageBeam;


    }

    @Override
    public void createUser(User user) {
        userMapper.createUser(user);
    }

    @Override
    public void updateUser(User user, Integer userId) {
        userMapper.update(user.getUsername(),user.getRole(),user.getPhone(),userId);

    }

    @Override
    public void deleteUser(Integer userid) {
        userMapper.deleteUser(userid);
    }


}
