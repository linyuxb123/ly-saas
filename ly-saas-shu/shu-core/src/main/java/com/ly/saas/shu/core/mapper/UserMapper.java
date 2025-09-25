package com.ly.saas.shu.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.shu.core.constant.Constants;
import com.ly.saas.shu.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository(Constants.PREFIX + "UserMapper")
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM saas_user")
    List<User> allUsers();
}