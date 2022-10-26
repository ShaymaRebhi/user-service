package com.proxym.poleactualities.Security.Services;

import com.proxym.poleactualities.Models.User;

import java.util.List;

public interface IUserService {
    void updateUser(User user, long id);

    User getUserById(long id);

    List<User> retrieveAllClients();

    void deleteUser(Long id);
}
