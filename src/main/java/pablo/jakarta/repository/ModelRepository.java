package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.enums.Brand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ModelRepository {
    
    private final Map<UUID, Model> models = new ConcurrentHashMap<>();
    
    public List<Model> findAll() {
        return new ArrayList<>(models.values());
    }
    
    public Optional<Model> findById(UUID id) {
        return Optional.ofNullable(models.get(id));
    }
    
    public List<Model> findByBrand(Brand brand) {
        return models.values().stream()
                .filter(model -> model.getBrand() == brand)
                .toList();
    }
    
    public Model save(Model model) {
        if (model.getId() == null) {
            model.setId(UUID.randomUUID());
        }
        if (model.getCars() == null) {
            model.setCars(new ArrayList<>());
        }
        models.put(model.getId(), model);
        return model;
    }
    
    public boolean existsById(UUID id) {
        return models.containsKey(id);
    }
    
    public void deleteById(UUID id) {
        models.remove(id);
    }
}
