package com.muli.planning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.constant.UserConstant;
import com.muli.planning.domain.User;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.mapper.UserMapper;
import com.muli.planning.service.UserService;
import com.muli.planning.utils.AliOssUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.muli.planning.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 沐璃
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-01-18 18:09:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    private static final String SALT = "muli";

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword, Long code) {
        //校验环节
        //保证传参不为空
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword) || code == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断账户长度
        if(userAccount.length() < 5 || userAccount.length() > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须在5到16个字符之间。");
        }
        //判断账户是否只包含数字和字母
        if(!StringUtils.isAlphanumeric(userAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名只能包含字母和数字");
        }
        //校验密码长度
        if(userPassword.length() < 4 || userPassword.length() > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度必须在4到16个字符之间");
        }
        //判断密码是否只包含数字和字母
        if(!StringUtils.isAlphanumeric(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码只能包含字母和数字");
        }
        //判断密码和校验密码是否相同
        if(!StringUtils.equals(userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不同");
        }
        //编号规则校验
        if(code < 10000 || code > 999999999){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号为5-10位纯数字");
        }
        //检验账户是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }
        //检验编号是否重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setCode(code);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    @Override
    public User getSafetyUser(User originUser) {
        if(originUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCode(originUser.getCode());
        return safetyUser;
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount, userPassword) || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断账户和密码是否符合规范
        if(!StringUtils.isAlphanumeric(userAccount) || !StringUtils.isAlphanumeric(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户或密码出现非字母数字");
        }
        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //数据库查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    @Override
    public Integer userUpdate(User newUser, User loginUser, HttpServletRequest request) {
        //判断需要修改的用户信息是否符合规范
        //昵称 1 - 16字符
        if(newUser.getUsername() != null && newUser.getUsername().isEmpty() || newUser.getUsername().length() > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称长度必须在1到16个字符之间");
        }
        //性别 1男， 2女
        if(newUser.getGender() != null && newUser.getGender() > 2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "国内暂时不支持该性别亲");
        }
        //个人简介 小于200字符
        if(newUser.getProfile() != null && newUser.getProfile().length() > 200){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "个人简介必须小于200字符");
        }
        //用户邮箱 是否有@就行
        if(newUser.getEmail() != null) {
            String validPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            Matcher matcher = Pattern.compile(validPattern).matcher(newUser.getEmail());
            if (!matcher.matches()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱填写错误");
            }
        }
        //进行修改
        newUser.setId(loginUser.getId());
        int result = userMapper.updateById(newUser);
        if(result <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //将修改过后的信息存入session
        User newLoginUser = getSafetyUser(userMapper.selectById(newUser.getId()));
        request.getSession().setAttribute(USER_LOGIN_STATE, newLoginUser);
        return result;
    }

    @Override
    public Integer userUpdatePassword(Long id, String oldPassword, String newPassword, String checkPassword) {
        //判断新密码是否符合规范
        if(!StringUtils.isAlphanumeric(newPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码出现非字母数字");
        }
        //新密码和验证密码是否相同
        if(!StringUtils.equals(newPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不相同");
        }
        //判断旧密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if(!StringUtils.equals(userMapper.selectById(id).getUserPassword(), encryptPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码输入错误");
        }
        //进行修改
        User user = new User();
        encryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        user.setId(id);
        user.setUserPassword(encryptPassword);
        Integer result = userMapper.updateById(user);
        if(result <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Integer userUpdateAvatar(Long id, MultipartFile file){
        String url = "";
        try {
            url = AliOssUtil.uploadFile(file.getOriginalFilename(), file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(url == null || url.isEmpty()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        User user = new User();
        user.setId(id);
        user.setAvatarUrl(url);
        int result = userMapper.updateById(user);
        if(result <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }


}




