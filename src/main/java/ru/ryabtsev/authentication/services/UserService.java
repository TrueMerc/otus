package ru.ryabtsev.authentication.services;

import java.util.List;
import java.util.Optional;
import ru.ryabtsev.authentication.entities.User;

public interface UserService {

    boolean validatesCredentials(String userName, String password);

    public Optional<User> getByUserName(String userName);

    List<User> getAllByNames(List<String> userName);

    void addAll(List<User> newUsers);
}
