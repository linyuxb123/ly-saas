package com.ly.saas.xia.api.controller;

import com.ly.saas.xia.core.constant.Constants;
import com.ly.saas.xia.core.entity.Dept;
import com.ly.saas.xia.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController("xiaDeptController")
@RequestMapping(Constants.API_PREFIX + "/dept")
public class DeptController {

    @Autowired
    @Qualifier("xiaDeptService")
    private DeptService deptService;

    /**
     * 获取部门列表
     *
     * @param dept 查询条件
     * @return 部门列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Dept>> list(Dept dept) {
        List<Dept> depts = deptService.listDepts(dept);
        return ResponseEntity.ok(depts);
    }

    /**
     * 获取部门树结构
     *
     * @param dept 查询条件
     * @return 部门树结构
     */
    @GetMapping("/tree")
    public ResponseEntity<List<Dept>> tree(Dept dept) {
        List<Dept> depts = deptService.listDepts(dept);
        List<Dept> deptTree = deptService.buildDeptTree(depts);
        return ResponseEntity.ok(deptTree);
    }

    /**
     * 获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dept> getInfo(@PathVariable("id")  Long id) {
        Dept dept = deptService.getById(id);
        return ResponseEntity.ok(dept);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Boolean> add(@RequestBody Dept dept) {
        boolean result = deptService.save(dept);
        return ResponseEntity.ok(result);
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 结果
     */
    @PutMapping
    public ResponseEntity<Boolean> update(@RequestBody Dept dept) {
        boolean result = deptService.updateById(dept);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id")  Long id) {
        boolean result = deptService.removeById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取子部门ID列表
     *
     * @param id 部门ID
     * @return 子部门ID列表
     */
    @GetMapping("/child/{id}")
    public ResponseEntity<List<Long>> getChildIds(@PathVariable("id") Long id) {
        List<Long> childIds = deptService.getChildDeptIds(id);
        return ResponseEntity.ok(childIds);
    }
}