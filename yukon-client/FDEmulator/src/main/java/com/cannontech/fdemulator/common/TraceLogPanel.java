/* Author: Ben Wallace
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.EtchedBorder;

//implements com.cannontech.util.MessageEventListener
public class TraceLogPanel extends JPanel
{
	private static final int LIST_DELETE_INCREMENT = 25;
	private int maxMessages = 250;

	private javax.swing.JScrollPane displayListScrollPane;
	private javax.swing.JList displayList;
	private javax.swing.JLabel systemStatusLabel;
	private boolean showTitle = true;

	public static final int MESS_TYPE_NORMAL = 0;
	public static final int MESS_TYPE_RECV = 1;
	public static final int MESS_TYPE_SENT = 2;
	public static final int MESS_TYPE_ERROR = 3;
	public static final int MESS_TYPE_WARNING = 4;

	public static final Color ERROR_LOG_COLOR = new Color(200, 0, 0);
	public static final Color SENT_LOG_COLOR = new Color(0, 150, 0);
	public static final Color RECV_LOG_COLOR = new Color(180, 0, 180);
	public static final Color WARN_LOG_COLOR = new Color(255, 125, 0);
	public static final Color NORMAL_LOG_COLOR = new Color(0, 0, 200);

	// These are important for use in the ListCellRenderer
	// is is called many times and you will run out of memory
	// if allocation is performed in the renderer
	public static final JLabel errorLogMess = new JLabel();
	public static final JLabel recvLogMess = new JLabel();
	public static final JLabel sentLogMess = new JLabel();
	public static final JLabel warningLogMess = new JLabel();
	public static final JLabel normalLogMess = new JLabel();

	// Set the colors in this static block initializer
	static {
		errorLogMess.setForeground(ERROR_LOG_COLOR);
		recvLogMess.setForeground(RECV_LOG_COLOR);
		sentLogMess.setForeground(SENT_LOG_COLOR);
		warningLogMess.setForeground(WARN_LOG_COLOR);
		normalLogMess.setForeground(NORMAL_LOG_COLOR);
	}

	private JScrollBar sb = null;

	private class MessageCellRenderer implements javax.swing.ListCellRenderer
	{

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			JLabel tempLabel;

			/* 
			 * VERY IMPORTANT: Always use static variables here 
			 * and do not allocate memory in this method; because
			 * it is called frequently, you will end of with an 
			 * out of memory error.
			 */

			if (((LogItem) value).getLogTypeCode() == MESS_TYPE_RECV)
			{
				tempLabel = recvLogMess;
			} else if (((LogItem) value).getLogTypeCode() == MESS_TYPE_SENT)
			{
				tempLabel = sentLogMess;
			} else if (((LogItem) value).getLogTypeCode() == MESS_TYPE_ERROR)
			{
				tempLabel = errorLogMess;
			} else if (((LogItem) value).getLogTypeCode() == MESS_TYPE_WARNING)
			{
				tempLabel = warningLogMess;
			} else
			{
				// all other messages are normal
				tempLabel = normalLogMess;
			}

			tempLabel.setText(value.toString());
			return tempLabel;
		}
	}

	private class LogItem extends Object
	{
		private int logTypeCode = MESS_TYPE_NORMAL;
		private String lineMessage = null;

		public LogItem(String aMessage)
		{
			lineMessage = aMessage;
		}

		public LogItem(String aMessage, int typeCode)
		{
			lineMessage = aMessage;
			logTypeCode = typeCode;
		}

		public String toString()
		{
			return lineMessage;
		}

		/**
		* Returns the log Type Code
		* @return int a log message code
		*/
		public int getLogTypeCode()
		{
			return logTypeCode;
		}

		/**
		* Sets the type of log message that is to be logger
		* @param int typeCode The code for color use
		*/
		public void setLogTypeCode(int typeCode)
		{
			this.logTypeCode = typeCode;
		}

	}

	/**
	 * SystemStatusPanel constructor comment.
	 */
	public TraceLogPanel()
	{
		super();
		initialize();
	}
	/**
	 * SystemStatusPanel constructor comment.
	 */
	public TraceLogPanel(boolean title)
	{
		super();
		this.showTitle = title;
		initialize();
	}

	/**
	 * This method was created in VisualAge.
	 */
	public void clear()
	{
		((javax.swing.DefaultListModel) displayList.getModel()).removeAllElements();
	}

	/**
	 * This method was created in VisualAge.
	 */
	private void initialize()
	{
		systemStatusLabel = new javax.swing.JLabel(" Output Log");
		systemStatusLabel.setForeground(new Color(0, 0, 150));
		systemStatusLabel.setFont(new java.awt.Font("dialog", 0, 14));
		systemStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		displayList = new javax.swing.JList(new javax.swing.DefaultListModel());
		displayList.setFont(new java.awt.Font("dialog", 0, 14));

		displayListScrollPane = new javax.swing.JScrollPane(displayList);
		displayListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		displayListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		setPreferredSize(new java.awt.Dimension(575, 90));
		setLayout(new java.awt.BorderLayout());

		if (this.showTitle)
		{
			add(systemStatusLabel, "North");
		}
		add(displayListScrollPane, "Center");
		displayList.setCellRenderer(new MessageCellRenderer());
		displayListScrollPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
	}

	/**
	 * This append a message text and color code to the 
	 * a jlist to display on the screen.
	 * 
	 * @param String logMessage that is the text of a log
	 * @param int is a color code based on message type
	 */
	//public void messageEvent(MessageEvent eventObject) {
	public void append(String logMessage, int colorCode)
	{
		LogItem eventObject = new LogItem(logMessage, colorCode);
		// add the item to the list

		 ((javax.swing.DefaultListModel) displayList.getModel()).addElement(eventObject);

		// limit the size of the list
		int numMsgs = displayList.getModel().getSize();
		if (numMsgs > (maxMessages + LIST_DELETE_INCREMENT))
		{
			// will delete some increment of them, oldest first
			try
			{
				// remove LIST_DELETE_INCREMENT number of messages
				 ((javax.swing.DefaultListModel) displayList.getModel()).removeRange(0, LIST_DELETE_INCREMENT);
			} catch (ArrayIndexOutOfBoundsException e)
			{
			}
		}

		// this code forces the list to the end
		int position = displayList.getModel().getSize();
		java.awt.Rectangle cellBounds = displayList.getCellBounds(position - 1, position - 1);
		if (cellBounds != null)
		{
			// 2* so that you get bottom of cell
			cellBounds.translate(0, 2 * cellBounds.height);
			displayList.scrollRectToVisible(cellBounds);
		}
		this.doLayout();
	}

	/**
	 * This method was created in VisualAge.
	 * @param max int
	 */
	public void setMaxMessages(int max)
	{
		this.maxMessages = max;
	}

	/**
	 * This method was created in VisualAge.
	 * @param val boolean
	 */
	public void showTitle(boolean val)
	{
		if (val == this.showTitle)
			return;
		if (val)
		{
			this.remove(systemStatusLabel);
		} else
		{
			this.add(systemStatusLabel, "North");
		}
		revalidate();
	}
}