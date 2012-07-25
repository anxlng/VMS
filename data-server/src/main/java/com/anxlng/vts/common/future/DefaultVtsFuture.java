/**
 * data-server. 2012-6-21
 */
package com.anxlng.vts.common.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class DefaultVtsFuture<T extends VtsFuture> implements VtsFuture<T> {

	public static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;

	private VtsFutureListener<T> firstListener;

	private List<VtsFutureListener<T>> otherListeners;

	private final Object lock;

	private Object result;
	private boolean ready;
	private int waiters = 0; //等待结果的对象数

	public DefaultVtsFuture(Object lock) {
		this.lock = lock != null ? lock : this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T await() throws InterruptedException {

		synchronized (lock) {
			if (!ready) {
				waiters++;

				try {

					lock.wait(DEAD_LOCK_CHECK_INTERVAL);
				} finally {
					waiters--;
					if (!ready) {
						checkDeadLock();
					}
				}
			}
		}

		return (T) this;
	}

	/**
	 * 等待 future 对象执行完成；如果超时时间是0 或者负数，则立即返回当前“ready”的结果； 等待线程会每隔
	 * DEAD_LOCK_CHECK_INTERVAL 秒检查线程是否死锁或者未释放； 例如这样的情况： 有两个线程拿着同样的future,
	 * 第一个线程进行 await()，并且超时时间是 Long.MAX_VALUE,相当于无限等待了，
	 * 等待第二个线程处理完成后，进行setValue(); 第一个线程的await()才能完成并返回结果；如果第二个线程一直
	 * 不进行到setValue()这一步，或者直接跳过去了，那么第一个线程会一直重复的await 下去；这就类似一个死锁了； 死锁检查主要排查这样的问题
	 * 
	 * <h1>知识点：lock.wait()方法不占用同步锁；在等待后即释放同步锁，而sleep方法则占用同步锁！！</h1>
	 * 
	 * @param timeoutMillis
	 *            超时时间
	 * @param interruptable
	 *            是否可异常中断
	 * @return 对象执行完成结果
	 * @throws InterruptedException
	 *             如果interruptable = true，则如果wait中断后抛出
	 */
	private boolean await0(long timeoutMillis, boolean interruptable)
			throws InterruptedException {
		long endTime = System.currentTimeMillis() + timeoutMillis;

		synchronized (lock) {
			if (ready || timeoutMillis <= 0) {
				return ready;
			}

			waiters++;
			try {
				long timeout = Math
						.min(timeoutMillis, DEAD_LOCK_CHECK_INTERVAL);
				for (;;) {
					try {

						lock.wait(timeout);
					} catch (InterruptedException ex) {
						if (interruptable) {
							throw ex;
						}
					}

					if (ready || endTime < System.currentTimeMillis()) {
						return ready;
					}

					// write：追求实时，不延迟相应，一般响应在5秒内的不需要
					// rewrite：下边这个不需要，因为如果计算出结果后，在赋值时会检查是否等待，然后notifyAll
					// rewrite：一般对超时时间进行严格规定，超时实时，则可以使用下边这个表达式
					// timeout = Math.min(timeoutMillis - timeout,
					// DEAD_LOCK_CHECK_INTERVAL);
				}
			} finally {
				waiters--;
				if (!ready) {
					checkDeadLock();
				}
			}
		}

		
	}

	/**
	 * 死锁检查
	 */
	private void checkDeadLock() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return await0(timeoutMillis, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return await0(unit.toMillis(timeout), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean awaitUninterruptibly() {
		try {
			return await0(Long.MAX_VALUE, false);
		} catch (InterruptedException e) {
			throw new InternalError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		try {
			return await0(timeoutMillis, false);
		} catch (InterruptedException e) {
			throw new InternalError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		try {
			return await0(unit.toMillis(timeout), false);
		} catch (InterruptedException e) {
			throw new InternalError();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T addFutureListener(VtsFutureListener<T> listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		boolean notifyNow = false;
		synchronized (lock) {
			if (ready) {
				notifyNow = true;
			} else {
				if (firstListener == null) {
					firstListener = listener;
				} else {
					if (otherListeners == null) {
						otherListeners = new ArrayList<VtsFutureListener<T>>(1); // 初始容量只有一个
					}
					otherListeners.add(listener);
				}
			}
		}

		if (notifyNow) {
			notifyListener(listener);
		}

		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T removeFutureListener(VtsFutureListener<T> listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}

		synchronized (lock) {
			if (!ready) {
				if (listener == firstListener) {
					if (otherListeners != null && !otherListeners.isEmpty()) {
						firstListener = otherListeners.remove(0);
					} else {
						firstListener = null;
					}
				} else if (otherListeners != null) {
					otherListeners.remove(listener);

					if (otherListeners.isEmpty()) {
						otherListeners = null;
					}
				}
			}
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	private void notifyListener(VtsFutureListener<T> listener) {
		try {
			listener.operationComplete((T) this);
		} catch (Throwable t) {

		}
	}

	private void notifyListeners() {
		// 这里不需要再进行并发检查以及其他问题的检查了，因为已经在addListener和removeListener中进行了对照检查

		if (firstListener != null) {
			notifyListener(firstListener);
			firstListener = null; // 执行完后就不需要了

			if (otherListeners != null) {
				for (VtsFutureListener<T> l : otherListeners) {
					notifyListener(l);
				}
				otherListeners = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDone() {
		synchronized (lock) {
			return ready;
		}
	}

	/**
	 * 给结果赋值，并改变状态为完成状态(ready = true)
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		synchronized (lock) {
			// 只能能赋值一次
			if (ready) {
				return;
			}
			result = value;
			ready = true;
			if (waiters > 0) { // 说明还有线程在等待中
				lock.notifyAll();
			}
		}
		
		notifyListeners();
	}

	/**
	 * 返回计算结果，如果未执行完成，结果可能为null
	 * 
	 * @return
	 */
	public Object getValue() {
		synchronized (lock) {
			return result;
		}
	}

}
