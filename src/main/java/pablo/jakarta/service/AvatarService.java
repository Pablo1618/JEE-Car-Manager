package pablo.jakarta.service;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pablo.jakarta.repository.AvatarRepository;
import pablo.jakarta.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Stateless
@LocalBean
public class AvatarService {
    
    @Inject
    private AvatarRepository avatarRepository;
    
    @Inject
    private UserRepository userRepository;
    
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
