
package com.vurtnewk.emsg;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vurtnewk.emsg.beans.DefPayload;
import com.vurtnewk.emsg.beans.IPacket;
import com.vurtnewk.emsg.util.JsonUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PacketReader<T> implements Define {

    static MyLogger logger = new MyLogger(PacketReader.class);

    private Thread readerThread;
    private final BlockingQueue<String> queue;
    private EmsgClient client = null;
    volatile boolean done;

    public void kill() {
        try {
            queue.put(KILL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void recv(String msg) throws InterruptedException {
        queue.put(msg);
    }

    protected PacketReader(EmsgClient client) {
        this.client = client;
        this.queue = new ArrayBlockingQueue<String>(500, true);
        this.init();
    }

    public void init() {
        readerThread = new Thread() {
            public void run() {
                try {
                    while (true) {
                        String packet = null;
                        try{
                            packet = queue.poll(heartBeat+(int)(heartBeat/2), TimeUnit.MILLISECONDS);
                        }catch(InterruptedException ie){
                            break;
                        }

                        if (KILL.equals(packet)) {
                            break;
                        }
                        JsonObject jp = null;//new JsonParser().parse(packet).getAsJsonObject();
                        try{
                            if(packet != null){
                                jp = new JsonParser().parse(packet).getAsJsonObject();
                            }
                        }catch(Exception e){
                            System.out.println("com.emsg packet_error ::> " + packet);
                            e.printStackTrace();
                            continue;
                        }
                        JsonObject envelope = jp.get("envelope").getAsJsonObject();
                        String id = JsonUtil.getAsString(envelope, "id");
                        if (envelope.has("ack") && JsonUtil.getAsInt(envelope, "ack", 0) == 1) {
                            sendAck(id);
                        }
                        if (envelope.has("type") && JsonUtil.getAsInt(envelope, "type", 0) == 0) {
                            JsonObject entity = jp.get("entity").getAsJsonObject();
                            String result = JsonUtil.getAsString(entity, "result");
                            if ("ok".equals(result)) {
                                client.setAuth(true);
                                sendAck(id);
                            }
                        }
                        if (client.listener != null) {
                            EmsgClient.logger.info("reader :::> " + packet);
                            IPacket<DefPayload> p = client.getProvider().decode(packet);
                            int type = p.getEnvelope().getType();
                            if (MSG_TYPE_P2P_SOUND == type || MSG_TYPE_P2P_VIDEO == type) {
                                client.listener.mediaPacket(p);
                            }
                            if (MSG_TYPE_OPEN_SESSION == type) {

                                List<IPacket<DefPayload>> packets = null;
                                if (p.getDelay() != null) {
                                    if (p.getDelay().getTotal() > 0) {
                                        packets = p.getDelay().getPackets();
                                        p.setDelay(null);
                                    }
                                }
                                client.listener.sessionPacket(p);
                                if (packets != null)
                                    client.listener.offlinePacket(packets);
                            } else if (MSG_TYPE_PUBSUB == type) {
                                client.listener.pubsubPacket(p.getPubsub());
                            } else {
                                client.listener.processPacket(p);
                            }
                        }
                    }
                    logger.info("啊啊啊我死啦 [ "+ Thread.currentThread().getName()+" ]");

                } catch (Exception e) {
                    e.printStackTrace();
                    client.shutdown("packet_reader");
                }
            }
        };
        readerThread.setName("IOListener__packet_reader__" + new Date());
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void sendAck(String id) throws InterruptedException {
        JsonObject ack = new JsonObject();
        JsonObject ack_envelope = new JsonObject();
        ack_envelope.addProperty("id", id);
        ack_envelope.addProperty("from", client.getJid());
        ack_envelope.addProperty("to", "server_ack");
        ack_envelope.addProperty("type", MSG_TYPE_STATE);
        ack.add("envelope", ack_envelope);
        client.send(ack.toString());
    }
}
