package com.muli.planning.controller;

import com.muli.planning.common.BaseResponse;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.common.ResultUtils;
import com.muli.planning.domain.User;
import com.muli.planning.domain.request.UserLoginRequest;
import com.muli.planning.domain.request.UserRegisterRequest;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.service.UserService;
import com.muli.planning.utils.AliOssUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.muli.planning.constant.UserConstant.USER_LOGIN_STATE;


@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        Long code = userRegisterRequest.getCode();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword, code);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录后，将用户信息保存在session中
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户退出登录，清除session中保存的登录信息
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logoutUser(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User newUser, HttpServletRequest request){
        if(newUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.userUpdate(newUser, loginUser, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/updateAvatar")
    public BaseResponse<Integer> updateUserAvatar(MultipartFile file, HttpServletRequest request){
        if(file == null || file.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传图片有误");
        }
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userService.getLoginUser(request).getId();
        Integer Result = userService.userUpdateAvatar(id, file);
        return ResultUtils.success(Result);
    }

    @PostMapping("/updatePassword")
    public BaseResponse<Integer> updateUserPassword(String oldPassword, String newPassword, String checkPassword, HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userService.getLoginUser(request).getId();
        Integer result = userService.userUpdatePassword(id, oldPassword, newPassword, checkPassword);
        return ResultUtils.success(result);
    }
}
