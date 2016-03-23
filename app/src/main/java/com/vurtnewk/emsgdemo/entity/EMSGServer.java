package com.vurtnewk.emsgdemo.entity;

/**
 * @author VurtneWk
 * @time created on 2016/3/18.15:29
 */
public class EMSGServer {

    private String host;
    private String port;
    private String domain;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "EMSGServer{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
