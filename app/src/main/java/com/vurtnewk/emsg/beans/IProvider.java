package com.vurtnewk.emsg.beans;

public interface IProvider<T> {
	
	/**
	 * 将 对象 转换成 字符串，发送出去
	 * @param packet
	 * @return
	 */
	public String encode(IPacket<T> packet);
	
	/**
	 * 将接收到的 字符串 转换成 对象以供使用
	 * @param packet
	 * @return
	 */
	public IPacket<T> decode(String packet);
	
}
