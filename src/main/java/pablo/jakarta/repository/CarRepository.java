package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablo.jakarta.model.Car;

import java.util.*;

@ApplicationScoped
public class CarRepository {
    
    @PersistenceContext(unitName = "carManagerPU")
    private EntityManager entityManager;
    
    public List<Car> findAll() {
        return entityManager.createQuery("SELECT c FROM Car c", Car.class)
                .getResultList();
    }
    
    public Optional<Car> findById(UUID id) {
        Car car = entityManager.find(Car.class, id);
        return Optional.ofNullable(car);
    }
    
    public List<Car> findByModelId(UUID modelId) {
        return entityManager.createQuery(
                "SELECT c FROM Car c WHERE c.model.id = :modelId", Car.class)
                .setParameter("modelId", modelId)
                .getResultList();
    }
    
    public List<Car> findByOwnerId(UUID ownerId) {
        return entityManager.createQuery(
                "SELECT c FROM Car c WHERE c.owner.id = :ownerId", Car.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }
    
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
            entityManager.persist(car);
            return car;
        } else {
            return entityManager.merge(car);
        }
    }
    
    public boolean existsById(UUID id) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(c) FROM Car c WHERE c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
    
    public void deleteById(UUID id) {
        Car car = entityManager.find(Car.class, id);
        if (car != null) {
            entityManager.remove(car);
        }
    }
}
