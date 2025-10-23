package com.interlude.service;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserEmailCode;
import com.interlude.entity.query.UserEmailCodeQuery;
/**
 * @Description:邮箱验证码Service
 * @auther:dazhi
 * @date:2025/10/14
 */

public interface UserEmailCodeService{

	/**
	 * 根据条件查询列表
	 */
	List<UserEmailCode> findListByParam(UserEmailCodeQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserEmailCodeQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserEmailCode> findListByPage(UserEmailCodeQuery param);

	/**
	 * 新增
	 */
	Integer add(UserEmailCode bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserEmailCode> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserEmailCode> listBean);

	/**
	 * 根据EmailAndCode查询
	 */
	UserEmailCode getUserEmailCodeByEmailAndCode(String email, String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateUserEmailCodeByEmailAndCode(UserEmailCode bean, String email, String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteUserEmailCodeByEmailAndCode(String email, String code);

}