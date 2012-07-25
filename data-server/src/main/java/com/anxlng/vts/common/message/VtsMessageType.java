/**
 * data-server. 2012-6-22
 */
package com.anxlng.vts.common.message;

import java.util.HashMap;


/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public enum VtsMessageType implements MessageProcessor{
	GPS(0x8001, new GPSMessageHandler()),
	ALARM(0x8002, new AlarmMessageHandler()),
	DEFAULT(0x0000, new DefaultMessageHandler());

	private final int type;
	private final MessageHandler handler;
	
	private static final HashMap<Integer,MessageProcessor> handlerMap;
	
	private VtsMessageType(int type, MessageHandler handler) {
		this.type = type;
		this.handler = handler;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		return Integer.toHexString(type);
	}
	
	public boolean equals(VtsMessageType type) {
		return type.type == this.type;
	}
	
	
	static {
		handlerMap = new HashMap<Integer,MessageProcessor>();
		for (VtsMessageType h : VtsMessageType.values()) {
			if (!handlerMap.containsKey(h.getType())) {
				handlerMap.put(h.getType(), h);
			}
		}
	}
	
	public static MessageProcessor getProcessor(int type) {
		return handlerMap.get(type);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enum toEnum() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageHandler getHandler() {
		return handler;
	}
}
