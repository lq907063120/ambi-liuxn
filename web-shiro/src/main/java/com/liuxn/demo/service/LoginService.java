package com.liuxn.demo.service;

import com.liuxn.demo.entity.UserVo;
import org.springframework.stereotype.Service;

/**
 * @author liuxn
 * @date 2022/1/11
 */
@Service
public class LoginService {


    public UserVo getUserInfo(String userName) {
        if (!"admin".equals(userName)) {
            return null;
        }
        UserVo vo = new UserVo();
        vo.setUsername("admin");
        //vo.setPassword("d033e22ae348aeb5660fc2140aec35850c4da997");
        vo.setPassword("admin");
        return vo;
    }


}
