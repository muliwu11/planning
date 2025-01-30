package com.muli.planning.controller;

import com.muli.planning.common.BaseResponse;
import com.muli.planning.common.ErrorCode;
import com.muli.planning.common.ResultUtils;
import com.muli.planning.domain.PlanDay;
import com.muli.planning.domain.request.PlanDayRequest;
import com.muli.planning.domain.request.PlanRequest;
import com.muli.planning.exception.BusinessException;
import com.muli.planning.service.PlanDayService;
import com.muli.planning.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planDay")
@CrossOrigin
public class PlanDayController {
    @Resource
    private PlanDayService planDayService;

    @Resource
    private UserService userService;

    /**
     * 新增日程表计划
     * @param planDayRequest
     * @param request
     * @return 该计划id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPlanDay(@RequestBody PlanDayRequest planDayRequest, HttpServletRequest request){
        //先判断传入参数是否为空
        if(planDayRequest == null){
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
        Long result = planDayService.addPlanDay(userId, planDayRequest);
        return ResultUtils.success(result);
    }

    /**
     * 查询日常计划
     * @param request
     * @return 该用户所有的日常计划
     */
    @GetMapping("/getPlanDay")
    public BaseResponse<List<PlanDay>> getPlanDay(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        List<PlanDay> planDays = planDayService.getPlanDay(userId);
        return ResultUtils.success(planDays);
    }

    /**
     * 更新日常计划
     * @param planDayRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePlanDay(@RequestBody PlanDayRequest planDayRequest, HttpServletRequest request){
        if (planDayRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Boolean result = planDayService.updatePlanDay(planDayRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 删除日常计划
     * @param planDayRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Integer> deletePlanDay(@RequestBody PlanDayRequest planDayRequest, HttpServletRequest request){
        if(planDayRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Integer result = planDayService.deletePlanDay(planDayRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 完成计划
     * @param planDayRequest
     * @param request
     * @return
     */
    @PostMapping("/complete")
    public BaseResponse<Boolean> completePlanDay(@RequestBody PlanDayRequest planDayRequest, HttpServletRequest request){
        if(planDayRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userService.getLoginUser(request).getId();
        Boolean result = planDayService.completePlanDay(planDayRequest, userId);
        return ResultUtils.success(result);
    }
}
