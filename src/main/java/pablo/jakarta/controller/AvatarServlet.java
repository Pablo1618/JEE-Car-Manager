package pablo.jakarta.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import pablo.jakarta.repository.AvatarRepository;
import pablo.jakarta.repository.UserRepository;
import pablo.jakarta.service.AvatarService;
import pablo.jakarta.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "AvatarServlet", urlPatterns = "/api/avatars/*", loadOnStartup = 2)
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class AvatarServlet extends HttpServlet {

    private AvatarService avatarService;

    @Override
    public void init() throws ServletException {
        super.init();

        avatarService = (AvatarService) getServletContext().getAttribute("avatarService");
        if (avatarService == null) {
            String dir = getServletContext().getInitParameter("avatars.directory");
            if (dir == null) {
                dir = System.getProperty("user.home") + "/.car-manager/avatars";
            }
            else {
                dir = dir.replace("${user.home}", System.getProperty("user.home"));
            }

            UserRepository userRepo = (UserRepository) getServletContext().getAttribute("userRepository");
            if (userRepo == null) {
                UserService userService = (UserService) getServletContext().getAttribute("userService");
                userRepo = new UserRepository();
                getServletContext().setAttribute("userRepository", userRepo);
            }

            avatarService = new AvatarService(new AvatarRepository(dir), userRepo);
            getServletContext().setAttribute("avatarService", avatarService);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UUID userId = getUserId(req);
        Optional<Path> path = avatarService.getAvatar(userId);

        if (path.isEmpty()) {
            sendError(res, HttpServletResponse.SC_NOT_FOUND, "Avatar not found");
            return;
        }

        res.setContentType("image/png");
        res.setContentLengthLong(Files.size(path.get()));
        try (InputStream in = Files.newInputStream(path.get());
             OutputStream out = res.getOutputStream()) {
            in.transferTo(out);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        UUID userId = getUserId(req);
        Part file = req.getPart("avatar");

        if (file == null) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, "No avatar file provided");
            return;
        }

        if (!"image/png".equals(file.getContentType())) {
            sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Only PNG images are supported");
            return;
        }

        boolean updated = avatarService.hasAvatar(userId);
        try (InputStream in = file.getInputStream()) {
            avatarService.saveAvatar(userId, in);
        }

        res.setStatus(updated ? HttpServletResponse.SC_OK : HttpServletResponse.SC_CREATED);
        res.setContentType("application/json");
        res.getWriter().write("{\"message\":\"Avatar " + (updated ? "updated" : "created") + " successfully\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UUID userId = getUserId(req);
        boolean deleted = avatarService.deleteAvatar(userId);

        if (!deleted)
            sendError(res, HttpServletResponse.SC_NOT_FOUND, "Avatar not found");
        else
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private UUID getUserId(HttpServletRequest req) {
        String path = Optional.ofNullable(req.getPathInfo()).orElse("").replaceAll("^/|/$", "");
        if (path.isEmpty()) throw new IllegalArgumentException("User ID is required");
        return UUID.fromString(path);
    }

    private void sendError(HttpServletResponse res, int status, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + msg + "\"}");
    }
}
