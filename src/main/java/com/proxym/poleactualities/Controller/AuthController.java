package com.proxym.poleactualities.Controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.proxym.poleactualities.Models.*;
import com.proxym.poleactualities.Models.token.RefreshToken;
import com.proxym.poleactualities.Payload.request.TokenRefreshRequest;
import com.proxym.poleactualities.Payload.response.*;
import com.proxym.poleactualities.Payload.request.PasswordResetLinkRequest;
import com.proxym.poleactualities.Payload.request.PasswordResetRequest;
import com.proxym.poleactualities.Repository.UserRepository;
import com.proxym.poleactualities.Repository.RoleRepository;
import com.proxym.poleactualities.Security.JWT.JwtUtils;
import com.proxym.poleactualities.Security.Services.*;
import com.proxym.poleactualities.event.OnGenerateResetLinkEvent;
import com.proxym.poleactualities.event.OnUserAccountChangeEvent;
import com.proxym.poleactualities.exception.PasswordResetException;
import com.proxym.poleactualities.exception.PasswordResetLinkException;
import com.proxym.poleactualities.exception.TokenRefreshException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private PasswordResetTokenServiceImpl passwordResetToken;
    @Autowired
    JwtUtils jwtUtils;
    private static final Logger logger = Logger.getLogger(AuthController.class);
    @Autowired
     AuthService authService;
    @Autowired
    UserService userService;


    @Autowired
    JwtUtils tokenProvider;
    @Autowired

    RefreshTokenService refreshTokenService;
    @Autowired

    ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/getUserByEmail/{email}")
    public Optional<User> getUserByEmail(@PathVariable(value = "email") String email){

        return userService.findByEmail(email);
    }
    @PostMapping("/updateUser/{id}")
    public List<User> UpdateUser(@Valid @RequestBody UpdateRequest updateRequest, @PathVariable long id){
        User user =  userService.getUserById(id);
        Set<String> strRoles = updateRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userr = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userr);
        } else {
            strRoles.forEach(role -> {

                        Role admin = roleRepository.findByName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(admin);

            });
        }
        user.setRoles(roles);
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setHiringDate(updateRequest.getHiringDate());
        user.setBirthdate(updateRequest.getBirthdate());
        user.setLastName(updateRequest.getLastName());
        user.setFirstName(updateRequest.getFirstName());
        user.setEmail(updateRequest.getEmail());
        userService.updateUser(user,id);
        return userService.retrieveAllClients();

    }
    @PostMapping("/signin")

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJWTToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
    /**
     * Receives the reset link request and publishes an event to send email id containing
     * the reset link if the request is valid. In future the deeplink should open within
     * the app itself.
     */
    @PostMapping(value = "/password/resetlink" )
    @ApiOperation(value = "Receive the reset link request and publish event to send mail containing the password " +
            "reset link")
    public PasswordResetToken  resetLink(@ApiParam(value = "The PasswordResetLinkRequest payload") @Valid @RequestBody PasswordResetLinkRequest passwordResetLinkRequest) {

        return authService.generatePasswordResetToken(passwordResetLinkRequest)
                .map(passwordResetToken -> {
                    UriComponentsBuilder urlBuilder = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:4200/auth/reset-password");
                    OnGenerateResetLinkEvent generateResetLinkMailEvent = new OnGenerateResetLinkEvent(passwordResetToken,
                            urlBuilder);
                    applicationEventPublisher.publishEvent(generateResetLinkMailEvent);
                    return passwordResetToken ;

                })
                .orElseThrow(() -> new PasswordResetLinkException(passwordResetLinkRequest.getEmail(), "Couldn't create a valid token"));

    }

    /**
     * Receives a new passwordResetRequest and sends the acknowledgement after
     * changing the password to the user's mail through the event.
     */

    @PostMapping("/password/reset")
    @ApiOperation(value = "Reset the password after verification and publish an event to send the acknowledgement " +
            "email")
    public ResponseEntity resetPassword(@ApiParam(value = "The PasswordResetRequest payload") @Valid @RequestBody PasswordResetRequest passwordResetRequest) {

        return authService.resetPassword(passwordResetRequest)
                .map(changedUser -> {
                    OnUserAccountChangeEvent onPasswordChangeEvent = new OnUserAccountChangeEvent(changedUser, "Reset Password",
                            "Changed Successfully");
                    applicationEventPublisher.publishEvent(onPasswordChangeEvent);
                    return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
                })
                .orElseThrow(() -> new PasswordResetException(passwordResetRequest.getToken(), "Error in resetting password"));
    }
/****
 *
 */
@PostMapping("/register")
public List<User> register(@Valid @RequestBody RegisterRequest registerRequest) {
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new RuntimeException("Error: Username is already taken.");
    }
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
        throw new RuntimeException("Error : Email is already taken");
    }
    // Create new user's account
    User user = new User(registerRequest.getUsername(),
            registerRequest.getEmail(),
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getBirthdate(),
            registerRequest.getHiringDate(),
            registerRequest.getPhoneNumber(),
            encoder.encode(registerRequest.getPassword()));
    Set<String> strRoles = registerRequest.getRole();
    Set<Role> roles = new HashSet<>();
    if (strRoles == null) {
        Role devRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(devRole);
    } else {
        strRoles.forEach(role -> {


                    Role resppoleRole = roleRepository.findByName(RoleName.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(resppoleRole);


        });
    }

    user.setRoles(roles);
    userRepository.save(user);
    return userService.retrieveAllClients();
}
}
