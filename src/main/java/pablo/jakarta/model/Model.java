package pablo.jakarta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pablo.jakarta.model.enums.Brand;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    private UUID id;
    private Brand brand;
    private String name;
    private Integer hp;
    private List<Car> cars;
}
