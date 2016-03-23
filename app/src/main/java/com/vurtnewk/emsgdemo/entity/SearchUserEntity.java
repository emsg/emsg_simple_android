package com.vurtnewk.emsgdemo.entity;

import com.vurtnewk.emsgdemo.base.BaseEntity;

import java.util.List;

/**
 * @author VurtneWk
 * @time created on 2016/3/21.21:19
 */
public class SearchUserEntity extends BaseEntity<SearchUserEntity.Entity> {

    public class Entity {

        private String token;
        private List<UserInfo> user_list;
        private String code;
        private String reason;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<UserInfo> getUser_list() {
            return user_list;
        }

        public void setUser_list(List<UserInfo> user_list) {
            this.user_list = user_list;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "token='" + token + '\'' +
                    ", user_list=" + user_list +
                    ", code='" + code + '\'' +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }

}
