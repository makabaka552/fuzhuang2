package user.service;
import model.PageBeam;
import model.User;

public interface UserService {
    User FindUser(String username);

    PageBeam GetUserList(Integer page, Integer pageSize);

    void createUser(User user);

    void updateUser(User user, Integer userId);

    void deleteUser(Integer userid);
}
