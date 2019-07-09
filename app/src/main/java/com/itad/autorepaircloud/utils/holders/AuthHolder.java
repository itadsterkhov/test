package com.itad.autorepaircloud.utils.holders;

public class AuthHolder {
    private static AuthHolder authHolder = new AuthHolder();
    private String token;

    public AuthHolder(){
    }

    public static AuthHolder getInstance() {
        return authHolder;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token=token;
    }

    public boolean tokeInNotNull(){
        return token!=null;
    }
}
