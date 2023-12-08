package be.shwan.modules.zone.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCity(String zoneName);

    Zone findByLocalNameOfCity(String zoneName);
}
