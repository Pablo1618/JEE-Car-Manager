package pablo.jakarta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pablo.jakarta.model.enums.Brand;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    private Brand brand;
    private String name;
    private Integer hp;
    private List<Car> cars;
}
