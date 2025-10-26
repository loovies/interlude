package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户信息表
 Mapper
 * @auther:dazhi
 * @date:2025/10/14
 */
public interface UserInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据UserId查询
	 */
	T selectByUserId(@Param("userId") String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateByUserId(@Param("bean") P p, @Param("userId") String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteByUserId(@Param("userId") String userId);

	/**
	 * 根据NickName查询
	 */
	T selectByNickName(@Param("nickName") String nickName);

	/**
	 * 根据NickName更新
	 */
	Integer updateByNickName(@Param("bean") T t, @Param("nickName") String nickName);

	/**
	 * 根据NickName删除
	 */
	Integer deleteByNickName(@Param("nickName") String nickName);

	/**
	 * 根据Phone查询
	 */
	T selectByPhone(@Param("phone") String phone);

	/**
	 * 根据Phone更新
	 */
	Integer updateByPhone(@Param("bean") T t, @Param("phone") String phone);

	/**
	 * 根据Phone删除
	 */
	Integer deleteByPhone(@Param("phone") String phone);

	/**
	 * 根据Email查询
	 */
	T selectByEmail(@Param("email") String email);

	/**
	 * 根据Email更新
	 */
	Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteByEmail(@Param("email") String email);


	T selectByParam(@Param("query") P query);

	Integer deleteByUserIds(@Param("userIds") String[] split);
}