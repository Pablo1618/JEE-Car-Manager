package pablo.jakarta.service;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pablo.jakarta.model.Car;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.User;
import pablo.jakarta.repository.CarRepository;
import pablo.jakarta.repository.ModelRepository;
import pablo.jakarta.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
@LocalBean
public class CarService {
    
    @Inject
    private CarRepository carRepository;
    
    @Inject
    private ModelRepository modelRepository;
    
    @Inject
    private UserRepository userRepository;
    
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
    
    public Optional<Car> getCarById(UUID id) {
        return carRepository.findById(id);
    }
    
    public List<Car> getCarsByModelId(UUID modelId) {
        return carRepository.findByModelId(modelId);
    }
    
    public List<Car> getCarsByOwnerId(UUID ownerId) {
        return carRepository.findByOwnerId(ownerId);
    }
    
    public Car createCar(Car car) {

        if (car.getModel() == null || car.getModel().getId() == null) {
            throw new IllegalArgumentException("Model is required");
        }
        Model model = modelRepository.findById(car.getModel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        car.setModel(model);

        if (car.getOwner() != null && car.getOwner().getId() != null) {
            User owner = userRepository.findById(car.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            car.setOwner(owner);
        }

        if (car.getLicensePlate() == null || car.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("License plate is required");
        }

        return carRepository.save(car);
    }
    
    public Optional<Car> updateCar(UUID id, Car updatedCar) {
        Optional<Car> existingCar = carRepository.findById(id);
        if (existingCar.isEmpty()) {
            return Optional.empty();
        }
        
        updatedCar.setId(id);

        if (updatedCar.getModel() != null && updatedCar.getModel().getId() != null) {
            Model newModel = modelRepository.findById(updatedCar.getModel().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found"));
            updatedCar.setModel(newModel);
        } else {
            updatedCar.setModel(existingCar.get().getModel());
        }

        if (updatedCar.getOwner() != null && updatedCar.getOwner().getId() != null) {
            User newOwner = userRepository.findById(updatedCar.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            updatedCar.setOwner(newOwner);
        } else {
            updatedCar.setOwner(existingCar.get().getOwner());
        }
        
        return Optional.of(carRepository.save(updatedCar));
    }
    
    public boolean deleteCar(UUID id) {
        if (!carRepository.existsById(id)) {
            return false;
        }
        
        carRepository.deleteById(id);
        return true;
    }
}
