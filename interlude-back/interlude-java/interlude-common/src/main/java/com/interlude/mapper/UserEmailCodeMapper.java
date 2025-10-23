package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:邮箱验证码 Mapper
 * @auther:dazhi
 * @date:2025/10/14
 */
public interface UserEmailCodeMapper<T, P> extends BaseMapper {
	/**
	 * 根据EmailAndCode查询
	 */
	T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateByEmailAndCode(@Param("bean") T t, @Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);


}