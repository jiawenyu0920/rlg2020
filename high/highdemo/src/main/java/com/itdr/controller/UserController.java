package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.pojo.bo.UserVo;
import com.itdr.service.UserService;
import com.itdr.utils.ObjectToVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
//返回的所有的都是jesion格式
@ResponseBody
@RequestMapping("/portal/user/")
public class UserController {
 @Autowired
 UserService userService;
    /*
    *
    * 登录
    * */
    @RequestMapping("login.do")
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse sr = userService.login(username,password);
//        登陆成功，在session中保存用户数据
        if(sr.isSuccess()){
            session.setAttribute("user",sr.getData());
        }
        return sr;
    }
    /*
    * 注册
    * */
    @RequestMapping("register.do")
    public ServerResponse<User> register(User u){
        return userService.register(u);
    }

/*
  检查用户名或邮箱是否有效（重复）
 */
    @RequestMapping("check_vaild.do")
//    str代表用户名或邮箱 type代表username或email
    public ServerResponse<User> register(String str,String type){
         return userService.checkVaild(str,type);
    }

    /*
    *
    * 获取用户登录信息
    * (需要封装原始数据，因为返回的是用户的所有信息，现在只想返回部分信息)
    * */
    @RequestMapping("get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user =(User) session.getAttribute("user");
        UserVo userVo = ObjectToVOUtils.UserToUserVo(user);
        return ServerResponse.successRS(userVo);
    }

    /*
    * 获取登录用户详细信息
    * */
    @RequestMapping("get_inforamtion.do")
    public ServerResponse<User> getInforamtion(HttpSession session){
//        判断用户是否登录
        User user =(User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return ServerResponse.successRS(user);
    }

    @RequestMapping("update_inforamtion.do")
    public ServerResponse<User> updateInforamtion(String email,String phone,String question,String answer,HttpSession session){
//        判断用户是否登录
        User user =(User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.FORCE_EXIT.getDesc());
        }
        return userService.updateInforamtion(email,phone,question,answer,user);
    }

/*
* 用户退出
* */
    @RequestMapping("logout.do")
    public ServerResponse<User> logout(HttpSession session){
        session.removeAttribute("user");
        return ServerResponse.successRS(ConstCode.UserEnum.LOGOUT.getDesc());
    }

    /*
     * 忘记密码
     * */
    @RequestMapping("forget_get_question.do")
    public ServerResponse<User> forgetGetQuestion(String username) {
        return userService.forgetGetQuestion(username);
    }

    /*
     * 提交问题答案
     * */
    @RequestMapping("forget_check_answer.do")
    public ServerResponse<User> forgetCheckAnswer(String username,String question,String answer) {
        return userService.forgetCheckAnswer(username,question,answer);
    }

    /*
     * 重设密码
     * */
    @RequestMapping("forget_reset_password.do")
    public ServerResponse<User> forgetResetPassword(String username,String passwordNew,String forgetToken,HttpSession session) {
        ServerResponse<User> userServerResponse = userService.forgetResetPassword(username, passwordNew, forgetToken);
        if(userServerResponse.isSuccess()){
        session.removeAttribute("user");
        }
        return userServerResponse;
    }

    /*
     * 获取登录用户详细信息
     * */
    @RequestMapping("reset_password.do")
    public ServerResponse<User> resetPassword(String passwordOld,String passwordNew,HttpSession session){
//        判断用户是否登录
        User user =(User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.defeatedRS(
                    ConstCode.DEFAULT_FAIL,
                    ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return userService.resetPassword(user,passwordOld,passwordNew);
    }

}
