package com.liuxn.demo.shiro.controller;

import com.liuxn.demo.entity.ResultVo;
import com.liuxn.demo.entity.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * shiro 登陆地址
 *
 * @author liuxn
 * @date 2022/1/11
 */
@Controller
public class ShiroController {


    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     *
     * @return
     */
    @RequestMapping(value = "/unauth")
    @ResponseBody
    public ResultVo unAuth() {
        return new ResultVo<>(-1, "未登陆!!!");
    }


    @RequestMapping(path = "/login")
    @ResponseBody
    public ResultVo miniLoginIng(@RequestBody UserVo user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || password == null) {
            return new ResultVo(-2, "参数丢失");
        }
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        try {
            subject.login(new UsernamePasswordToken(username, password));
        } catch (AuthenticationException e) {
            return new ResultVo(-3, "用户名密码错误");
        }
        return new ResultVo<>(0, "登陆成功", session.getId().toString());
    }


    @RequestMapping("/logout")
    @ResponseBody
    public Object miniLogout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ResultVo(0, "您已经安全退出");
    }
}
