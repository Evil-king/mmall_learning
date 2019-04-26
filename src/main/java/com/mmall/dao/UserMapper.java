package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password")String md5Password);

    int checkEmail(String email);

    String selectQuestionByUserName(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updateByUserName(@Param("username") String username,@Param("md5Password") String md5Password);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailById(@Param("emali") String email,@Param("userId") Integer userId);
}