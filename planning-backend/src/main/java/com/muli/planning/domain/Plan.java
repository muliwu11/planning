package com.muli.planning.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName plan
 */
@TableName(value ="plan")
@Data
public class Plan implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务详细描述
     */
    private String description;

    /**
     * 优先级(默认0，中1，高2)
     */
    private Integer priority;

    /**
     * 任务分类
     */
    private String tags;

    /**
     * 任务状态(未完成0，已完成1，已过期2)
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 逻辑删除(默认0，1已删除)
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