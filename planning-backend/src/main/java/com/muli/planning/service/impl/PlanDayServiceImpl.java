package com.muli.planning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.domain.PlanDay;
import com.muli.planning.domain.request.PlanDayRequest;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.mapper.PlanDayMapper;
import com.muli.planning.service.PlanDayService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 沐璃
* @description 针对表【plan_day】的数据库操作Service实现
* @createDate 2025-01-18 18:09:31
*/
@Service
public class PlanDayServiceImpl extends ServiceImpl<PlanDayMapper, PlanDay> implements PlanDayService {
    @Resource
    private PlanDayMapper planDayMapper;


    @Override
    public Long addPlanDay(Long userId, PlanDayRequest planDayRequest) {
        //判断计划名称是否符合规则
        if(planDayRequest.getName() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称不能为空");
        }
        if(planDayRequest.getName().isEmpty() || planDayRequest.getName().length() > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称必须为1 - 16字符");
        }
        //计划详细描述不能大于500字符
        if(!(planDayRequest.getDescription() == null) && planDayRequest.getDescription().length() > 500){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划描述不能大于500字符");
        }
        //判断优先级是否符合规则
        if (planDayRequest.getPriority() > 3){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优先级暂时支持低、中、高三种");
        }
        //重复次数不能为负数
        if(planDayRequest.getCount() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "重复次数不能为负数");
        }
        //设置userId
        PlanDay planDay = requestTo(planDayRequest, userId);
        //新增计划
        boolean saveResult = this.save(planDay);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return planDay.getId();
    }

    //todo 重复次数还没用上
    @Override
    public List<PlanDay> getPlanDay(Long userId) {
        QueryWrapper<PlanDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<PlanDay> planDays = this.list(queryWrapper);
        //对更新时间不在今天的状态进行置0
        for(PlanDay planDay : planDays){
            LocalDate localDate = planDay.getUpdateTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 比较两个LocalDate是否相等
            if(!localDate.isEqual(today)){
                planDay.setState(0);
                PlanDay newPlanDay = new PlanDay();
                newPlanDay.setState(0);
                newPlanDay.setId(planDay.getId());
                updateById(newPlanDay);
            }
        }
        return planDays;
    }

    @Override
    public Boolean updatePlanDay(PlanDayRequest planDayRequest, Long userId) {
        //判断计划名称是否符合规则
        if(planDayRequest.getName() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称不能为空");
        }
        if(planDayRequest.getName().isEmpty() || planDayRequest.getName().length() > 16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称必须为1 - 16字符");
        }
        //计划详细描述不能大于500字符
        if(!(planDayRequest.getDescription() == null) && planDayRequest.getDescription().length() > 500){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划描述不能大于500字符");
        }
        //计划状态只能为未完成0和已完成
        if(planDayRequest.getState() != 1 && planDayRequest.getState() != 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划状态出错");
        }
        //判断优先级是否符合规则
        if (planDayRequest.getPriority() > 3){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优先级暂时支持低、中、高三种");
        }
        //重复次数不能为负数
        if(planDayRequest.getCount() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "重复次数不能为负数");
        }
        //验证该计划是否属于该用户
        PlanDay planDay = planDayMapper.selectById(planDayRequest.getId());
        if(!Objects.equals(planDay.getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        planDay = requestTo(planDayRequest, userId);
        planDay.setId(planDayRequest.getId());
        Boolean result = updateById(planDay);
        return result;
    }

    @Override
    public Integer deletePlanDay(PlanDayRequest planDayRequest, Long userId) {
        PlanDay planDay = planDayMapper.selectById(planDayRequest.getId());
        if(!Objects.equals(planDay.getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        int result = planDayMapper.deleteById(planDay);
        if(result <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Boolean completePlanDay(PlanDayRequest planDayRequest, Long userId) {
        PlanDay planDay = planDayMapper.selectById(planDayRequest.getId());
        if(!Objects.equals(planDay.getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        planDay = new PlanDay();
        planDay.setId(planDayRequest.getId());
        planDay.setState(1);
        Boolean result = updateById(planDay);
        return result;
    }

    private PlanDay requestTo(PlanDayRequest planDayRequest, Long userId){
        PlanDay planDay = new PlanDay();
        planDay.setUserId(userId);
        planDay.setName(planDayRequest.getName());
        planDay.setDescription(planDayRequest.getDescription());
        planDay.setState(planDayRequest.getState());
        planDay.setPriority(planDayRequest.getPriority());
        planDay.setCount(planDayRequest.getCount());
        planDay.setStartTime(planDayRequest.getStartTime());
        return planDay;
    }
}




