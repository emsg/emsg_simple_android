package com.vurtnewk.emsg;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PacketWriter implements Define {

    static MyLogger logger = new MyLogger(PacketWriter.class);

    private final BlockingQueue<String> queue;

    private String heart = null;

    public void kill() {
    	try {
    		queue.put(KILL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void write(String packet) throws InterruptedException {
    	queue.put(packet);
    }

    public String take(String _heart) throws Exception {
        //如果传入的标志不等于当前对象的标志，那么请自杀
        if(heart.equals(_heart)){
            //如果1.5个心跳的时间都没有发过任何消息，那么你可以去死了
            try{
                return queue.poll(heartBeat+(int)(heartBeat/2), TimeUnit.MILLISECONDS);
            }catch(InterruptedException ie){
                return KILL;
            }
        }else{
            logger.info("packetWrite_heart_not_same");
            return KILL;
        }
    }

    protected PacketWriter(String heart) {
        this.heart = heart;
        this.queue = new ArrayBlockingQueue<String>(500, true);
    }

}
