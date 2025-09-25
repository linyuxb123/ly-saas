package com.ly.saas.qiu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.saas.qiu.core.entity.Dept;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DeptService extends IService<Dept> {
    
    /**
     * 查询部门列表
     *
     * @param dept 部门查询条件
     * @return 部门列表
     */
    List<Dept> listDepts(Dept dept);
    
    /**
     * 构建部门树结构
     *
     * @param depts 部门列表
     * @return 部门树结构
     */
    List<Dept> buildDeptTree(List<Dept> depts);
    
    /**
     * 根据部门ID查询所有子部门ID
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> getChildDeptIds(Long deptId);
}