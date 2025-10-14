package pablo.jakarta.model;

import jakarta.json.bind.annotation.JsonbTransient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String login;
    private Integer age;
    private LocalDate driverLicenseIssueDate;
    
    @JsonbTransient
    private List<Car> cars = new ArrayList<>();
    
    public User(String login, Integer age, LocalDate driverLicenseIssueDate) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.age = age;
        this.driverLicenseIssueDate = driverLicenseIssueDate;
        this.cars = new ArrayList<>();
    }
}
