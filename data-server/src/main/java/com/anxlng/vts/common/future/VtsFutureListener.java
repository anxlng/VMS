/**
 * data-server. 2012-6-21
 */
package com.anxlng.vts.common.future;

import java.util.EventListener;

/**
 * 响应{@link VtsFuture} 完成的一些操作
 * @author tangjixing <anxlng@sina.com>
 */
public interface VtsFutureListener<T extends VtsFuture> extends EventListener{

	/**
	 * 在VtsFuture中添加监听器后，当相应的操作执行完成后响应该方法
	 * @param future
	 */
	void operationComplete(T future);
}
