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
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
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
                        getMessage("msg.success"), getMessage("msg.model_deleted")));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        getMessage("msg.error"), getMessage("msg.model_not_found")));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    getMessage("msg.error"), getMessage("msg.model_delete_failed", e.getMessage())));
        }
        return null;
    }

    private String getMessage(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        return bundle.getString(key);
    }

    private String getMessage(String key, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String msg = bundle.getString(key);
        return MessageFormat.format(msg, params);
    }
}
