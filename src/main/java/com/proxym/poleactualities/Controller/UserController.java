package com.proxym.poleactualities.Controller;

import com.proxym.poleactualities.Payload.UserProfile;
import com.proxym.poleactualities.Repository.UserRepository;
import com.proxym.poleactualities.Security.Services.AuthService;
import com.proxym.poleactualities.Security.Services.UserDetailsImpl;
import com.proxym.poleactualities.Security.Services.UserService;
import com.proxym.poleactualities.Models.User;
import com.proxym.poleactualities.annotation.CurrentUser;
import com.proxym.poleactualities.exception.ResourceNotFoundException;
import com.proxym.poleactualities.util.AppConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Api(tags = "User Manager")
@RequestMapping("/User")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserRepository userRepository;




    // http://localhost:8089/Proxym/User/getUser
    @GetMapping("/getUser")
    @ApiOperation(value = "Get All Users ")
    @ResponseBody
    public List<User> getUsers() {
        List<User> listUsers = userService.retrieveAllClients();
        return listUsers;
    }
    @PostMapping("/updateUser/{id}")
    public User UpdateUser(@RequestBody User user, @PathVariable long id){
        userService.updateUser(user,id);
        return userService.getUserById(id);

    }
    @GetMapping("/me")
    public User getCurrentUser(@CurrentUser UserDetailsImpl currentUser) {
        User user = new User(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());
        return user;
    }


    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize(" hasRole('ROLE_DEVELOPER') or hasRole('ROLE_PROJECT_MANAGER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_RESPPOLE')")
    public String userAccess() {
        return "User Content.";
    }
    @GetMapping("/mod")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }
    @GetMapping("/responsable")
    @PreAuthorize("hasRole('ROLE_RESPPOLE')")
    public String responsableAccess() {
        return "Admin Board.";
    }

    @DeleteMapping("/removeUser/{user-id}")
    @ResponseBody
    public List<User> removeUser(@PathVariable("user-id") Long UserId){
        userService.deleteUser(UserId);
        return userService.retrieveAllClients();
    }
    @GetMapping("/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));




        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getFirstName(),user.getLastName(), user.getCreatedAt(), user.getAddress(),user.getBirthdate(),user.getHiringDate(),user.getPhoneNumber(), user.getRoles());

        return userProfile;
    }




}
