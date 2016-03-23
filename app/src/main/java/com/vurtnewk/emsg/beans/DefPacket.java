package com.vurtnewk.emsg.beans;


import com.vurtnewk.emsg.Define;

import java.util.Map;
import java.util.UUID;

/**
 * 一个数据包，包含envelope和一个payload
 * @author liangc
 */
public class DefPacket implements IPacket<DefPayload>{
	
	private IEnvelope envelope = null;
	
	private DefPayload payload = null;
	
	private Entity entity = null;
	
	private String vsn = Define.VSN;
	
	private Delay<DefPayload> delay = null;
	
	private Pubsub pubsub = null;
	
	public DefPacket() {
		super();
	}
	
	public DefPacket(String to,String content,Integer type){
		quickBuild(to,content,type,Define.ACK_ENABLE,null);
	}
	
	public DefPacket(String to,String content,Integer type,Integer ack){
		quickBuild(to,content,type,ack,null);
	}
	
	public DefPacket(String to,String content,Integer type,Integer ack,Map<String,String> attrs){
		quickBuild(to,content,type,ack,attrs);
	}
	
	private void quickBuild(String to,String content,Integer type,Integer ack,Map<String,String> attrs){
		IEnvelope envelope = new Envelope();
		envelope.setId(UUID.randomUUID().toString());
		envelope.setAck(ack);
		envelope.setTo(to);
		envelope.setType(type);
		
		DefPayload payload = new DefPayload();
		payload.setContent(content);
		payload.setAttrs(attrs);
		
		this.envelope = envelope;
		this.payload = payload;
	}
	
	public DefPacket(IEnvelope envelope, DefPayload payload) {
		super();
		this.envelope = envelope;
		this.payload = payload;
	}

	@Override
	public void setEnvelope(IEnvelope envelope) {
		this.envelope = envelope;
	}

	@Override
	public void setPayload(DefPayload payload) {
		this.payload = payload;
	}

	@Override
	public IEnvelope getEnvelope() {
		return envelope;
	}

	@Override
	public DefPayload getPayload() {
		return payload;
	}

	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public String getVsn() {
		return vsn;
	}

	@Override
	public void setVsn(String vsn) {
		this.vsn = vsn;
	}

	@Override
	public void setDelay(Delay<DefPayload> delay) {
		this.delay = delay;
	}

	@Override
	public Delay<DefPayload> getDelay() {
		return delay;
	}

	public Pubsub getPubsub() {
		return pubsub;
	}

	public void setPubsub(Pubsub pubsub) {
		this.pubsub = pubsub;
	}

	@Override
	public String toString() {
		return "DefPacket [envelope=" + envelope + ", payload=" + payload
				+ ", entity=" + entity + ", vsn=" + vsn + ", delay=" + delay
				+ ", pubsub=" + pubsub + "]";
	}
	
}
