package com.vurtnewk.emsgdemo.entity;

import com.vurtnewk.emsgdemo.base.BaseEntity;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.15:44
 */
public class LoginReturnEntity extends BaseEntity<LoginReturnEntity.Entity> {

    public class Entity {

        private String token;
        private UserInfo user;
        private EMSGServer emsg_server;
        private String code;
        private String reason;

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

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }

        public EMSGServer getEmsg_server() {
            return emsg_server;
        }

        public void setEmsg_server(EMSGServer emsg_server) {
            this.emsg_server = emsg_server;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "token='" + token + '\'' +
                    ", user=" + user +
                    ", emsg_server=" + emsg_server +
                    ", code='" + code + '\'' +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }


}
