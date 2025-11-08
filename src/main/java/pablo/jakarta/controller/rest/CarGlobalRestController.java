package pablo.jakarta.controller.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pablo.jakarta.model.Car;
import pablo.jakarta.service.CarService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
@Path("/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarGlobalRestController {

    private CarService carService;

    public CarGlobalRestController() {
    }

    @Inject
    public CarGlobalRestController(CarService carService) {
        this.carService = carService;
    }

    @GET
    public Response getAllCars() {
        List<Car> cars = carService.getAllCars();
        return Response.ok(cars).build();
    }
}
