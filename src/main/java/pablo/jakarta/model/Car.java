package pablo.jakarta.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"model", "owner"})
@EqualsAndHashCode(exclude = {"model", "owner"})
@Entity
@Table(name = "cars")
public class Car {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;
    
    @Column(name = "mileage")
    private Integer mileage;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
}
