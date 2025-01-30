package com.muli.planning.service;

import com.muli.planning.domain.Plan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muli.planning.domain.request.PlanRequest;

import java.util.List;

/**
* @author 沐璃
* @description 针对表【plan】的数据库操作Service
* @createDate 2025-01-18 17:55:19
*/
public interface PlanService extends IService<Plan> {


    /**
     * 计划表新增
     * @param userId
     * @param planRequest
     * @return
     */
    Long addPlan(Long userId, PlanRequest planRequest);

    /**
     * 获取计划表中开始时间为空的任务
     * @param userId
     * @return
     */
    List<Plan> getPlan(Long userId);

    /**
     * 获取计划表中开始时间不为空的任务
     * @param userId
     * @return
     */
    List<Plan> getPlanStart(Long userId);

    /**
     * 更新计划表
     * @param planRequest
     * @param userId
     * @return
     */
    Boolean updatePlan(PlanRequest planRequest, Long userId);

    /**
     * 删除计划
     * @param planRequest
     * @param userId
     * @return
     */
    Integer deletePlan(PlanRequest planRequest, Long userId);

    /**
     * 完成计划
     * @param planRequest
     * @param userId
     * @return
     */
    Boolean completePlan(PlanRequest planRequest, Long userId);
}
