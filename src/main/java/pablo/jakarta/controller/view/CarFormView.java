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
import pablo.jakarta.model.User;
import pablo.jakarta.service.CarService;
import pablo.jakarta.service.ModelService;
import pablo.jakarta.service.UserService;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named
@ViewScoped
public class CarFormView implements Serializable {

    @Inject
    private CarService carService;

    @Inject
    private ModelService modelService;

    @Inject
    private UserService userService;

    @Getter
    @Setter
    private UUID carId;

    @Getter
    @Setter
    private UUID modelId;

    @Getter
    @Setter
    private Car car = new Car();

    @Getter
    private boolean editMode = false;

    public void init() {
        if (carId != null) {
            // Edit car
            Optional<Car> carOpt = carService.getCarById(carId);
            if (carOpt.isPresent()) {
                this.car = carOpt.get();
                this.editMode = true;
                if (this.car.getModel() != null) {
                    this.modelId = this.car.getModel().getId();
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Car not found"));
            }
        } else {
            // Add car
            this.car = new Car();
            this.editMode = false;
            
            if (modelId != null) {
                Optional<Model> modelOpt = modelService.getModelById(modelId);
                modelOpt.ifPresent(model -> this.car.setModel(model));
            }
        }
    }

    public String saveCar() {
        try {
            if (editMode) {
                // Update car
                Optional<Car> updated = carService.updateCar(carId, car);
                if (updated.isPresent()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                            "Success", "Car updated successfully"));
                    
                    return "/car/car_detail.xhtml?faces-redirect=true&carId=" + carId;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Error", "Failed to update car"));
                    return null;
                }
            } else {
                // Create new car
                Car created = carService.createCar(car);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Success", "Car created successfully"));
                
                if (car.getModel() != null && car.getModel().getId() != null) {
                    return "/model/model_detail.xhtml?faces-redirect=true&modelId=" + car.getModel().getId();
                }
                
                return "/car/car_detail.xhtml?faces-redirect=true&carId=" + created.getId();
            }
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", e.getMessage()));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "An unexpected error occurred: " + e.getMessage()));
            return null;
        }
    }

    public String cancel() {
        if (editMode && carId != null) {
            return "/car/car_detail.xhtml?faces-redirect=true&carId=" + carId;
        } else if (modelId != null) {
            return "/model/model_detail.xhtml?faces-redirect=true&modelId=" + modelId;
        }
        return "/model/model_list.xhtml?faces-redirect=true";
    }


    public List<Model> getAvailableModels() {
        return modelService.getAllModels();
    }

    public List<User> getAvailableUsers() {
        return userService.getAllUsers();
    }

    public String getPageTitle() {
        return editMode ? "Edit Car" : "Add New Car";
    }

    public String getSubmitButtonText() {
        return editMode ? "Update Car" : "Create Car";
    }


}
