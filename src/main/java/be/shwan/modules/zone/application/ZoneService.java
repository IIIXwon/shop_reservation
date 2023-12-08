package be.shwan.modules.zone.application;

import be.shwan.modules.zone.domain.Zone;
import be.shwan.modules.zone.dto.RequestZoneDto;
import be.shwan.modules.zone.dto.ResponseZoneDto;

import java.util.Collection;
import java.util.List;

public interface ZoneService {
    Zone findZone(String zoneName);

    List<ResponseZoneDto> tagifyZonesToResponseZoneDto(Collection<Zone> zones);

    List<String> tagifyZonesToString();
}
