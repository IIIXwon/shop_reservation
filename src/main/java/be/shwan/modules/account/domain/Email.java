package be.shwan.modules.account.domain;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Embeddable
//@NoArgsConstructor
public class Email {
    private String id;
    private String domain;

    public Email(String id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    public Email(String email) {
        boolean b = checkEmail(email);
    }

    public static Email create(String email) throws Exception {
        if(checkEmail(email)) {
            String[] tempStr = email.split("@");
            return new Email(tempStr[0], tempStr[1]);
        }
        throw new IllegalArgumentException("email 형식이 올바르지 않습니다");
    }

    private static boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return id + "@" + domain;
    }
}
