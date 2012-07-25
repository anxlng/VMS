/**
 * data-server. 2012-6-20
 */
package com.anxlng.vts.common;

/**
 * 数据报文，所有解码接收到的或者即将编码发送的数据报文
 * @author tangjixing <anxlng@sina.com>
 */
public interface Message<T> {

	/**
	 * 信息包，报文包装内的信息包；用来进行发送和查看源数据；
	 * 如果是接收的报文，则是 通过 codec 解码后的数据包
	 * 如果是准备发送的报文，则是即将 codec 编码发送的数据包
	 * @return
	 */
	T getPacket();
}
