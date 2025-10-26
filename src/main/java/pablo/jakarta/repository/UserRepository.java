package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import pablo.jakarta.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserRepository {
    
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    public boolean existsById(UUID id) {
        return users.containsKey(id);
    }
    
    public void deleteById(UUID id) {
        users.remove(id);
    }

}