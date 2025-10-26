package pablo.jakarta.model;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"model", "owner"})
@EqualsAndHashCode(exclude = {"model", "owner"})
public class Car {
    private UUID id;
    private Model model;
    private User owner;
    private String licensePlate;
    private Integer mileage;
    private LocalDate purchaseDate;
}
