package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muli.planning.domain.User;
import generator.service.UserService;
import com.muli.planning.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 沐璃
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-01-18 18:09:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




