package com.muli.planning.service;

import com.muli.planning.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author 沐璃
* @description 针对表【user】的数据库操作Service
* @createDate 2025-01-18 18:09:37
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @param code
     * @return 注册的用户id
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword, Long code);

    /**
     * 防止私密数据泄露
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request 为了将用户信息存入session
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户数据
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 更新用户信息
     * @param newUser 需要更新的信息
     * @param loginUser 当前登录用户信息
     * @param request  更新session中保存的用户信息
     * @return
     */
    Integer userUpdate(User newUser, User loginUser, HttpServletRequest request);

    /**
     * 更新密码
     * @param id 更新用户id
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @param checkPassword 校验新密码
     * @return
     */
    Integer userUpdatePassword(Long id, String oldPassword, String newPassword, String checkPassword);

    /**
     * 更新用户头像
     * @param id
     * @param file
     * @return
     */
    Integer userUpdateAvatar(Long id, MultipartFile file);
}
