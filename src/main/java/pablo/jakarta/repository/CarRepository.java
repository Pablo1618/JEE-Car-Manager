package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import pablo.jakarta.model.Car;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CarRepository {
    
    private final Map<UUID, Car> cars = new ConcurrentHashMap<>();
    
    public List<Car> findAll() {
        return new ArrayList<>(cars.values());
    }
    
    public Optional<Car> findById(UUID id) {
        return Optional.ofNullable(cars.get(id));
    }
    
    public List<Car> findByModelId(UUID modelId) {
        return cars.values().stream()
                .filter(car -> car.getModel() != null && car.getModel().getId().equals(modelId))
                .toList();
    }
    
    public List<Car> findByOwnerId(UUID ownerId) {
        return cars.values().stream()
                .filter(car -> car.getOwner() != null && car.getOwner().getId().equals(ownerId))
                .toList();
    }
    
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        cars.put(car.getId(), car);
        return car;
    }
    
    public boolean existsById(UUID id) {
        return cars.containsKey(id);
    }
    
    public void deleteById(UUID id) {
        cars.remove(id);
    }
}
