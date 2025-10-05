package pablo.jakarta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private Model model;
    private User owner;
    private String licensePlate;
    private Integer mileage;
    private LocalDate purchaseDate;
}
