package pablo.jakarta.controller;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pablo.jakarta.model.Car;
import pablo.jakarta.service.CarService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "CarServlet", urlPatterns = {"/api/cars", "/api/cars/*"})
public class CarServlet extends HttpServlet {

    @Inject
    private CarService carService;
    
    private Jsonb jsonb;

    @Override
    public void init() throws ServletException {
        super.init();
        jsonb = JsonbBuilder.create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);

        try {
            String id = extractId(req);
            String modelIdParam = req.getParameter("modelId");
            String ownerIdParam = req.getParameter("ownerId");
            
            if (id == null && modelIdParam == null && ownerIdParam == null) {
                // GET /api/cars
                resp.getWriter().write(jsonb.toJson(carService.getAllCars()));
            } else if (id == null && modelIdParam != null) {
                // GET /api/cars?modelId=ID
                List<Car> cars = carService.getCarsByModelId(UUID.fromString(modelIdParam));
                resp.getWriter().write(jsonb.toJson(cars));
            } else if (id == null && ownerIdParam != null) {
                // GET /api/cars?ownerId=ID
                List<Car> cars = carService.getCarsByOwnerId(UUID.fromString(ownerIdParam));
                resp.getWriter().write(jsonb.toJson(cars));
            } else {
                // GET /api/cars/ID
                Optional<Car> car = carService.getCarById(UUID.fromString(id));
                if (car.isPresent()) {
                    resp.getWriter().write(jsonb.toJson(car.get()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            Car car = jsonb.fromJson(req.getReader(), Car.class);
            Car created = carService.createCar(car);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonb.toJson(created));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid car data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            String id = extractId(req);
            if (id == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Car ID required");
                return;
            }

            Car updated = jsonb.fromJson(req.getReader(), Car.class);
            Optional<Car> result = carService.updateCar(UUID.fromString(id), updated);

            if (result.isPresent()) {
                resp.getWriter().write(jsonb.toJson(result.get()));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            String id = extractId(req);
            if (id == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Car ID required");
                return;
            }

            boolean deleted = carService.deleteCar(UUID.fromString(id));
            resp.setStatus(deleted ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void setupJson(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(jsonb.toJson(Map.of("error", message)));
    }

    private String extractId(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/") || path.isEmpty()) return null;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
