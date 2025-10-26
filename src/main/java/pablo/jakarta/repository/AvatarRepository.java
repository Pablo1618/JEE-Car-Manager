package pablo.jakarta.repository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AvatarRepository {
    
    private Path avatarsDirectory;
    
    @PostConstruct
    public void init() {
        String dir = System.getProperty("avatars.directory");
        if (dir == null) {
            dir = System.getProperty("user.home") + "/car-manager/avatars";
        } else {
            dir = dir.replace("${user.home}", System.getProperty("user.home"));
        }
        this.avatarsDirectory = Paths.get(dir);
        initializeDirectory();
    }
    
    private void initializeDirectory() {
        try {
            if (!Files.exists(avatarsDirectory)) {
                Files.createDirectories(avatarsDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create avatars directory: " + avatarsDirectory, e);
        }
    }
    
    public void saveAvatar(UUID userId, InputStream inputStream) throws IOException {
        Path avatarPath = getAvatarPath(userId);
        Files.copy(inputStream, avatarPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public Optional<Path> getAvatar(UUID userId) {
        Path avatarPath = getAvatarPath(userId);
        return Files.exists(avatarPath) ? Optional.of(avatarPath) : Optional.empty();
    }
    
    public boolean hasAvatar(UUID userId) {
        return Files.exists(getAvatarPath(userId));
    }
    
    public boolean deleteAvatar(UUID userId) throws IOException {
        return Files.deleteIfExists(getAvatarPath(userId));
    }

    private Path getAvatarPath(UUID userId) {
        return avatarsDirectory.resolve(userId.toString() + ".png");
    }
}
