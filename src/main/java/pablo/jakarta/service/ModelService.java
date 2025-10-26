package pablo.jakarta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pablo.jakarta.model.Car;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.enums.Brand;
import pablo.jakarta.repository.CarRepository;
import pablo.jakarta.repository.ModelRepository;
import pablo.jakarta.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ModelService {
    
    private ModelRepository modelRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    
    public ModelService() {
    }
    
    @Inject
    public ModelService(ModelRepository modelRepository, CarRepository carRepository, UserRepository userRepository) {
        this.modelRepository = modelRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }
    
    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }
    
    public Optional<Model> getModelById(UUID id) {
        return modelRepository.findById(id);
    }
    
    public List<Model> getModelsByBrand(Brand brand) {
        return modelRepository.findByBrand(brand);
    }
    
    public Model createModel(Model model) {
        if (model.getBrand() == null) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (model.getName() == null || model.getName().isBlank()) {
            throw new IllegalArgumentException("Model name is required");
        }
        return modelRepository.save(model);
    }
    
    public Optional<Model> updateModel(UUID id, Model updatedModel) {
        Optional<Model> existingModel = modelRepository.findById(id);
        if (existingModel.isPresent()) {
            updatedModel.setId(id);
            if (updatedModel.getCars() == null) {
                updatedModel.setCars(existingModel.get().getCars());
            }
            return Optional.of(modelRepository.save(updatedModel));
        }
        return Optional.empty();
    }
    
    public boolean deleteModel(UUID id) {
        if (!modelRepository.existsById(id)) {
            return false;
        }

        Optional<Model> model = modelRepository.findById(id);
        if (model.isPresent()) {
            // Remove cars of this model
            List<Car> carsToDelete = carRepository.findByModelId(id);
            for (Car car : carsToDelete) {
                // Also remove car from owner list
                if (car.getOwner() != null && car.getOwner().getCars() != null) {
                    car.getOwner().getCars().removeIf(c -> c.getId().equals(car.getId()));
                    userRepository.save(car.getOwner());
                }

                carRepository.deleteById(car.getId());
            }
        }

        modelRepository.deleteById(id);
        return true;
    }
}
