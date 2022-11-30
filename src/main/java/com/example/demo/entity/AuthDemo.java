package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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


}
