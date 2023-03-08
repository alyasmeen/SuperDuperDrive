package com.udacity.jwdnd.course1.cloudstorage.model;

public class CredentialFormObj {
    private String credentialId;
    private String url;
    private String username;
    private String password;



    public String getCredentialId(){
        return credentialId;
    }
    public String getUrl() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
