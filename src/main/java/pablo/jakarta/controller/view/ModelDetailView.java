package pablo.jakarta.controller.view;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import pablo.jakarta.model.Car;
import pablo.jakarta.model.Model;
import pablo.jakarta.service.CarService;
import pablo.jakarta.service.ModelService;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

@Named
@ViewScoped
public class ModelDetailView implements Serializable {

    @Inject
    private ModelService modelService;

    @Inject
    private CarService carService;

    @Getter
    @Setter
    private UUID modelId;

    @Getter
    private Model model;

    @Getter
    private List<Car> cars;

    public void init() {
        if (modelId != null) {
            Optional<Model> modelOpt = modelService.getModelById(modelId);
            if (modelOpt.isPresent()) {
                this.model = modelOpt.get();
                this.cars = carService.getCarsByModelId(modelId);
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                    .getFlash().put("error", getMessage("msg.model_not_found"));
            }
        }
    }

    public void deleteCar(UUID carId) {
        try {
            boolean deleted = carService.deleteCar(carId);
            if (deleted) {
                this.cars = carService.getCarsByModelId(modelId);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        getMessage("msg.success"), getMessage("msg.car_deleted")));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        getMessage("msg.error"), getMessage("msg.car_not_found")));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    getMessage("msg.error"), getMessage("msg.car_delete_failed", e.getMessage())));
        }
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
