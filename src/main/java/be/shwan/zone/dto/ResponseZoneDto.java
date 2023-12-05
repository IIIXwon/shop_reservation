package be.shwan.zone.dto;

public record ResponseZoneDto(String city, String localNameOfCity, String province) {
    public String toString() {
        return String.format("%s(%s)/%s", city, localNameOfCity, province);
    }
}
