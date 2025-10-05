package pablo.jakarta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String login;
    private Integer age;
    private LocalDate driverLicenseIssueDate;
    private List<Car> cars;
}
