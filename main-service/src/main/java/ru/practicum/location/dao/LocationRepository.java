package ru.practicum.location.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByLatAndLon(Float lat, Float lon);

    Location findByLatAndLon(Float lat, Float lon);
}
