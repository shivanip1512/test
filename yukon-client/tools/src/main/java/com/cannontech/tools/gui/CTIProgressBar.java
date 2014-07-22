/*
 * Created on Jun 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * @author ASolberg
 *
 * Simple class to construct a JProgress bar that checks progress every second and updates the bar accordingly.
 * Uses a timer that is started when a job is started and stoped when a job is done.
 * Uses a float for progress: 100 should be devided by the jobs iterations giving you the amount to add to the
 * over all progress with the addProgress method after each job section or iteration finishes.
 */
public class CTIProgressBar extends JProgressBar
{

	public Timer timer;
	private static final int ONE_SECOND = 1000;
	public float progress;
	
	/**
	 * 
	 */
	public CTIProgressBar()
	{
		super();
		progress = 0.0f;
		//Create a timer.
		timer = new Timer(ONE_SECOND, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				Double newDouble = new Double(progress);
				int newInt = newDouble.intValue();
				getBar().setValue(newInt);

				if (progress == 100)
				{
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
				}
			}
		});
	}
	
	public void start()
	{
		timer.start();
	}
	
	public void stop()
	{
		timer.stop();
	}
	
	public CTIProgressBar getBar(){
		return this;
	}
	
	public void setProgress(float value)
	{
		progress = value;
	}
	
	public void addProgress(float value)
	{
		progress += value;
	}
	
	public float getProgress()
	{
		return progress;
	}

	/**
	 * @param orient
	 */
	public CTIProgressBar(int orient)
	{
		super(orient);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param min
	 * @param max
	 */
	public CTIProgressBar(int min, int max)
	{
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param orient
	 * @param min
	 * @param max
	 */
	public CTIProgressBar(int orient, int min, int max)
	{
		super(orient, min, max);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param newModel
	 */
	public CTIProgressBar(BoundedRangeModel newModel)
	{
		super(newModel);
		// TODO Auto-generated constructor stub
	}

}
