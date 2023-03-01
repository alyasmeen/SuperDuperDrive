package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public void createNote(Integer userId, String title, String description) {
        noteMapper.insert(new Note(0, title, description, userId));
    }

    public Note getNote(Integer noteId) {
        return noteMapper.getNote(noteId);
    }

    public void updateNote(Integer noteId, String title, String description) {
        noteMapper.updateNote(noteId, title, description);
    }

    public Note[] getNotesByUser(Integer userId) {
        return noteMapper.getNotesByUser(userId);
    }

    public void removeNote(Integer noteId){
        noteMapper.deleteNote(noteId);
    }
}
