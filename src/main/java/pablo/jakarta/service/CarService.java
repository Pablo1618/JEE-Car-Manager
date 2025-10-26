package pablo.jakarta.service;

import jakarta.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class CarService {
    
    private CarRepository carRepository;
    private ModelRepository modelRepository;
    private UserRepository userRepository;
    
    public CarService() {
    }
    
    @Inject
    public CarService(CarRepository carRepository, ModelRepository modelRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
    }
    
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

        User owner = null;
        if (car.getOwner() != null && car.getOwner().getId() != null) {
            owner = userRepository.findById(car.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            car.setOwner(owner);
        }

        if (car.getLicensePlate() == null || car.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("License plate is required");
        }

        Car savedCar = carRepository.save(car);

        if (model.getCars() != null && !model.getCars().contains(savedCar)) {
            model.getCars().add(savedCar);
            modelRepository.save(model);
        }

        if (owner != null && owner.getCars() != null && !owner.getCars().contains(savedCar)) {
            owner.getCars().add(savedCar);
            userRepository.save(owner);
        }
        
        return savedCar;
    }
    
    public Optional<Car> updateCar(UUID id, Car updatedCar) {
        Optional<Car> existingCar = carRepository.findById(id);
        if (existingCar.isEmpty()) {
            return Optional.empty();
        }
        
        Car oldCar = existingCar.get();
        Model oldModel = oldCar.getModel();
        User oldOwner = oldCar.getOwner();
        
        updatedCar.setId(id);

        Model newModel;
        if (updatedCar.getModel() != null && updatedCar.getModel().getId() != null) {
            newModel = modelRepository.findById(updatedCar.getModel().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found"));
            updatedCar.setModel(newModel);
        } else {
            newModel = oldModel;
            updatedCar.setModel(newModel);
        }

        User newOwner;
        if (updatedCar.getOwner() != null && updatedCar.getOwner().getId() != null) {
            newOwner = userRepository.findById(updatedCar.getOwner().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
            updatedCar.setOwner(newOwner);
        } else {
            newOwner = oldOwner;
            updatedCar.setOwner(newOwner);
        }
        
        Car savedCar = carRepository.save(updatedCar);

        // Checking if model changed
        if (oldModel != null && !oldModel.getId().equals(newModel.getId())) {
            if (oldModel.getCars() != null) {
                oldModel.getCars().removeIf(c -> c.getId().equals(id));
                modelRepository.save(oldModel);
            }

            if (newModel.getCars() != null && !newModel.getCars().contains(savedCar)) {
                newModel.getCars().add(savedCar);
                modelRepository.save(newModel);
            }
        }
        
        // Checking if owner changed
        if (oldOwner != null && newOwner != null && !oldOwner.getId().equals(newOwner.getId())) {
            if (oldOwner.getCars() != null) {
                oldOwner.getCars().removeIf(c -> c.getId().equals(id));
                userRepository.save(oldOwner);
            }

            if (newOwner.getCars() != null && !newOwner.getCars().contains(savedCar)) {
                newOwner.getCars().add(savedCar);
                userRepository.save(newOwner);
            }
        } else if (oldOwner == null && newOwner != null) {
            if (newOwner.getCars() != null && !newOwner.getCars().contains(savedCar)) {
                newOwner.getCars().add(savedCar);
                userRepository.save(newOwner);
            }
        }
        
        return Optional.of(savedCar);
    }
    
    public boolean deleteCar(UUID id) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isEmpty()) {
            return false;
        }
        
        Car carToDelete = car.get();

        Model model = carToDelete.getModel();
        if (model != null && model.getCars() != null) {
            model.getCars().removeIf(c -> c.getId().equals(id));
            modelRepository.save(model);
        }

        User owner = carToDelete.getOwner();
        if (owner != null && owner.getCars() != null) {
            owner.getCars().removeIf(c -> c.getId().equals(id));
            userRepository.save(owner);
        }
        
        carRepository.deleteById(id);
        return true;
    }
}
