/**
 * data-server. 2012-6-22
 */
package com.anxlng.vts.common.message;


/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public interface MessageProcessor {

	Enum toEnum();
	
	MessageHandler getHandler();
	
	
}
