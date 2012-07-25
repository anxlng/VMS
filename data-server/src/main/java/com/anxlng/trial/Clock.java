/**
 * data-server. 2012-6-26
 */
package com.anxlng.trial;

/**
 * 计时器，用来计时方法执行的时间
 * @author tangjixing <anxlng@sina.com>
 */
public class Clock extends Thread {
	
	private final ClockAble body;
	public Clock(ClockAble body) {
		this.body = body;
	}
	
	public void run() {
		long s = System.currentTimeMillis();
		body.exec();
		long e = System.currentTimeMillis();
		System.out.println(body.getName() + "执行时间：" + (e - s) + "ms");
	}
}

interface ClockAble {
	
	String getName();
	
	void exec();
}

