package pablo.jakarta.controller.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pablo.jakarta.model.User;
import pablo.jakarta.service.UserService;

import java.util.UUID;

@Named
@ApplicationScoped
@FacesConverter(value = "userConverter", managed = true)
public class UserConverter implements Converter<User> {

    @Inject
    private UserService userService;

    @Override
    public User getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            UUID userId = UUID.fromString(value.trim());
            return userService.getUserById(userId).orElse(null);
        } catch (IllegalArgumentException e) {
            throw new ConverterException("Invalid user ID format: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, User value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
