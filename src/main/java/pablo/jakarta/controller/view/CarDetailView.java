package pablo.jakarta.controller.view;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import pablo.jakarta.model.Car;
import pablo.jakarta.service.CarService;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Named
@ViewScoped
public class CarDetailView implements Serializable {

    @Inject
    private CarService carService;

    @Getter
    @Setter
    private UUID carId;
    
    @Getter
    private Car car;

    public void init() {
        if (carId != null) {
            Optional<Car> carOpt = carService.getCarById(carId);
            if (carOpt.isPresent()) {
                this.car = carOpt.get();
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                    .getFlash().put("error", "Car not found");
            }
        }
    }
}
