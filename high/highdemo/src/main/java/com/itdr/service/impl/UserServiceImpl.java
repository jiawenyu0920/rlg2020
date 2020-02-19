package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.TokenCache;
import com.itdr.mapper.UserMapper;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse login(String username, String password) {
//       参数非空判断
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(password)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
//        查询用户
       User u = userMapper.selectByUserNameAndPassword(username,password);
        if(u == null){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.FAIL_LOGIN.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.FAIL_LOGIN.getDesc());
        }
//        返回成功数据
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse<User> register(User u) {
//       参数非空判断
        if(StringUtils.isEmpty(u.getUsername())){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(u.getPassword())){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if(StringUtils.isEmpty(u.getQuestion())){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }
        if(StringUtils.isEmpty(u.getAnswer())){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }
//       查找用户是否存在（根据用户名进行查找）
        ServerResponse<User> username = checkVaild(u.getUsername(), "username");
//       查找邮箱是否存在（根据用户名进行查找）
        ServerResponse<User> email = checkVaild(u.getEmail(), "email");
        if(!username.isSuccess() || !email.isSuccess()){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc());
        }



//        注册用户信息
        int insert = userMapper.insert(u);
        if(insert<=0){
            return  ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.DEFAULT_USER.getDesc());

        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_USER.getDesc());
    }

    @Override
    public ServerResponse<User> checkVaild(String str, String type) {
        //       参数非空判断
        if(StringUtils.isEmpty(str)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.DEFAULT_FAIL,
                    // 错误信息
                    ConstCode.UserEnum.NO_USEROREMAIL.getDesc());
        }
        if(StringUtils.isEmpty(type)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.DEFAULT_FAIL,
                    // 错误信息
                    ConstCode.UserEnum.NO_TYPE.getDesc());
        }
//        查找用户名和邮箱是否存在
        int i = userMapper.selectByUsernameOrEmail(str,type);
        if(i>0){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_MSG.getDesc());
    }

    /**
     * 登陆状态更新个人信息
     *
     */

    @Override
    public ServerResponse<User> updateInforamtion(String email, String phone, String question, String answer,User user) {
        User u = new User();
        u.setId(user.getId());
        u.setEmail(email);
        u.setPhone(phone);
        u.setQuestion(question);
        u.setAnswer(answer);
        int i = userMapper.updateByPrimaryKeySelective(u);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    "信息更新失败"
            );
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_USERMSG.getDesc());
    }

    @Override
    public ServerResponse<User> forgetGetQuestion(String username) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
//        该用户是否存在(在前端通过ajax方式判断)
        User user = userMapper.selectByUsername(username);
        if(user == null){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.INEXISTENCE_USER.getCode(),
                    ConstCode.UserEnum.INEXISTENCE_USER.getDesc());
        }
//        获取用户密保问题
        String question = user.getQuestion();
        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.NO_QUESTION.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.NO_QUESTION.getDesc());
        }
        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,question);
    }

    @Override
    public ServerResponse<User> forgetCheckAnswer(String username, String question, String answer) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_QUESTION.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }
        if(StringUtils.isEmpty(answer)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_ANSWER.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }
//        判断答案是否正确
       int i = userMapper.selectByUserNameAndQuestionAndAnswer(username,question,answer);
       if(i<=0){
           ServerResponse.defeatedRS(
                   // 错误的状态码
                   ConstCode.UserEnum.ERROR_ANSWER.getCode(),
                   // 错误信息
                   ConstCode.UserEnum.ERROR_ANSWER.getDesc());
       }
//       返回随机令牌
        String s = UUID.randomUUID().toString();
//把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis替代
        TokenCache.set("token_" + username, s);
        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,s);
    }

    @Override
    public ServerResponse<User> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_USERNAME.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if(StringUtils.isEmpty(forgetToken)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }
//      判断缓存中的token
        String token = TokenCache.get("token_" + username);
        if (token==null||token.equals("")){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.LOSE_EFFICACY.getCode(),
                    ConstCode.UserEnum.LOSE_EFFICACY.getDesc());
        }if (!token.equals(forgetToken)){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode()
                    ,ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }
//        重置密码
       int i = userMapper.updateByUserNameAndPasswordNew(username,passwordNew);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode()
                    ,ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode()
                ,ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());

    }

    @Override
    public ServerResponse<User> resetPassword(User user,String passwordOld, String passwordNew) {
        if(StringUtils.isEmpty(passwordOld)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if(StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS(
                    // 错误的状态码
                    ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),
                    // 错误信息
                    ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
//更新密码
       int i = userMapper.updateByUserNameAndPasswordOldAndPasswordNew(user.getUsername(),passwordOld,passwordNew);
        if(i<=0){
            return ServerResponse.defeatedRS(
                    ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode()
                    ,ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS(
                ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode()
                ,ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());

    }

}
