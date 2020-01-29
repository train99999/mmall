package com.quan.dao;

import com.quan.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //检查用户名是否存在
    int checkUsername(String username);
    //检查用户名和密码是否匹配
    User selectLogin(@Param("username") String username, @Param("password") String password);

    //检查邮箱是否存在
    int checkEmail(String email);

    //查找用户问题
    String selectQuestionByUsername(String username);
    //校验问题答案
    int checkAnswer(@Param("username") String username,@Param ("question")String question,@Param("answer") String answer);
    //重置密码
    int updatePasswordByUsername(@Param("username") String username,@Param("password") String password);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);
}