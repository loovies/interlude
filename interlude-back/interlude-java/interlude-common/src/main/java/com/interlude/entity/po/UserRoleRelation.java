package com.interlude.entity.po;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:
 * @auther:dazhi
 * @date:2025/10/14
 */
public class UserRoleRelation implements Serializable {
	/**
	 * 关联ID
	 */
	private Integer id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 角色ID
	 */
	private Long roleId;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getRoleId() {
		return this.roleId;
	}

	@Override
	public String toString() {
		return "关联ID:" + (id == null ? "空" : id) + ",用户ID:" + (userId == null ? "空" : userId) + ",角色ID:" + (roleId == null ? "空" : roleId);
	}
}