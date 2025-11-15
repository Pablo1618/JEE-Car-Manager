package pablo.jakarta.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "login", nullable = false, unique = true)
    private String login;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "driver_license_issue_date")
    private LocalDate driverLicenseIssueDate;
    
    public User(String login, Integer age, LocalDate driverLicenseIssueDate) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.age = age;
        this.driverLicenseIssueDate = driverLicenseIssueDate;
    }
}
