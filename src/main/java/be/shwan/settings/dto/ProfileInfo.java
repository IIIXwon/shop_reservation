package be.shwan.settings.dto;

import org.hibernate.validator.constraints.Length;

public record ProfileInfo(
        @Length(max = 30)
        String bio,
        @Length(max = 50)
        String url,
        @Length(max = 20)
        String occupation,
        @Length(max = 20)
        String location,

        String profileImage) {

}
