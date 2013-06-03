package com.cannontech.dbeditor.editor.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.gui.table.JComboCellEditor;
import com.cannontech.common.gui.tree.CTITreeModel;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

public class UserRolePanel extends DataInputPanel implements TreeSelectionListener {
	private static final Logger log = YukonLogManager.getLogger(UserRolePanel.class);

	private JPanel ivjJPanelLoginDescription;
	private JTree ivjJTreeRoles;
	private JScrollPane ivjJScrollJTree;
	private JScrollPane ivjJScrollPaneDescr;
	private JTextPane ivjJTextPaneDescription;
	private JPanel ivjJPanelProperties;
	private JScrollPane ivjJScrollPaneTable;
	private JTable ivjJTableProperties;
	private RolePropertyTableModel propertyModel;
	private YukonGroup yukonGroup;
	private CheckNodeSelectionListener nodeListener;

	public UserRolePanel() {
		super();
		initialize();
	}

	/**
	 * Return the JPanelTrigger property value.
	 */
	private JPanel getJPanelLoginDescription() {
		if (ivjJPanelLoginDescription == null) {
			try {
				TitleBorder ivjLocalBorder = new TitleBorder();
				ivjLocalBorder.setTitleFont(new Font("Arial", 1, 14));
				ivjLocalBorder.setTitle("Description");
				ivjJPanelLoginDescription = new JPanel();
				ivjJPanelLoginDescription.setName("JPanelLoginDescription");
				ivjJPanelLoginDescription.setBorder(ivjLocalBorder);
				ivjJPanelLoginDescription.setLayout(new BorderLayout());
				getJPanelLoginDescription().add(getJScrollPaneDescr(), "Center");
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJPanelLoginDescription;
	}

	/**
	 * Return the JPanelValue property value.
	 */
	private JPanel getJPanelProperties() {
		if (ivjJPanelProperties == null) {
			try {
				TitleBorder ivjLocalBorder1;
				ivjLocalBorder1 = new TitleBorder();
				ivjLocalBorder1.setTitleFont(new Font("dialog", 0, 14));
				ivjLocalBorder1.setTitle("Properties");
				ivjJPanelProperties = new JPanel();
				ivjJPanelProperties.setName("JPanelProperties");
				ivjJPanelProperties.setBorder(ivjLocalBorder1);
				ivjJPanelProperties.setLayout(new BorderLayout());
				getJPanelProperties().add(getJScrollPaneTable(), "Center");
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJPanelProperties;
	}

	/**
	 * Return the JScrollJTree property value.
	 */
	private JScrollPane getJScrollJTree() {
		if (ivjJScrollJTree == null) {
			try {
				ivjJScrollJTree = new JScrollPane();
				ivjJScrollJTree.setName("JScrollJTree");
				getJScrollJTree().setViewportView(getJTreeRoles());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollJTree;
	}

	/**
	 * Return the JScrollPaneDescr property value.
	 */
	private JScrollPane getJScrollPaneDescr() {
		if (ivjJScrollPaneDescr == null) {
			try {
				ivjJScrollPaneDescr = new JScrollPane();
				ivjJScrollPaneDescr.setName("JScrollPaneDescr");
				getJScrollPaneDescr().setViewportView(getJTextPaneDescription());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneDescr;
	}

	/**
	 * Return the JScrollPane1 property value.
	 */
	private JScrollPane getJScrollPaneTable() {
		if (ivjJScrollPaneTable == null) {
			try {
				ivjJScrollPaneTable = new JScrollPane();
				ivjJScrollPaneTable.setName("JScrollPaneTable");
				ivjJScrollPaneTable
						.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjJScrollPaneTable
						.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				getJScrollPaneTable().setViewportView(getJTableProperties());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJScrollPaneTable;
	}

	/**
	 * Return the ScrollPaneTable1 property value.
	 */
	private JTable getJTableProperties() {
		if (ivjJTableProperties == null) {
			try {
				ivjJTableProperties = new JTable();
				ivjJTableProperties.setName("JTableProperties");
				getJScrollPaneTable().setColumnHeaderView(
						ivjJTableProperties.getTableHeader());
				ivjJTableProperties.setAutoResizeMode(0);
				ivjJTableProperties.setPreferredSize(new Dimension(
						115, 168));
				ivjJTableProperties.setBounds(0, 0, 132, 269);

				// do this to force the table to layout completely in the
				// ScrollPane
				// VAJ puts the above setting on automatically
				ivjJTableProperties.setPreferredSize(null);

				ivjJTableProperties.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
				ivjJTableProperties.setOpaque(false);
				ivjJTableProperties.setShowVerticalLines(false);
				ivjJTableProperties.setShowHorizontalLines(false);

				ivjJTableProperties.setIntercellSpacing(new Dimension(0, 0));
				ivjJTableProperties.setRowHeight((int) (ivjJTableProperties.getFont().getSize() * 1.75));

				ivjJTableProperties.setGridColor(getJTableProperties()
						.getTableHeader().getBackground());
				ivjJTableProperties.setBackground(getJTableProperties()
						.getTableHeader().getBackground());
				ivjJTableProperties.createDefaultColumnsFromModel();

				ivjJTableProperties.setModel(getJTablePropertyModel());
				ivjJTableProperties.setDefaultRenderer(Object.class,
						new RolePropertyRenderer());

				TableColumnModel tcm = ivjJTableProperties.getColumnModel();
				TableColumn tc = tcm.getColumn(RolePropertyTableModel.COL_VALUE);

				JComboBox<?> combo = new JComboBox<>();
				combo.setEditable(true);

				// try to center our editor text when editing
				if (combo.getEditor().getEditorComponent() instanceof JTextField) {
					JTextField txtEditor = (JTextField) combo.getEditor()
							.getEditorComponent();
					txtEditor.setHorizontalAlignment(JTextField.CENTER);
				}

				JComboCellEditor jc = new JComboCellEditor(combo);
				tc.setCellEditor(jc);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTableProperties;
	}

	private RolePropertyTableModel getJTablePropertyModel() {
		if (propertyModel == null) {
			propertyModel = new RolePropertyTableModel();
		}
		return propertyModel;
	}

	/**
	 * Return the JTextPaneDescription property value.
	 * 
	 * @return JTextPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTextPane getJTextPaneDescription() {
		if (ivjJTextPaneDescription == null) {
			try {
				ivjJTextPaneDescription = new JTextPane();
				ivjJTextPaneDescription.setName("JTextPaneDescription");
				ivjJTextPaneDescription
						.setDisabledTextColor(Color.black);
				ivjJTextPaneDescription.setBounds(0, 0, 224, 60);
				ivjJTextPaneDescription.setEditable(false);
				ivjJTextPaneDescription.setBackground(this.getBackground());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextPaneDescription;
	}

	/**
	 * This method was created in VisualAge.
	 * 
	 * @return CTITreeMode
	 */
	private CTITreeModel getJTreeModel() {
		return (CTITreeModel) getJTreeRoles().getModel();
	}

	/**
	 * Return the JTree1 property value.
	 * 
	 * @return JTree
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTree getJTreeRoles() {
		if (ivjJTreeRoles == null) {
			try {
				ivjJTreeRoles = new JTree();
				ivjJTreeRoles.setName("JTreeRoles");
				ivjJTreeRoles.setBounds(0, 0, 165, 243);

				DefaultMutableTreeNode root = new DefaultMutableTreeNode("Role Categories");

				ivjJTreeRoles.setModel(new CTITreeModel(root));
				ivjJTreeRoles.setCellRenderer(new CheckRenderer());
				ivjJTreeRoles.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

				IDatabaseCache cache = DefaultDatabaseCache.getInstance();

				synchronized (cache) {
					List<LiteYukonRole> roles = cache.getAllYukonRoles();
					Collections.sort(roles, LiteComparators.liteRoleCategoryComparator);
					String tmpCat = null;
					DefaultMutableTreeNode categoryParent = null;

					for (int i = 0; i < roles.size(); i++) {
						LiteYukonRole role = roles.get(i);

						if (!role.getCategory().equalsIgnoreCase(tmpCat)) {
							tmpCat = role.getCategory();

							if (UserUtils.isReadOnlyCategory(tmpCat)) {
								DBTreeNode d = new DBTreeNode(tmpCat
										+ " [SYSTEM]");
								d.setIsSystemReserved(true);

								categoryParent = d;
							} else
								categoryParent = new DefaultMutableTreeNode(
										tmpCat);

							root.add(categoryParent);
						}

						LiteBaseNode lbNode = new LiteBaseNode(role);

						// This extra clause is necessary for the RADIUS
						// security interface
						if (((LiteYukonRole) lbNode.getUserObject())
								.getRoleName().compareTo(UserUtils.CAT_RADIUS) == 0) {
							ClientSession session = ClientSession.getInstance();
							if (session.getUser().getUserID() == com.cannontech.user.UserUtils.USER_ADMIN_ID) {
								lbNode.setIsSystemReserved(false);
							} else {
								lbNode.setIsSystemReserved(true);
							}
						}
						// set this to tell the GUI if this node is editable or
						// not
						else
							lbNode.setIsSystemReserved(UserUtils
									.isReadOnlyCategory(tmpCat));

						categoryParent.add(lbNode);
					}

				}

				// expand the root
				ivjJTreeRoles.expandPath(new TreePath(root.getPath()));

				ivjJTreeRoles.addMouseListener(getNodeListener());

				ivjJTreeRoles.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						valueChanged(null);
					}
				});

			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTreeRoles;
	}

	/**
	 * Returns the correct Role for the specified object
	 * 
	 * @throws ConfigurationException
	 *             - The role trying to be added already to this role group
	 *             already exists in one of its attached user groups.
	 */
	private void createRoleEntry(LiteYukonRole role_,
			LiteYukonRoleProperty prop_, YukonGroup yukonGroup,
			Hashtable<Integer, Integer> ridMap_) throws ConfigurationException {

		String strVal = getJTablePropertyModel().getPropertyChange(prop_);
		Integer roleEntryID = ridMap_.get(new Integer(prop_
				.getRolePropertyID()));
		YukonGroupRole yukonGroupRole = null;

		yukonGroupRole = new YukonGroupRole(roleEntryID, yukonGroup.getID(),
				role_.getRoleID(), prop_.getRolePropertyID(), " "); // Default
																	// value for
																	// YukonGroupRole
																	// is one
																	// single
																	// space

		// process the roles defined value here
		if (strVal != null && yukonGroupRole != null)
			yukonGroupRole.setValue(strVal);

		// add the role to our role Vector
		if (yukonGroupRole != null) {
			yukonGroup.addYukonGroupRole(yukonGroupRole);
		}
	}

	/**
	 * getValue method comment.
	 */
	@Override
	public Object getValue(Object obj) {
		YukonGroup yukonGroup = (YukonGroup) obj;
		Hashtable<Integer, Integer> roleEntryIDMap = new Hashtable<Integer, Integer>();

		// Create map: RolePropertyID -> GroupRoleID or UserRoleID
		for (int i = 0; i < yukonGroup.getYukonRoles().size(); i++) {
			if (yukonGroup.getYukonRoles().get(i) instanceof YukonGroupRole) {
				YukonGroupRole groupRole = (YukonGroupRole) yukonGroup
						.getYukonRoles().get(i);
				roleEntryIDMap.put(groupRole.getRolePropertyID(),
						groupRole.getGroupRoleID());
			}
		}

		yukonGroup.removeAllYukonRoles();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getJTreeRoles()
				.getModel().getRoot();
		List<?> allRoleNodes = getJTreeModel().getAllLeafNodes(
				new TreePath(root));

		for (int i = 0; i < allRoleNodes.size(); i++) {
			if (allRoleNodes.get(i) instanceof LiteBaseNode
					&& ((LiteBaseNode) allRoleNodes.get(i)).isSelected()) {

				LiteBaseNode rNode = (LiteBaseNode) allRoleNodes.get(i);
				LiteYukonRole role = (LiteYukonRole) rNode.getUserObject();

				try {
					LiteYukonRoleProperty[] props = DaoFactory.getRoleDao()
							.getRoleProperties(role.getRoleID());
					for (LiteYukonRoleProperty prop : props) {
						// modifies o role vector
						createRoleEntry(role, prop, yukonGroup, roleEntryIDMap);
					}
				} catch (ConfigurationException e) {
					log.error("The role trying to be added already to this role group already exists in one of its attached user groups.", e);
				}
			}
		}

		return obj;
	}

	/**
	 * Called whenever the part throws an exception.
	 * 
	 * @param exception
	 *            Throwable
	 */
	private void handleException(Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		com.cannontech.clientutils.CTILogger
				.info("--------- UNCAUGHT EXCEPTION ---------");
		com.cannontech.clientutils.CTILogger.error(exception.getMessage(),
				exception);
		;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("UserRolePanel");
			setToolTipText("");
			setLayout(new GridBagLayout());
			setSize(405, 364);

			GridBagConstraints constraintsJScrollJTree = new GridBagConstraints();
			constraintsJScrollJTree.gridx = 1;
			constraintsJScrollJTree.gridy = 1;
			constraintsJScrollJTree.fill = GridBagConstraints.BOTH;
			constraintsJScrollJTree.anchor = GridBagConstraints.WEST;
			constraintsJScrollJTree.weighty = 1.0;
			constraintsJScrollJTree.ipadx = 180;
			constraintsJScrollJTree.ipady = 209;
			constraintsJScrollJTree.insets = new Insets(12, 4, 5, 2);
			add(getJScrollJTree(), constraintsJScrollJTree);

			GridBagConstraints constraintsJPanelLoginDescription = new GridBagConstraints();
			constraintsJPanelLoginDescription.gridx = 1;
			constraintsJPanelLoginDescription.gridy = 2;
			constraintsJPanelLoginDescription.fill = GridBagConstraints.BOTH;
			constraintsJPanelLoginDescription.anchor = GridBagConstraints.WEST;
			constraintsJPanelLoginDescription.ipadx = 180;
			constraintsJPanelLoginDescription.ipady = 56;
			constraintsJPanelLoginDescription.insets = new Insets(5,
					4, 7, 2);
			add(getJPanelLoginDescription(), constraintsJPanelLoginDescription);

			GridBagConstraints constraintsJPanelProperties = new GridBagConstraints();
			constraintsJPanelProperties.gridx = 2;
			constraintsJPanelProperties.gridy = 1;
			constraintsJPanelProperties.gridheight = 2;
			constraintsJPanelProperties.fill = GridBagConstraints.BOTH;
			constraintsJPanelProperties.anchor = GridBagConstraints.WEST;
			constraintsJPanelProperties.weightx = 1.0;
			constraintsJPanelProperties.weighty = 1.0;
			constraintsJPanelProperties.ipadx = 170;
			constraintsJPanelProperties.ipady = 345;
			constraintsJPanelProperties.insets = new Insets(12, 3, 7,
					4);
			add(getJPanelProperties(), constraintsJPanelProperties);
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnections();
		updateSelectionCountNodes();
	}

	private String getRolePropertyValue(int rolePropID_, String defValue_) {
		if (getRoleContainer() instanceof YukonGroup) {
			return DaoFactory.getRoleDao().getRolePropValueGroup(
					getRoleContainer().getID(), rolePropID_, defValue_);
		} else if (getRoleContainer() == null) {
			return defValue_;
		} else {
			throw new IllegalArgumentException("Unrecognized role container: "
					+ (getRoleContainer() == null ? "(null)"
							: getRoleContainer().getClass().getName()));
		}
	}

	private void initConnections() {

		MouseListener tableMl = new MouseAdapter() {
			public void mousePressed(final MouseEvent e) {
				int selRow = getJTableProperties().rowAtPoint(e.getPoint());
				int selCol = getJTableProperties().columnAtPoint(e.getPoint());
				if (selRow != -1) {
					if (getJTablePropertyModel().isCellEditable(selRow, selCol)) {
						fireInputUpdate();
					}

					StringBuffer sBuff = new StringBuffer(
							getJTextPaneDescription().getText());
					int indx = getJTextPaneDescription().getText().indexOf(
							System.getProperty("line.separator"));

					sBuff.replace((indx >= 0 ? indx : sBuff.length()),
							sBuff.length(),
							System.getProperty("line.separator")
									+ getJTablePropertyModel().getRowAt(selRow)
											.getLiteProperty().getKeyName()
									+ " : "
									+ getJTablePropertyModel().getRowAt(selRow)
											.getLiteProperty().getDescription());

					getJTextPaneDescription().setText(sBuff.toString());
				}
			}
		};
		getJTableProperties().addMouseListener(tableMl);

		// add the TreeSelectionListener for the JTree
		getJTreeRoles().addTreeSelectionListener(this);

	}

	public void valueChanged(TreeSelectionEvent e) {
		if (getJTableProperties().isEditing()) {
			getJTableProperties().getCellEditor().stopCellEditing();
		}

		int selRow = getJTreeRoles().getMaxSelectionRow();
		if (selRow != -1) {
			TreeNode node = (TreeNode) getJTreeRoles().getPathForRow(selRow)
					.getLastPathComponent();

			if (node instanceof LiteBaseNode) {
				LiteBaseNode liteBaseNode = (LiteBaseNode) node;
				LiteYukonRole ly = (LiteYukonRole) liteBaseNode.getUserObject();

				getJTextPaneDescription().setText(ly.getDescription());

				getJTablePropertyModel().clear();
				LiteYukonRoleProperty[] props = DaoFactory.getRoleDao()
						.getRoleProperties(ly.getRoleID());

				// sort by keys
				Arrays.sort(props, LiteComparators.liteStringComparator);
				for (LiteYukonRoleProperty yukonRolePropery : props) {
					String rolePropertyValue = getRolePropertyValue(
							yukonRolePropery.getRolePropertyID(),
							yukonRolePropery.getDefaultValue());
					getJTablePropertyModel().addRolePropertyRow(
							yukonRolePropery, rolePropertyValue);
				}

				// if we are read only, dont do any enabling/disabling
				if (isReadOnlyTree()) {
					// do nothing
				} else if (!((CheckNode) liteBaseNode).isSelected()) {
					// always disable the property if the role is NOT selected
					getJTableProperties().setEnabled(false);

				} else {
					// if the ROLE_CATEGORY is SystemReserved, dont allow
					// editing
					getJTableProperties().setEnabled(
							!(liteBaseNode.isSystemReserved()));
				}
			} else {
				getJTablePropertyModel().clear();
				getJTextPaneDescription().setText(""); // clear out any text
			}

			// this must fire here because the NodeCheckBox only fires these
			// events
			if (node instanceof CheckNode && !isReadOnlyTree()) {
				getJTableProperties().setEnabled(
						((CheckNode) node).isSelected());
			}

			fireInputUpdate();
		}
	}

	public void setFirstFocus() {
		// Make sure that when its time to display this panel, the focus starts
		// in the top component
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getJTreeRoles().requestFocus();
			}
		});
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 */
	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame();
			UserLoginBasePanel aUserLoginBasePanel;
			aUserLoginBasePanel = new UserLoginBasePanel();
			frame.setContentPane(aUserLoginBasePanel);
			frame.setSize(aUserLoginBasePanel.getSize());
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right,
					frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
			exception.printStackTrace(System.out);
		}
	}

	private CheckNodeSelectionListener getNodeListener() {
		if (nodeListener == null) {
			nodeListener = new CheckNodeSelectionListener(getJTreeRoles());
		}
		return nodeListener;
	}

	private boolean isReadOnlyTree() {
		MouseListener[] lists = getJTreeRoles().getMouseListeners();
		for (int i = 0; i < lists.length; i++) {
			if (lists[i] == getNodeListener()) {
				return false;
			}
		}

		return true;
	}

	public void setRoleTabledEnabled(boolean val_) {
		getJTableProperties().setEnabled(val_);

		if (val_) {
			getJTreeRoles().addMouseListener(getNodeListener());
		} else {
			getJTreeRoles().removeMouseListener(getNodeListener());
		}

	}

	/**
	 * Returns true if the input for this panel is valid. Currently only checks
	 * the WebClientRole.DEFAULT_TIMEZONE role property for valid timezones.
	 * 
	 * @return boolean
	 */
	public boolean isInputValid() {
		for (int i = 0; i < getJTablePropertyModel().getRowCount(); i++) {
			RolePropertyRow propertyRow = getJTablePropertyModel().getRowAt(i);
			if (propertyRow.getLiteProperty().getRolePropertyID() == WebClientRole.DEFAULT_TIMEZONE) {
				if (!isDefaultTimeZoneValid(propertyRow)) {
					return false;
				}
			}

			if (!isRoleSelectionValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks to see if the roles selected are valid for the role group as well
	 * as the user groups attached to that role group
	 */
	private boolean isRoleSelectionValid() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getJTreeRoles()
				.getModel().getRoot();
		List<LiteBaseNode> allRoleNodes = getJTreeModel().getAllLeafNodes(new TreePath(root));
		YukonGroup yukonGroup = getRoleContainer();

		for (LiteBaseNode rNode : allRoleNodes) {
			if (rNode.isSelected()) {
				LiteYukonRole role = (LiteYukonRole) rNode.getUserObject();

				YukonRole newRole = YukonRole.getForId(role.getRoleID());
				if (yukonGroup != null
						&& !yukonGroup.isYukonGroupRoleAddable(
								yukonGroup.getGroupID(), newRole)) {

					YukonGroupDao yukonGroupDao = YukonSpringHook.getBean(
							"yukonGroupDao", YukonGroupDao.class);
					LiteYukonGroup liteYukonGroup = yukonGroupDao
							.getLiteYukonGroup(yukonGroup.getGroupID());
					setErrorString("The "
							+ newRole
							+ " role already exists on one of the user groups that use the group "
							+ liteYukonGroup.getGroupName());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks to see if the default time zone supplied is valid.
	 */
	private boolean isDefaultTimeZoneValid(RolePropertyRow propertyRow) {
		String value = propertyRow.getValue();
		if (StringUtils.isBlank(value)) {
			return true;
		}

		try {
			CtiUtilities.getValidTimeZone(value);
			return true;
		} catch (BadConfigurationException e) {
			setErrorString("Invalid value in WebClientRole Default TimeZone property: "
					+ value
					+ " \nTimezones should be in the form \"America/Chicago\".");
			return false;
		}
	}

	/**
	 * setValue method comment.
	 */
	public void setValue(Object o) {
		if (o == null)
			return;

		YukonGroup yukonGroup = (YukonGroup) o;
		// be sure the rest of the panel knows what user we are dealing with
		setRoleContainer(yukonGroup);

		/* *******
		 * RoleProperty has the DefaultValue for YukonGroupRole. YukonGroupRole
		 * have the exact same row count as RoleProperty if the user has the
		 * given YukonRole.
		 * ****
		 */
		for (YukonGroupRole yukonGroupRole : yukonGroup.getYukonRoles()) {

			// set the selected node
			DefaultMutableTreeNode tnode = getJTreeModel().findNode(
					new TreePath(getJTreeModel().getRoot()),
					new LiteYukonRole(yukonGroupRole.getRoleID().intValue()));

			if (tnode != null)
				((CheckNode) tnode).setSelected(true);

			LiteYukonRoleProperty[] props = DaoFactory.getRoleDao()
					.getRoleProperties(yukonGroupRole.getRoleID().intValue());

			// (none) or empty string value means we use the default, don't do
			// anything in that case
			if (StringUtils.isNotBlank(yukonGroupRole.getValue())
					&& !(yukonGroupRole.getValue().trim()
							.equals(CtiUtilities.STRING_NONE))) {
				for (int j = 0; j < props.length; j++) {
					if (props[j].getRoleID() == yukonGroupRole.getRoleID()
							.intValue()
							&& props[j].getRolePropertyID() == yukonGroupRole
									.getRolePropertyID().intValue()) {
						getJTablePropertyModel().addPropertyValue(props[j],
								yukonGroupRole.getValue());
					}

				}
			}

		}
		if (o instanceof YukonUser) {
			if (((YukonUser) o).getUserID().intValue() == UserUtils.USER_ADMIN_ID)
				setRoleTabledEnabled(false);
		}

		getJTreeModel().reload();

		updateSelectionCountNodes();
	}

	private void updateSelectionCountNodes() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getJTreeModel().getRoot();

		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode currParent = (DefaultMutableTreeNode) root.getChildAt(i);

			int selected = 0;
			for (int j = 0; j < currParent.getChildCount(); j++) {
				if (currParent.getChildAt(j) instanceof CheckNode) {
					if (((CheckNode) currParent.getChildAt(j)).isSelected()) {
						selected++;
					}
				}
			}

			int endIndx = currParent.getUserObject().toString().indexOf("\t");

			currParent.setUserObject(currParent
					.getUserObject()
					.toString()
					.substring(0, (endIndx >= 0 ? endIndx : currParent.getUserObject().toString().length())) + "\t   (" + selected + " Selected)");

			// let the tree repaint itself
			getJTreeModel().nodeChanged(currParent);
			getJTreeRoles().invalidate();
			getJTreeRoles().repaint();
		}
	}

	private YukonGroup getRoleContainer() {
		return yukonGroup;
	}

	private void setRoleContainer(YukonGroup yukonGroup) {
		this.yukonGroup = yukonGroup;
	}
}