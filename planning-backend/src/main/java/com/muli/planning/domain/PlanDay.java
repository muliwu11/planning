package com.muli.planning.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName plan_day
 */
@TableName(value ="plan_day")
@Data
public class PlanDay implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 该计划归属哪位用户
     */
    private Long userId;

    /**
     * 任务名称（1 - 16字符）
     */
    private String name;

    /**
     * 任务详细描述
     */
    private String description;

    /**
     * 任务状态（0未完成， 1已完成）
     */
    private Integer state;

    /**
     * 优先级(默认0，中1，高2)
     */
    private Integer priority;

    /**
     * 重复次数(默认100000)
     */
    private Integer count;

    /**
     * 开始时间(默认为创建该计划当天)
     */
    private Date startTime;

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
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}