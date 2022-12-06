package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author elliott
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_demo")
@ApiModel(value = "AuthDemo对象", description = "权限测试")
public class AuthDemo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer field1;

    private String field2;

    private Integer node;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;


}
