package antifraud.controller;

import antifraud.models.User;
import antifraud.request.EditRoleReq;
import antifraud.request.SetAccessReq;
import antifraud.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User user) {
        logger.info("Registering user: {}", user);
        return authService.registerUser(user);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listUsers() {
        logger.info("Listing all users");
        return new ResponseEntity<>(authService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable @Valid String username) {
        logger.info("Deleting user with username: {}", username);
        return authService.deleteUserByUsername(username);
    }

    @PutMapping("/role")
    public ResponseEntity<?> editRole(@RequestBody @Valid EditRoleReq request) {
        logger.info("Processing editRole req for username: {}, newRole: {}", request.getUsername(), request.getRole());
        return authService.editRole(request);
    }

    @PostMapping("/access")
    public ResponseEntity<?> setAccess(@RequestBody @Valid SetAccessReq request) {
        logger.info("Processing access request for username: {} to status: {}", request.getUsername(), request.getOperation());
        return authService.setAccess(request);
    }
}
