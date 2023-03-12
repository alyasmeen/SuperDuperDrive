package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionController  implements HandlerExceptionResolver {
    private UserService userService;
    private FileService fileService;
    private NoteService noteService;
    private CredentialService credentialService;
    private EncryptionService encryptionService;
    private User user;

    public ExceptionController(UserService userService, FileService fileService, NoteService noteService,
                               CredentialService credentialService, EncryptionService encryptionService) {
        this.userService=userService;
        this.fileService = fileService;
        this.noteService=noteService;
        this.credentialService=credentialService;
        this.encryptionService=encryptionService;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFoundExceptionHandle(Model model){
        model.addAttribute("result", "error");
        model.addAttribute("errorMsg", "Not found");
        setModelAttributes(model);
        return "result";
    }
    public void setModelAttributes(Model model) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                user=userService.getUser(auth.getName());
            }

            Integer userId = getUserId(auth);
            model.addAttribute("files", fileService.getFileNames(userId));
            model.addAttribute("notes", noteService.getNotesByUser(userId));
            model.addAttribute("credentials", credentialService.getCredentialsByUser(userId));
            model.addAttribute("encryptionService", encryptionService);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    public void setModelAndView(ModelAndView modelAndView){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                user=userService.getUser(auth.getName());
            }

            Integer userId = getUserId(auth);
            modelAndView.addObject("notes",noteService.getNotesByUser(userId));
            modelAndView.addObject("credentials",credentialService.getCredentialsByUser(userId));
            modelAndView.addObject("files",fileService.getFileNames(userId));
            modelAndView.addObject("encryptionService", encryptionService);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView modelAndView = new ModelAndView();
        if(e instanceof MaxUploadSizeExceededException){
            modelAndView = new ModelAndView();
            setModelAndView(modelAndView);
            modelAndView.addObject("error", "File is too large");
            modelAndView.setViewName("home.html");
            return modelAndView;
        }
        modelAndView.setViewName("home.html");
        return modelAndView;
    }

    public Integer getUserId(Authentication auth) {
        return userService.getUser(auth.getName()).getUserId();
    }
}