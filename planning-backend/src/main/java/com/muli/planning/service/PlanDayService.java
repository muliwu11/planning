package com.muli.planning.service;

import com.muli.planning.domain.PlanDay;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muli.planning.domain.request.PlanDayRequest;

import java.util.List;

/**
* @author 沐璃
* @description 针对表【plan_day】的数据库操作Service
* @createDate 2025-01-18 18:09:31
*/
public interface PlanDayService extends IService<PlanDay> {

    /**
     * 日程表计划添加
     * @param userId
     * @param planDay
     * @return
     */
    Long addPlanDay(Long userId, PlanDayRequest planDayRequest);

    /**
     *
     * @param userId
     * @return
     */
    List<PlanDay> getPlanDay(Long userId);

    /**
     * 更新日程表计划
     * @param planDay
     * @param userId
     * @return
     */
    Boolean updatePlanDay(PlanDayRequest planDayRequest, Long userId);

    /**
     * 删除日程表计划
     * @param planDayRequest
     * @param userId
     * @return
     */

    Integer deletePlanDay(PlanDayRequest planDayRequest, Long userId);

    /**
     * 完成计划
     * @param planDayRequest
     * @param userId
     * @return
     */
    Boolean completePlanDay(PlanDayRequest planDayRequest, Long userId);
}
