package user.service;
import model.PageBeam;
import model.User;

public interface UserService {
    User FindUser(String username);

    User findById(Long id);

    PageBeam GetUserList(Integer page, Integer pageSize);

    void createUser(User user);

    void updateUser(User user, Integer userId);

    void deleteUser(Integer userid);
}
