package ru.ryabtsev.authentication.services;

public interface UserService {

    boolean validatesCredentials(String userName, String password);

    public Long getIdByUserName(String userName);
}
