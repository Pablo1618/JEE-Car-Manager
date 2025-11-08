package pablo.jakarta.controller.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pablo.jakarta.model.Car;
import pablo.jakarta.model.Model;
import pablo.jakarta.service.CarService;
import pablo.jakarta.service.ModelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
@Path("/models/{modelId}/cars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarRestController {

    private CarService carService;
    private ModelService modelService;

    public CarRestController() {
    }

    @Inject
    public CarRestController(CarService carService, ModelService modelService) {
        this.carService = carService;
        this.modelService = modelService;
    }

    @GET
    public Response getAllCarsForModel(@PathParam("modelId") UUID modelId) {
        Optional<Model> model = modelService.getModelById(modelId);
        if (model.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Model not found", "Model with id " + modelId + " does not exist"))
                    .build();
        }

        List<Car> cars = carService.getCarsByModelId(modelId);
        return Response.ok(cars).build();
    }

    @GET
    @Path("/{carId}")
    public Response getCarById(@PathParam("modelId") UUID modelId, @PathParam("carId") UUID carId) {
        Optional<Model> model = modelService.getModelById(modelId);
        if (model.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Model not found", "Model with id " + modelId + " does not exist"))
                    .build();
        }

        Optional<Car> car = carService.getCarById(carId);
        
        if (car.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Car not found", "Car with id " + carId + " does not exist"))
                    .build();
        }

        if (!car.get().getModel().getId().equals(modelId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Car does not belong to this model", 
                            "Car " + carId + " belongs to a different model"))
                    .build();
        }

        return Response.ok(car.get()).build();
    }

    @POST
    public Response createCar(@PathParam("modelId") UUID modelId, Car car) {
        try {
            Optional<Model> modelOpt = modelService.getModelById(modelId);
            if (modelOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Model not found", "Model with id " + modelId + " does not exist"))
                        .build();
            }

            if (car.getId() == null) {
                car.setId(UUID.randomUUID());
            }

            Model model = new Model();
            model.setId(modelId);
            car.setModel(model);
            
            Car createdCar = carService.createCar(car);
            
            return Response.status(Response.Status.CREATED)
                    .entity(createdCar)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid car data", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to create car", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{carId}")
    public Response updateCar(@PathParam("modelId") UUID modelId, 
                              @PathParam("carId") UUID carId, 
                              Car car) {
        try {
            Optional<Model> modelOpt = modelService.getModelById(modelId);
            if (modelOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Model not found", "Model with id " + modelId + " does not exist"))
                        .build();
            }

            Optional<Car> existingCar = carService.getCarById(carId);
            if (existingCar.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Car not found", "Car with id " + carId + " does not exist"))
                        .build();
            }

            if (!existingCar.get().getModel().getId().equals(modelId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Car does not belong to this model", 
                                "Car " + carId + " does not belong to model " + modelId + ". Cannot update through this endpoint"))
                        .build();
            }

            Model model = new Model();
            model.setId(modelId);
            car.setModel(model);
            car.setId(carId);
            
            Optional<Car> updatedCar = carService.updateCar(carId, car);
            
            if (updatedCar.isPresent()) {
                return Response.ok(updatedCar.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Car not found", "Car with id " + carId + " does not exist"))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid car data", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to update car", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{carId}")
    public Response deleteCar(@PathParam("modelId") UUID modelId, @PathParam("carId") UUID carId) {
        try {
            Optional<Model> model = modelService.getModelById(modelId);
            if (model.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Model not found", "Model with id " + modelId + " does not exist"))
                        .build();
            }

            Optional<Car> car = carService.getCarById(carId);
            if (car.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Car not found", "Car with id " + carId + " does not exist"))
                        .build();
            }

            if (!car.get().getModel().getId().equals(modelId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Car does not belong to this model", 
                                "Car " + carId + " does not belong to model " + modelId + ". Cannot delete through this endpoint"))
                        .build();
            }

            boolean deleted = carService.deleteCar(carId);
            
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Car not found", "Car with id " + carId + " does not exist"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to delete car", e.getMessage()))
                    .build();
        }
    }
}
