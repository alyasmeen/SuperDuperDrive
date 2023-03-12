package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialFormObj;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteFormObj;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequestMapping("/home")
public class HomeController {
    private UserService userService;
    private FileService fileService;
    private NoteService noteService;
    private CredentialService credentialService;
    private EncryptionService encryptionService;

    public HomeController(UserService userService, FileService fileService, NoteService noteService, CredentialService credentialService, EncryptionService encryptionService) {
        this.userService=userService;
        this.fileService = fileService;
        this.noteService=noteService;
        this.credentialService=credentialService;
        this.encryptionService=encryptionService;
    }

    @GetMapping()
    public String getHomePage(@ModelAttribute("note") NoteFormObj note, @ModelAttribute("credential") CredentialFormObj credential, Model model, Authentication auth) {
        Integer userId = getUserId(auth);
        model.addAttribute("files", fileService.getFileNames(userId));
        model.addAttribute("notes", noteService.getNotesByUser(userId));
        model.addAttribute("credentials", credentialService.getCredentialsByUser(userId));
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    @PostMapping("file-upload")
    public String handleFileUpload(@RequestParam("fileUpload") MultipartFile fileUpload, Authentication auth,
                                   Model model) {
        if (fileUpload.isEmpty()){
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "No file was uploaded");
            return "result";
        }

        if (fileService.fileNameExists(auth.getName(), fileUpload.getOriginalFilename())){
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "File name exists");
            return "result";
        }

        try {
            fileService.uploadFile(fileUpload, auth.getName());
        } catch (IOException e) {
            model.addAttribute("result", "error");
            model.addAttribute("errorMsg", "Error");
            return "result";
        }
        model.addAttribute("result", "success");
        return "result";
    }

    public Integer getUserId(Authentication auth) {
        return userService.getUser(auth.getName()).getUserId();
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
        }
        else {
            Note oldNote = noteService.getNote(Integer.parseInt(note.getNoteId()));
            noteService.updateNote(oldNote.getNoteId(), note.getTitle(), note.getDescription());
        }

        model.addAttribute("notes", noteService.getNotesByUser(getUserId(auth)));
        model.addAttribute("result", "success");
        return "result";
    }

    @GetMapping("/remove-note/{note}")
    public String removeNote(@PathVariable Integer note,Authentication auth,Model model){
        noteService.removeNote(note);
        model.addAttribute("notes", noteService.getNotesByUser(getUserId(auth)));
        model.addAttribute("result", "success");
        return "result";
    }

    @PostMapping("create-credential")
    public String createCredential(@ModelAttribute("credential") CredentialFormObj credential,Authentication auth, Model model) {
        byte[] key=new byte[16];
        new SecureRandom().nextBytes(key);
        String encoded=Base64.getEncoder().encodeToString(key);
        String encrypted= encryptionService.encryptValue(credential.getPassword(),encoded);

        if (credential.getCredentialId().isEmpty()){
            credentialService.createCredential(credential.getUrl(), credential.getUsername(), encoded, encrypted, getUserId(auth));
        }
        else{
            Credential oldCredential = credentialService.getCredential(Integer.parseInt(credential.getCredentialId()));
            credentialService.modifyCredential(oldCredential.getCredentialId(), credential.getUrl(), credential.getUsername(), encoded, encrypted);
        }

        model.addAttribute("credentials", credentialService.getCredentialsByUser(getUserId(auth)));
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");
        return "result";
    }

    @GetMapping("/remove-credential/{credential}")
    public String removeCredential(@PathVariable Integer credential, Authentication auth, Model model){
        credentialService.removeCredential(credential);
        model.addAttribute("credentials", credentialService.getCredentialsByUser(getUserId(auth)));
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");
        return "result";
    }
}
