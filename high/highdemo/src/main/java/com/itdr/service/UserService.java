package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface UserService {
    ServerResponse login(String username, String password);

    ServerResponse<User> register(User u);

    ServerResponse<User> checkVaild(String str, String type);

    ServerResponse<User> updateInforamtion(String email, String phone, String question, String answer,User u);

    ServerResponse<User> forgetGetQuestion(String username);

    ServerResponse<User> forgetCheckAnswer(String username, String question, String answer);

    ServerResponse<User> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<User> resetPassword(User u,String passwordOld, String passwordNew);
}
