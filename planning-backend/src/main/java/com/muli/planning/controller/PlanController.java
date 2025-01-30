package com.muli.planning.controller;

import com.muli.planning.common.BaseResponse;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.common.ResultUtils;
import com.muli.planning.domain.Plan;
import com.muli.planning.domain.Plan;
import com.muli.planning.domain.request.PlanDayRequest;
import com.muli.planning.domain.request.PlanRequest;
import com.muli.planning.domain.request.PlanRequest;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.service.PlanService;
import com.muli.planning.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan")
@CrossOrigin
public class PlanController {
    @Resource
    private PlanService planService;

    @Resource
    private UserService userService;

    /**
     * 新增计划表
     * @param planRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPlan(@RequestBody PlanRequest planRequest, HttpServletRequest request){
        //先判断传入参数是否为空
        if(planRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断用户是否为登录，登录了拿用户id
        Long userId = userService.getLoginUser(request).getId();
        if(userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long result = planService.addPlan(userId, planRequest);
        return ResultUtils.success(result);
    }

    /**
     * 获取没有开始时间的计划
     * @param request
     * @return
     */
    @PostMapping("/getPlan")
    public BaseResponse<List<Plan>> getPlan(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        List<Plan> plans = planService.getPlan(userId);
        return ResultUtils.success(plans);
    }

    /**
     * 获取有开始时间的计划
     * @param request
     * @return
     */
    @PostMapping("/getPlanTime")
    public BaseResponse<List<Plan>> getPlanTime(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        List<Plan> plans = planService.getPlanStart(userId);
        return ResultUtils.success(plans);
    }


    /**
     * 更新计划
     * @param planRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePlan(@RequestBody PlanRequest planRequest, HttpServletRequest request){
        if (planRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Boolean result = planService.updatePlan(planRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 删除计划
     * @param planRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Integer> deletePlan(@RequestBody PlanRequest planRequest, HttpServletRequest request){
        if(planRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Integer result = planService.deletePlan(planRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 完成计划
     * @param planRequest
     * @param request
     * @return
     */
    @PostMapping("/complete")
    public BaseResponse<Boolean> completePlan(@RequestBody PlanRequest planRequest, HttpServletRequest request){
        if(planRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Boolean result = planService.completePlan(planRequest, userId);
        return ResultUtils.success(result);
    }
}
