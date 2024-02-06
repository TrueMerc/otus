package ru.ryabtsev.authentication.services;

import java.util.ArrayList;
import java.util.List;
import ru.ryabtsev.authentication.entities.User;

public class SimpleUserService implements UserService {

    private static final int DEFAULT_CAPACITY = 100;

    private final List<User> users;

    public SimpleUserService() {
        users = new ArrayList<>(100);
    }

    @Override
    public boolean validatesCredentials(String userName, String password) {
        return false;
    }

    @Override
    public Long getIdByUserName(String userName) {
        return null;
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
