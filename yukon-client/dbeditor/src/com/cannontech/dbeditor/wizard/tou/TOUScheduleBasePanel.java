/*
 * Created on Jun 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.dbeditor.wizard.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.tou.TOUDay;
import com.cannontech.database.db.tou.TOUDayRateSwitches;
import com.cannontech.database.db.tou.TOUDayMapping;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.database.Transaction;

import java.awt.event.FocusEvent;
import java.util.Calendar;
import java.util.Vector;
import java.util.HashMap;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import com.cannontech.common.gui.table.MultiJComboCellRenderer;
import com.cannontech.common.gui.table.MultiJComboCellEditor;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.ComboBoxTableRenderer;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TOUScheduleBasePanel extends DataInputPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField jTextFieldScheduleName = null;
	private JComboBox jComboBoxDefaultRate = null;
	private JComboBox jComboBoxTuesday = null;
	private JComboBox jComboBoxWednesday = null;
	private JComboBox jComboBoxFriday = null;
	private JComboBox jComboBoxHoliday = null;
	private JComboBox jComboBoxSunday = null;
	private JComboBox jComboBoxMonday = null;
	private JComboBox jComboBoxThursday = null;
	private javax.swing.JLabel jLabelScheduleName = null;
	private javax.swing.JLabel jLabelDefaultRate = null;
	private javax.swing.JComboBox jComboBoxSaturday = null;
	private javax.swing.JLabel jLabelSunday = null;
	private javax.swing.JLabel jLabelMonday = null;
	private javax.swing.JLabel jLabelTuesday = null;
	private javax.swing.JLabel jLabelWednesday = null;
	private javax.swing.JLabel jLabelFriday = null;
	private javax.swing.JLabel jLabelSaturday = null;
	private javax.swing.JLabel jLabelHoliday = null;
	private javax.swing.JLabel jLabelThursday = null;
	private javax.swing.JPanel jPanelDayEditor = null;
	private javax.swing.JLabel jLabelDayName = null;
	private javax.swing.JTextField jTextFieldDayName = null;
	private javax.swing.JTable jTableMadTOUDisease = null;
	private MadTOUDiseaseTableModel tableModel = null;
	private TOUDay currentlySelected = null;
	private boolean isNewSched = false;
	private boolean healthyAction = true;
	
	String[] daFourRates = new String[4];
	HashMap touDays = new HashMap(4);
	JComboBox[] theWeekCombos = new JComboBox[9];
	
	final int HOLIDAY = 8;
		
	private javax.swing.JButton jButtonSaveDay = null;
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener, java.awt.event.FocusListener 
	{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(!healthyAction)
				return;
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxSunday()) 
				handleWeekEvents(Calendar.SUNDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxMonday()) 
				handleWeekEvents(Calendar.MONDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxTuesday()) 
				handleWeekEvents(Calendar.TUESDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxWednesday()) 
				handleWeekEvents(Calendar.WEDNESDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxThursday()) 
				handleWeekEvents(Calendar.THURSDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxFriday()) 
				handleWeekEvents(Calendar.FRIDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxSaturday()) 
				handleWeekEvents(Calendar.SATURDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxHoliday()) 
				handleWeekEvents(HOLIDAY);
			if( e.getSource() == getJButtonSaveDay())
				saveCurrentDaySettings(currentlySelected.getDayID());

		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == TOUScheduleBasePanel.this.getJTextFieldScheduleName()) 
				fireInputUpdate();
			if (e.getSource() == TOUScheduleBasePanel.this.getJTextFieldDayName()) 
				fireInputUpdate();
		};
		public void mouseClicked(java.awt.event.MouseEvent e) 
		{
			//System.out.println("Mouse clicked");
		};
		public void mouseEntered(java.awt.event.MouseEvent e) 
		{
			//System.out.println("Mouse entered");
		};
		public void mouseExited(java.awt.event.MouseEvent e) 
		{
			//System.out.println("Mouse exited");		
		};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == TOUScheduleBasePanel.this.getJTableMadTOUDisease()) 
				fireInputUpdate();
		};
		public void mouseReleased(java.awt.event.MouseEvent e) 
		{
			//System.out.println("Mouse released");
		};
		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(FocusEvent e) 
		{
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxSunday()) 
				handleWeekEvents(Calendar.SUNDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxMonday()) 
				handleWeekEvents(Calendar.MONDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxTuesday()) 
				handleWeekEvents(Calendar.TUESDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxWednesday()) 
				handleWeekEvents(Calendar.WEDNESDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxThursday()) 
				handleWeekEvents(Calendar.THURSDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxFriday()) 
				handleWeekEvents(Calendar.FRIDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxSaturday()) 
				handleWeekEvents(Calendar.SATURDAY);
			if (e.getSource() == TOUScheduleBasePanel.this.getJComboBoxHoliday()) 
				handleWeekEvents(HOLIDAY);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent e) 
		{
			//System.out.println("Focus gained");
		}

	};
	
	/**
	 * This method initializes 
	 * 
	 */
	public TOUScheduleBasePanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	
	private void initConnections()
	{
		for(int x = 1; x < theWeekCombos.length; x++)
		{
			theWeekCombos[x].addActionListener(ivjEventHandler);
			theWeekCombos[x].addFocusListener(ivjEventHandler);
		}
		
		getJButtonSaveDay().addActionListener(ivjEventHandler);
	}
	
	private void initialize() {
        
		//these are the possible rates for an MCT410 TOU
		daFourRates[0] = "A";
		daFourRates[1] = "B";
		daFourRates[2] = "C";
		daFourRates[3] = "D";
		
		theWeekCombos[Calendar.SUNDAY] = getJComboBoxSunday();
		theWeekCombos[Calendar.MONDAY] = getJComboBoxMonday();
		theWeekCombos[Calendar.TUESDAY] = getJComboBoxTuesday();
		theWeekCombos[Calendar.WEDNESDAY] = getJComboBoxWednesday();
		theWeekCombos[Calendar.THURSDAY] = getJComboBoxThursday();
		theWeekCombos[Calendar.FRIDAY] = getJComboBoxFriday();
		theWeekCombos[Calendar.SATURDAY] = getJComboBoxSaturday();
		theWeekCombos[HOLIDAY] = getJComboBoxHoliday();
		 
		for(int j = 0; j < daFourRates.length; j++)
		{
			getJComboBoxDefaultRate().addItem(daFourRates[j]);
		}
		
		int initialDayID = TOUDay.getNextTOUDayID().intValue();
		TOUDay dayOne = new TOUDay(new Integer(initialDayID + 1),"Daily1");	
		TOUDay dayTwo = new TOUDay(new Integer(initialDayID + 2),"Daily2");
		TOUDay dayThree = new TOUDay(new Integer(initialDayID + 3),"Daily3");
		TOUDay dayFour = new TOUDay(new Integer(initialDayID + 4),"Daily4");
		dayOne.getTOURateSwitchVector().addElement(new TOUDayRateSwitches("A", new Integer(0)));
		dayTwo.getTOURateSwitchVector().addElement(new TOUDayRateSwitches("A", new Integer(0)));
		dayThree.getTOURateSwitchVector().addElement(new TOUDayRateSwitches("A", new Integer(0)));
		dayFour.getTOURateSwitchVector().addElement(new TOUDayRateSwitches("A", new Integer(0)));
		touDays.put(dayOne.getDayID(), dayOne);
		touDays.put(dayTwo.getDayID(), dayTwo);
		touDays.put(dayThree.getDayID(), dayThree);
		touDays.put(dayFour.getDayID(), dayFour);
		
		refreshDayCombos();
        
        java.awt.GridBagConstraints consGridBagConstraints22 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints23 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints24 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints25 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints28 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints30 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints31 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints27 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints32 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints33 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints34 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints35 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints36 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints37 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints38 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints40 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints41 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints42 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints39 = new java.awt.GridBagConstraints();
        consGridBagConstraints37.insets = new java.awt.Insets(4,9,4,5);
        consGridBagConstraints37.gridy = 6;
        consGridBagConstraints37.gridx = 1;
        consGridBagConstraints29.insets = new java.awt.Insets(5,5,3,6);
        consGridBagConstraints29.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints29.weightx = 1.0;
        consGridBagConstraints29.gridy = 4;
        consGridBagConstraints29.gridx = 2;
        consGridBagConstraints30.insets = new java.awt.Insets(5,5,5,6);
        consGridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints30.weightx = 1.0;
        consGridBagConstraints30.gridy = 7;
        consGridBagConstraints30.gridx = 2;
        consGridBagConstraints31.insets = new java.awt.Insets(13,9,8,17);
        consGridBagConstraints31.ipady = 4;
        consGridBagConstraints31.ipadx = 30;
        consGridBagConstraints31.gridwidth = 2;
        consGridBagConstraints31.gridy = 1;
        consGridBagConstraints31.gridx = 1;
        consGridBagConstraints24.insets = new java.awt.Insets(4,5,3,6);
        consGridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints24.weightx = 1.0;
        consGridBagConstraints24.gridy = 5;
        consGridBagConstraints24.gridx = 2;
        consGridBagConstraints25.insets = new java.awt.Insets(4,5,4,6);
        consGridBagConstraints25.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints25.weightx = 1.0;
        consGridBagConstraints25.gridy = 6;
        consGridBagConstraints25.gridx = 2;
        consGridBagConstraints26.insets = new java.awt.Insets(5,5,4,6);
        consGridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints26.weightx = 1.0;
        consGridBagConstraints26.gridy = 8;
        consGridBagConstraints26.gridx = 2;
        consGridBagConstraints28.insets = new java.awt.Insets(7,5,4,6);
        consGridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints28.weightx = 1.0;
        consGridBagConstraints28.gridy = 3;
        consGridBagConstraints28.gridx = 2;
        consGridBagConstraints22.insets = new java.awt.Insets(13,11,6,38);
        consGridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints22.weightx = 1.0;
        consGridBagConstraints22.gridy = 1;
        consGridBagConstraints22.gridx = 3;
        consGridBagConstraints27.ipady = -21;
        consGridBagConstraints27.ipadx = -92;
        consGridBagConstraints27.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints27.weightx = 1.0;
        consGridBagConstraints27.gridheight = -1;
        consGridBagConstraints27.gridwidth = -1;
        consGridBagConstraints27.gridy = 1;
        consGridBagConstraints27.gridx = 1;
        consGridBagConstraints23.insets = new java.awt.Insets(6,11,6,126);
        consGridBagConstraints23.ipadx = 64;
        consGridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints23.weightx = 1.0;
        consGridBagConstraints23.gridy = 2;
        consGridBagConstraints23.gridx = 3;
        consGridBagConstraints33.insets = new java.awt.Insets(5,5,121,6);
        consGridBagConstraints33.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints33.weightx = 1.0;
        consGridBagConstraints33.gridy = 9;
        consGridBagConstraints33.gridx = 2;
        consGridBagConstraints36.insets = new java.awt.Insets(4,9,3,5);
        consGridBagConstraints36.gridy = 5;
        consGridBagConstraints36.gridx = 1;
        consGridBagConstraints34.insets = new java.awt.Insets(7,9,4,5);
        consGridBagConstraints34.gridy = 3;
        consGridBagConstraints34.gridx = 1;
        consGridBagConstraints32.insets = new java.awt.Insets(6,9,11,17);
        consGridBagConstraints32.gridwidth = 2;
        consGridBagConstraints32.gridy = 2;
        consGridBagConstraints32.gridx = 1;
        consGridBagConstraints38.insets = new java.awt.Insets(5,9,4,5);
        consGridBagConstraints38.gridy = 8;
        consGridBagConstraints38.gridx = 1;
        consGridBagConstraints40.ipady = -21;
        consGridBagConstraints40.ipadx = -31;
        consGridBagConstraints40.gridheight = -1;
        consGridBagConstraints40.gridwidth = -1;
        consGridBagConstraints40.gridy = 1;
        consGridBagConstraints40.gridx = 1;
        consGridBagConstraints41.insets = new java.awt.Insets(5,9,5,5);
        consGridBagConstraints41.gridy = 7;
        consGridBagConstraints41.gridx = 1;
        consGridBagConstraints42.insets = new java.awt.Insets(7,6,91,6);
        consGridBagConstraints42.ipady = -84;
        consGridBagConstraints42.gridheight = 7;
        consGridBagConstraints42.gridy = 3;
        consGridBagConstraints42.gridx = 3;
        consGridBagConstraints39.insets = new java.awt.Insets(5,9,121,5);
        consGridBagConstraints39.gridy = 9;
        consGridBagConstraints39.gridx = 1;
        consGridBagConstraints35.insets = new java.awt.Insets(5,9,3,5);
        consGridBagConstraints35.gridy = 4;
        consGridBagConstraints35.gridx = 1;
        this.setLayout(new java.awt.GridBagLayout());
        this.add(getJTextFieldScheduleName(), consGridBagConstraints22);
        this.add(getJComboBoxDefaultRate(), consGridBagConstraints23);
        this.add(getJComboBoxTuesday(), consGridBagConstraints24);
        this.add(getJComboBoxWednesday(), consGridBagConstraints25);
        this.add(getJComboBoxFriday(), consGridBagConstraints26);
        this.add(getJComboBoxHoliday(), consGridBagConstraints27);
        this.add(getJComboBoxSunday(), consGridBagConstraints28);
        this.add(getJComboBoxMonday(), consGridBagConstraints29);
        this.add(getJComboBoxThursday(), consGridBagConstraints30);
        this.add(getJLabelScheduleName(), consGridBagConstraints31);
        this.add(getJLabelDefaultRate(), consGridBagConstraints32);
        this.add(getJComboBoxSaturday(), consGridBagConstraints33);
        this.add(getJLabelSunday(), consGridBagConstraints34);
        this.add(getJLabelMonday(), consGridBagConstraints35);
        this.add(getJLabelTuesday(), consGridBagConstraints36);
        this.add(getJLabelWednesday(), consGridBagConstraints37);
        this.add(getJLabelFriday(), consGridBagConstraints38);
        this.add(getJLabelSaturday(), consGridBagConstraints39);
        this.add(getJLabelHoliday(), consGridBagConstraints40);
        this.add(getJLabelThursday(), consGridBagConstraints41);
        this.add(getJPanelDayEditor(), consGridBagConstraints42);
        this.setSize(383, 404);
        
        initConnections();
		handleWeekEvents(Calendar.SUNDAY);
		
	}
	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(java.lang.Object)
	 */
	public Object getValue(Object o) 
	{
		//make sure cells get saved even though they might be currently being edited
		if( getJTableMadTOUDisease().isEditing() )
			getJTableMadTOUDisease().getCellEditor().stopCellEditing();
	
		TOUSchedule sched = (TOUSchedule)o;
	
		if(sched == null)
			sched = new TOUSchedule();
		
		sched.setScheduleName(getJTextFieldScheduleName().getText());
		
		sched.getTOUSchedule().setDefaultRate(getJComboBoxDefaultRate().getSelectedItem().toString());
		
		Vector newDayMappings = new Vector(8);
		HashMap markedDays = new HashMap();
		//go through all the weekly combos and get the selected days
		for(int w = 1; w < theWeekCombos.length; w++)
		{
			TOUDayMapping mapDay = new TOUDayMapping();
			
			mapDay.setDayID(((TOUDay)theWeekCombos[w].getSelectedItem()).getDayID());
			markedDays.put(mapDay.getDayID(), (TOUDay)theWeekCombos[w].getSelectedItem());
			mapDay.setDayOffset(new Integer(w));
			mapDay.setScheduleID(sched.getScheduleID());
			
			newDayMappings.addElement(mapDay);
		}
		
		sched.setTOUDayMappingVector(newDayMappings);
		
		//Deal with the individual days
		Vector tempDays = new Vector(4);
		tempDays.addAll(markedDays.values());
		java.util.Collections.sort(tempDays);
				
		for(int j = 0; j < tempDays.size(); j++)
		{
			TOUDay touDay = (TOUDay)tempDays.elementAt(j);
			
			try
			{
				TOUDay decoyDay = new TOUDay();
				decoyDay.setDayID(touDay.getDayID());
				Transaction t;
				t = Transaction.createTransaction(Transaction.RETRIEVE, decoyDay);
				t.execute();
				//doesn't currently exist
				if(decoyDay.getDayName() == null)
					t = Transaction.createTransaction(Transaction.INSERT, touDay);
				else
					t = Transaction.createTransaction(Transaction.UPDATE, touDay);
				t.execute();
				
			}
			catch (com.cannontech.database.TransactionException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
		return sched;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(java.lang.Object)
	 */
	public void setValue(Object o) 
	{
		TOUSchedule sched = (TOUSchedule)o;

		if(sched == null)
			sched = new TOUSchedule();
		
		if(!isNewSched)
		{
			getJTextFieldScheduleName().setText(sched.getScheduleName());
		
			getJComboBoxDefaultRate().setSelectedItem(sched.getTOUSchedule().getDefaultRate());
	
			//store the days
			Vector theRelevantDays = sched.getTOUDayMappingVector();
			touDays.values().removeAll(touDays.values());
			int size = theRelevantDays.size();
			for(int m = 0; m < size; m++)
			{
				try
				{
					TOUDay dayOfTheDead = new TOUDay();
					dayOfTheDead.setDayID(((TOUDayMapping)theRelevantDays.elementAt(m)).getDayID());
					Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, dayOfTheDead);
					t.execute();
					touDays.put(dayOfTheDead.getDayID(), dayOfTheDead);
				
					//then need to select which days are selected in each day combo box
					Integer offset = ((TOUDayMapping)theRelevantDays.elementAt(m)).getDayOffset();
					theWeekCombos[offset.intValue()].addItem(dayOfTheDead);
					theWeekCombos[offset.intValue()].setSelectedItem(dayOfTheDead);
				}
				catch (com.cannontech.database.TransactionException e)
				{
					com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				}
			}
					
			refreshDayCombos();
		
			//show Sunday's rate switches and selected day name to begin with
			handleWeekEvents(Calendar.SUNDAY);
		}
	}

	/**
	 * This method initializes jTextFieldScheduleName
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldScheduleName() {
		if(jTextFieldScheduleName == null) {
			jTextFieldScheduleName = new javax.swing.JTextField();
			jTextFieldScheduleName.setMaximumSize(new java.awt.Dimension(183,22));
			jTextFieldScheduleName.setMinimumSize(new java.awt.Dimension(183,22));
			jTextFieldScheduleName.setPreferredSize(new java.awt.Dimension(183,22));
			jTextFieldScheduleName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(30));
		}
		return jTextFieldScheduleName;
	}
	/**
	 * This method initializes jComboBoxDefaultRate
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxDefaultRate() {
		if(jComboBoxDefaultRate == null) {
			jComboBoxDefaultRate = new javax.swing.JComboBox();
		}
		return jComboBoxDefaultRate;
	}
	/**
	 * This method initializes jComboBoxTuesday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxTuesday() {
		if(jComboBoxTuesday == null) {
			jComboBoxTuesday = new javax.swing.JComboBox();
			jComboBoxTuesday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxTuesday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxTuesday.setPreferredSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxTuesday;
	}
	/**
	 * This method initializes jComboBoxWednesday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxWednesday() {
		if(jComboBoxWednesday == null) {
			jComboBoxWednesday = new javax.swing.JComboBox();
			jComboBoxWednesday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxWednesday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxWednesday.setPreferredSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxWednesday;
	}
	/**
	 * This method initializes jComboBoxFriday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxFriday() {
		if(jComboBoxFriday == null) {
			jComboBoxFriday = new javax.swing.JComboBox();
			jComboBoxFriday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxFriday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxFriday.setPreferredSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxFriday;
	}
	/**
	 * This method initializes jComboBoxHoliday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxHoliday() {
		if(jComboBoxHoliday == null) {
			jComboBoxHoliday = new javax.swing.JComboBox();
			jComboBoxHoliday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxHoliday.setVisible(false);
		}
		return jComboBoxHoliday;
	}
	/**
	 * This method initializes jComboBoxSunday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxSunday() {
		if(jComboBoxSunday == null) {
			jComboBoxSunday = new javax.swing.JComboBox();
			jComboBoxSunday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxSunday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxSunday.setMaximumSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxSunday;
	}
	/**
	 * This method initializes jComboBoxMonday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxMonday() {
		if(jComboBoxMonday == null) {
			jComboBoxMonday = new javax.swing.JComboBox();
			jComboBoxMonday.setPreferredSize(new java.awt.Dimension(92,21));
			jComboBoxMonday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxMonday.setMaximumSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxMonday;
	}
	/**
	 * This method initializes jComboBoxThursday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxThursday() {
		if(jComboBoxThursday == null) {
			jComboBoxThursday = new javax.swing.JComboBox();
			jComboBoxThursday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxThursday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxThursday.setPreferredSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxThursday;
	}
	/**
	 * This method initializes jLabelScheduleName
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelScheduleName() {
		if(jLabelScheduleName == null) {
			jLabelScheduleName = new javax.swing.JLabel();
			jLabelScheduleName.setText("Schedule Name: ");
			jLabelScheduleName.setName("jLabelScheduleName");
			jLabelScheduleName.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelScheduleName;
	}
	/**
	 * This method initializes jLabelDefaultRate
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelDefaultRate() {
		if(jLabelDefaultRate == null) {
			jLabelDefaultRate = new javax.swing.JLabel();
			jLabelDefaultRate.setText("Default Rate: ");
			jLabelDefaultRate.setPreferredSize(new java.awt.Dimension(125,20));
			jLabelDefaultRate.setMaximumSize(new java.awt.Dimension(125,20));
			jLabelDefaultRate.setMinimumSize(new java.awt.Dimension(125,20));
			jLabelDefaultRate.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelDefaultRate;
	}
	/**
	 * This method initializes jComboBoxSaturday
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxSaturday() {
		if(jComboBoxSaturday == null) {
			jComboBoxSaturday = new javax.swing.JComboBox();
			jComboBoxSaturday.setMinimumSize(new java.awt.Dimension(92,21));
			jComboBoxSaturday.setMaximumSize(new java.awt.Dimension(92,21));
			jComboBoxSaturday.setPreferredSize(new java.awt.Dimension(92,21));
		}
		return jComboBoxSaturday;
	}
	/**
	 * This method initializes jLabelSunday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelSunday() {
		if(jLabelSunday == null) {
			jLabelSunday = new javax.swing.JLabel();
			jLabelSunday.setText("Sun: ");
			jLabelSunday.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jLabelSunday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelSunday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelSunday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelSunday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelSunday;
	}
	/**
	 * This method initializes jLabelMonday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelMonday() {
		if(jLabelMonday == null) {
			jLabelMonday = new javax.swing.JLabel();
			jLabelMonday.setText("Mon: ");
			jLabelMonday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelMonday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelMonday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelMonday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelMonday;
	}
	/**
	 * This method initializes jLabelTuesday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelTuesday() {
		if(jLabelTuesday == null) {
			jLabelTuesday = new javax.swing.JLabel();
			jLabelTuesday.setText("Tue: ");
			jLabelTuesday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelTuesday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelTuesday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelTuesday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelTuesday;
	}
	/**
	 * This method initializes jLabelWednesday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelWednesday() {
		if(jLabelWednesday == null) {
			jLabelWednesday = new javax.swing.JLabel();
			jLabelWednesday.setText("Wed: ");
			jLabelWednesday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelWednesday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelWednesday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelWednesday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelWednesday;
	}
	/**
	 * This method initializes jLabelFriday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelFriday() {
		if(jLabelFriday == null) {
			jLabelFriday = new javax.swing.JLabel();
			jLabelFriday.setText("Fri: ");
			jLabelFriday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelFriday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelFriday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelFriday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelFriday;
	}
	/**
	 * This method initializes jLabelSaturday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelSaturday() {
		if(jLabelSaturday == null) {
			jLabelSaturday = new javax.swing.JLabel();
			jLabelSaturday.setText("Sat: ");
			jLabelSaturday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelSaturday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelSaturday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelSaturday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelSaturday;
	}
	/**
	 * This method initializes jLabelHoliday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelHoliday() {
		if(jLabelHoliday == null) {
			jLabelHoliday = new javax.swing.JLabel();
			jLabelHoliday.setText("Hol");
			jLabelHoliday.setMaximumSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setMinimumSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setPreferredSize(new java.awt.Dimension(31,21));
			jLabelHoliday.setVisible(false);
		}
		return jLabelHoliday;
	}
	/**
	 * This method initializes jLabelThursday
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelThursday() {
		if(jLabelThursday == null) {
			jLabelThursday = new javax.swing.JLabel();
			jLabelThursday.setText("Thu: ");
			jLabelThursday.setMaximumSize(new java.awt.Dimension(34,21));
			jLabelThursday.setMinimumSize(new java.awt.Dimension(34,21));
			jLabelThursday.setPreferredSize(new java.awt.Dimension(34,21));
			jLabelThursday.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelThursday;
	}
	/**
	 * This method initializes jPanelDayEditor
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelDayEditor() {
		if(jPanelDayEditor == null) {
			jPanelDayEditor = new javax.swing.JPanel();
			jPanelDayEditor.setLayout(null);
			jPanelDayEditor.add(getJLabelDayName(), null);
			jPanelDayEditor.add(getJTextFieldDayName(), null);
			jPanelDayEditor.add(getJTableMadTOUDisease(), null);
			jPanelDayEditor.add(getJButtonSaveDay(), null);
			jPanelDayEditor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TOU Day Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, null));
			jPanelDayEditor.setPreferredSize(new java.awt.Dimension(220,312));
			jPanelDayEditor.setMinimumSize(new java.awt.Dimension(220,312));
			jPanelDayEditor.setMaximumSize(new java.awt.Dimension(220,312));
			jPanelDayEditor.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jPanelDayEditor;
	}
	/**
	 * This method initializes jLabelDayName
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelDayName() {
		if(jLabelDayName == null) {
			jLabelDayName = new javax.swing.JLabel();
			jLabelDayName.setBounds(16, 24, 55, 17);
			jLabelDayName.setText("Name: ");
			jLabelDayName.setMaximumSize(new java.awt.Dimension(55,17));
			jLabelDayName.setMinimumSize(new java.awt.Dimension(55,17));
			jLabelDayName.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jLabelDayName;
	}
	/**
	 * This method initializes jTextFieldDayName
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldDayName() {
		if(jTextFieldDayName == null) {
			jTextFieldDayName = new javax.swing.JTextField();
			jTextFieldDayName.setBounds(84, 24, 124, 20);
			jTextFieldDayName.setPreferredSize(new java.awt.Dimension(127,20));
			jTextFieldDayName.setMinimumSize(new java.awt.Dimension(127,20));
			jTextFieldDayName.setMaximumSize(new java.awt.Dimension(127,20));
			jTextFieldDayName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(30));
		}
		return jTextFieldDayName;
	}
	/**
	 * This method initializes jTableMadTOUDisease
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTableMadTOUDisease() {
		if(jTableMadTOUDisease == null) {
			jTableMadTOUDisease = new javax.swing.JTable();
			jTableMadTOUDisease.setAutoCreateColumnsFromModel(true);
			jTableMadTOUDisease.setModel(getTableModel());
			jTableMadTOUDisease.setBounds(16, 58, 190, 139);
			jTableMadTOUDisease.setCellSelectionEnabled(true);
			jTableMadTOUDisease.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			
			//Do any column specific initialization here, with the exception of the gear column.
			javax.swing.table.TableColumn offsetColumn = getJTableMadTOUDisease().getColumnModel().getColumn(MadTOUDiseaseTableModel.OFFSET_COLUMN);
			javax.swing.table.TableColumn rateColumn = getJTableMadTOUDisease().getColumnModel().getColumn(MadTOUDiseaseTableModel.RATE_COLUMN);
				
			offsetColumn.setPreferredWidth(100);
			rateColumn.setPreferredWidth(60);

			//create our editor for the time field
			JTextFieldTimeEntry field = new JTextFieldTimeEntry();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);
			offsetColumn.setCellEditor( ed );
			
			//create our renderer for the Integer field
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			offsetColumn.setCellRenderer(rend);
			
			//rate combo
			javax.swing.JComboBox rCombo = new javax.swing.JComboBox();
			rCombo.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					fireInputUpdate();
				}
			});
			rCombo.addItem(daFourRates[0]);
			rCombo.addItem(daFourRates[1]);
			rCombo.addItem(daFourRates[2]);
			rCombo.addItem(daFourRates[3]);
			rateColumn.setCellEditor( new javax.swing.DefaultCellEditor(rCombo) );
			
			ComboBoxTableRenderer comboBxRender = new ComboBoxTableRenderer();
			comboBxRender.addItem(daFourRates[0]);
			comboBxRender.addItem(daFourRates[1]);
			comboBxRender.addItem(daFourRates[2]);
			comboBxRender.addItem(daFourRates[3]);
			rateColumn.setCellRenderer(comboBxRender);
			
		}
		return jTableMadTOUDisease;
	}
	
	private MadTOUDiseaseTableModel getTableModel() 
	{
		if( tableModel == null )
			tableModel = new MadTOUDiseaseTableModel();
		
		return tableModel;
	}
	
	public void refreshDayCombos()
	{
		healthyAction = false;
		Vector tempDays = new Vector(4);
		tempDays.addAll(touDays.values());
		java.util.Collections.sort(tempDays);
		
		for(int u = 1; u < theWeekCombos.length; u++)
		{
			TOUDay selectedDay = (TOUDay) theWeekCombos[u].getSelectedItem();
			theWeekCombos[u].removeAllItems();
			for(int j = 0; j < tempDays.size(); j++)
			{
				theWeekCombos[u].addItem(tempDays.elementAt(j));
			}
			//add any extra days needed
			if(tempDays.size() < 4)
			{
				int initialDayID = TOUDay.getNextTOUDayID().intValue();
				for(int d = tempDays.size(); d < 4; d++)
				{
					TOUDay day = new TOUDay(new Integer(initialDayID + d),"Daily" + d);
					day.getTOURateSwitchVector().addElement(new TOUDayRateSwitches("A", new Integer(0)));
					touDays.put(day.getDayID(), day);
					theWeekCombos[u].addItem(day);
				}
			}
			
			if(selectedDay == null)
				theWeekCombos[u].setSelectedItem((TOUDay)tempDays.elementAt(0));
			else if(currentlySelected != null && currentlySelected.getDayID().intValue() == selectedDay.getDayID().intValue())
				theWeekCombos[u].setSelectedItem(currentlySelected);
			else
				theWeekCombos[u].setSelectedItem(selectedDay);
				
		}
		healthyAction = true;
	}
	
	public void handleWeekEvents(int dayNum)
	{
		//for some reason, updating the combos is firing off the event handler...stop this with a boolean
		healthyAction = false;		
		TOUDay tempDay;
		
		if(dayNum > 8 || dayNum < 1)
			tempDay = (TOUDay) theWeekCombos[Calendar.SUNDAY].getSelectedItem();
		else
			tempDay = (TOUDay) theWeekCombos[dayNum].getSelectedItem();
		
		if(tempDay == null)
			populateRateOffsets(currentlySelected.getTOURateSwitchVector());
		else if(currentlySelected == null || currentlySelected.getDayID().compareTo(tempDay.getDayID()) != 0)
		{
			getJTextFieldDayName().setText(tempDay.toString());
			populateRateOffsets(tempDay.getTOURateSwitchVector());
			currentlySelected = tempDay;
		}
		//must still be the same day
		else
		{
			healthyAction = true;
			return;
		}
								
		healthyAction = true;
		fireInputUpdate();
	}
	
	public void populateRateOffsets(Vector dayRateOffsets)
	{
		int size = getTableModel().getRowCount();
		for(int f = size; f >= 0; f--)
		{
			getTableModel().removeRowValue(f);
		}
		
		int vectSize = dayRateOffsets.size();
		
		for(int x = 0; x < vectSize; x++)
		{
			TOUDayRateSwitches tempSwitch = (TOUDayRateSwitches) dayRateOffsets.elementAt(x);
			getTableModel().addRowValue( JTextFieldTimeEntry.setTimeTextForField(tempSwitch.getSwitchOffset()), tempSwitch.getSwitchRate());
    	}
    	//populate up to the the proper 6 possible
    	int tableSize = getTableModel().getRowCount();
    	if(tableSize < 6)
    	{
    		for(int g = tableSize; g < 6; g++ )
    		{
				if(g == 0)
					getTableModel().addRowValue( JTextFieldTimeEntry.setTimeTextForField(new Integer(0)), daFourRates[0]);
				else
					getTableModel().addRowValue( ":", daFourRates[0]);
    		}
    	}
	}
	
	public void saveCurrentDaySettings(Integer dayID)
	{
		Vector rateSwitches = new Vector(6);
		
		for(int j = 0; j < getJTableMadTOUDisease().getRowCount(); j++)
		{
			TOUDayRateSwitches tempSwitch = new TOUDayRateSwitches(); 
			
			Integer offset = JTextFieldTimeEntry.getTimeTotalSeconds(getTableModel().getOffsetAt(j));
			if(offset.intValue() != 0 || j == 0)
			{
				tempSwitch.setSwitchOffset(offset);
				tempSwitch.setSwitchRate(getTableModel().getRateAt(j));
			
				rateSwitches.add(tempSwitch);
			}
		}
		
		((TOUDay)touDays.get(dayID)).setTOURateSwitchVector(rateSwitches);
		if(getJTextFieldDayName().getText().length() > 0)
			((TOUDay)touDays.get(dayID)).setName(getJTextFieldDayName().getText());
		
		
		currentlySelected = ((TOUDay)touDays.get(dayID));
		//getJTableMadTOUDisease().removeAll();
			
		refreshDayCombos();
	}
		
	/**
	 * This method initializes jButtonSaveDay
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonSaveDay() {
		if(jButtonSaveDay == null) {
			jButtonSaveDay = new javax.swing.JButton();
			jButtonSaveDay.setBounds(16, 200, 190, 20);
			jButtonSaveDay.setText("Save Day Settings");
			jButtonSaveDay.setPreferredSize(new java.awt.Dimension(190,20));
			jButtonSaveDay.setMaximumSize(new java.awt.Dimension(190,20));
			jButtonSaveDay.setMinimumSize(new java.awt.Dimension(190,20));
			jButtonSaveDay.setName("jButtonSaveDay");
			jButtonSaveDay.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		}
		return jButtonSaveDay;
	}
	
	public void setIsNewSchedule(boolean truth)
	{
		isNewSched = truth;
	}
	
	public boolean isInputValid() 
	{
	   if( getJTextFieldScheduleName().getText().length() <= 0 )
	   {
		  setErrorString("A name should be specified for this schedule.");
		  return false;
	   }
	   else
		  return true;
	}

}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
