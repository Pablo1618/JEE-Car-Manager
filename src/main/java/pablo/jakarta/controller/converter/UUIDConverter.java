package pablo.jakarta.controller.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

import java.util.UUID;

@Named
@ApplicationScoped
@FacesConverter(value = "uuidConverter", managed = true)
public class UUIDConverter implements Converter<UUID> {

    @Override
    public UUID getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            throw new ConverterException("Invalid UUID format: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, UUID value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
