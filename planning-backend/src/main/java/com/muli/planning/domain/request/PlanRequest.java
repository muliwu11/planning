package com.muli.planning.domain.request;

import lombok.Data;

import java.io.Serial;
import java.util.Date;

/**
 * 计划表请求体
 */
@Data
public class PlanRequest {
    @Serial
    private static final long serialVersionUID = 1363717773786437684L;

    private Long id;

    private String name;

    private String description;

    private int state;

    private int priority;

    private String tags;

    private Date startTime;

    private Date endTime;
}
