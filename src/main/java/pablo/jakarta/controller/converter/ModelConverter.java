package pablo.jakarta.controller.converter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pablo.jakarta.model.Model;
import pablo.jakarta.service.ModelService;

import java.util.UUID;

@Named
@ApplicationScoped
@FacesConverter(value = "modelConverter", managed = true)
public class ModelConverter implements Converter<Model> {

    @Inject
    private ModelService modelService;

    @Override
    public Model getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            UUID modelId = UUID.fromString(value.trim());
            return modelService.getModelById(modelId).orElse(null);
        } catch (IllegalArgumentException e) {
            throw new ConverterException("Invalid model ID format: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Model value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
