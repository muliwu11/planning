package com.muli.planning.domain.request;

import lombok.Data;

import java.io.Serial;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginRequest {

    @Serial
    private static final long serialVersionUID = 1363717773786437684L;

    private String userAccount;
    private String userPassword;
}
