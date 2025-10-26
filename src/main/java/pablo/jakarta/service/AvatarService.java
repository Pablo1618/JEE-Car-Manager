package pablo.jakarta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pablo.jakarta.repository.AvatarRepository;
import pablo.jakarta.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AvatarService {
    
    private AvatarRepository avatarRepository;
    private UserRepository userRepository;
    
    public AvatarService() {
    }
    
    @Inject
    public AvatarService(AvatarRepository avatarRepository, UserRepository userRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
    }
    
    public void saveAvatar(UUID userId, InputStream inputStream) throws IOException {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        avatarRepository.saveAvatar(userId, inputStream);
    }
    
    public Optional<Path> getAvatar(UUID userId) {
        return userRepository.existsById(userId) 
            ? avatarRepository.getAvatar(userId) 
            : Optional.empty();
    }
    
    public boolean hasAvatar(UUID userId) {
        return userRepository.existsById(userId) && avatarRepository.hasAvatar(userId);
    }
    
    public boolean deleteAvatar(UUID userId) throws IOException {
        return userRepository.existsById(userId) && avatarRepository.deleteAvatar(userId);
    }
}
