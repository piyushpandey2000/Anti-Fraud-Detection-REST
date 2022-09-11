package antifraud.service;

import antifraud.models.ERole;
import antifraud.models.User;
import antifraud.repository.UserRepository;
import antifraud.request.EditRoleReq;
import antifraud.request.SetAccessReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<?> registerUser(User user) {
        logger.info("Processing values before saving..");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(userRepository.count()==0) {
            user.setRole(ERole.ADMINISTRATOR);
            user.setAccountNonLocked(true);
        }

        if(userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            logger.error("User already exists! {}", user);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            logger.info("Saving user: {}", user);
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public ResponseEntity<?> deleteUserByUsername(String username) {
        if(userRepository.existsByUsernameIgnoreCase(username)) {
            userRepository.deleteByUsernameIgnoreCase(username);
            return new ResponseEntity<>(Map.of("username", username,
                    "status", "Deleted successfully!"), HttpStatus.OK);
        }

        logger.error("User not found");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> editRole(EditRoleReq request) {
        Optional<User> user = userRepository.findUserByUsernameIgnoreCase(request.getUsername());

        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(request.getRole()==ERole.ADMINISTRATOR) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (request.getRole() == user.get().getRole()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        user.get().setRole(request.getRole());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> setAccess(SetAccessReq request) {
        Optional<User> userOptional = userRepository.findUserByUsernameIgnoreCase(request.getUsername());

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        if(user.getRole() == ERole.ADMINISTRATOR && request.getOperation()== SetAccessReq.Operation.LOCK) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.setAccountNonLocked(request.getOperation()== SetAccessReq.Operation.UNLOCK);
        return new ResponseEntity<>(Map.of("status", String.format("User %s %s!", user.getUsername(), user.isAccountNonLocked()?"unlocked":"locked")), HttpStatus.OK);
    }
}
