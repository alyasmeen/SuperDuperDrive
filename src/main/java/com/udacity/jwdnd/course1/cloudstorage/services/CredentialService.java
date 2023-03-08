package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {
    private CredentialMapper credentialMapper;

    public CredentialService(CredentialMapper credentialMapper) {
        this.credentialMapper=credentialMapper;
    }
    public Credential[] getCredentialsByUser(Integer userId) {
        return credentialMapper.getCredentialsByUser(userId);
    }

    public void createCredential(String url, String userNameOfCredential, String key, String password, Integer userId) {
        credentialMapper.insertCredential(new Credential(0, url, userNameOfCredential, key, password, userId));
    }

    public Credential getCredential(Integer credentialId) {
        return credentialMapper.getCredential(credentialId);
    }

    public void modifyCredential(Integer credentialId, String url, String userNameOfCredential, String key, String password) {
        credentialMapper.updateCredential(credentialId, url, userNameOfCredential, key, password);
    }

    public void removeCredential(Integer credentialId) {
        credentialMapper.deleteCredential(credentialId);
    }
}
