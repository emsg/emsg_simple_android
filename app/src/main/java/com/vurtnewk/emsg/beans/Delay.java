package com.vurtnewk.emsg.beans;

import java.util.ArrayList;
import java.util.List;

public class Delay <T> {
	
	private int total = 0;
	
	private List<IPacket<T>> packets = new ArrayList<IPacket<T>>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<IPacket<T>> getPackets() {
		return packets;
	}

	public void setPackets(List<IPacket<T>> packets) {
		this.packets = packets;
	}
	
}
