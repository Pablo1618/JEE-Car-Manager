package pablo.jakarta.service;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pablo.jakarta.model.User;
import pablo.jakarta.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
@LocalBean
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> updateUser(UUID id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            updatedUser.setId(id);
            return Optional.of(userRepository.save(updatedUser));
        }
        return Optional.empty();
    }
    
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}