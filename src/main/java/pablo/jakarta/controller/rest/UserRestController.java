package pablo.jakarta.controller.rest;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pablo.jakarta.model.User;
import pablo.jakarta.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRestController {
    
    @Inject
    private UserService userService;
    
    @GET
    public Response getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") UUID id) {
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                return Response.ok(user.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @POST
    public Response createUser(User user) {
        try {
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Login is required");
            }
            User created = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return createErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id, User user) {
        try {
            Optional<User> updated = userService.updateUser(id, user);
            if (updated.isPresent()) {
                return Response.ok(updated.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private Response createErrorResponse(Response.Status status, String message) {
        JsonObject error = Json.createObjectBuilder()
                .add("error", message)
                .build();
        return Response.status(status).entity(error).build();
    }
}
