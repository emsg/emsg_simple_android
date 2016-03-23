package com.vurtnewk.emsg;


import com.vurtnewk.emsg.beans.DefPacket;
import com.vurtnewk.emsg.beans.DefPayload;
import com.vurtnewk.emsg.beans.Entity;
import com.vurtnewk.emsg.beans.Envelope;
import com.vurtnewk.emsg.beans.IEnvelope;
import com.vurtnewk.emsg.beans.IPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author liangc
 */
public class P2PHandler<T> implements Define {

    static MyLogger logger = new MyLogger(P2PHandler.class);

    private Socket socket = null;
    private InputStream reader = null;
    private OutputStream writer = null;

    public void open(IPacket<T> packet) throws UnknownHostException, IOException {
        int type = packet.getEnvelope().getType();
        if (MSG_TYPE_P2P_SOUND == type || MSG_TYPE_P2P_VIDEO == type) {
            Map<String, String> attrs = packet.getEntity().getAttrs();
            String sid = attrs.get("sid");
            String host = attrs.get("host");
            String ip = host.split(":")[0];
            int port = Integer.parseInt(host.split(":")[1]);
            logger.debug("p2p__ip=" + ip + "  ;  port=" + port + "  ;  sid=" + sid);
            this.socket = new Socket(ip, port);
            socket.getOutputStream();
            reader = socket.getInputStream();
            writer = socket.getOutputStream();
            byte[] sid_bin = sid.getBytes();
            int len = sid_bin.length * 8;
            System.out.println(len + "_" + sid);
            byte[] len_bin = int2binary(len);
            byte[] packet_bin = new byte[sid_bin.length + 4];
            for (int i = 0; i < packet_bin.length; i++) {
                if (i < 4) {
                    packet_bin[i] = len_bin[i];
                } else {
                    packet_bin[i] = sid_bin[i - 4];
                }
            }
            writer.write(packet_bin);
        }
    }

    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    public void send_packet(byte[] packet) {

    }

    public static void main(String[] args) throws Exception {
        IPacket<DefPayload> packet = new DefPacket();
        IEnvelope envelope = new Envelope();
        envelope.setType(MSG_TYPE_P2P_SOUND);
        Entity entity = new Entity();
        entity.attrs.put("host", "192.168.2.11:4321");
        entity.attrs.put("sid", "zzzzzzzzzzzzzzzz");
        packet.setEntity(entity);
        packet.setEnvelope(envelope);
        P2PHandler<DefPayload> p2p = new P2PHandler<DefPayload>();
        p2p.open(packet);
        System.out.println("open");
        Thread.sleep(500000);
    }

    private byte[] int2binary(int i) {
        byte[] buff = new byte[4];
        buff[0] = ((byte) (i >> 24));
        buff[1] = ((byte) (i >> 16));
        buff[2] = ((byte) (i >> 8));
        buff[3] = ((byte) (i & 0xff));
        return buff;
    }

}