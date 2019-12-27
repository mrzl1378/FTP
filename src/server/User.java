
package server;

public class User {
    private String username;
    private String password;
    private boolean isLogin;


    User(String username, String password) {

        this.username = username;
        this.password = password;
        this.isLogin = true;

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setOnline(boolean bool) {
        isLogin = bool;
    }
}
