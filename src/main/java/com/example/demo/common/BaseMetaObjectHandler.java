package com.example.demo.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatisPlus的自动注入
 */
@Component
public class BaseMetaObjectHandler implements MetaObjectHandler {

    public static final String FIELD_CREATE_TIME = "createTime";
    public static final String FIELD_UPDATE_TIME = "updateTime";
    public static final String FIELD_CREATE_USER = "createUserId";
    public static final String FIELD_DEPT_ID = "deptId";
    public static final String FIELD_UPDATE_USER = "updateUserId";

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        this.strictInsertFill(metaObject, FIELD_CREATE_TIME, Date.class, date);
//        if(UserUtil.getUserId()!=null) {
//            this.strictInsertFill(metaObject, FIELD_CREATE_USER, Long.class, UserUtil.getUserId());
//        }
//        if(UserUtil.getUser()!=null && UserUtil.getUser().getDeptId()!=null) {
//            this.strictInsertFill(metaObject, FIELD_DEPT_ID, Integer.class, UserUtil.getUser().getDeptId());
//        }
    }


    @Override
    public MetaObjectHandler fillStrategy(MetaObject metaObject, String fieldName, Object fieldVal) {
        setFieldValByName(fieldName, fieldVal, metaObject);
        return this;
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, FIELD_UPDATE_TIME, Date.class, new Date());
//        if (UserUtil.getUserId()!=null) {
//            this.strictUpdateFill(metaObject, FIELD_UPDATE_USER, Integer.class, UserUtil.getUserId());
//        }
        TableInfo tableInfo = findTableInfo(metaObject);
    }
}
