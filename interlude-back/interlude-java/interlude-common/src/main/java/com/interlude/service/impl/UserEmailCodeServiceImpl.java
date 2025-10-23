package com.interlude.service.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserEmailCode;
import com.interlude.entity.query.UserEmailCodeQuery;
import com.interlude.mapper.UserEmailCodeMapper;
import com.interlude.service.UserEmailCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:邮箱验证码Service
 * @auther:dazhi
 * @date:2025/10/14
 */

@Service("userEmailCodeService")
public class UserEmailCodeServiceImpl implements UserEmailCodeService{

	@Resource
	private UserEmailCodeMapper<UserEmailCode,UserEmailCodeQuery> userEmailCodeMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserEmailCode> findListByParam(UserEmailCodeQuery query) {
		return this.userEmailCodeMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserEmailCodeQuery query) {
		return this.userEmailCodeMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserEmailCode> findListByPage(UserEmailCodeQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserEmailCode> listByParam = this.findListByParam(query);
		PaginationResultVO<UserEmailCode> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserEmailCode bean) {
		return this.userEmailCodeMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserEmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userEmailCodeMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserEmailCode> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userEmailCodeMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据EmailAndCode查询
	 */
	public UserEmailCode getUserEmailCodeByEmailAndCode(String email, String code) {
		return this.userEmailCodeMapper.selectByEmailAndCode(email, code);
	}

	/**
	 * 根据EmailAndCode更新
	 */
	public Integer updateUserEmailCodeByEmailAndCode(UserEmailCode bean, String email, String code) {
		return this.userEmailCodeMapper.updateByEmailAndCode(bean, email, code);
	}

	/**
	 * 根据EmailAndCode删除F
	 */
	public Integer deleteUserEmailCodeByEmailAndCode(String email, String code) {
		return this.userEmailCodeMapper.deleteByEmailAndCode(email, code);
	}
}