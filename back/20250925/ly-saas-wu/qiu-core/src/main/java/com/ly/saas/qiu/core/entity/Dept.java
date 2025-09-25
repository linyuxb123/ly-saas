package com.ly.saas.qiu.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体类
 */
@Data
@TableName("saas_dept")
@Alias("qiuDept")
public class Dept {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 父部门ID
     */
    private Long parentId;
    
    /**
     * 祖级列表
     */
    private String ancestors;
    
    /**
     * 显示顺序
     */
    private Integer orderNum;
    
    /**
     * 负责人
     */
    private String leader;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建人
     */
    private Long createBy;
    
    /**
     * 更新人
     */
    private Long updateBy;
    
    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
    
    /**
     * 子部门列表
     */
    @TableField(exist = false)
    private List<Dept> children = new ArrayList<>();
}