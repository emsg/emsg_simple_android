package com.vurtnewk.emsg.beans;

public interface IPacket<T> {

    void setEnvelope(IEnvelope envelope);

    IEnvelope getEnvelope();

    void setPayload(T payload);

    T getPayload();

    void setEntity(Entity entity);

    Entity getEntity();

    void setVsn(String vsn);

    String getVsn();

    void setDelay(Delay<T> delay);

    Delay<T> getDelay();

    void setPubsub(Pubsub pubsub);

    Pubsub getPubsub();

}
