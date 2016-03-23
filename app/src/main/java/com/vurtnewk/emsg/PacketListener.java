/**
 * $RCSfile$
 * $Revision: 11613 $
 * $Date: 2010-02-09 03:55:56 -0800 (Tue, 09 Feb 2010) $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vurtnewk.emsg;


import com.vurtnewk.emsg.beans.IPacket;
import com.vurtnewk.emsg.beans.Pubsub;

import java.util.List;

public interface PacketListener<T> {
	
	/**
	 * 
	 * @param packet
	 */
	 void processPacket(IPacket<T> packet);

	/**
	 * 打开session 的包
	 * @param packet
	 */
	 void sessionPacket(IPacket<T> packet);

	/**
	 * 离线消息
	 * @param packets
	 */
	 void offlinePacket(List<IPacket<T>> packets);
	
	/**
	 * 订阅消息
	 * @param pubsub
	 */
	 void pubsubPacket(Pubsub pubsub);

	/**
	 * 类型 type=5 type=6 
	 * @param packet
	 */
	 void mediaPacket(IPacket<T> packet);

}
