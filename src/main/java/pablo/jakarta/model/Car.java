package pablo.jakarta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private UUID id;
    private Model model;
    private User owner;
    private String licensePlate;
    private Integer mileage;
    private LocalDate purchaseDate;
}
