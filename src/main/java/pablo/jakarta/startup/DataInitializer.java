package pablo.jakarta.startup;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pablo.jakarta.model.Car;
import pablo.jakarta.model.Model;
import pablo.jakarta.model.User;
import pablo.jakarta.model.enums.Brand;
import pablo.jakarta.service.CarService;
import pablo.jakarta.service.ModelService;
import pablo.jakarta.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class DataInitializer {
    
    private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());
    
    @Inject
    private ModelService modelService;
    
    @Inject
    private CarService carService;
    
    @Inject
    private UserService userService;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        userService.createUser(new User("user1", 25, LocalDate.of(2024, 5, 15)));
        userService.createUser(new User("user2", 30, LocalDate.of(2015, 8, 22)));
        userService.createUser(new User("user3", 28, LocalDate.of(2016, 12, 10)));
        userService.createUser(new User("user4", 35, LocalDate.of(2012, 3, 5)));

        Model camry = new Model();
        camry.setBrand(Brand.TOYOTA);
        camry.setName("Camry");
        camry.setHp(203);
        camry = modelService.createModel(camry);
        
        Model corolla = new Model();
        corolla.setBrand(Brand.TOYOTA);
        corolla.setName("Corolla");
        corolla.setHp(168);
        corolla = modelService.createModel(corolla);
        
        Model mustang = new Model();
        mustang.setBrand(Brand.FORD);
        mustang.setName("Mustang");
        mustang.setHp(450);
        mustang = modelService.createModel(mustang);

        List<User> users = userService.getAllUsers();

        if (!users.isEmpty()) {

            Car car1 = new Car();
            car1.setModel(camry);
            car1.setOwner(users.get(0));
            car1.setLicensePlate("GDA12345");
            car1.setMileage(50000);
            car1.setPurchaseDate(LocalDate.of(2023, 5, 15));
            carService.createCar(car1);

            Car car2 = new Car();
            car2.setModel(corolla);
            car2.setOwner( users.get(0));
            car2.setLicensePlate("GD32132");
            car2.setMileage(30000);
            car2.setPurchaseDate(LocalDate.of(2024, 8, 20));
            carService.createCar(car2);

            Car car3 = new Car();
            car3.setModel(mustang);
            car3.setOwner( users.get(1));
            car3.setLicensePlate("GD99999");
            car3.setMileage(5000);
            car3.setPurchaseDate(LocalDate.of(2025, 1, 10));
            carService.createCar(car3);

            Car car4 = new Car();
            car4.setModel(camry);
            car4.setOwner(users.get(2));
            car4.setLicensePlate("GDA88888");
            car4.setMileage(75000);
            car4.setPurchaseDate(LocalDate.of(2020, 3, 10));
            carService.createCar(car4);
        }

    }
}
