/**
 * data-server. 2012-6-21
 */
package com.anxlng.vts.common.future;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public interface VtsFuture<T extends VtsFuture> {

	/**
	 * 等待异步处理事件的完成；可以通过关联的listener监听事件的完成
	 * @return 异步处理事件的关联vtsFuture
	 */
	T await() throws InterruptedException;
	
	/**
	 * 等待异步处理事件的完成; 如果超时，则停止，并返回结果
	 * @param timeoutMillis 超时时间
	 * @return 处理结果，如果成功则返回true
	 * @throws InterruptedException wait如果中断后抛出异常
	 */
	boolean await(long timeoutMillis) throws InterruptedException;
	
	/**
	 * 等待异步处理事件的完成; 并返回结果，如果超时，则停止，并返回结果
	 * @param timeout 超时时间
	 * @param unit 时间单位
	 * @return 处理结果，如果成功则返回true
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
	
	/**
	 * 等待异步处理事件的完成，不出现中断; 并返回结果，对事件的wait方法中断不影响等待的继续执行
	 * @return 处理结果，如果成功则返回true
	 */
	boolean awaitUninterruptibly();
	
	/**
	 * 等待异步处理事件的完成，不出现异常中断; 并返回结果，对事件的wait方法中断不影响等待的继续执行
	 * @param timeoutMillis 超时时间，毫秒
	 * @return 处理结果，如果成功则返回true
	 */
	boolean awaitUninterruptibly(long timeoutMillis);
	
	/**
	 * 等待异步处理事件的完成，不出现中断; 并返回结果，对事件的wait方法中断不影响等待的继续执行
	 * @param timeout 超时时间
	 * @param unit 时间单位
	 * @return 处理结果，如果成功则返回true
	 */
	boolean awaitUninterruptibly(long timeout, TimeUnit unit);
	
	/**
     * 异步处理操作完成后返回的结果
     */
    boolean isDone();
	
	/**
	 * 添加关联的异步事件的处理监听器
	 * @param listener 监听器
	 * @return
	 */
	T addFutureListener(VtsFutureListener<T> listener);
	
	/**
	 * 取消关联的异步事件的处理监听器
	 * @param listener 监听器
	 * @return
	 */
	T removeFutureListener(VtsFutureListener<T> listener);
	
}
