package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {
    private FileMapper fileMapper;
    private UserMapper userMapper;

    public FileService(FileMapper fileMapper, UserMapper userMapper) {
        this.fileMapper = fileMapper;
        this.userMapper = userMapper;
    }
    public boolean fileNameExists(String username, String fileName) {
        return fileMapper.getFileByNameAndUser(userMapper.getUser(username).getUserId(), fileName)!=null;
    }

    public void uploadFile(MultipartFile file, String username) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String fileSize = String.valueOf(file.getSize());
        Integer userId = userMapper.getUser(username).getUserId();
        byte[] fileData = file.getBytes();
        fileMapper.insert(new File(null, fileName, contentType, fileSize, userId, fileData));
    }

    public String[] getFileNames(Integer userId) {
        return fileMapper.getFileNames(userId);
    }

    public void deleteFile(String fileName) {
        fileMapper.deleteFile(fileName);
    }

    public File getFile(String fileName) {
        return fileMapper.getFile(fileName);
    }
}
