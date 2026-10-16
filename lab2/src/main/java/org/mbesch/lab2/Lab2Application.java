package org.mbesch.lab2;

import org.mbesch.lab2.entity.*;
import org.mbesch.lab2.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.List;

@SpringBootApplication
public class Lab2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            UserRepository users,
            EnclosureRepository enclosures,
            AnimalRepository animals,
            AdoptionApplicationRepository applications,
            AnimalHandoverRecordRepository records) {
        return args -> {
            users.save(new User(users.nextId(), "Матвей", "matvey@example.com", "+79991234567", "ADOPTER", Instant.now()));
            enclosures.save(new Enclosure(enclosures.nextId(), "Вольер №1", List.of("Собака", "Щенок"), 3));
            animals.save(new Animal(animals.nextId(), "Бобик", "Собака", 2, "IN_SHELTER", 1L));
            applications.save(new AdoptionApplication(applications.nextId(), 1L, 1L, "SUBMITTED", Instant.now()));
            records.save(new AnimalHandoverRecord(records.nextId(), 1L, Instant.now(), null, "PREPARED"));
        };
    }
}
