package com.quan.service.impl;

import com.quan.common.Const;
import com.quan.common.ServerResponse;
import com.quan.common.TokenCache;
import com.quan.dao.UserMapper;
import com.quan.pojo.User;
import com.quan.service.IUserService;
import com.quan.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int result = userMapper.checkUsername(username);

        if(result == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Pw = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Pw);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        int result = userMapper.checkUsername(user.getUsername());

        if(result > 0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }

        int i = userMapper.checkEmail(user.getEmail());
        if(i > 0){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5 加密
        System.out.println(user.getPassword());
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insert = userMapper.insert(user);
        if(insert == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int result = userMapper.checkUsername(str);
                if(result > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int i = userMapper.checkEmail(str);
                if(i > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
           //用户不存在
           return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("没有找到问题");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int result = userMapper.checkAnswer(username,question,answer);
        if(result>0){
            String forgetToken = UUID.randomUUID().toString();
            System.out.println(forgetToken);
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String token){
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("参数异常");
        }

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //获取缓存 token
        String token2 = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token2)){
            return ServerResponse.createByErrorMessage("token无效");
        }
        if(StringUtils.equals(token,token2)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int row = userMapper.updatePasswordByUsername(username,md5Password);
            if(row > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }

        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新生成token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword( String passwordOld, String passwordNew,User user){
        int result = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(result == 0){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int update = userMapper.updateByPrimaryKeySelective(user);
        if(update > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        // username 是不能被更新的
        // Email 也要进行一个校验，校验新的 email 是不是已经存在，并且存在的 Email 不是当前用户的.
        int result = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        // result 大于 0 表示邮箱已经被占用
        if(result > 0){
            return ServerResponse.createByErrorMessage("邮箱已经被占用");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());

        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int update = userMapper.updateByPrimaryKeySelective(updateUser);
        if(update > 0 ){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
