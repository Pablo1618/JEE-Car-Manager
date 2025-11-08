package pablo.jakarta.controller.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pablo.jakarta.model.Model;
import pablo.jakarta.service.ModelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
@Path("/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelRestController {

    private ModelService modelService;

    public ModelRestController() {
    }

    @Inject
    public ModelRestController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GET
    public Response getAllModels() {
        List<Model> models = modelService.getAllModels();
        return Response.ok(models).build();
    }

    @GET
    @Path("/{id}")
    public Response getModelById(@PathParam("id") UUID id) {
        Optional<Model> model = modelService.getModelById(id);
        
        if (model.isPresent()) {
            return Response.ok(model.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Model not found", "Model with id " + id + " does not exist"))
                    .build();
        }
    }

    @POST
    public Response createModel(Model model) {
        try {
            if (model.getId() == null) {
                model.setId(UUID.randomUUID());
            }
            
            Model createdModel = modelService.createModel(model);
            return Response.status(Response.Status.CREATED)
                    .entity(createdModel)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid model data", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to create model", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateModel(@PathParam("id") UUID id, Model model) {
        try {
            Optional<Model> updatedModel = modelService.updateModel(id, model);
            
            if (updatedModel.isPresent()) {
                return Response.ok(updatedModel.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Model not found", "Model with id " + id + " does not exist"))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid model data", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to update model", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteModel(@PathParam("id") UUID id) {
        try {
            boolean deleted = modelService.deleteModel(id);
            
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Model not found", "Model with id " + id + " does not exist"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to delete model", e.getMessage()))
                    .build();
        }
    }
}
