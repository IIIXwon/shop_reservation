package be.shwan.zone.application;

import be.shwan.zone.domain.Zone;
import be.shwan.zone.dto.ResponseZoneDto;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ZoneService {
    Zone findZone(String zoneName);

    List<ResponseZoneDto> tagifyZonesToResponseZoneDto(Collection<Zone> zones);

    List<String> tagifyZonesToString();
}
