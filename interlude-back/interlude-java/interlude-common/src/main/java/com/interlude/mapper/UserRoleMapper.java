package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: Mapper
 * @auther:dazhi
 * @date:2025/10/14
 */
public interface UserRoleMapper<T, P> extends BaseMapper {
	/**
	 * 根据RoleId查询
	 */
	T selectByRoleId(@Param("roleId") Long roleId);

	/**
	 * 根据RoleId更新
	 */
	Integer updateByRoleId(@Param("bean") T t, @Param("roleId") Long roleId);

	/**
	 * 根据RoleId删除
	 */
	Integer deleteByRoleId(@Param("roleId") Long roleId);


}