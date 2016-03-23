package com.vurtnewk.emsgdemo.entity;

import com.vurtnewk.emsgdemo.base.BaseEntity;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.22:11
 */
public class SimpleData extends BaseEntity<SimpleData.Entity> {

    public class Entity {
        public String code;
        public String reason;
    }
}
