package com.jzz.test.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestThreadException {
//	private Logger log = LoggerFactory.getLogger(TestThreadException.class);
//	private Boolean isDebug = log.isDebugEnabled();
	
	
//	@Test
	public void testRunnable(){
		System.out.println("testRunnable begin ");
		try{
			Runnable t = new InitialtiveThread();
			t.run();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("testRunnable end ");
	}
//	@Test
	public void testThread(){
		System.out.println("testThread begin ");
		try{
			Thread t = new Thread(new InitialtiveThread());
//			start() : 它的作用是启动一个新线程，新线程会执行相应的run()方法。start()不能被重复调用。
//			run()   : run()就和普通的成员方法一样，可以被重复调用。单独调用run()的话，会在当前线程中执行run()，而并不会启动新线程！
			t.start();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("testThread end ");
	}
//	@Test
	public void testThreadWhithHandler(){
		System.out.println("testThread begin ");
		try{
			Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
			
			Thread t = new Thread(new InitialtiveThread());
//			start() : 它的作用是启动一个新线程，新线程会执行相应的run()方法。start()不能被重复调用。
//			run()   : run()就和普通的成员方法一样，可以被重复调用。单独调用run()的话，会在当前线程中执行run()，而并不会启动新线程！
			
			t.start();// 注意 log4j 的配置是否正确
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("testThread end ");
	}
	
	@Test
	public void test(){
		System.out.println("test begin ");
		try {
//			参考：http://blog.onlycatch.com/post/Java线程池异常处理最佳实践
//			可以看到，程序会捕获包括Error在内的所有异常，并且在程序最后，将出现过的异常和当前任务传递给afterExecute方法。
//			而ThreadPoolExecutor中的afterExecute方法是没有任何实现的：
//			 protected void afterExecute(Runnable r, Throwable t) { }
//			也就是说，默认情况下，线程池会捕获任务抛出的所有异常，但是不做任何处理（包括spring线程池）。
			
			Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
			
//			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); spring
//			executor.setCorePoolSize(8);
//			executor.setQueueCapacity(10000);
//			executor.setMaxPoolSize(16);
//			executor.setKeepAliveSeconds(1);
//			executor.initialize();
			ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
			
			for(int i=0; i<3; i++){
				Runnable t = new InitialtiveThread();
				executor.submit(t);
			}
			
			while(executor.getActiveCount()>0){
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw e;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("test end ");
	}
	
	class InitialtiveThread implements Runnable {
		@Override
		public void run() {
			System.out.println(3 / 2);
			System.out.println(3 / 0);
			System.out.println(3 / 1);
		}
	}
}
