package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muli.planning.domain.Plan;
import generator.service.PlanService;
import com.muli.planning.mapper.PlanMapper;
import org.springframework.stereotype.Service;

/**
* @author 沐璃
* @description 针对表【plan】的数据库操作Service实现
* @createDate 2025-01-18 17:55:19
*/
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan>
    implements PlanService{

}




