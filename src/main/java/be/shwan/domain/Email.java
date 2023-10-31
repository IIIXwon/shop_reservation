package be.shwan.domain;


import jakarta.persistence.Embeddable;

@Embeddable
public class Email {
    private final String id;
    private final String domain;

    public Email(String id, String domain) {
        this.id = id;
        this.domain = domain;

    }
}
