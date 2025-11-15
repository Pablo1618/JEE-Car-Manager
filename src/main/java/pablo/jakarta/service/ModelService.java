package pablo.jakarta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
    
    @Transactional
    public Model createModel(Model model) {
        if (model.getBrand() == null) {
            throw new IllegalArgumentException("Brand is required");
        }
        if (model.getName() == null || model.getName().isBlank()) {
            throw new IllegalArgumentException("Model name is required");
        }
        return modelRepository.save(model);
    }
    
    @Transactional
    public Optional<Model> updateModel(UUID id, Model updatedModel) {
        Optional<Model> existingModel = modelRepository.findById(id);
        if (existingModel.isPresent()) {
            updatedModel.setId(id);
            return Optional.of(modelRepository.save(updatedModel));
        }
        return Optional.empty();
    }
    
    @Transactional
    public boolean deleteModel(UUID id) {
        if (!modelRepository.existsById(id)) {
            return false;
        }
        
        modelRepository.deleteById(id);
        return true;
    }
}
