/**
 * data-server. 2012-6-22
 */
package com.anxlng.vts.common;

import java.net.SocketAddress;

/**
 * 表示一个会话session
 * @author tangjixing <anxlng@sina.com>
 */
public interface Session {

	/**
	 * 通过key在session中获取用户自定义的 key-value值
	 * @param key
	 * @return
	 */
	<T> T getAttribute(AttributeKey<T> key);
	
	/**
	 * 自定义设置key-value值对属性
	 * @param key 不能为null
	 * @param value 不能为null
	 * @return 如果对应的key已有value值，则返回老的value值，如果没有，则返回null
	 */
	<T, E extends T> T setAttribute(AttributeKey<T> key, E value);
	
	/**
	 * 获取会话的唯一名称，如果远程对象未进行注册名称，则会话名称为远程地址格式为 IP:port
	 * 如果已远程通过报文进行名称注册，则使用注册名称
	 * @return 会话唯一名称
	 */
	String getName();
	
	/**
	 * 会话的远程地址
	 * @return 格式为  IP:Port 字符串
	 */
	SocketAddress getRemoteAddress();
	
	SocketAddress getLocalAddress();
	
	/**
	 * 返回session当前状态，是否验证注册，加密等
	 * @return
	 */
	SessionState getState();
	
	/**
	 * 获取上下文关联的对象
	 * @return
	 */
	VtsServerContext getServerContext();
	
	/**
	 * 
	 * @param immediately 如果为true，则立即关闭；否则发送完剩余数据，然后关闭
	 */
	void close(boolean immediately);
	
	
	/**
	 * 向远程会话地址发送数据
	 * @param msg
	 */
	void write(Message<?> msg);
	
	/**
	 * 添加监听器
	 * @param listener
	 */
	void addListener(SessionListener listener);
	
	
	/**
	 * 移除session监听器
	 * @param listener
	 */
	void removeListener(SessionListener listener);
	
	
	public class AttributeKey<T> {	
	}
	
	/**
	 * 
	 * session状态，session整个周期的各个时期状态
	 * session可以根据各个时期的状态来进行下一步操作
	 * 如：客户端和服务器的session建立，这个时候是initiated需要远程发送登陆包，
	 * 这个时候服务器根据状态判断是否接收到的是登陆包，否则不处理
	 */
	public enum SessionState {
		//这个根据后期开发，可能状态需要进行微调
		/**
		 * 未连接，初始状态
		 */
		UNCONNECTED,
		
		/**
		 * initiated，session数据流打开，但还未有数据
		 */
		INITIATED,
		
		/**
		 * 开始接收或者发送登陆包，但还未登陆加密,未登陆状态
		 * 
		 */
		STARTED,
		
//		/**
//		 * encryption,进行加密以及验证;登陆已经完成，开进行加密或者密钥交换
//		 */
//		ENCRYPTION_STARTED,
//		
//		/**
//		 * 加密文成
//		 */
//		ENCRYPTED,
		
		/**
		 * authenticated 验证完成，这个时候，可以进行正常的进行数据通讯
		 */
		AUTHENTICATED,
		
		/**
		 * session流已经关闭
		 */
		CLOSED
	}
}
