package ru.ryabtsev.authentication.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.ryabtsev.authentication.entities.User;

public class SimpleUserService implements UserService {

    private static final int DEFAULT_CAPACITY = 100;

    private final List<User> users;

    public SimpleUserService() {
        users = new ArrayList<>(100);
    }

    @Override
    public boolean validatesCredentials(final String userName, final String password) {
        return getByUserName(userName)
                .map(User::password)
                .map(userPassword -> userPassword.equals(password))
                .orElse(false);
    }

    @Override
    public Optional<User> getByUserName(final String userName) {
        return users.stream().filter(user -> user.login().equals(userName)).findFirst();
    }

    @Override
    public List<User> getAllByNames(List<String> names) {
        return users.stream().filter(user -> names.contains(user.login())).toList();
    }

    @Override
    public void addAll(final List<User> newUsers) {
        users.addAll(newUsers);
    }
}
