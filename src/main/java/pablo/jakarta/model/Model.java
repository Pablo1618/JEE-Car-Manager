package pablo.jakarta.model;

import jakarta.json.bind.annotation.JsonbTransient;
import lombok.*;
import pablo.jakarta.model.enums.Brand;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cars"})
@EqualsAndHashCode(exclude = {"cars"})
public class Model {
    private UUID id;
    private Brand brand;
    private String name;
    private Integer hp;
    
    @JsonbTransient
    private List<Car> cars;
}
