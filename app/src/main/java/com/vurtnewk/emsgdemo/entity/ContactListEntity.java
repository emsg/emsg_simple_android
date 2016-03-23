package com.vurtnewk.emsgdemo.entity;

import com.vurtnewk.emsgdemo.base.BaseEntity;

import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.22:15
 */
public class ContactListEntity extends BaseEntity<ContactListEntity.Entity> {

    public class Entity{
       public  List<UserInfo> contacts;
    }
}
