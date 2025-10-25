package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: Mapper
 * @auther:dazhi
 * @date:2025/10/14
 */
public interface UserRoleRelationMapper<T, P> extends BaseMapper {
	/**
	 * 根据Id查询
	 */
	T selectById(@Param("id") Integer id);

	T selectByUserId(@Param("userId") String userId);

	/**
	 * 根据Id更新
	 */
	Integer updateById(@Param("bean") T t, @Param("id") Integer id);

	/**
	 * 根据Id删除
	 */
	Integer deleteById(@Param("id") Integer id);


}