package com.vurtnewk.emsg.beans;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vurtnewk.emsg.Define;
import com.vurtnewk.emsg.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class DefProvider implements IProvider<DefPayload> {

	static Gson gson = new Gson();

	public static void main(String[] args) {
		DefProvider d = new DefProvider();

		String packet = "{\"envelope\":{\"id\":\"emsg_main@192.168.2.11_1410938798206830\",\"from\":\"server_ack\",\"to\":\"liangc@test.com\",\"type\":7,\"ack\":0},\"pubsub\":{\"items\":[{\"node\":\"hello\",\"title\":\"may i fuck you ?\",\"cb\":\"liangc@test.com\",\"summary\":\"come on girls .\",\"et\":1410938785679,\"id\":null,\"ct\":1410938785679},{\"node\":\"hello\",\"title\":\"may i fuck you ?\",\"cb\":\"liangc@test.com\",\"summary\":\"come on girls .\",\"et\":1410938786582,\"id\":null,\"ct\":1410938786582}]}}";
		IPacket<DefPayload> p = d.decode(packet);
		System.out.println(packet);
		System.out.println(p);
		System.out.println(d.encode(p));
		
	}
	
	@Override
	public String encode(IPacket<DefPayload> packet) {
		return gson.toJson(packet);
	}
	@Override
	public IPacket<DefPayload> decode(String packet) {
		JsonParser parser = new JsonParser();
		JsonElement elPacket = parser.parse(packet);
		JsonObject jPacket = elPacket.getAsJsonObject();
		JsonObject jdelay = JsonUtil.getAsJsonObject(jPacket, "delay");
		JsonObject jpubsub = JsonUtil.getAsJsonObject(jPacket, "pubsub");
		
		IPacket<DefPayload> r = decode(elPacket);
		Delay<DefPayload> delay = delay(jdelay);
		Pubsub pubsub = pubsub(jpubsub);
		r.setDelay(delay);
		r.setPubsub(pubsub);
		return r;
	}

	public IPacket<DefPayload> decode(JsonElement elPacket) {
		JsonObject jPacket = elPacket.getAsJsonObject();
		JsonObject jenvelope = JsonUtil.getAsJsonObject(jPacket, "envelope");
		JsonObject jpayload = JsonUtil.getAsJsonObject(jPacket, "payload");
		
		String vsn = Define.VSN;
		if(elPacket.getAsJsonObject().has("vsn")){
			vsn = JsonUtil.getAsString(jPacket, "vsn");
		}
		
		JsonObject jentity = JsonUtil.getAsJsonObject(jPacket, "entity");
		gson.fromJson(jenvelope, Envelope.class);
		IEnvelope envelope = gson.fromJson(jenvelope, Envelope.class);
		DefPayload payload = null;
		Entity entity = null;
		IPacket<DefPayload> r = new DefPacket(envelope,payload);
		r.setVsn(vsn);
		if(jpayload!=null){
			payload = gson.fromJson(jpayload, DefPayload.class);
			r.setPayload(payload);
		}
		if(jentity!=null){
			entity = gson.fromJson(jentity, Entity.class);
			r.setEntity(entity);
		}
		return r;
	}	
	
	private Delay<DefPayload> delay(JsonObject jdelay){
		Delay<DefPayload> delay = null;
		if(jdelay!=null){
			int total = jdelay.get("total").getAsInt();
			if(total>0){
				delay = new Delay<DefPayload>();
				delay.setTotal(total);
				List<IPacket<DefPayload>> packets = new ArrayList<IPacket<DefPayload>>();
				JsonArray jpackets = jdelay.get("packets").getAsJsonArray();
				for(int i=0;i<jpackets.size();i++){
					JsonElement e = jpackets.get(i);
					IPacket<DefPayload> p = decode(e);
					packets.add(p);
				}
				delay.setPackets(packets);
			}
		}
		return delay;
	}
	
	private Pubsub pubsub(JsonObject jpubsub){
		Pubsub pubsub = null;
		if(jpubsub!=null){
			pubsub = new Pubsub();
			ArrayList<Item> items = new ArrayList<Item>();
			JsonArray jitems = jpubsub.get("items").getAsJsonArray();
			for(int i=0;i<jitems.size();i++){
				JsonElement jitem = jitems.get(i);
				Item item = gson.fromJson(jitem, Item.class);
				items.add(item);
			}
			pubsub.setItems(items);
		}
		return pubsub;
	}
	
}
