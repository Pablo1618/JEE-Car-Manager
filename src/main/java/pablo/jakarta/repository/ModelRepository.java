package pablo.jakarta.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.enums.Brand;

import java.util.*;

@ApplicationScoped
public class ModelRepository {
    
    @PersistenceContext(unitName = "carManagerPU")
    private EntityManager entityManager;
    
    public List<Model> findAll() {
        return entityManager.createQuery("SELECT m FROM Model m", Model.class)
                .getResultList();
    }
    
    public Optional<Model> findById(UUID id) {
        Model model = entityManager.find(Model.class, id);
        return Optional.ofNullable(model);
    }
    
    public List<Model> findByBrand(Brand brand) {
        return entityManager.createQuery(
                "SELECT m FROM Model m WHERE m.brand = :brand", Model.class)
                .setParameter("brand", brand)
                .getResultList();
    }
    
    public Model save(Model model) {
        if (model.getId() == null) {
            model.setId(UUID.randomUUID());
            entityManager.persist(model);
            return model;
        } else {
            return entityManager.merge(model);
        }
    }
    
    public boolean existsById(UUID id) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(m) FROM Model m WHERE m.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
    
    public void deleteById(UUID id) {
        entityManager.createQuery("DELETE FROM Car c WHERE c.model.id = :modelId")
                .setParameter("modelId", id)
                .executeUpdate();
        
        findById(id).ifPresent(model -> {
            entityManager.remove(model);
        });
    }
}
