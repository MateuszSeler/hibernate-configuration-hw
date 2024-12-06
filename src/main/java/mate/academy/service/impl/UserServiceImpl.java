package mate.academy.service.impl;

import mate.academy.dao.UserDao;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Inject
    private UserDao userDao;
    @Inject
    private ShoppingCartService shoppingCartService;

    @Override
    public User add(User user) {
        String currentPassword = user.getPassword();
        user.setSalt(HashUtil.getSalt());
        user.setPassword(HashUtil.hashPassword(user.getPassword(), user.getSalt()));
        if (user.getPassword().equals(currentPassword)) {
            throw new RegistrationException("password was not encrypted");
        }
        userDao.add(user);
        shoppingCartService.registerNewShoppingCart(user);

        return userDao.get(user.getEmail()).orElseThrow(
                () -> new RegistrationException(
                        "registration failed, " + user + " not in database"));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.get(email);
    }
}