package be.shwan.zone.application.impl;

import be.shwan.zone.application.ZoneService;
import be.shwan.zone.domain.Zone;
import be.shwan.zone.domain.ZoneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SimpleZoneService implements ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zone_kr.csv");
            List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream().map(line -> {
                        String[] split = line.split(",");
                        return new Zone(split[0], split[1], split[2]);
                    }).toList();
            zoneRepository.saveAll(zones);
        }
    }

    @Override
    public Zone findZone(String zoneName) {
        int startIndex = zoneName.indexOf("(");
        int endIndex = zoneName.indexOf(")");
        String localNameOfCity = zoneName.substring(startIndex + 1, endIndex);
        String[] split = zoneName.split("\\(");
        String city = split[0];
        Zone zone = zoneRepository.findByCity(city);
        if (zone != null) {
            return zone;
        }

        zone = zoneRepository.findByLocalNameOfCity(localNameOfCity);
        if (zone != null) {
            return zone;
        }
        return null;
    }
}
