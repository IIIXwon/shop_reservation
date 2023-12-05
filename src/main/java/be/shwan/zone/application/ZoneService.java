package be.shwan.zone.application;

import be.shwan.zone.domain.Zone;

public interface ZoneService {
    Zone findZone(String zoneName);
}
