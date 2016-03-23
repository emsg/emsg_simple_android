package com.vurtnewk.emsgdemo.base;

import java.io.Serializable;

/**
 * @author VurtneWk
 * @time created on 2016/3/17.16:23
 */
public abstract class  BaseEntity<T> implements Serializable{

    private String sn;
    private boolean success;
    private T entity;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "sn='" + sn + '\'' +
                ", success=" + success +
                ", entity=" + entity +
                '}';
    }
}
