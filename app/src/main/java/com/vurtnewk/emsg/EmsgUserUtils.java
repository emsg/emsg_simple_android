package com.vurtnewk.emsg;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.19:19
 * 后续提取 进行联系人的增删改查
 */
public class EmsgUserUtils {

    public interface OnOperateUser{
        void onSuccess();
        void onFail();
    }

    public static void addContact(String userId ,OnOperateUser onOperateUser){

    }


}
