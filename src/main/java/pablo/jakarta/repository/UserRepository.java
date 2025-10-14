package pablo.jakarta.repository;

import pablo.jakarta.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    
    public UserRepository() {
        initializeTestUsers();
    }
    
    private void initializeTestUsers() {
        User user1 = new User("user1", 25, LocalDate.of(2024, 5, 15));
        User user2 = new User("user2", 30, LocalDate.of(2015, 8, 22));
        User user3 = new User("user3", 28, LocalDate.of(2016, 12, 10));
        User user4 = new User("user4", 35, LocalDate.of(2012, 3, 5));
        
        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);
        users.put(user3.getId(), user3);
        users.put(user4.getId(), user4);
    }
    
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