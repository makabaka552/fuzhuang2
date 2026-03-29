package auth.service;
import model.User;

public interface UserService {
    void register(User user);

    User findByUsername(String username);

    User findByPhone(String phone);

    User login(String username, String password);

    User loginByPhone(String phone, String password);

    User adminlogin(String username, String password);

    void updatePasswordByPhone(String phone, String password);
}
