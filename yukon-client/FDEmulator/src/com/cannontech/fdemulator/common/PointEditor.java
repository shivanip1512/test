/*
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.VirtualDevice;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.fdemulator.fileio.AcsFileIO;
import com.cannontech.fdemulator.fileio.RdexFileIO;
import com.cannontech.fdemulator.fileio.ValmetFileIO;
import com.cannontech.fdemulator.model.AcsPointTableModel;
import com.cannontech.fdemulator.model.RdexPointTableModel;
import com.cannontech.fdemulator.model.ValmetPointTableModel;
import com.cannontech.fdemulator.protocols.ACSPoint;
import com.cannontech.fdemulator.protocols.RdexPoint;
import com.cannontech.fdemulator.protocols.ValmetPoint;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PointEditor extends JFrame implements ActionListener
{

	// component variables
	private ImageIcon icon = new ImageIcon("resource/fdeicon.gif");
	private JTabbedPane tabbedPane;
	private JPanel acsPanel;
	private JPanel rdexPanel;
	private JPanel valmetPanel;

	private JLabel rdexTypeLabel;
	private JLabel rdexNameLabel;
	private JLabel rdexIntervalLabel;
	private JLabel rdexFunctionLabel;
	private JLabel rdexMinLabel;
	private JLabel rdexMaxLabel;
	private JLabel rdexDeltaLabel;
	private JComboBox rdexTypeCB;
	private JTextField rdexNameTF;
	private JComboBox rdexIntervalCB;
	private JComboBox rdexFunctionCB;
	private JTextField rdexMinTF;
	private JTextField rdexMaxTF;
	private JTextField rdexDeltaTF;
	private JCheckBox rdexMaxStartCB;

	private JLabel acsTypeLabel;
	private JLabel acsRemoteLabel;
	private JLabel acsPointLabel;
	private JLabel acsCategoryLabel;
	private JLabel acsIntervalLabel;
	private JLabel acsFunctionLabel;
	private JLabel acsMinLabel;
	private JLabel acsMaxLabel;
	private JLabel acsDeltaLabel;
	private JComboBox acsTypeCB;
	private JTextField acsRemoteTF;
	private JTextField acsPointTF;
	private JTextField acsCategoryTF;
	private JComboBox acsIntervalCB;
	private JComboBox acsFunctionCB;
	private JTextField acsMinTF;
	private JTextField acsMaxTF;
	private JTextField acsDeltaTF;
	private JCheckBox acsMaxStartCB;

	private JLabel valmetTypeLabel;
	private JLabel valmetNameLabel;
	private JLabel valmetPortLabel;
	private JLabel valmetIntervalLabel;
	private JLabel valmetFunctionLabel;
	private JLabel valmetMinLabel;
	private JLabel valmetMaxLabel;
	private JLabel valmetDeltaLabel;
	private JComboBox valmetTypeCB;
	private JTextField valmetNameTF;
	private JTextField valmetPortTF;
	private JComboBox valmetIntervalCB;
	private JComboBox valmetFunctionCB;
	private JTextField valmetMinTF;
	private JTextField valmetMaxTF;
	private JTextField valmetDeltaTF;
	private JCheckBox valmetMaxStartCB;

	private JButton rdexAdd;
	private JButton rdexEdit;
	private JButton rdexSave;
	private JButton rdexGenerate;
	private JButton rdexCopy;
	private JButton rdexDelete;
	private JTable rdexTable;
	private JScrollPane rdexScrollPane;

	private JButton acsAdd;
	private JButton acsEdit;
	private JButton acsSave;
	private JButton acsGenerate;
	private JButton acsCopy;
	private JButton acsDelete;
	private JTable acsTable;
	private JScrollPane acsScrollPane;

	private JButton valmetAdd;
	private JButton valmetEdit;
	private JButton valmetSave;
	private JButton valmetGenerate;
	private JButton valmetCopy;
	private JButton valmetDelete;
	private JTable valmetTable;
	private JScrollPane valmetScrollPane;

	private String valmetFile = "resource/valmet_points.cfg";
	private String rdexFile = "resource/rdex_points.cfg";
	private String acsFile = "resource/acs_points.cfg";

	private Object[] valmetArray;
	private Object[] rdexArray;
	private Object[] acsArray;

	private AcsPointTableModel acsTableModel = null;
	private AcsFileIO acsFileIO = null;
	private RdexPointTableModel rdexTableModel = null;
	private RdexFileIO rdexFileIO = null;
	private ValmetPointTableModel valmetTableModel = null;
	private ValmetFileIO valmetFileIO = null;
	
	private boolean rdexediting = false;
	private boolean valmetediting = false;
	private boolean acsediting = false;

	private RandomAccessFile rdexPointList;
	private RandomAccessFile acsPointList;
	private RandomAccessFile valmetPointList;

	private ValmetPoint oldvalmetpoint;
	private ACSPoint oldacspoint;
	private RdexPoint oldrdexpoint;

	private VirtualDevice virtDevice = null;
	private boolean deviceadded = false;

	/**
	 * 
	 */
	public PointEditor()
	{
		try
		{
			init();
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	private void init() throws Exception
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		tabbedPane = new JTabbedPane();
		//ImageIcon icon = createImageIcon("resource/fdeicon.GIF");
		icon = new ImageIcon("resource/fdeicon.gif");
		acsPanel = new JPanel();
		acsPanel.setLayout(null);
		acsTypeLabel = new JLabel("Type:");
		acsTypeLabel.setBounds(new Rectangle(10, 10, 30, 20));
		acsPanel.add(acsTypeLabel);
		acsTypeCB = new JComboBox(new String[] { "Value", "Status", "Control" });
		acsTypeCB.setBounds(new Rectangle(45, 10, 60, 20));
		acsPanel.add(acsTypeCB);
		acsRemoteLabel = new JLabel("R:");
		acsRemoteLabel.setBounds(new Rectangle(115, 10, 15, 20));
		acsPanel.add(acsRemoteLabel);
		acsRemoteTF = new JTextField("");
		acsRemoteTF.setBounds(new Rectangle(125, 10, 25, 20));
		acsPanel.add(acsRemoteTF);
		acsPointLabel = new JLabel("P:");
		acsPointLabel.setBounds(new Rectangle(160, 10, 15, 20));
		acsPanel.add(acsPointLabel);
		acsPointTF = new JTextField("");
		acsPointTF.setBounds(new Rectangle(170, 10, 25, 20));
		acsPanel.add(acsPointTF);
		acsCategoryLabel = new JLabel("C:");
		acsCategoryLabel.setBounds(new Rectangle(205, 10, 15, 20));
		acsPanel.add(acsCategoryLabel);
		acsCategoryTF = new JTextField("");
		acsCategoryTF.setBounds(new Rectangle(215, 10, 25, 20));
		acsPanel.add(acsCategoryTF);
		acsIntervalLabel = new JLabel("Interval:");
		acsIntervalLabel.setBounds(new Rectangle(10, 40, 40, 20));
		acsPanel.add(acsIntervalLabel);
		acsIntervalCB = new JComboBox(new String[] { "10", "30", "60", "300", "900", "3600" });
		acsIntervalCB.setBounds(new Rectangle(55, 40, 50, 20));
		acsPanel.add(acsIntervalCB);
		acsFunctionLabel = new JLabel("Function:");
		acsFunctionLabel.setBounds(new Rectangle(115, 40, 45, 20));
		acsPanel.add(acsFunctionLabel);
		acsFunctionCB = new JComboBox(new String[] { "RANDOM", "PYRAMID", "DROPOFF" });
		acsFunctionCB.setBounds(new Rectangle(170, 40, 80, 20));
		acsPanel.add(acsFunctionCB);
		acsMinLabel = new JLabel("Min:");
		acsMinLabel.setBounds(new Rectangle(260, 40, 40, 20));
		acsPanel.add(acsMinLabel);
		acsMinTF = new JTextField("");
		acsMinTF.setBounds(new Rectangle(285, 40, 40, 20));
		acsPanel.add(acsMinTF);
		acsMaxLabel = new JLabel("Max:");
		acsMaxLabel.setBounds(new Rectangle(340, 40, 40, 20));
		acsPanel.add(acsMaxLabel);
		acsMaxTF = new JTextField("");
		acsMaxTF.setBounds(new Rectangle(370, 40, 40, 20));
		acsPanel.add(acsMaxTF);
		acsDeltaLabel = new JLabel("Delta:");
		acsDeltaLabel.setBounds(new Rectangle(420, 40, 40, 20));
		acsPanel.add(acsDeltaLabel);
		acsDeltaTF = new JTextField("");
		acsDeltaTF.setBounds(new Rectangle(455, 40, 40, 20));
		acsPanel.add(acsDeltaTF);
		acsMaxStartCB = new JCheckBox("MaxStart");
		acsMaxStartCB.setBounds(new Rectangle(500, 40, 70, 20));
		acsPanel.add(acsMaxStartCB);

		acsTable = new JTable(getAcsTableModel());

		acsTable.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				Point cell = e.getPoint();
				loadAcsPoint(cell);
			}
		});

		TableColumn acsIntervalColumn = acsTable.getColumnModel().getColumn(4);

		JComboBox acsIntervalComboBox = new JComboBox();
		acsIntervalComboBox.addItem("10");
		acsIntervalComboBox.addItem("30");
		acsIntervalComboBox.addItem("60");
		acsIntervalComboBox.addItem("300");
		acsIntervalComboBox.addItem("900");
		acsIntervalComboBox.addItem("3600");
		acsIntervalColumn.setCellEditor(new DefaultCellEditor(acsIntervalComboBox));

		TableColumn acsTypeColumn = acsTable.getColumnModel().getColumn(0);

		JComboBox acsTypeComboBox = new JComboBox();
		acsTypeComboBox.addItem("Value");
		acsTypeComboBox.addItem("Status");
		acsTypeComboBox.addItem("Control");
		acsTypeColumn.setCellEditor(new DefaultCellEditor(acsTypeComboBox));

		TableColumn acsFunctionColumn = acsTable.getColumnModel().getColumn(5);

		JComboBox acsFunctionComboBox = new JComboBox();
		acsFunctionComboBox.addItem("RANDOM");
		acsFunctionComboBox.addItem("PYRAMID");
		acsFunctionComboBox.addItem("DROPOFF");
		acsFunctionColumn.setCellEditor(new DefaultCellEditor(acsFunctionComboBox));
		
		TableColumn acsCategoryColumn = acsTable.getColumnModel().getColumn(3);

		JComboBox acsCategoryComboBox = new JComboBox();
		acsCategoryComboBox.addItem("P");
		acsCategoryComboBox.addItem("R");
		acsCategoryComboBox.addItem("C");
		acsCategoryColumn.setCellEditor(new DefaultCellEditor(acsCategoryComboBox));
		
		TableColumn acsMaxStartColumn = acsTable.getColumnModel().getColumn(9);
		
		JComboBox acsMaxStartComboBox = new JComboBox();
		acsMaxStartComboBox.addItem("true");
		acsMaxStartComboBox.addItem("false");
		acsMaxStartColumn.setCellEditor(new DefaultCellEditor(acsMaxStartComboBox));

		acsScrollPane = new JScrollPane(acsTable);
		acsScrollPane.setBounds(new Rectangle(10, 65, 570, 265));
		acsScrollPane.setPreferredSize(new Dimension(570, 265));

		acsArray = getAcsFileIO().getAcsPointsFromFile();
		loadAcsFile(acsArray);

		acsPanel.add(acsScrollPane);
		acsSave = new JButton("Save");
		acsSave.setBounds(new Rectangle(520, 10, 60, 20));
		acsPanel.add(acsSave);
		acsSave.setVisible(false);
		acsDelete = new JButton("Delete");
		acsDelete.setBounds(new Rectangle(520, 10, 65, 20));
		acsPanel.add(acsDelete);
		acsDelete.setVisible(true);
		acsEdit = new JButton("Edit");
		acsEdit.setBounds(new Rectangle(460, 10, 55, 20));
		acsPanel.add(acsEdit);
		acsAdd = new JButton("Add");
		acsAdd.setBounds(new Rectangle(400, 10, 55, 20));
		acsPanel.add(acsAdd);
		acsGenerate = new JButton("Generate");
		acsGenerate.setBounds(new Rectangle(315, 10, 80, 20));
		acsPanel.add(acsGenerate);
		acsCopy = new JButton("Copy");
		acsCopy.setBounds(new Rectangle(245, 10, 65, 20));
		acsPanel.add(acsCopy);
		acsCopy.setVisible(false);
		acsPanel.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab("ACS Points", icon, acsPanel, "ACS Point Editor");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		disableAcsFields();

		acsSave.addActionListener(this);
		acsDelete.addActionListener(this);
		acsEdit.addActionListener(this);
		acsAdd.addActionListener(this);
		acsGenerate.addActionListener(this);
		acsCopy.addActionListener(this);

		try
		{
			acsPointList = new RandomAccessFile("resource/acs_points.cfg", "rw");
		} catch (Exception e)
		{
			System.out.println("error making random access file" + e);
		}

		rdexPanel = new JPanel();
		rdexPanel.setLayout(null);
		rdexTypeLabel = new JLabel("Type:");
		rdexTypeLabel.setBounds(new Rectangle(10, 10, 30, 20));
		rdexPanel.add(rdexTypeLabel);
		rdexTypeCB = new JComboBox(new String[] { "Value", "Status", "Control" });
		rdexTypeCB.setBounds(new Rectangle(50, 10, 60, 20));
		rdexPanel.add(rdexTypeCB);
		rdexNameLabel = new JLabel("Name:");
		rdexNameLabel.setBounds(new Rectangle(120, 10, 30, 20));
		rdexPanel.add(rdexNameLabel);
		rdexNameTF = new JTextField("");
		rdexNameTF.setBounds(new Rectangle(160, 10, 80, 20));
		rdexPanel.add(rdexNameTF);
		rdexIntervalLabel = new JLabel("Interval:");
		rdexIntervalLabel.setBounds(new Rectangle(10, 40, 40, 20));
		rdexPanel.add(rdexIntervalLabel);
		rdexIntervalCB = new JComboBox(new String[] { "10", "30", "60", "300", "900", "3600" });
		rdexIntervalCB.setBounds(new Rectangle(55, 40, 50, 20));
		rdexPanel.add(rdexIntervalCB);
		rdexFunctionLabel = new JLabel("Function:");
		rdexFunctionLabel.setBounds(new Rectangle(115, 40, 45, 20));
		rdexPanel.add(rdexFunctionLabel);
		rdexFunctionCB = new JComboBox(new String[] { "RANDOM", "PYRAMID", "DROPOFF" });
		rdexFunctionCB.setBounds(new Rectangle(170, 40, 80, 20));
		rdexPanel.add(rdexFunctionCB);

		rdexMinLabel = new JLabel("Min:");
		rdexMinLabel.setBounds(new Rectangle(260, 40, 40, 20));
		rdexPanel.add(rdexMinLabel);
		rdexMinTF = new JTextField("");
		rdexMinTF.setBounds(new Rectangle(285, 40, 40, 20));
		rdexPanel.add(rdexMinTF);
		rdexMaxLabel = new JLabel("Max:");
		rdexMaxLabel.setBounds(new Rectangle(340, 40, 40, 20));
		rdexPanel.add(rdexMaxLabel);
		rdexMaxTF = new JTextField("");
		rdexMaxTF.setBounds(new Rectangle(370, 40, 40, 20));
		rdexPanel.add(rdexMaxTF);
		rdexDeltaLabel = new JLabel("Delta:");
		rdexDeltaLabel.setBounds(new Rectangle(420, 40, 40, 20));
		rdexPanel.add(rdexDeltaLabel);
		rdexDeltaTF = new JTextField("");
		rdexDeltaTF.setBounds(new Rectangle(455, 40, 40, 20));
		rdexPanel.add(rdexDeltaTF);
		rdexMaxStartCB = new JCheckBox("MaxStart");
		rdexMaxStartCB.setBounds(new Rectangle(500, 40, 70, 20));
		rdexPanel.add(rdexMaxStartCB);

		rdexTable = new JTable(getRdexTableModel());

		rdexTable.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				Point cell = e.getPoint();
				loadRdexPoint(cell);
				
			}
		});
		
		TableColumn rdexIntervalColumn = rdexTable.getColumnModel().getColumn(2);

		JComboBox rdexIntervalComboBox = new JComboBox();
		rdexIntervalComboBox.addItem("10");
		rdexIntervalComboBox.addItem("30");
		rdexIntervalComboBox.addItem("60");
		rdexIntervalComboBox.addItem("300");
		rdexIntervalComboBox.addItem("900");
		rdexIntervalComboBox.addItem("3600");
		rdexIntervalColumn.setCellEditor(new DefaultCellEditor(rdexIntervalComboBox));

		TableColumn rdexTypeColumn = rdexTable.getColumnModel().getColumn(0);

		JComboBox rdexTypeComboBox = new JComboBox();
		rdexTypeComboBox.addItem("Value");
		rdexTypeComboBox.addItem("Status");
		rdexTypeComboBox.addItem("Control");
		rdexTypeColumn.setCellEditor(new DefaultCellEditor(rdexTypeComboBox));

		TableColumn rdexFunctionColumn = rdexTable.getColumnModel().getColumn(3);

		JComboBox rdexFunctionComboBox = new JComboBox();
		rdexFunctionComboBox.addItem("RANDOM");
		rdexFunctionComboBox.addItem("PYRAMID");
		rdexFunctionComboBox.addItem("DROPOFF");
		rdexFunctionColumn.setCellEditor(new DefaultCellEditor(rdexFunctionComboBox));
		
		TableColumn rdexMaxStartColumn = rdexTable.getColumnModel().getColumn(7);
		
		JComboBox rdexMaxStartComboBox = new JComboBox();
		rdexMaxStartComboBox.addItem("true");
		rdexMaxStartComboBox.addItem("false");
		rdexMaxStartColumn.setCellEditor(new DefaultCellEditor(rdexMaxStartComboBox));

		rdexScrollPane = new JScrollPane(rdexTable);
		rdexScrollPane.setBounds(new Rectangle(10, 65, 570, 265));
		rdexScrollPane.setPreferredSize(new Dimension(570, 265));

		rdexArray = getRdexFileIO().getRdexPointsFromFile();
		loadRdexFile(rdexArray);

		rdexPanel.add(rdexScrollPane);
		rdexSave = new JButton("Save");
		rdexSave.setBounds(new Rectangle(520, 10, 60, 20));
		rdexPanel.add(rdexSave);
		rdexSave.setVisible(false);
		rdexDelete = new JButton("Delete");
		rdexDelete.setBounds(new Rectangle(520, 10, 65, 20));
		rdexPanel.add(rdexDelete);
		rdexDelete.setVisible(true);
		rdexEdit = new JButton("Edit");
		rdexEdit.setBounds(new Rectangle(460, 10, 55, 20));
		rdexPanel.add(rdexEdit);
		rdexAdd = new JButton("Add");
		rdexAdd.setBounds(new Rectangle(400, 10, 55, 20));
		rdexPanel.add(rdexAdd);
		rdexGenerate = new JButton("Generate");
		rdexGenerate.setBounds(new Rectangle(315, 10, 80, 20));
		rdexPanel.add(rdexGenerate);
		rdexCopy = new JButton("Copy");
		rdexCopy.setBounds(new Rectangle(245, 10, 65, 20));
		rdexPanel.add(rdexCopy);
		rdexCopy.setVisible(false);

		disableRdexFields();

		rdexPanel.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab("RDEX Points", icon, rdexPanel, "RDEX Point Editor");
		//tabbedPane.setMnemonicAt(1, KeyEvent.VK_R);

		rdexSave.addActionListener(this);
		rdexDelete.addActionListener(this);
		rdexEdit.addActionListener(this);
		rdexAdd.addActionListener(this);
		rdexGenerate.addActionListener(this);
		rdexCopy.addActionListener(this);

		try
		{
			rdexPointList = new RandomAccessFile("resource/rdex_points.cfg", "rw");
		} catch (Exception e)
		{
			System.out.println("error making random access file" + e);
		}
		
		valmetPanel = new JPanel();
		valmetPanel.setLayout(null);
		valmetTypeLabel = new JLabel("Type:");
		valmetTypeLabel.setBounds(new Rectangle(10, 10, 30, 20));
		valmetPanel.add(valmetTypeLabel);
		valmetTypeCB = new JComboBox(new String[] { "Value", "Status", "Control", "Analog Output" });
		valmetTypeCB.setBounds(new Rectangle(50, 10, 60, 20));
		valmetPanel.add(valmetTypeCB);
		valmetNameLabel = new JLabel("Name:");
		valmetNameLabel.setBounds(new Rectangle(120, 10, 30, 20));
		valmetPanel.add(valmetNameLabel);
		valmetNameTF = new JTextField("");
		valmetNameTF.setBounds(new Rectangle(160, 10, 80, 20));
		valmetPanel.add(valmetNameTF);
		valmetPortLabel = new JLabel("Port:");
		valmetPortLabel.setBounds(new Rectangle(250, 10, 30, 20));
		valmetPanel.add(valmetPortLabel);
		valmetPortTF = new JTextField("");
		valmetPortTF.setBounds(new Rectangle(280, 10, 50, 20));
		valmetPanel.add(valmetPortTF);
		valmetIntervalLabel = new JLabel("Interval:");
		valmetIntervalLabel.setBounds(new Rectangle(10, 40, 40, 20));
		valmetPanel.add(valmetIntervalLabel);
		valmetIntervalCB = new JComboBox(new String[] { "10", "30", "60", "300", "900", "3600" });
		valmetIntervalCB.setBounds(new Rectangle(55, 40, 50, 20));
		valmetPanel.add(valmetIntervalCB);
		valmetFunctionLabel = new JLabel("Function:");
		valmetFunctionLabel.setBounds(new Rectangle(110, 40, 45, 20));
		valmetPanel.add(valmetFunctionLabel);
		valmetFunctionCB = new JComboBox(new String[] { "RANDOM", "PYRAMID", "DROPOFF" });
		valmetFunctionCB.setBounds(new Rectangle(160, 40, 80, 20));
		valmetPanel.add(valmetFunctionCB);

		valmetMinLabel = new JLabel("Min:");
		valmetMinLabel.setBounds(new Rectangle(245, 40, 40, 20));
		valmetPanel.add(valmetMinLabel);
		valmetMinTF = new JTextField("");
		valmetMinTF.setBounds(new Rectangle(270, 40, 40, 20));
		valmetPanel.add(valmetMinTF);
		valmetMaxLabel = new JLabel("Max:");
		valmetMaxLabel.setBounds(new Rectangle(315, 40, 40, 20));
		valmetPanel.add(valmetMaxLabel);
		valmetMaxTF = new JTextField("");
		valmetMaxTF.setBounds(new Rectangle(345, 40, 40, 20));
		valmetPanel.add(valmetMaxTF);
		valmetDeltaLabel = new JLabel("Delta:");
		valmetDeltaLabel.setBounds(new Rectangle(390, 40, 40, 20));
		valmetPanel.add(valmetDeltaLabel);
		valmetDeltaTF = new JTextField("");
		valmetDeltaTF.setBounds(new Rectangle(420, 40, 40, 20));
		valmetPanel.add(valmetDeltaTF);
		valmetMaxStartCB = new JCheckBox("MaxStart");
		valmetMaxStartCB.setBounds(new Rectangle(465, 40, 70, 20));
		valmetPanel.add(valmetMaxStartCB);

		valmetTable = new JTable(getValmetTableModel());

		valmetTable.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				Point cell = e.getPoint();
				loadValmetPoint(cell);
			}
		});
		
		TableColumn valmetIntervalColumn = valmetTable.getColumnModel().getColumn(ValmetPointTableModel.INTERVAL_COLUMN);

		JComboBox valmetIntervalComboBox = new JComboBox();
		valmetIntervalComboBox.addItem("10");
		valmetIntervalComboBox.addItem("30");
		valmetIntervalComboBox.addItem("60");
		valmetIntervalComboBox.addItem("300");
		valmetIntervalComboBox.addItem("900");
		valmetIntervalComboBox.addItem("3600");
		valmetIntervalColumn.setCellEditor(new DefaultCellEditor(valmetIntervalComboBox));

		TableColumn valmetTypeColumn = valmetTable.getColumnModel().getColumn(ValmetPointTableModel.TYPE_COLUMN);

		JComboBox valmetTypeComboBox = new JComboBox();
		valmetTypeComboBox.addItem("Value");
		valmetTypeComboBox.addItem("Status");
		valmetTypeComboBox.addItem("Control");
	    valmetTypeComboBox.addItem("Analog Output");

		valmetTypeColumn.setCellEditor(new DefaultCellEditor(valmetTypeComboBox));

		TableColumn valmetFunctionColumn = valmetTable.getColumnModel().getColumn(ValmetPointTableModel.FUNCTION_COLUMN);

		JComboBox valmetFunctionComboBox = new JComboBox();
		valmetFunctionComboBox.addItem("RANDOM");
		valmetFunctionComboBox.addItem("PYRAMID");
		valmetFunctionComboBox.addItem("DROPOFF");
		valmetFunctionColumn.setCellEditor(new DefaultCellEditor(valmetFunctionComboBox));
		
		TableColumn valmetMaxStartColumn = valmetTable.getColumnModel().getColumn(ValmetPointTableModel.MAXSTART_COLUMN);
		
		JComboBox valmetMaxStartComboBox = new JComboBox();
		valmetMaxStartComboBox.addItem("true");
		valmetMaxStartComboBox.addItem("false");
		valmetMaxStartColumn.setCellEditor(new DefaultCellEditor(valmetMaxStartComboBox));

		valmetScrollPane = new JScrollPane(valmetTable);
		valmetScrollPane.setBounds(new Rectangle(10, 65, 595, 265));
		valmetScrollPane.setPreferredSize(new Dimension(595, 265));

		valmetArray = getValmetFileIO().getAllValmetPointsFromFile();
		loadValmetFile(valmetArray);

		valmetPanel.add(valmetScrollPane);
		valmetSave = new JButton("Save");
		valmetSave.setBounds(new Rectangle(540, 10, 65, 20));
		valmetPanel.add(valmetSave);
		valmetSave.setVisible(false);
		valmetDelete = new JButton("Delete");
		valmetDelete.setBounds(new Rectangle(540, 10, 65, 20));
		valmetPanel.add(valmetDelete);
		valmetDelete.setVisible(true);
		valmetEdit = new JButton("Edit");
		valmetEdit.setBounds(new Rectangle(480, 10, 55, 20));
		valmetPanel.add(valmetEdit);
		valmetAdd = new JButton("Add");
		valmetAdd.setBounds(new Rectangle(420, 10, 55, 20));
		valmetPanel.add(valmetAdd);
		valmetGenerate = new JButton("Generate");
		valmetGenerate.setBounds(new Rectangle(335, 10, 80, 20));
		valmetPanel.add(valmetGenerate);
		valmetCopy = new JButton("Copy");
		valmetCopy.setBounds(new Rectangle(540, 40, 65, 20));
		valmetPanel.add(valmetCopy);
		valmetCopy.setVisible(false);

		disableValmetFields();

		valmetPanel.setPreferredSize(new Dimension(635, 400));
		tabbedPane.addTab("Valmet Points", icon, valmetPanel, "Valmet Point Editor");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		valmetSave.addActionListener(this);
		valmetDelete.addActionListener(this);
		valmetEdit.addActionListener(this);
		valmetAdd.addActionListener(this);
		valmetGenerate.addActionListener(this);
		valmetCopy.addActionListener(this);

		try
		{
			valmetPointList = new RandomAccessFile("resource/valmet_points.cfg", "rw");
		} catch (Exception e)
		{
			System.out.println("error making random access file" + e);
		}

		//Add the tabbed pane to this panel. 
		this.getContentPane().add(tabbedPane);

		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.setSize(new Dimension(635, 400));
		this.setTitle("Point Editor");

		this.getContentPane().setBackground(Color.gray);

		this.setIconImage(icon.getImage());

	}


	/**
	 * @param valmetpoints
	 */
	private void loadValmetFile(Object[] valmetpoints)
	{
		for (int i = 0; i < valmetpoints.length; i++)
		{
			ValmetPoint nextPoint = (ValmetPoint) valmetpoints[i];

			try
			{
				if (nextPoint.getPointType().equalsIgnoreCase(null))
				{
					return;
				}
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					return;
				}
			}
			getValmetTableModel().addRow((ValmetPoint) valmetpoints[i]);

		}

		getValmetTableModel().fireTableDataChanged();
	}

	/**
	 * @param rdexpoints
	 */
	private void loadRdexFile(Object[] rdexpoints)
	{

		for (int i = 0; i < rdexpoints.length; i++)
		{
			RdexPoint nextPoint = (RdexPoint) rdexpoints[i];

			try
			{
				if (nextPoint.getPointType().equalsIgnoreCase(null))
				{
					return;
				}
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					return;
				}
			}
			getRdexTableModel().addRow((RdexPoint) rdexpoints[i]);

		}

		getRdexTableModel().fireTableDataChanged();
	}

	/**
	 * @param acspoints
	 */
	private void loadAcsFile(Object[] acspoints)
	{

		for (int i = 0; i < acspoints.length; i++)
		{
			ACSPoint nextPoint = (ACSPoint) acspoints[i];

			try
			{
				if (nextPoint.getPointType().equalsIgnoreCase(null))
				{
					return;
				}
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					return;
				}
			}
			getAcsTableModel().addRow((ACSPoint) acspoints[i]);

		}

		getAcsTableModel().fireTableDataChanged();
	}

	public RdexPointTableModel getRdexTableModel()
	{
		if (rdexTableModel == null)
		{
			rdexTableModel = new RdexPointTableModel();
		}
		return rdexTableModel;
	}
	
	public RdexFileIO getRdexFileIO()
	{
		if(rdexFileIO == null)
		{
			rdexFileIO = new RdexFileIO(rdexFile);
		}
		return rdexFileIO;
	}

	public AcsPointTableModel getAcsTableModel()
	{
		if (acsTableModel == null)
		{
			acsTableModel = new AcsPointTableModel();
		}
		return acsTableModel;
	}
	
	public AcsFileIO getAcsFileIO()
	{
		if(acsFileIO == null)
		{
			acsFileIO = new AcsFileIO(acsFile);
		}
		return acsFileIO;
	}

	public ValmetPointTableModel getValmetTableModel()
	{
		if (valmetTableModel == null)
		{
			valmetTableModel = new ValmetPointTableModel();
		}
		return valmetTableModel;
	}
	
	public ValmetFileIO getValmetFileIO()
	{
		if(valmetFileIO == null)
		{
			valmetFileIO = new ValmetFileIO(valmetFile);
		}
		return valmetFileIO;
	}

	/**
	 * @param cell cell of row clicked
	 */
	protected void loadValmetPoint(Point cell)
	{
		int r = getValmetTable().rowAtPoint(cell);
		JTable valmetTable = getValmetTable();
		valmetTypeCB.setSelectedItem(valmetTable.getValueAt(r, ValmetPointTableModel.TYPE_COLUMN));
		valmetNameTF.setText((String) valmetTable.getValueAt(r, ValmetPointTableModel.NAME_COLUMN));
		valmetPortTF.setText((String) valmetTable.getValueAt(r, ValmetPointTableModel.PORT_COLUMN));
		valmetIntervalCB.setSelectedItem(valmetTable.getValueAt(r, ValmetPointTableModel.INTERVAL_COLUMN));
		valmetFunctionCB.setSelectedItem(valmetTable.getValueAt(r, ValmetPointTableModel.FUNCTION_COLUMN));
		valmetMinTF.setText((String) valmetTable.getValueAt(r, ValmetPointTableModel.MIN_COLUMN));
		valmetMaxTF.setText((String) valmetTable.getValueAt(r, ValmetPointTableModel.MAX_COLUMN));
		valmetDeltaTF.setText((String) valmetTable.getValueAt(r, ValmetPointTableModel.DELTA_COLUMN));
		Boolean bool = new Boolean((String) valmetTable.getValueAt(r, ValmetPointTableModel.MAXSTART_COLUMN));
		valmetMaxStartCB.setSelected(bool.booleanValue());
	}

	/**
	 * @param cell cell of row clicked
	 */
	protected void loadAcsPoint(Point cell)
	{
		int r = getAcsTable().rowAtPoint(cell);
		acsTypeCB.setSelectedItem(getAcsTable().getValueAt(r, 0));
		acsRemoteTF.setText((String) getAcsTable().getValueAt(r, 1));
		acsPointTF.setText((String) getAcsTable().getValueAt(r, 2));
		acsCategoryTF.setText((String) getAcsTable().getValueAt(r, 3));
		acsIntervalCB.setSelectedItem(getAcsTable().getValueAt(r, 4));
		acsFunctionCB.setSelectedItem(getAcsTable().getValueAt(r, 5));
		acsMinTF.setText((String) getAcsTable().getValueAt(r, 6));
		acsMaxTF.setText((String) getAcsTable().getValueAt(r, 7));
		acsDeltaTF.setText((String) getAcsTable().getValueAt(r, 8));
		Boolean bool = new Boolean((String) getAcsTable().getValueAt(r, 9));
		acsMaxStartCB.setSelected(bool.booleanValue());
	}

	/**
	 * @param cell cell of row clicked
	 */
	protected void loadRdexPoint(Point cell)
	{
		int r = getRdexTable().rowAtPoint(cell);
		rdexTypeCB.setSelectedItem(getRdexTable().getValueAt(r, 0));
		rdexNameTF.setText((String) getRdexTable().getValueAt(r, 1));
		rdexIntervalCB.setSelectedItem(getRdexTable().getValueAt(r, 2));
		rdexFunctionCB.setSelectedItem(getRdexTable().getValueAt(r, 3));
		rdexMinTF.setText((String) getRdexTable().getValueAt(r, 4));
		rdexMaxTF.setText((String) getRdexTable().getValueAt(r, 5));
		rdexDeltaTF.setText((String) getRdexTable().getValueAt(r, 6));
		Boolean bool = new Boolean((String) getRdexTable().getValueAt(r, 7));
		rdexMaxStartCB.setSelected(bool.booleanValue());
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == acsAdd)
		{
			acsAdd_actionPerformed();
		}
		if (e.getSource() == acsEdit)
		{
			acsEdit_actionPerformed();
		}
		if (e.getSource() == acsSave)
		{
			acsSave_actionPerformed();
		}
		if (e.getSource() == acsDelete)
		{
			acsDelete_actionPerformed();
		}
		if (e.getSource() == acsGenerate)
		{
			acsGenerate_actionPerformed();
		}
		if (e.getSource() == acsCopy)
		{
			acsCopy_actionPerformed();
		}
		if (e.getSource() == rdexAdd)
		{
			rdexAdd_actionPerformed();
		}
		if (e.getSource() == rdexEdit)
		{
			rdexEdit_actionPerformed();
		}
		if (e.getSource() == rdexSave)
		{
			rdexSave_actionPerformed();
		}
		if (e.getSource() == rdexDelete)
		{
			rdexDelete_actionPerformed();
		}
		if (e.getSource() == rdexGenerate)
		{
			rdexGenerate_actionPerformed();
		}
		if (e.getSource() == rdexCopy)
		{
			rdexCopy_actionPerformed();
		}
		if (e.getSource() == valmetAdd)
		{
			valmetAdd_actionPerformed();
		}
		if (e.getSource() == valmetEdit)
		{
			valmetEdit_actionPerformed();
		}
		if (e.getSource() == valmetSave)
		{
			valmetSave_actionPerformed();
		}
		if (e.getSource() == valmetDelete)
		{
			valmetDelete_actionPerformed();
		}
		if (e.getSource() == valmetGenerate)
		{
			valmetGenerate_actionPerformed();
		}
		if (e.getSource() == valmetCopy)
		{
			valmetCopy_actionPerformed();
		}
	}
	
	/**
	 * 
	 */
	private void valmetCopy_actionPerformed()
	{
		

	}

	/**
	 * 
	 */
	private void valmetGenerate_actionPerformed()
	{
		if (!deviceadded)
		{

			try
			{
				Transaction.createTransaction(Transaction.INSERT, getVirtualDevice()).execute();
				deviceadded = true;
			} catch (TransactionException tex)
			{
				System.out.println(tex);
			}
		}
		PointBase analogPoint;
		Object[] points = getValmetFileIO().getAllValmetPointsFromFile();
		int j = 0;
		int exit = 0;
		while (exit != 1)
		{
			String type = "";
			String name = "";

			try
			{
				ValmetPoint thispoint = (ValmetPoint) points[j];
				type = thispoint.getPointType();
				name = thispoint.getPointName();
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					System.out.println("Finished generating Valmet points.");
				} else
					e.printStackTrace(System.out);
				exit = 1;
			}

			if (type.equals(null))
			{
				exit = 1;
			} else if ("Value".equalsIgnoreCase(type))
			{
				analogPoint = PointFactory.createAnalogPoint("VALMET " + name, virtDevice.getPAObjectID(), null, 0, PointUnits.UOMID_KW, -1);
				
				Integer pointID = analogPoint.getPoint().getPointID();

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");

				String trans = "Point:" + name + ";POINTTYPE:Analog;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("VALMET");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}

			} else if ("Status".equalsIgnoreCase(type))
			{
                Integer pointID = null;

				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "VALMET " + name, virtDevice.getPAObjectID(), new Integer(0));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");

				String trans = "Point:" + name + ";POINTTYPE:Status;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("VALMET");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}

			} else if ("Control".equalsIgnoreCase(type))
			{
                Integer pointID = null;
			    
				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "VALMET " + name, virtDevice.getPAObjectID(), new Integer(1));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive for control");

				String trans = "Point:" + name + ";POINTTYPE:Control;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("VALMET");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}
			}
			j++;
		}
	}

	private void valmetSave_actionPerformed()
	{
		if (valmetediting)
		{
			// editing an old point
			// save new point attributes
			String newValmetType = valmetTypeCB.getSelectedItem().toString();
			String newValmetName = valmetNameTF.getText();
			String newValmetInterval = valmetIntervalCB.getSelectedItem().toString();
			String newValmetFunction = valmetFunctionCB.getSelectedItem().toString();
			String newValmetMin = valmetMinTF.getText();
			String newValmetMax = valmetMaxTF.getText();
			String newValmetDelta = valmetDeltaTF.getText();
			String newValmetPort = valmetPortTF.getText();
			Boolean bool = new Boolean(valmetMaxStartCB.isSelected());
			String newValmetMaxStart = bool.toString();
			ValmetPoint newpoint = new ValmetPoint(newValmetType, newValmetName, newValmetPort, newValmetInterval, newValmetFunction, newValmetMin, newValmetMax, newValmetDelta, newValmetMaxStart);

			String[] points = getValmetFileIO().readFile(new File(valmetFile));

			String oldpoint = oldvalmetpoint.getPointType() + ";" + oldvalmetpoint.getPointName() + ";"  + oldvalmetpoint.getPort() + ";" + oldvalmetpoint.getPointInterval() + ";" + oldvalmetpoint.getPointFunction() + ";" + oldvalmetpoint.getPointMin() + ";" + oldvalmetpoint.getPointMax() + ";" + oldvalmetpoint.getPointDelta() + ";" + oldvalmetpoint.getPointMaxStart();

			for (int i = 0; i < points.length; i++)
			{
				if (oldpoint.equalsIgnoreCase(points[i]))
				{
					String newpointstring = newpoint.getPointType() + ";" + newpoint.getPointName() + ";" + newpoint.getPort() + ";" + newpoint.getPointInterval() + ";" + newpoint.getPointFunction() + ";" + newpoint.getPointMin() + ";" + newpoint.getPointMax() + ";" + newpoint.getPointDelta() + ";" + newpoint.getPointMaxStart();

					points[i] = newpointstring;
					break;
				}
			}
			
			getValmetFileIO().writeValmetFile(points, new File(valmetFile));

			valmetArray = getValmetFileIO().getAllValmetPointsFromFile();
			getValmetTableModel().clear();
			loadValmetFile(valmetArray);

			valmetediting = false;

		} else
		{
			//adding a new point
			Boolean bool = new Boolean(valmetMaxStartCB.isSelected());

			ValmetPoint newpoint = new ValmetPoint(valmetTypeCB.getSelectedItem().toString(), valmetNameTF.getText(), valmetPortTF.getText(), valmetIntervalCB.getSelectedItem().toString(), valmetFunctionCB.getSelectedItem().toString(), valmetMinTF.getText(), valmetMaxTF.getText(), valmetDeltaTF.getText(), bool.toString());

			getValmetFileIO().addValmetPointToFile(newpoint);
			getValmetTableModel().addRow(newpoint);
		}

		disableValmetFields();
		valmetEdit.setEnabled(true);
		valmetAdd.setEnabled(true);
		valmetDelete.setVisible(true);
		valmetSave.setVisible(false);
		valmetCopy.setVisible(false);
	}

	private void valmetDelete_actionPerformed()
	{
		int row = getValmetTable().getSelectedRow();
		Vector<String> vec = new Vector<String>();
		String[] points = getValmetFileIO().readFile(new File(valmetFile));
		for (int i = 0; i < points.length; i++)
		{
			vec.add(points[i]);
		}
		String delType = getValmetTableModel().getValueAt(row, ValmetPointTableModel.TYPE_COLUMN).toString();
		String delName = getValmetTableModel().getValueAt(row, ValmetPointTableModel.NAME_COLUMN).toString();
		String portNum = getValmetTableModel().getValueAt(row, ValmetPointTableModel.PORT_COLUMN).toString();
		String delInterval = getValmetTableModel().getValueAt(row, ValmetPointTableModel.INTERVAL_COLUMN).toString();
		String delFunction = getValmetTableModel().getValueAt(row, ValmetPointTableModel.FUNCTION_COLUMN).toString();
		String delMin = getValmetTableModel().getValueAt(row, ValmetPointTableModel.MIN_COLUMN).toString();
		String delMax = getValmetTableModel().getValueAt(row, ValmetPointTableModel.MAX_COLUMN).toString();
		String delDelta = getValmetTableModel().getValueAt(row, ValmetPointTableModel.DELTA_COLUMN).toString();
		String delMaxStart = getValmetTableModel().getValueAt(row, ValmetPointTableModel.MAXSTART_COLUMN).toString();

		String del = delType + ";" + delName + ";" + portNum + ";"+ delInterval + ";" + delFunction + ";" + delMin + ";" + delMax + ";" + delDelta + ";" + delMaxStart;
		System.out.println("del: " + del);
		int index = vec.indexOf((Object) del);
		vec.remove(index);
		vec.toArray(points);

		getValmetFileIO().writeValmetFile((String[]) points, new File(valmetFile));
		
		valmetArray = getValmetFileIO().getAllValmetPointsFromFile();
		getValmetTableModel().clear();
		loadValmetFile(valmetArray);
	}

	/**
	 * 
	 */
	private void valmetEdit_actionPerformed()
	{
		// save the old attributes
		String oldValmetType = valmetTypeCB.getSelectedItem().toString();
		String oldValmetName = valmetNameTF.getText();
		String oldValmetInterval = valmetIntervalCB.getSelectedItem().toString();
		String oldValmetFunction = valmetFunctionCB.getSelectedItem().toString();
		String oldValmetMin = valmetMinTF.getText();
		String oldValmetMax = valmetMaxTF.getText();
		String oldValmetDelta = valmetDeltaTF.getText();
		String oldValmetPort = valmetPortTF.getText();
		Boolean bool = new Boolean(valmetMaxStartCB.isSelected());
		String oldValmetMaxStart = bool.toString();
		oldvalmetpoint = new ValmetPoint(oldValmetType, oldValmetName, oldValmetPort, oldValmetInterval, oldValmetFunction, oldValmetMin, oldValmetMax, oldValmetDelta, oldValmetMaxStart);
		valmetediting = true;
		enableValmetFields();
		valmetAdd.setEnabled(false);
		valmetDelete.setVisible(false);
		valmetSave.setVisible(true);
		valmetCopy.setVisible(true);
	}

	/**
	 * 
	 */
	private void valmetAdd_actionPerformed()
	{
		enableValmetFields();
		clearValmetFields();
		valmetEdit.setEnabled(false);
		valmetDelete.setVisible(false);
		valmetSave.setVisible(true);
		valmetCopy.setVisible(true);
	}

	/**
	 * 
	 */
	private void rdexCopy_actionPerformed()
	{
		

	}

	private void rdexGenerate_actionPerformed()
	{

		String destination = JOptionPane.showInputDialog("Enter Destination: ie:SIM");

		if (!deviceadded)
		{

			try
			{
				Transaction.createTransaction(Transaction.INSERT, getVirtualDevice()).execute();
				deviceadded = true;
			} catch (TransactionException tex)
			{
				System.out.println(tex);
			}
		}
		PointBase analogPoint;
		Object[] points = getRdexFileIO().getRdexPointsFromFile();
		int j = 0;
		int exit = 0;
		while (exit != 1)
		{
			String type = "";
			String name = "";

			try
			{
				RdexPoint thispoint = (RdexPoint) points[j];
				type = thispoint.getPointType();
				name = thispoint.getPointName();
			} catch (Exception e)
			{
				if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
				{
					System.out.println("Finished generating RDEX points.");
				} else
					e.printStackTrace(System.out);
				exit = 1;
			}

			if (type.equals(null))
			{
				exit = 1;
			} else if (type.equalsIgnoreCase("Value"))
			{

				analogPoint = PointFactory.createAnalogPoint("RDEX " + name, virtDevice.getPAObjectID(), null, 0, PointUnits.UOMID_KW, -1);
				Integer pointID = analogPoint.getPoint().getPointID();

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");

				String trans = "Translation:" + name + ";Destination/Source:" + destination + ";POINTTYPE:Analog;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("RDEX");

				newFDRTranslation.setDestination(destination);

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}

			} else if ("Status".equalsIgnoreCase(type))
			{
                Integer pointID = null;

				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "RDEX " + name, virtDevice.getPAObjectID(), new Integer(0));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");

				String trans = "Translation:" + name + ";Destination/Source:" + destination + ";POINTTYPE:Status;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("RDEX");

				newFDRTranslation.setDestination(destination);

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException e)
				{
					System.out.println(e);

				}

			} else if ("Control".equalsIgnoreCase(type))
			{
                Integer pointID = null;

				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "RDEX " + name, virtDevice.getPAObjectID(), new Integer(1));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive for control");

				String trans = "Translation:" + name + ";Destination/Source:" + destination + ";POINTTYPE:Control;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("RDEX");

				newFDRTranslation.setDestination(destination);

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}
			}
			j++;
		}
	}

	/**
	 * 
	 */
	private void rdexSave_actionPerformed()
	{
		if (rdexediting)
		{
			// editing an old point
			// save new point attributes
			String newRdexType = rdexTypeCB.getSelectedItem().toString();
			String newRdexName = rdexNameTF.getText();
			String newRdexInterval = rdexIntervalCB.getSelectedItem().toString();
			String newRdexFunction = rdexFunctionCB.getSelectedItem().toString();
			String newRdexMin = rdexMinTF.getText();
			String newRdexMax = rdexMaxTF.getText();
			String newRdexDelta = rdexDeltaTF.getText();
			Boolean bool = new Boolean(rdexMaxStartCB.isSelected());
			String newRdexMaxStart = bool.toString();
			RdexPoint newpoint = new RdexPoint(newRdexType, newRdexName, newRdexInterval, newRdexFunction, newRdexMin, newRdexMax, newRdexDelta, newRdexMaxStart);
			String[] points = getRdexFileIO().readFile(new File(rdexFile));

			String oldpoint = oldrdexpoint.getPointType() + ";" + oldrdexpoint.getPointName() + ";" + oldrdexpoint.getPointInterval() + ";" + oldrdexpoint.getPointFunction() + ";" + oldrdexpoint.getPointMin() + ";" + oldrdexpoint.getPointMax() + ";" + oldrdexpoint.getPointDelta() + ";" + oldrdexpoint.getPointMaxStart();
			for (int i = 0; i < points.length; i++)
			{
				if (oldpoint.equalsIgnoreCase(points[i]))
				{
					String newpointstring = newpoint.getPointType() + ";" + newpoint.getPointName() + ";" + newpoint.getPointInterval() + ";" + newpoint.getPointFunction() + ";" + newpoint.getPointMin() + ";" + newpoint.getPointMax() + ";" + newpoint.getPointDelta() + ";" + newpoint.getPointMaxStart();
					points[i] = newpointstring;
					break;
				}
			}
			
			getRdexFileIO().writeRdexFile(points, new File(rdexFile));

			rdexArray = getRdexFileIO().getRdexPointsFromFile();
			getRdexTableModel().clear();
			loadRdexFile(rdexArray);

			rdexediting = false;
		} else
		{
			Boolean bool = new Boolean(rdexMaxStartCB.isSelected());

			RdexPoint newpoint = new RdexPoint(rdexTypeCB.getSelectedItem().toString(), rdexNameTF.getText(), rdexIntervalCB.getSelectedItem().toString(), rdexFunctionCB.getSelectedItem().toString(), rdexMinTF.getText(), rdexMaxTF.getText(), rdexDeltaTF.getText(), bool.toString());
			getRdexFileIO().addRdexPointToFile(newpoint);
			getRdexTableModel().addRow(newpoint);
		}
		disableRdexFields();
		rdexEdit.setEnabled(true);
		rdexAdd.setEnabled(true);
		rdexDelete.setVisible(true);
		rdexSave.setVisible(false);
		rdexCopy.setVisible(false);
	}

	private void rdexDelete_actionPerformed()
	{
		int row = getRdexTable().getSelectedRow();
		Vector<String> vec = new Vector<String>();
		String[] points = getRdexFileIO().readFile(new File(rdexFile));
		for (int i = 0; i < points.length; i++)
		{
			vec.add(points[i]);
		}
		String delType = getRdexTableModel().getValueAt(row, 0).toString();
		String delName = getRdexTableModel().getValueAt(row, 1).toString();
		String delInterval = getRdexTableModel().getValueAt(row, 2).toString();
		String delFunction = getRdexTableModel().getValueAt(row, 3).toString();
		String delMin = getRdexTableModel().getValueAt(row, 4).toString();
		String delMax = getRdexTableModel().getValueAt(row, 5).toString();
		String delDelta = getRdexTableModel().getValueAt(row, 6).toString();
		String delMaxStart = getRdexTableModel().getValueAt(row, 7).toString();

		String del = delType + ";" + delName + ";" + delInterval + ";" + delFunction + ";" + delMin + ";" + delMax + ";" + delDelta + ";" + delMaxStart;
		System.out.println("del: " + del);
		int index = vec.indexOf((Object) del);
		vec.remove(index);
		vec.toArray(points);

		
		getRdexFileIO().writeRdexFile((String[]) points, new File(rdexFile));
		rdexArray = getRdexFileIO().getRdexPointsFromFile();
		getRdexTableModel().clear();
		loadRdexFile(rdexArray);
	}

	/**
	 * 
	 */
	private void rdexEdit_actionPerformed()
	{
		// save the old attributes
		String oldRdexType = rdexTypeCB.getSelectedItem().toString();
		String oldRdexName = rdexNameTF.getText();
		String oldRdexInterval = rdexIntervalCB.getSelectedItem().toString();
		String oldRdexFunction = rdexFunctionCB.getSelectedItem().toString();
		String oldRdexMin = rdexMinTF.getText();
		String oldRdexMax = rdexMaxTF.getText();
		String oldRdexDelta = rdexDeltaTF.getText();
		Boolean bool = new Boolean(rdexMaxStartCB.isSelected());
		String oldRdexMaxStart = bool.toString();
		oldrdexpoint = new RdexPoint(oldRdexType, oldRdexName, oldRdexInterval, oldRdexFunction, oldRdexMin, oldRdexMax, oldRdexDelta, oldRdexMaxStart);
		rdexediting = true;
		enableRdexFields();
		rdexAdd.setEnabled(false);
		rdexDelete.setVisible(false);
		rdexSave.setVisible(true);
		rdexCopy.setVisible(true);
	}

	/**
	 * 
	 */
	private void rdexAdd_actionPerformed()
	{
		enableRdexFields();
		clearRdexFields();
		rdexEdit.setEnabled(false);
		rdexDelete.setVisible(false);
		rdexSave.setVisible(true);
		rdexCopy.setVisible(true);
	}

	/**
	 * 
	 */
	private void acsCopy_actionPerformed()
	{
		

	}

	private VirtualDevice getVirtualDevice()
	{
		if (virtDevice == null)
		{
			virtDevice = (VirtualDevice) DeviceFactory.createDevice(DeviceTypes.VIRTUAL_SYSTEM);
			Integer id = DaoFactory.getPaoDao().getNextPaoId();
            DaoFactory.getPaoDao().getNextPaoId();
			System.out.println("PAO Name: FDEmulator" + id);
			virtDevice.setPAOName("FDEmulator" + id.toString());
		}
		return virtDevice;
	}

	/**
	 * 
	 */
	private void acsGenerate_actionPerformed()
	{
		if (!deviceadded)
		{

			try
			{
				Transaction.createTransaction(Transaction.INSERT, getVirtualDevice()).execute();
				deviceadded = true;
			} catch (TransactionException tex)
			{
				System.out.println(tex);
			}
		}
		PointBase analogPoint;
		Object[] points = getAcsFileIO().getAcsPointsFromFile();
		int j = 0;
		int exit = 0;
		while (exit != 1)
		{
			String type = "";
			String remote = "";
			String point = "";
			String category = "";
			try
			{
				ACSPoint thispoint = (ACSPoint) points[j];
				type = thispoint.getPointType();
				Integer newremote = new Integer(thispoint.getPointRemote());
				remote = newremote.toString();
				Integer newpointnum = new Integer(thispoint.getPointNumber());
				point = newpointnum.toString();
				category = thispoint.getPointCategory();

			} catch (Exception e)
			{
				if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
				{
					System.out.println("Finished generating ACS points.");
				} else
					e.printStackTrace(System.out);
				exit = 1;
			}

			if (type.equals(null))
			{
				exit = 1;
			} else if ("Value".equalsIgnoreCase(type))
			{

				analogPoint = PointFactory.createAnalogPoint("ACS " + remote + " " + point + " " + category, virtDevice.getPAObjectID(), null, 0, PointUnits.UOMID_KW, -1);
				Integer pointID = analogPoint.getPoint().getPointID();

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");
				String cat = "";
				if ("P".equalsIgnoreCase(category))
				{
					cat = "PSEUDO";
				} else if ("R".equalsIgnoreCase(category))
				{
					cat = "REAL";
				} else
					cat = "CALCULATED";
				String trans = "Category:" + cat + ";Remote:" + remote + ";Point:" + point + ";POINTTYPE:Analog;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("ACS");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}

			} else if ("Status".equalsIgnoreCase(type))
			{
                Integer pointID = null;

				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "ACS " + remote + " " + point + " " + category, virtDevice.getPAObjectID(), new Integer(0));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive");
				String cat = "";
				if ("P".equalsIgnoreCase(category))
				{
					cat = "PSEUDO";
				} else if ("R".equalsIgnoreCase(category))
				{
					cat = "REAL";
				} else
					cat = "CALCULATED";
				String trans = "Category:" + cat + ";Remote:" + remote + ";Point:" + point + ";POINTTYPE:Status;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("ACS");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}

			} else if ("Control".equalsIgnoreCase(type))
			{
                Integer pointID = null;

				analogPoint = PointFactory.createNewPoint(pointID, com.cannontech.database.data.point.PointTypes.STATUS_POINT, "ACS " + remote + " " + point + " " + category, virtDevice.getPAObjectID(), new Integer(1));

				analogPoint.getPoint().setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));

				FDRTranslation newFDRTranslation = new FDRTranslation(pointID);
				newFDRTranslation.setDirectionType("Receive for control");
				String cat = "";
				if ("P".equalsIgnoreCase(category))
				{
					cat = "PSEUDO";
				} else if ("R".equalsIgnoreCase(category))
				{
					cat = "REAL";
				} else
					cat = "CALCULATED";
				String trans = "Category:" + cat + ";Remote:" + remote + ";Point:" + point + ";POINTTYPE:Control;";
				newFDRTranslation.setTranslation(trans);

				newFDRTranslation.setInterfaceType("ACS");

				newFDRTranslation.setDestination(newFDRTranslation.getInterfaceType());

				analogPoint.getPointFDRVector().addElement(newFDRTranslation);

				try
				{
					Transaction.createTransaction(Transaction.INSERT, analogPoint).execute();
				} catch (TransactionException tex)
				{
					System.out.println(tex);
				}
			}
			j++;
		}
	}

	/**
	 * 
	 */
	private void acsSave_actionPerformed()
	{
		if (acsediting)
		{
			// editing an old point
			// save new point attributes
			String newAcsType = acsTypeCB.getSelectedItem().toString();
			String newAcsRemote = acsRemoteTF.getText();
			String newAcsPointNum = acsPointTF.getText();
			String newAcsCategory = acsCategoryTF.getText();
			String newAcsInterval = acsIntervalCB.getSelectedItem().toString();
			String newAcsFunction = acsFunctionCB.getSelectedItem().toString();
			String newAcsMin = acsMinTF.getText();
			String newAcsMax = acsMaxTF.getText();
			String newAcsDelta = acsDeltaTF.getText();
			Boolean bool = new Boolean(acsMaxStartCB.isSelected());
			String newAcsMaxStart = bool.toString();
			ACSPoint newpoint = new ACSPoint(newAcsType, newAcsRemote, newAcsPointNum, newAcsCategory, newAcsInterval, newAcsFunction, newAcsMin, newAcsMax, newAcsDelta, newAcsMaxStart);
			String[] points = getAcsFileIO().readFile(new File(acsFile));

			String oldpoint = oldacspoint.getPointType() + ";" + oldacspoint.getPointRemote() + ";" + oldacspoint.getPointNumber() + ";" + oldacspoint.getPointCategory() + ";" + oldacspoint.getPointInterval() + ";" + oldacspoint.getPointFunction() + ";" + oldacspoint.getPointMin() + ";" + oldacspoint.getPointMax() + ";" + oldacspoint.getPointDelta() + ";" + oldacspoint.getPointMaxStart();
			for (int i = 0; i < points.length; i++)
			{
				if (oldpoint.equalsIgnoreCase(points[i]))
				{
					String newpointstring = newpoint.getPointType() + ";" + newpoint.getPointRemote() + ";" + newpoint.getPointNumber() + ";" + newpoint.getPointCategory() + ";" + newpoint.getPointInterval() + ";" + newpoint.getPointFunction() + ";" + newpoint.getPointMin() + ";" + newpoint.getPointMax() + ";" + newpoint.getPointDelta() + ";" + newpoint.getPointMaxStart();
					points[i] = newpointstring;
					break;
				}
			}
			
			getAcsFileIO().writeACSFile(points, new File(acsFile));
			acsArray = getAcsFileIO().getAcsPointsFromFile();
			getAcsTableModel().clear();
			loadAcsFile(acsArray);

			acsediting = false;
		} else
		{
			Boolean bool = new Boolean(acsMaxStartCB.isSelected());

			ACSPoint newpoint = new ACSPoint(acsTypeCB.getSelectedItem().toString(), acsRemoteTF.getText(), acsPointTF.getText(), acsCategoryTF.getText(), acsIntervalCB.getSelectedItem().toString(), acsFunctionCB.getSelectedItem().toString(), acsMinTF.getText(), acsMaxTF.getText(), acsDeltaTF.getText(), bool.toString());
			getAcsFileIO().addAcsPointToFile(newpoint);
			getAcsTableModel().addRow(newpoint);
		}
		disableAcsFields();
		acsEdit.setEnabled(true);
		acsAdd.setEnabled(true);
		acsDelete.setVisible(true);
		acsSave.setVisible(false);
		acsCopy.setVisible(false);

	}

	private void acsDelete_actionPerformed()
	{
		int row = getAcsTable().getSelectedRow();
		Vector<String> vec = new Vector<String>();
		String[] points = getAcsFileIO().readFile(new File(acsFile));
		for (int i = 0; i < points.length; i++)
		{
			vec.add(points[i]);
		}
		String delType = getAcsTableModel().getValueAt(row, 0).toString();
		String delRemote = getAcsTableModel().getValueAt(row, 1).toString();
		String delPoint = getAcsTableModel().getValueAt(row, 2).toString();
		String delCategory = getAcsTableModel().getValueAt(row, 3).toString();
		String delInterval = getAcsTableModel().getValueAt(row, 4).toString();
		String delFunction = getAcsTableModel().getValueAt(row, 5).toString();
		String delMin = getAcsTableModel().getValueAt(row, 6).toString();
		String delMax = getAcsTableModel().getValueAt(row, 7).toString();
		String delDelta = getAcsTableModel().getValueAt(row, 8).toString();
		String delMaxStart = getAcsTableModel().getValueAt(row, 9).toString();

		String del = delType + ";" + delRemote + ";" + delPoint + ";" + delCategory + ";" + delInterval + ";" + delFunction + ";" + delMin + ";" + delMax + ";" + delDelta + ";" + delMaxStart;
		System.out.println("del: " + del);
		int index = vec.indexOf((Object) del);
		vec.remove(index);
		vec.toArray(points);

		getAcsFileIO().writeACSFile((String[]) points, new File(acsFile));
		acsArray = getAcsFileIO().getAcsPointsFromFile();
		getAcsTableModel().clear();
		loadAcsFile(acsArray);
	}

	private void acsEdit_actionPerformed()
	{
		// save the old attributes
		String oldAcsType = acsTypeCB.getSelectedItem().toString();
		String oldAcsRemote = acsRemoteTF.getText();
		String oldAcsPoint = acsPointTF.getText();
		String oldAcsCategory = acsCategoryTF.getText();
		String oldAcsInterval = acsIntervalCB.getSelectedItem().toString();
		String oldAcsFunction = acsFunctionCB.getSelectedItem().toString();
		String oldAcsMin = acsMinTF.getText();
		String oldAcsMax = acsMaxTF.getText();
		String oldAcsDelta = acsDeltaTF.getText();
		Boolean bool = new Boolean(acsMaxStartCB.isSelected());
		String oldAcsMaxStart = bool.toString();
		oldacspoint = new ACSPoint(oldAcsType, oldAcsRemote, oldAcsPoint, oldAcsCategory, oldAcsInterval, oldAcsFunction, oldAcsMin, oldAcsMax, oldAcsDelta, oldAcsMaxStart);
		acsediting = true;
		enableAcsFields();
		acsAdd.setEnabled(false);
		acsDelete.setVisible(false);
		acsSave.setVisible(true);
		acsCopy.setVisible(true);
	}

	/**
	 * 
	 */
	private void acsAdd_actionPerformed()
	{

		enableAcsFields();
		clearAcsFields();
		acsEdit.setEnabled(false);
		acsDelete.setVisible(false);
		acsSave.setVisible(true);
		acsCopy.setVisible(true);
	}
	

	//	Exit from File mennu action performed
	public void fileExit_actionPerformed(ActionEvent e)
	{
		this.setVisible(false);
		this.dispose();
	}

	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			fileExit_actionPerformed(null);
		}
	}
	/**
	 * @return
	 */
	public JTable getRdexTable()
	{
		if (rdexTable == null)
		{
			rdexTable = new JTable(getRdexTableModel());
		}
		return rdexTable;
	}

	/**
	 * @return
	 */
	public JTable getValmetTable()
	{
		if (valmetTable == null)
		{
			valmetTable = new JTable(getValmetTableModel());
		}
		return valmetTable;
	}

	/**
	 * @return
	 */
	public JTable getAcsTable()
	{
		if (acsTable == null)
		{
			acsTable = new JTable(getAcsTableModel());
		}
		return acsTable;
	}

	public void clearAcsFields()
	{
		acsTypeCB.setSelectedIndex(0);
		acsRemoteTF.setText("");
		acsPointTF.setText("");
		acsCategoryTF.setText("");
		acsIntervalCB.setSelectedIndex(0);
		acsFunctionCB.setSelectedIndex(0);
		acsMinTF.setText("");
		acsMaxTF.setText("");
		acsDeltaTF.setText("");
		acsMaxStartCB.setSelected(false);
	}

	public void disableAcsFields()
	{
		acsTypeCB.setEnabled(false);
		acsRemoteTF.setEnabled(false);
		acsPointTF.setEnabled(false);
		acsCategoryTF.setEnabled(false);
		acsIntervalCB.setEnabled(false);
		acsFunctionCB.setEnabled(false);
		acsMinTF.setEnabled(false);
		acsMaxTF.setEnabled(false);
		acsDeltaTF.setEnabled(false);
		acsMaxStartCB.setEnabled(false);
	}

	public void enableAcsFields()
	{
		acsTypeCB.setEnabled(true);
		acsRemoteTF.setEnabled(true);
		acsPointTF.setEnabled(true);
		acsCategoryTF.setEnabled(true);
		acsIntervalCB.setEnabled(true);
		acsFunctionCB.setEnabled(true);
		acsMinTF.setEnabled(true);
		acsMaxTF.setEnabled(true);
		acsDeltaTF.setEnabled(true);
		acsMaxStartCB.setEnabled(true);
	}

	public void clearRdexFields()
	{
		rdexTypeCB.setSelectedIndex(0);
		rdexNameTF.setText("");
		rdexIntervalCB.setSelectedIndex(0);
		rdexFunctionCB.setSelectedIndex(0);
		rdexMinTF.setText("");
		rdexMaxTF.setText("");
		rdexDeltaTF.setText("");
		rdexMaxStartCB.setSelected(false);
	}

	public void disableRdexFields()
	{
		rdexTypeCB.setEnabled(false);
		rdexNameTF.setEnabled(false);
		rdexIntervalCB.setEnabled(false);
		rdexFunctionCB.setEnabled(false);
		rdexMinTF.setEnabled(false);
		rdexMaxTF.setEnabled(false);
		rdexDeltaTF.setEnabled(false);
		rdexMaxStartCB.setEnabled(false);
	}

	public void enableRdexFields()
	{
		rdexTypeCB.setEnabled(true);
		rdexNameTF.setEnabled(true);
		rdexIntervalCB.setEnabled(true);
		rdexFunctionCB.setEnabled(true);
		rdexMinTF.setEnabled(true);
		rdexMaxTF.setEnabled(true);
		rdexDeltaTF.setEnabled(true);
		rdexMaxStartCB.setEnabled(true);
	}

	public void clearValmetFields()
	{
		valmetTypeCB.setSelectedIndex(0);
		valmetNameTF.setText("");
		valmetIntervalCB.setSelectedIndex(0);
		valmetFunctionCB.setSelectedIndex(0);
		valmetMinTF.setText("");
		valmetMaxTF.setText("");
		valmetDeltaTF.setText("");
		valmetMaxStartCB.setSelected(false);
	}

	public void disableValmetFields()
	{
		valmetTypeCB.setEnabled(false);
		valmetNameTF.setEnabled(false);
		valmetPortTF.setEnabled(false);
		valmetIntervalCB.setEnabled(false);
		valmetFunctionCB.setEnabled(false);
		valmetMinTF.setEnabled(false);
		valmetMaxTF.setEnabled(false);
		valmetDeltaTF.setEnabled(false);
		valmetMaxStartCB.setEnabled(false);
	}

	public void enableValmetFields()
	{
		valmetTypeCB.setEnabled(true);
		valmetNameTF.setEnabled(true);
		valmetPortTF.setEnabled(true);
		valmetIntervalCB.setEnabled(true);
		valmetFunctionCB.setEnabled(true);
		valmetMinTF.setEnabled(true);
		valmetMaxTF.setEnabled(true);
		valmetDeltaTF.setEnabled(true);
		valmetMaxStartCB.setEnabled(true);
	}
}