package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/home")
public class HomeController {
    private FileService fileService;

    public HomeController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping()
    public String getHomePage(Model model) {
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
}
