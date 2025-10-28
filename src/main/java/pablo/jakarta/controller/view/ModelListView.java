package pablo.jakarta.controller.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pablo.jakarta.model.Model;
import pablo.jakarta.service.CarService;
import pablo.jakarta.service.ModelService;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@RequestScoped
public class ModelListView implements Serializable {

    @Inject
    private ModelService modelService;

    @Inject
    private CarService carService;

    public List<Model> getModels() {
        return modelService.getAllModels();
    }

    public int getCarCountForModel(UUID modelId) {
        return carService.getCarsByModelId(modelId).size();
    }

    public String deleteModel(UUID modelId) {
        try {
            boolean deleted = modelService.deleteModel(modelId);
            if (deleted) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Success", "Model deleted successfully"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Error", "Model not found"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "Failed to delete model: " + e.getMessage()));
        }
        return null;
    }
}
