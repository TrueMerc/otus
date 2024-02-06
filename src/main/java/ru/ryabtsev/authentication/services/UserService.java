package ru.ryabtsev.authentication.services;

import java.util.List;
import ru.ryabtsev.authentication.entities.User;

public interface UserService {

    boolean validatesCredentials(String userName, String password);

    public Long getIdByUserName(String userName);

    List<User> getAllByNames(List<String> userName);

    void addAll(List<User> newUsers);
}
