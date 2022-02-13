package com.project;

public class LoginObject {
    private String user;
    private String password;

    public LoginObject() {
    }

    public LoginObject(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public boolean accept(LoginObject o) {
        if (super.equals(o)) {
            return true;
        }
        return user.equals(o.user) && password.equals(o.password);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
