package com.muli.planning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.domain.Plan;
import com.muli.planning.domain.request.PlanRequest;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.mapper.PlanMapper;
import com.muli.planning.service.PlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
* @author 沐璃
* @description 针对表【plan】的数据库操作Service实现
* @createDate 2025-01-18 17:55:19
*/
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    @Resource
    private PlanMapper planMapper;

    @Override
    public Long addPlan(Long userId, PlanRequest planRequest) {
        //判断计划名称是否符合规则
        if (planRequest.getName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称不能为空");
        }
        if (planRequest.getName().isEmpty() || planRequest.getName().length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称必须为1 - 16字符");
        }
        //计划详细描述不能大于500字符
        if (planRequest.getDescription() != null && planRequest.getDescription().length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划描述不能大于500字符");
        }
        //判断优先级是否符合规则
        if (planRequest.getPriority() > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优先级暂时支持低、中、高三种");
        }
        //判断类型格式
        if (planRequest.getTags() != null && planRequest.getTags().length() >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "类型必须小于5个字符");
        }
        //结束时间不能大于开始时间
        if (planRequest.getStartTime() != null && planRequest.getEndTime() != null){
            LocalDate startTime = planRequest.getStartTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endTime = planRequest.getEndTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if(startTime.isAfter(endTime)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "开始时间必须早于结束时间");
            }
        }
        Plan plan = requestTo(planRequest, userId);
        //新增计划
        boolean saveResult = this.save(plan);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return plan.getId();
    }

    @Override
    public List<Plan> getPlan(Long userId) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.isNull("start_time");
        List<Plan> plans = this.list(queryWrapper);
        return plans;
    }

    @Override
    public List<Plan> getPlanStart(Long userId) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.isNotNull("start_time");
        List<Plan> plans = this.list(queryWrapper);
        //对计划的状态进行判断
        for(Plan plan : plans){
            if(plan.getEndTime() == null){
                continue;
            }
            LocalDate startTime = plan.getStartTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate endTime = plan.getEndTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            // 获取当前日期
            LocalDate today = LocalDate.now();

            // 比较开始时间来判断是否是未开始
            if(startTime.isAfter(today)){
                plan.setState(2);
                Plan newPlan = new Plan();
                newPlan.setState(2);
                newPlan.setId(plan.getId());
                updateById(newPlan);
                continue;
            }

            // 比较结束时间来判断是否是已过期
            if(endTime.isBefore(today)){
                plan.setState(3);
                Plan newPlan = new Plan();
                newPlan.setState(3);
                newPlan.setId(plan.getId());
                updateById(newPlan);
            }
        }
        return plans;
    }

    @Override
    public Boolean updatePlan(PlanRequest planRequest, Long userId) {
        if (planRequest.getName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称不能为空");
        }
        if (planRequest.getName().isEmpty() || planRequest.getName().length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名称必须为1 - 16字符");
        }
        //计划详细描述不能大于500字符
        if (planRequest.getDescription() != null && planRequest.getDescription().length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划描述不能大于500字符");
        }
        //判断优先级是否符合规则
        if (planRequest.getPriority() > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优先级暂时支持低、中、高三种");
        }
        //判断类型格式
        if (planRequest.getTags() != null && planRequest.getTags().length() >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "类型必须小于5个字符");
        }
        //结束时间不能大于开始时间
        if (planRequest.getStartTime() != null && planRequest.getEndTime() != null){
            LocalDate startTime = planRequest.getStartTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endTime = planRequest.getEndTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if(startTime.isAfter(endTime)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "开始时间必须早于结束时间");
            }
        }
        //验证该计划是否属于该用户
        if(!Objects.equals(planMapper.selectById(planRequest.getId()).getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        Plan plan = requestTo(planRequest, userId);
        plan.setId(planRequest.getId());
        Boolean result = updateById(plan);
        return result;
    }

    @Override
    public Integer deletePlan(PlanRequest planRequest, Long userId) {
        Plan plan = planMapper.selectById(planRequest.getId());
        if(!Objects.equals(plan.getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        int result = planMapper.deleteById(plan);
        if(result <= 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Boolean completePlan(PlanRequest planRequest, Long userId) {
        Plan plan = planMapper.selectById(planRequest.getId());
        if(!Objects.equals(plan.getUserId(), userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "这计划不属于你");
        }
        plan = new Plan();
        plan.setId(planRequest.getId());
        plan.setState(1);
        Boolean result = updateById(plan);
        return result;
    }

    /**
     * 将request转成实体类，方便mybatis-plus进行处理
     * @param planRequest
     * @param userId
     * @return
     */
    private Plan requestTo(PlanRequest planRequest, Long userId) {
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setName(planRequest.getName());
        plan.setDescription(planRequest.getDescription());
        plan.setState(planRequest.getState());
        plan.setPriority(planRequest.getPriority());
        plan.setTags(planRequest.getTags());
        plan.setStartTime(planRequest.getStartTime());
        plan.setEndTime(planRequest.getEndTime());
        return plan;
    }


}




