package com.cannontech.common.util;

/**
 * ThreadPool maintains a pool of threads and a queue of
 * Runnables.  ThreadPool will call the run method of each
 * Runnable in its queue as threads become available.
 * Creation date: (3/24/00 10:15:13 AM)
 * @author: Aaron Lauinger
 */
import java.util.Vector;
 
public class ThreadPool implements Runnable {

	class RunnerThread extends java.lang.Thread
	{
		{
			setDaemon(true);			
		}
		
		public volatile Runnable runnable = null;
		boolean stop = false;
		public void run()
		{
			try
			{				
				synchronized(this)
				{
					while(true)
					{
						if( stop )
							return;
							
						wait();

						if( stop )
							return;

						runnable.run();
						runnable = null;
						ThreadPool.this.availableThreads.add(this);	
					}					
				}					
			}
			catch( InterruptedException  e )
			{
				com.cannontech.clientutils.CTILogger.info("Pooled Thread interrupted");
			}
			finally
			{
				com.cannontech.clientutils.CTILogger.info("ThreadPool worker thread done");
			}	
		}
	}

	private Thread mainThread = null;
	private Vector queue = new java.util.Vector();

	private RunnerThread[] threads;
	private Vector availableThreads = new java.util.Vector();
	
	private volatile boolean doStop = false;
	
/**
 * ThreadPool constructor comment.
 */
public ThreadPool(int threads) {
	super();
	initPool(threads);
}
/**
 * Enqueue's a Runnable to be run on an available thread
 * Creation date: (3/24/00 10:26:48 AM)
 * @param runnable java.lang.Runnable
 */
public void enqueueRunnable(Runnable runnable) 
{
	if( !doStop )
	{
		synchronized( queue )
		{
			queue.add(runnable);
		}
	}
}
/**
 * Creation date: (3/24/00 10:21:18 AM)
 */
private void initPool(int nThreads) 
{
	threads = new RunnerThread[nThreads];
	
	for( int i = 0; i < threads.length; i++ )
	{
		RunnerThread thr = new RunnerThread();
		threads[i] = thr;
		availableThreads.add(thr);

		thr.start();
	}

	mainThread = new Thread(this);
	mainThread.setDaemon(true);
	mainThread.start();
}
/**
 * Creation date: (3/24/00 12:47:25 PM)
 */
public void join() 
{
	try
	{
		for( int i = 0; i < threads.length; i++ )
			threads[i].join();
	}
	catch( InterruptedException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}
/**
 * Creation date: (3/24/00 11:51:34 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	class r1 implements Runnable
	{
		String out;
		public r1(String o)
		{
			out = o;
		}

		public void run()
		{
			try
			{
				for( int i = 0; i < 20; i++ )
				{
					com.cannontech.clientutils.CTILogger.info(out);
					Thread.sleep(500);
				}
			}
			catch( InterruptedException e )
			{
				
			}
		}
	}
	
	try
	{
		ThreadPool pool = new ThreadPool(4);

		for( int i = 0; i < 10; i++ )
		{
			//Thread.sleep(1);
			pool.enqueueRunnable( new r1("happy " + i));
		}
	
		Thread.sleep(500);

		com.cannontech.clientutils.CTILogger.info("stop called");
		pool.stop();
		com.cannontech.clientutils.CTILogger.info("join called");
		pool.join();
	}
	catch( Throwable t )
	{
		com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
	}
}
/**
 * Creation date: (3/24/00 11:37:46 AM)
 */
public void run() 
{
	boolean sleep = false;
	
	try
	{
		while(true)
		{
			synchronized(queue)
			{
				if( queue.size() > 0 && availableThreads.size() > 0 )
				{
					RunnerThread runner = (RunnerThread) availableThreads.firstElement();
					Runnable runnable = (Runnable) queue.firstElement();

					runner.runnable = runnable;

					availableThreads.remove(runner);
					queue.remove(runnable);

					synchronized(runner)
					{
						runner.notifyAll();
					}
				}
				else
				{
					sleep = true;
					if( doStop && queue.size() == 0 )
						return;					
				}
			}

			if( sleep )
				Thread.sleep(100);
	 	}
	}
	catch( InterruptedException e )
	{
		com.cannontech.clientutils.CTILogger.info("ThreadPool interrupted");
	}
	finally
	{		
		for( int i = 0; i < threads.length; i++ )
		{
			synchronized(threads[i])
			{
				threads[i].stop = true;
				threads[i].notifyAll();
			}				
		}	
	}
	
}
/**
 * Creation date: (3/24/00 12:08:14 PM)
 */
public void stop() 
{
	doStop = true;		
}
}
