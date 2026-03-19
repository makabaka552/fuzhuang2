package auth.service;
import model.User;

public interface UserService {
    void register(User user);


    User findByUsername(String username);

    User login(String username, String password);

    User adminlogin(String username, String password);
}
