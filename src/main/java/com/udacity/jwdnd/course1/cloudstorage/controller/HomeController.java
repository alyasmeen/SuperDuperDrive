package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteFormObj;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
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
    private NoteService noteService;

    public HomeController(UserService userService, FileService fileService, NoteService noteService) {
        this.userService=userService;
        this.fileService = fileService;
        this.noteService=noteService;
    }

    @GetMapping()
    public String getHomePage(@ModelAttribute("note") NoteFormObj note, Model model, Authentication auth) {
        Integer userId = getUserId(auth);
        model.addAttribute("files", fileService.getFileNames(userId));
        model.addAttribute("notes", noteService.getNotesByUser(userId));
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
    public String deleteFile(Authentication auth, @PathVariable String fileName, Model model) {
        fileService.deleteFile(fileName);
        model.addAttribute("files", fileService.getFileNames(getUserId(auth)));
        model.addAttribute("result", "success");
        return "result";
    }

    @PostMapping("create-note")
    public String createNote(@ModelAttribute("note") NoteFormObj note, Authentication auth, Model model) {

        if (note.getNoteId().isEmpty()) {
            noteService.createNote(getUserId(auth), note.getTitle(), note.getDescription());
        } else {
            Note oldNote = noteService.getNote(Integer.parseInt(note.getNoteId()));
            noteService.updateNote(oldNote.getNoteId(), note.getTitle(), note.getDescription());
        }

        model.addAttribute("notes", noteService.getNotesByUser(getUserId(auth)));
        model.addAttribute("result", "success");
        return "result";
    }
}
