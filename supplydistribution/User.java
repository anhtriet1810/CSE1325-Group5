package supplydistribution;
import java.util.Arrays;

/// frontend file

/**
 * Room Supply Distribution System - User
 */
public final class User {
    private final String username;
    private final AccessLevel role;
    private final char[] password;

    public User(String username, AccessLevel role, char[] password) {
        this.username = username;
        this.role = role;
        this.password = password.clone(); // keep our own copy
    }

    public String getUsername() {
        return username;
    }

    public AccessLevel getRole() {
        return role;
    }
    public AccessLevel getAccessLevel()
    {
        return getRole();
    }

    public boolean passwordMatches(char[] attempt) {
        return Arrays.equals(password, attempt);
    }
}
