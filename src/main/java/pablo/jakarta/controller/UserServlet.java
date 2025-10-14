package pablo.jakarta.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import pablo.jakarta.model.User;
import pablo.jakarta.repository.UserRepository;
import pablo.jakarta.service.UserService;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/users", "/api/users/*"}, loadOnStartup = 1)
public class UserServlet extends HttpServlet {

    private UserService userService;
    private Jsonb jsonb;

    @Override
    public void init() throws ServletException {
        super.init();

        jsonb = JsonbBuilder.create();

        userService = (UserService) getServletContext().getAttribute("userService");
        if (userService == null) {
            UserRepository repo = Optional.ofNullable(
                    (UserRepository) getServletContext().getAttribute("userRepository")
            ).orElseGet(() -> {
                UserRepository newRepo = new UserRepository();
                getServletContext().setAttribute("userRepository", newRepo);
                return newRepo;
            });

            userService = new UserService(repo);
            getServletContext().setAttribute("userService", userService);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);

        try {
            String id = extractId(req);
            if (id == null) {
                //GET api/users
                resp.getWriter().write(jsonb.toJson(userService.getAllUsers()));
            } else {
                //GET api/users/{ID}
                Optional<User> user = userService.getUserById(UUID.fromString(id));
                if (user.isPresent()) {
                    resp.getWriter().write(jsonb.toJson(user.get()));
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
            User user = jsonb.fromJson(req.getReader(), User.class);
            User created = userService.createUser(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(jsonb.toJson(created));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user data");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            String id = extractId(req);
            if (id == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "User ID required");
                return;
            }

            User updated = jsonb.fromJson(req.getReader(), User.class);
            Optional<User> result = userService.updateUser(UUID.fromString(id), updated);

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
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "User ID required");
                return;
            }

            boolean deleted = userService.deleteUser(UUID.fromString(id));
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
