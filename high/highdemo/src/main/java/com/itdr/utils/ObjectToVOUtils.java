package com.itdr.utils;

import com.itdr.pojo.User;
import com.itdr.pojo.bo.UserVo;

public class ObjectToVOUtils {
    public static UserVo UserToUserVo(User u) {
        UserVo uv = new UserVo();
        uv.setId(u.getId());
        uv.setUsername(u.getUsername());
        uv.setEmail(u.getEmail());
        uv.setPhone(u.getPhone());
        uv.setCreateTime(u.getCreateTime());
        uv.setUpdateTime(u.getUpdateTime());
        return uv;
    }
}
