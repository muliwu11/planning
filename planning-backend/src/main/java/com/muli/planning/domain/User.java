package com.muli.planning.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号（登录使用）
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别（默认0，1男，2女）
     */
    private Integer gender;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户状态（默认0，管理员1）
     */
    private Integer userRole;

    /**
     * 用户编号（暴露给用户端的编号）
     */
    private Long plantCode;

    /**
     * 逻辑删除（默认0,已删除1）
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}