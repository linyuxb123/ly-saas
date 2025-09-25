package com.ly.saas.chun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.saas.chun.core.entity.Dept;
import com.ly.saas.chun.core.mapper.DeptMapper;
import com.ly.saas.chun.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service("chunDeptService")
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> listDepts(Dept dept) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        // 根据部门名称模糊查询
        if (dept != null && StringUtils.hasText(dept.getDeptName())) {
            queryWrapper.like(Dept::getDeptName, dept.getDeptName());
        }
        // 根据状态查询
        if (dept != null && dept.getStatus() != null) {
            queryWrapper.eq(Dept::getStatus, dept.getStatus());
        }
        // 排序
        queryWrapper.orderByAsc(Dept::getOrderNum);
        return list(queryWrapper);
    }

    @Override
    public List<Dept> buildDeptTree(List<Dept> depts) {
        List<Dept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(Dept::getId).collect(Collectors.toList());
        for (Dept dept : depts) {
            // 如果是顶级节点，遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    @Override
    public List<Long> getChildDeptIds(Long deptId) {
        List<Long> childIds = new ArrayList<>();
        List<Dept> deptList = list();
        recursionChildIds(deptList, deptId, childIds);
        return childIds;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<Dept> list, Dept t) {
        // 得到子节点列表
        List<Dept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Dept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<Dept> getChildList(List<Dept> list, Dept t) {
        List<Dept> tlist = new ArrayList<>();
        for (Dept n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Dept> list, Dept t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 递归获取子部门ID
     */
    private void recursionChildIds(List<Dept> deptList, Long deptId, List<Long> childIds) {
        for (Dept dept : deptList) {
            if (dept.getParentId() != null && dept.getParentId().equals(deptId)) {
                childIds.add(dept.getId());
                recursionChildIds(deptList, dept.getId(), childIds);
            }
        }
    }
}