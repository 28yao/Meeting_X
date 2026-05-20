package com.meeting.booking.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting.booking.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户数据访问接口，提供按用户名查询等操作。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按登录账号查询用户。
     *
     * @param username 登录账号
     * @return 用户记录，不存在时返回 null
     */
    default SysUser selectByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1");
        return selectOne(wrapper);
    }

    /**
     * 统计已启用的管理员数量。
     *
     * @return 启用中的 ADMIN 用户数
     */
    default long countEnabledAdmins() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "ADMIN")
                .eq(SysUser::getEnabled, 1);
        Long count = selectCount(wrapper);
        return count == null ? 0L : count.longValue();
    }
}
