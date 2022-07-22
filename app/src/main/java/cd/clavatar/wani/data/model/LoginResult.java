package cd.clavatar.wani.data.model;

public class LoginResult {
    private CompactUser user;
    private String token, password;
    private WaniParameter parameter;

    public LoginResult() {
    }

    public LoginResult(CompactUser user, String token, String password, WaniParameter parameter) {
        this.user = user;
        this.token = token;
        this.password = password;
        this.parameter = parameter;
    }

    public CompactUser getUser() {
        return user;
    }

    public void setUser(CompactUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public WaniParameter getParameter() {
        return parameter;
    }

    public void setParameter(WaniParameter parameter) {
        this.parameter = parameter;
    }
}
