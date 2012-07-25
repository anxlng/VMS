/**
 * data-server. 2012-6-26
 */
package com.anxlng.trial;

import com.anxlng.util.TimeFunc;

/**
 * 结论：
 * 1、当前线程调用类的同步静态方法时，其他线程不能进入类的其他同步静态方法；但可以进入该类实例的非静态方法，也可以进入类的非同步静态方法
 * 2、同理，当前线程进入同步的实例方法时，不能进入该类的其它非静态方法，其它线程可以进入该类的静态方法，也可以进入类的非同步实例方法
 * 原因：静态方法同步是获取StaticSynTest.class锁，而实例方法获取的是该“实例对象”的锁，它们互不干涉。
 * 注意，必须是同步的方法相同类型不能同时进入，不要误解为所有的静态方法
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class TimeTrial {

	public static void main(String[] args) throws InterruptedException {
		
//		final long time = System.currentTimeMillis();
		
		
		new Clock(new ClockAble(){

			@Override
			public String getName() {
				return "utctodefault";
			}

			@Override
			public void exec() {
				
				for(int i = 0; i < 10000; i++) {
					String t = TimeFunc.utcToDefault("2012-10-10 08:00:00", TimeFunc.DATE_TIME);
					System.out.println(t);
//					String t = TimeFunc.longtoString(time, TimeFunc.DATE_TIME);
				}
			}
			
		}).start();
		
		new Clock(new ClockAble() {

			@Override
			public String getName() {
				return "defualttoutc";
			}

			@Override
			public void exec() {
				for(int i = 0; i < 10000; i++) {
					String t = TimeFunc.defaultToUtc("2012-10-10 00:00:00", TimeFunc.DATE_TIME);
					System.out.println(t);
//					SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TimeFunc.DATE_TIME);
//					DATE_FORMAT.format(new Date());
				}
			}
			
		}).start();
		

		
	}
}
