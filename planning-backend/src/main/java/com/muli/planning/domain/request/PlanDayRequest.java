package com.muli.planning.domain.request;

import lombok.Data;

import java.io.Serial;
import java.util.Date;


/**
 * 日常计划请求体
 */
@Data
public class PlanDayRequest {
    @Serial
    private static final long serialVersionUID = 1363717773786437684L;

    private Long id;

    private String name;

    private String description;

    private int state;

    private int priority;

    private int count = 100000;

    private Date startTime;
}
