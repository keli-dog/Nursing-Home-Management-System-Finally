package com.ew.gerocomium.common.config.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ew.gerocomium.common.config.security.handler.AuthorityAssert;
import com.ew.gerocomium.dao.vo.LoginUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mybatis对象处理器
 **/
@Slf4j
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {
    @Resource
    private AuthorityAssert authorityAssert;

    private static final String CREATEID = "createId";
    private static final String CREATETIME = "createTime";
    private static final String UPDATEID = "updateId";
    private static final String UPDATETIME = "updateTime";

    /**
     * 自动填充创建时间修改时间
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取登录信息
        LoginUserVo loginUserInfo = authorityAssert.getLoginUserInfo();
        //获取所有的字段名称
        List<String> list = Arrays.stream(metaObject.getGetterNames()).collect(Collectors.toList());
        if (list.contains(CREATEID)) {
            // 有登录信息
            if (loginUserInfo != null) {
                this.setFieldValByName(CREATEID, loginUserInfo.getId(), metaObject);
                // 无登录信息
            } else {
                this.setFieldValByName(CREATEID, 1L, metaObject);
            }
        }
        if (list.contains(CREATETIME)) {
            this.setFieldValByName(CREATETIME, new Date(), metaObject);
        }
        if (list.contains(UPDATEID)) {
            // 有登录信息
            if (loginUserInfo != null) {
                this.setFieldValByName(UPDATEID, loginUserInfo.getId(), metaObject);
                // 无登录信息
            } else {
                this.setFieldValByName(UPDATEID, 1L, metaObject);
            }
        }
        if (list.contains(UPDATETIME)) {
            this.setFieldValByName(UPDATETIME, new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 获取登录信息
        LoginUserVo loginUserInfo = authorityAssert.getLoginUserInfo();
        //获取所有的字段名称
        List<String> list = Arrays.stream(metaObject.getGetterNames()).collect(Collectors.toList());
        if (list.contains(UPDATEID)) {
            // 有登录信息
            if (loginUserInfo != null) {
                this.setFieldValByName(UPDATEID, loginUserInfo.getId(), metaObject);
                // 无登录信息
            } else {
                this.setFieldValByName(UPDATEID, 1L, metaObject);
            }
        }
        if (list.contains(UPDATETIME)) {
            this.setFieldValByName(UPDATETIME, new Date(), metaObject);
        }
    }
}
