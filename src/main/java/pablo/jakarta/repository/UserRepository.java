package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablo.jakarta.model.User;

import java.util.*;

@ApplicationScoped
public class UserRepository {
    
    @PersistenceContext(unitName = "carManagerPU")
    private EntityManager entityManager;
    
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }
    
    public Optional<User> findById(UUID id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }
    
    public boolean existsById(UUID id) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
    
    public void deleteById(UUID id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}