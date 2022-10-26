package com.proxym.poleactualities.Security.Services;

import com.proxym.poleactualities.Repository.UserRepository;
import com.proxym.poleactualities.Models.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{
    @Autowired
    UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(UserService.class);


    @Autowired

   RefreshTokenService refreshTokenService;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public User saveUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepository.save(user);
    }
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUser(User user, long id) {

        User cl = userRepository.findById(id).get();
        if (user.getFirstName() != null) cl.setFirstName(user.getFirstName());
        if (user.getLastName() != null) cl.setLastName(user.getLastName());

        if (user.getEmail() != null)
            cl.setEmail(user.getEmail());

        if (user.getRoles() != null)

            cl.setRoles(user.getRoles());

        if (user.getAddress() != null)
            cl.setAddress(user.getAddress());

        if (user.getBirthdate() != null) cl.setBirthdate(user.getBirthdate());

        if (user.getHiringDate() != null) cl.setHiringDate(user.getHiringDate());

        if (user.getPhoneNumber() != 0) cl.setPhoneNumber(user.getPhoneNumber());


        userRepository.save(cl);
    }
    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> retrieveAllClients() {
        List<User> users = (List<User>) userRepository.findAll();

        return users;
    }
    /**
     * Finds a user in the database by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    /**
     * Check is the user exists given the email: naturalId
     */
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    /**
            * Check is the user exists given the username: naturalId
     */
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
