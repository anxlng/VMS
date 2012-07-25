/**
 * data-server. 2012-6-22
 */
package com.anxlng.vts.common.message;

import com.anxlng.vts.common.Message;

/**
 * Vessel Tracking Server内部通讯协议报文
 * @author tangjixing <anxlng@sina.com>
 */
public class AbstractDataServerMessage implements Message<byte[]>{

	private byte[] packet;
	
	
	public AbstractDataServerMessage(byte[] packet) {
		this.packet = packet;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getPacket() {
		// TODO Auto-generated method stub
		return packet;
	}
	
	

}
