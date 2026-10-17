public class User {
    private String name;
    private String email;
    private String password;
    private AccessLevel accessLevel;

    public User(String name, String pw, String email, AccessLevel lvl) {
        this.name = name;
        this.password = pw;
        this.email = email;
        this.accessLevel = lvl;
    }

    public String getName() {
        return name;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public boolean checkPassword(String pw) {
        return this.password != null && this.password.equals(pw);
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "', lvl=" + accessLevel + "}";
    }
}