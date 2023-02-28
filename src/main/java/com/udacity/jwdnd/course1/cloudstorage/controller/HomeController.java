package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/home")
public class HomeController {
    private UserService userService;
    private FileService fileService;

    public HomeController(UserService userService, FileService fileService) {
        this.userService=userService;
        this.fileService = fileService;
    }

    @GetMapping()
    public String getHomePage(Model model, Authentication auth) {
        Integer userId = getUserId(auth);
        model.addAttribute("files", fileService.getFileNames(userId));
        return "home";
    }

    @PostMapping("file-upload")
    public String handleFileUpload(@RequestParam("fileUpload") MultipartFile fileUpload, Authentication authentication,
                                   Model model) {
        if (fileUpload.isEmpty()){
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "No file was uploaded");
            return "result";
        }

        if (fileService.fileNameExists(authentication.getName(), fileUpload.getOriginalFilename())){
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "File name exists");
            return "result";
        }

        try {
            fileService.uploadFile(fileUpload, authentication.getName());
        } catch (IOException e) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "Error");
            return "result";
        }
        model.addAttribute("result", "success");
        return "result";
    }

    public Integer getUserId(Authentication authentication) {
        return userService.getUser(authentication.getName()).getUserId();
    }

    @GetMapping(value = "/view-file/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] viewFile(@PathVariable String fileName) {
        return fileService.getFile(fileName).getFileData();
    }

    @GetMapping("/delete-file/{fileName}")
    public String delete(Authentication auth, @PathVariable String fileName, Model model) {
        fileService.deleteFile(fileName);
        model.addAttribute("files", fileService.getFileNames(getUserId(auth)));
        model.addAttribute("result", "success");
        return "result";
    }
}
