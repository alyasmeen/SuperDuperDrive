package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE userid = #{userId} AND filename = #{fileName}")
    File getFileByNameAndUser(Integer userId, String fileName);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(File file);

    @Select("SELECT filename FROM FILES WHERE userid = #{userId}")
    String[] getFileNames(Integer userId);

    @Delete("DELETE FROM FILES WHERE filename=#{fileName}")
    void deleteFile(String fileName);

    @Select("SELECT * FROM FILES WHERE filename = #{fileName}")
    File getFile(String fileName);
}
