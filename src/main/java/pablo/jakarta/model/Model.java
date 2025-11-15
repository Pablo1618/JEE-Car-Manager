package pablo.jakarta.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
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
@Entity
@Table(name = "models")
public class Model {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false)
    private Brand brand;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "hp")
    private Integer hp;
    
    @JsonbTransient
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Car> cars;
}
