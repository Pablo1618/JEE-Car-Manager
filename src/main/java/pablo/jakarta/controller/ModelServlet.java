package pablo.jakarta.controller;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.enums.Brand;
import pablo.jakarta.service.ModelService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "ModelServlet", urlPatterns = {"/api/models", "/api/models/*"})
public class ModelServlet extends HttpServlet {

    @Inject
    private ModelService modelService;
    
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
            String brandParam = req.getParameter("brand");
            
            if (id == null && brandParam == null) {
                // GET /api/models
                resp.getWriter().write(jsonb.toJson(modelService.getAllModels()));
            } else if (id == null && brandParam != null) {
                // GET /api/models?brand=TOYOTA
                try {
                    Brand brand = Brand.valueOf(brandParam.toUpperCase());
                    List<Model> models = modelService.getModelsByBrand(brand);
                    resp.getWriter().write(jsonb.toJson(models));
                } catch (IllegalArgumentException e) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid brand: " + brandParam);
                }
            } else {
                // GET /api/models/ID
                Optional<Model> model = modelService.getModelById(UUID.fromString(id));
                if (model.isPresent()) {
                    resp.getWriter().write(jsonb.toJson(model.get()));
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
            Model model = jsonb.fromJson(req.getReader(), Model.class);
            Model created = modelService.createModel(model);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonb.toJson(created));
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid model data");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            String id = extractId(req);
            if (id == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Model ID required");
                return;
            }

            Model updated = jsonb.fromJson(req.getReader(), Model.class);
            Optional<Model> result = modelService.updateModel(UUID.fromString(id), updated);

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
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Model ID required");
                return;
            }

            boolean deleted = modelService.deleteModel(UUID.fromString(id));
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
