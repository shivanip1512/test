package com.cannontech.graph;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.TreeModel;

import org.jfree.chart.JFreeChart;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.DateComboBox;
import com.cannontech.common.gui.util.JEditorPanePrintable;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.database.model.AbstractDeviceTreeModel;
import com.cannontech.database.model.GraphDefinitionTreeModel;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.exportdata.SaveAsJFileChooser;
import com.cannontech.graph.menu.FileMenu;
import com.cannontech.graph.menu.HelpMenu;
import com.cannontech.graph.menu.OptionsMenu;
import com.cannontech.graph.menu.TrendMenu;
import com.cannontech.graph.menu.ViewMenu;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.jfreechart.chart.YukonChartPanel;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class GraphClient extends JPanel implements DBChangeListener, GraphDefines, ActionListener, WindowListener,
        ChangeListener, TreeSelectionListener {

    public static final URL GRAPH_IMG_16 = GraphClient.class.getResource("/GraphTrending16.png");
    public static final URL GRAPH_IMG_24 = GraphClient.class.getResource("/GraphTrending24.png");
    public static final URL GRAPH_IMG_32 = GraphClient.class.getResource("/GraphTrending32.png");
    public static final URL GRAPH_IMG_48 = GraphClient.class.getResource("/GraphTrending48.png");
    public static final URL GRAPH_IMG_64 = GraphClient.class.getResource("/GraphTrending64.png");

    public static List<Image> getIconsImages() {

        List<Image> iconsImages = new ArrayList<>();
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(GRAPH_IMG_16));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(GRAPH_IMG_24));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(GRAPH_IMG_32));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(GRAPH_IMG_48));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(GRAPH_IMG_64));

        return iconsImages;
    }

    private class TrendDataAutoUpdater extends Thread {
        public boolean ignoreAutoUpdate = false;

        @Override
        public void run() {
            while (true) {
                try {
                    // interval rate is in seconds (* 1000 for millis)
                    Thread.sleep(GraphClient.this.getGraph().getMinIntervalRate() * 1000);
                } catch (InterruptedException ie) {
                    return;
                }

                {
                    synchronized (GraphClient.class) {
                        if (ignoreAutoUpdate) {
                            CTILogger.info("** ignoreAutoUpdate **\n");
                            ignoreAutoUpdate = false;
                            continue;
                        }
                    }

                    if (GraphClient.this.getCurrentRadioButton().isSelected()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JFrame frame = GraphClient.this.getGraphParentFrame();
                                Cursor savedCursor = GraphClient.this.getCursor();

                                if (getTrendModel() != null) {
                                    frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                    // Set to currentDate - always want this
                                    // date to be TODAY!
                                    GraphClient.this.getStartDateComboBox().setSelectedDate(
                                        com.cannontech.util.ServletUtil.getToday());
                                    GraphClient.this.setStartDate(getStartDateComboBox().getSelectedDate());
                                    GraphClient.this.getGraph().setUpdateTrend(true);
                                    updateCurrentPane();
                                    long timer = (System.currentTimeMillis());
                                    frame.setTitle("Yukon Trending - ( Updated "
                                        + extendedDateTimeformat.format(new Date(timer)) + " )");
                                    CTILogger.info("[" + extendedDateTimeformat.format(new Date(timer))
                                        + "] - Yukon Trending - Auto Updating Point Data");
                                    frame.setCursor(savedCursor);

                                }
                            }
                        });
                    }
                }
            }
        }
    }

    // This is a somewhat temporary field user
    private int savedViewType = GraphRenderers.LINE;
    private static Graph graphClass = null;
    private static JFrame graphClientFrame = null;
    private static final int NO_WEEK = 0; // one week or less
    private static final int FIRST_WEEK = 1;// exactly the first week
    private static final int FOURTH_WEEK = 4;
    private static final int FIFTH_WEEK = 5; // exactly the last week
    // Components for controlling the view toggle (historical or current data)
    private int currIndex = 0; // Current timeperiodComboBox index
    private int histIndex = 0; // Historical timeperiodComboBox index
    private Object histDate = null;
    private int histWeek = 0;
    private int currentWeek = NO_WEEK; // used when more than one week is
                                       // selected.
    private Date[] sliderValuesArray = null;
    private ButtonGroup dataViewButtonGroup;
    private boolean timeToUpdateSlider = true; // true, update button was
                                               // pushed, initial is ture also
    private CreateGraphPanel createPanel = null;
    // Menu Bar and Items
    private JMenuBar menuBar = null;
    private FileMenu fileMenu = null;
    private TrendMenu trendMenu = null;
    private ViewMenu viewMenu = null;
    private OptionsMenu optionsMenu = null;
    private HelpMenu helpMenu = null;
    private TreeViewPanel ivjTreeViewPanel = null;
    private DateComboBox ivjStartDateComboBox = null;
    private JSpinner eventSpinner = null;
    private JLabel eventLabel = null;
    private YukonChartPanel freeChartPanel = null;
    private TrendDataAutoUpdater trendDataAutoUpdater;
    private JRadioButton ivjCurrentRadioButton = null;
    private JPanel ivjGraphTabPanel = null;
    private JRadioButton ivjHistoricalRadioButton = null;
    private JButton ivjRefreshButton = null;
    private JLabel ivjStartDateLabel = null;
    private JComboBox<String> ivjTimePeriodComboBox = null;
    private JLabel ivjTimePeriodLabel = null;
    private JEditorPanePrintable ivjTabularEditorPane = null;
    private JSlider ivjTabularSlider = null;
    private JPanel ivjTabularTabPanel = null;
    private JPanel ivjSliderPanel = null;
    private JScrollPane ivjTabularTabScrollPane = null;
    private JSplitPane ivjLeftRightSplitPane = null;
    private JTabbedPane ivjTabbedPane = null;
    private JPanel ivjTrendDisplayPanel = null;
    private JPanel ivjTrendSetupPanel = null;
    private JEditorPanePrintable ivjSummaryEditorPane = null;
    private JScrollPane ivjSummaryTabScrollPane = null;
    private AdvancedOptionsPanel advOptsPanel = null;

    /**
     * This method needs to be implemented for the abstract class
     * JCValueListener. JCValueListener is the DatePopupComboBox's listener.
     * This particular method is not needed for catching value changes.
     * ValueChanged(JCValueEvent) is used for that. Insert the method's
     * description here. SN Creation date: (7/19/2001 12:45:10 PM)
     * 
     * @param event com.klg.jclass.util.value.JCValueEvent
     */
    public GraphClient() {
        super();
        initialize();
    }

    public GraphClient(JFrame rootFrame) {
        super();
        setGraphClientFrame(rootFrame);
        initialize();
        initializeSwingComponents();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == getRefreshButton() || event.getSource() == getViewMenu().getRefreshMenuItem()) {
            // Don't force a refresh unless some option or other field has been
            // updated.
            // The updateTrend flag should be set at the other field's level
            CTILogger.info(" don't change model");
            refresh();
        } else if (event.getSource() == getStartDateComboBox()) {
            // Need to make sure the date has changed otherwise we are doing a
            // billion updates on the one stateChange.
            if (getStartDate().compareTo(getStartDateComboBox().getSelectedDate()) != 0) {
                setStartDate(getStartDateComboBox().getSelectedDate());
                if (currentWeek != NO_WEEK) {
                    currentWeek = FIRST_WEEK;
                }
                refresh();
            }
        } else if (event.getSource() == getViewMenu().getDefaultRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.DEFAULT);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getLineRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.LINE);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getLineAreaRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.LINE_AREA);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getLineShapesRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.LINE_SHAPES);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getLineAreaShapesRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.LINE_AREA_SHAPES);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getStepRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.STEP);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getStepAreaRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.STEP_AREA);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getStepShapesRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.STEP_SHAPES);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getStepAreaShapesRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.STEP_AREA_SHAPES);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getStepRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.STEP);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getBarRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.BAR);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
            getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getViewMenu().getBar3DRadioButtonItem()) {
            getGraph().setViewType(GraphRenderers.BAR_3D);
            savedViewType = getGraph().getViewType();
            refresh();
            getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
            getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
            getFileMenu().getExportMenuitem().setEnabled(true);
        } else if (event.getSource() == getOptionsMenu().getResetPeaksAllPointsMenuItem()) {
            ResetPeaksPanel peaksPanel = new ResetPeaksPanel();
            boolean hasChanged = peaksPanel.showResetDialog(getGraphParentFrame());
            getGraph().setUpdateTrend(hasChanged);
            // The rest of this if statement are done to ensure reloading of the
            // graphDataSeries since the moreData column may have changed.
            getGraph().setUpdateTrend(hasChanged);
            if (hasChanged) {
                Object item = getTreeViewPanel().getSelectedItem();
                if (item == null || !(item instanceof LiteBase)) {
                    return;
                }

                // Item is an instance of LiteBase...(from previous statement)
                getGraph().setGraphDefinition(((LiteBase) item).getLiteID());
                refresh();
            }
        } else if (event.getSource() == getOptionsMenu().getResetPeakSelectedTrendMenuItem()) {
            /*
             * TODO Need to update the selected GDEF correctly to cause a
             * reload.
             */
            Object selected = getTreeViewPanel().getSelectedItem();

            if (selected == null) {
                showPopupMessage("Please select a Trend to Edit from the List", JOptionPane.WARNING_MESSAGE);
            } else {
                if (selected instanceof LiteGraphDefinition) {
                    ResetPeaksPanel peaksPanel =
                        new ResetPeaksPanel(GraphDataSeries.getAllGraphDataSeries(new Integer(
                            ((LiteGraphDefinition) selected).getGraphDefinitionID())));
                    boolean hasChanged = peaksPanel.showResetDialog(getGraphParentFrame());
                    getGraph().setUpdateTrend(hasChanged);
                    // The rest of this if statement are done to ensure
                    // reloading of the graphDataSeries since the moreData
                    // column may have changed.
                    getGraph().setUpdateTrend(hasChanged);
                    if (hasChanged) {
                        Object item = getTreeViewPanel().getSelectedItem();
                        if (item == null || !(item instanceof LiteBase)) {
                            return;
                        }

                        // Item is an instance of LiteBase...(from previous
                        // statement)
                        getGraph().setGraphDefinition(((LiteBase) item).getLiteID());
                        refresh();
                    }
                }
            }
        } else if (event.getSource() == getOptionsMenu().getLoadDurationMenuItem()) {
            boolean isMasked = getOptionsMenu().getLoadDurationMenuItem().isSelected();
            updateTimePeriodComboBox();
            getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LOAD_DURATION_MASK, isMasked);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getNoneResMenuItem()) {
            getTrendProperties().setResolutionInMillis(1L);
        } else if (event.getSource() == getOptionsMenu().getSecondResMenuItem()) {
            getTrendProperties().setResolutionInMillis(1000L);
        } else if (event.getSource() == getOptionsMenu().getMinuteResMenuItem()) {
            getTrendProperties().setResolutionInMillis(1000L * 60L);
        } else if (event.getSource() == getOptionsMenu().getPlotYesterdayMenuItem()) {
            CTILogger.info("yesterday change");
            getOptionsMenu().getPlotYesterdayMenuItem().isSelected();
            getGraph().setUpdateTrend(true);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getMultiplierMenuItem()) {
            boolean isMasked = getOptionsMenu().getMultiplierMenuItem().isSelected();
            getTrendProperties().updateOptionsMaskSettings(GraphRenderers.GRAPH_MULTIPLIER_MASK, isMasked);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getPlotMinMaxValuesMenuItem()) {
            boolean isMasked = getOptionsMenu().getPlotMinMaxValuesMenuItem().isSelected();
            getTrendProperties().updateOptionsMaskSettings(GraphRenderers.PLOT_MIN_MAX_MASK, isMasked);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getShowLoadFactorMenuItem()) {
            boolean isMasked = getOptionsMenu().getShowLoadFactorMenuItem().isSelected();
            getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LEGEND_LOAD_FACTOR_MASK, isMasked);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getShowMinMaxMenuItem()) {
            boolean isMasked = getOptionsMenu().getShowMinMaxMenuItem().isSelected();
            getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LEGEND_MIN_MAX_MASK, isMasked);
            refresh();
        } else if (event.getSource() == getOptionsMenu().getAdvancedOptionsMenuItem()) {
            TrendProperties props = getAdvOptsPanel().showAdvancedOptions(getGraphParentFrame());
            if (props != null) {
                refresh();
            }
            advOptsPanel = null;
        }

        else if (event.getSource() == getTimePeriodComboBox()) {
            updateTimePeriod();
            refresh();
        } else if (event.getSource() == getTrendMenu().getCreateMenuItem()) {
            create();
        } else if (event.getSource() == getTrendMenu().getEditMenuItem()) {
            edit();
        } else if (event.getSource() == getCurrentRadioButton() || event.getSource() == getHistoricalRadioButton()) {
            toggleTimePeriod();
        } else if (event.getSource() == getFileMenu().getExportMenuitem()) {
            export();
        } else if (event.getSource() == getFileMenu().getPrintMenuItem()) {
            print();
        } else if (event.getSource() == getTrendMenu().getDeleteMenuItem()) {
            delete();
        } else if (event.getSource() == getHelpMenu().getHelpTopicsMenuItem()) {
            com.cannontech.common.util.CtiUtilities.showHelp(HELP_FILE);
        } else if (event.getSource() == getHelpMenu().getAboutMenuItem()) {
            about();
        } else if (event.getSource() == getFileMenu().getExitMenuItem()) {
            exit();
        } else if (event.getSource() == getTrendMenu().getGetDataNowAllMetersMenuItem()) {
            getGraph().getDataNow(null);
        } else if (event.getSource() == getTrendMenu().getGetDataNowSelectedTrendMenuItem()) {
            List<LiteYukonPAObject> trendPaobjects =
                YukonSpringHook.getBean(GraphDao.class).getLiteYukonPaobjects(
                    getGraph().getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue());
            getGraph().getDataNow(trendPaobjects);
        } else {
            CTILogger.info(" other action");
        }
    }

    /**
     * Method to add or remove the Event time period from the combo box based on
     * the Load Duration selection
     */
    public void updateTimePeriodComboBox() {

        getTimePeriodComboBox().removeItem(ServletUtil.EVENT);

        if (!getOptionsMenu().getLoadDurationMenuItem().isSelected()) {
            getTimePeriodComboBox().addItem(ServletUtil.EVENT);

            if (ServletUtil.EVENT.equals(getGraph().getPeriod())) {
                getTimePeriodComboBox().setSelectedItem(ServletUtil.EVENT);
            }
        }

    }

    /**
     * Display the dialog with about information.
     */
    public void about() {
        AboutDialog aboutDialog = new AboutDialog(getGraphParentFrame(), "About Trending", true);

        aboutDialog.setLocationRelativeTo(getGraphParentFrame());
        aboutDialog.setValue(null);
        aboutDialog.show();
    }

    /**
     * Display a Create Graph Panel. Set gDef based on the value returned from
     * the create panel.
     */
    public void create() {
        createPanel = new CreateGraphPanel();
        GraphDefinition gDef = createPanel.showCreateGraphPanelDialog(getGraphParentFrame());

        if (gDef != null) {
            getGraph().create(gDef);
            getTreeViewPanel().refresh();
            getTreeViewPanel().selectObject(gDef);
        }
        createPanel = null;
    }

    /**
     * Delete the gDef selected in the tree panel.
     */
    public void delete() {
        Object selected = getTreeViewPanel().getSelectedItem();

        if (selected != null && selected instanceof LiteBase) {
            int option =
                JOptionPane.showConfirmDialog(getGraphParentFrame(), "Are you sure you want to permanently delete '"
                    + ((LiteGraphDefinition) selected).getName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                DBPersistent dbPersistent = LiteFactory.createDBPersistent((LiteBase) selected);
                getGraph().delete(dbPersistent);
                getTreeViewPanel().refresh();
            }
        }
    }

    /**
     * Displays the Create Graph Panel for the gDef selected in the
     * treeViewPanel. Sets gDef based on the returned updated value.
     */
    public void edit() {
        Cursor savedCursor = null;
        createPanel = new CreateGraphPanel();
        try {
            Object selected = getTreeViewPanel().getSelectedItem();

            if (selected == null) {
                showPopupMessage("Please select a Trend to Edit from the List", JOptionPane.WARNING_MESSAGE);
            }

            // Set cursor to show waiting for update.
            savedCursor = this.getCursor();
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (selected instanceof LiteBase) {
                GraphDefinition gDef = (GraphDefinition) LiteFactory.createDBPersistent((LiteBase) selected);
                Transaction<GraphDefinition> t = Transaction.createTransaction(Transaction.RETRIEVE, gDef);
                gDef = t.execute();

                createPanel.setValue(gDef);
                gDef = createPanel.showCreateGraphPanelDialog(getGraphParentFrame());

                if (gDef != null) // 'OK' out of dialog to continue on.
                {
                    getGraph().update(gDef);
                    getTreeViewPanel().refresh();
                    getTreeViewPanel().selectObject(gDef); // inits a
                                                           // valueChanged
                                                           // event.
                    updateCurrentPane();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.setCursor(savedCursor);
            createPanel = null;
        }

    }

    /**
     * Displays SaveAsJFileChooser based on the currently selected tab from
     * trendProperties.
     */
    public void export() {
        switch (getTrendProperties().getViewType()) {
        case GraphRenderers.TABULAR:
            new SaveAsJFileChooser(CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(),
                getGraph().getHtmlString(), getTrendModel().getChartName().toString(), getTrendModel());
            break;

        case GraphRenderers.SUMMARY:
            new SaveAsJFileChooser(CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(),
                getGraph().getHtmlString(), getTrendModel().getChartName().toString());
            break;

        default:
            new SaveAsJFileChooser(CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(), getFreeChart(),
                getTrendModel().getChartName().toString(), getTrendModel());
            break;

        }
    }

    /**
     * Update the selected gDef from treeViewPanel. Calls updateCurrentPane() to
     * actually update the display.
     */
    public void refresh() {
        Cursor savedCursor = null;

        try {
            // Set cursor to show waiting for update.
            savedCursor = this.getCursor();
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // Check that a trend from the tree has been selected.
            if (getTreeViewPanel().getSelectedItem() == null) {
                // Only tell the user to select a trend if there are trends to
                // select
                if (getTrendCount() > 0) {
                    showPopupMessage("Please select a Trend from the list", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            timeToUpdateSlider = true; // flags that update button was selected
            updateCurrentPane();

        } catch (Exception e) {
            CTILogger.info("-> Null pointer Exception - GraphClient.ActionPerformed on UpdateButton");
            e.printStackTrace();
        } finally {
            getGraphParentFrame().setTitle(
                "Yukon Trending - ( Updated " + extendedDateTimeformat.format(new Date(System.currentTimeMillis()))
                    + " )");
            this.setCursor(savedCursor);
        }
    }

    /**
     * Updates currentWeek and period values based on TimePeriod selected.
     */
    public void updateTimePeriod() {
        Object selected = getTimePeriodComboBox().getSelectedItem();
        if (selected != null) {
            String period = selected.toString();
            getGraph().setPeriod(period);
            if (getCurrentRadioButton().isSelected() || period.equalsIgnoreCase(ServletUtil.ONEDAY)
                || period.equalsIgnoreCase(ServletUtil.THREEDAYS) || period.equalsIgnoreCase(ServletUtil.ONEWEEK)) {
                currentWeek = NO_WEEK;
            } else {
                currentWeek = FIRST_WEEK;
            }

            if (!period.equalsIgnoreCase(ServletUtil.TODAY) && !period.equalsIgnoreCase(ServletUtil.ONEDAY)) {
                getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
                getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
            } else {
                getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
            }

            // Enable / Disable the load duration and event controls
            if (ServletUtil.EVENT.equals(period)) {
                if (getCurrentRadioButton().isSelected()) {
                    // Set date to tomorrow for Event so all of today's events
                    // are
                    // displayed
                    getStartDateComboBox().setSelectedDate(ServletUtil.getTomorrow());
                }
                getOptionsMenu().getLoadDurationMenuItem().setEnabled(false);
                getEventJSpinner().setEnabled(true);
                getEventLabel().setEnabled(true);
            } else {
                if (getCurrentRadioButton().isSelected()) {
                    getStartDateComboBox().setSelectedDate(ServletUtil.getToday());
                }
                getOptionsMenu().getLoadDurationMenuItem().setEnabled(true);
                getEventJSpinner().setEnabled(false);
                getEventLabel().setEnabled(false);
            }

            getGraph().setUpdateTrend(true);
        }
    }

    /**
     * Updates the editable items and start date display when period selection
     * changes between current and historical.
     */
    public void toggleTimePeriod() {
        // ADD CODE FOR WHEN CURRENT/HISTORICAL IS SELECTED 2 TIMES IN A ROW!!!
        // Put action events from getTimePeriodComboBox on hold until the method
        // ends
        getTimePeriodComboBox().removeActionListener(this);
        getStartDateComboBox().removeActionListener(this);

        if (getCurrentRadioButton().isSelected()) {
            histDate = getStartDateComboBox().getSelectedDate();
            histIndex = getTimePeriodComboBox().getSelectedIndex(); // save
                                                                    // current
                                                                    // period
            histWeek = currentWeek;

            getStartDateLabel().setEnabled(false);
            getStartDateComboBox().setEnabled(false);
            getTimePeriodComboBox().removeAllItems();

            for (int i = 0; i < ServletUtil.currentPeriods.length; i++) {
                getTimePeriodComboBox().addItem(ServletUtil.currentPeriods[i]);
            }

            Date date = ServletUtil.getToday();
            if (!((getTrendProperties().getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK)) {
                getTimePeriodComboBox().setSelectedIndex(currIndex); // set to
                                                                     // saved
                                                                     // currentPeriod
            } else {
                getTimePeriodComboBox().setSelectedItem(ServletUtil.EVENT);
                date = ServletUtil.getTomorrow();
            }
            getStartDateComboBox().setSelectedDate(date); // set to currentDate
            setStartDate(date);
            currentWeek = NO_WEEK;
        } else if (getHistoricalRadioButton().isSelected()) {
            currIndex = getTimePeriodComboBox().getSelectedIndex(); // save
                                                                    // current
                                                                    // period
            currentWeek = histWeek;
            getStartDateLabel().setEnabled(true);
            getStartDateComboBox().setEnabled(true);
            getStartDateComboBox().setEditable(true);
            getTimePeriodComboBox().removeAllItems();

            // -- Fill combo box with historical time periods
            for (int i = 0; i < ServletUtil.historicalPeriods.length; i++) {
                getTimePeriodComboBox().addItem(ServletUtil.historicalPeriods[i]);
            }

            if (!((getTrendProperties().getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK)) {
                getTimePeriodComboBox().setSelectedIndex(histIndex); // set to
                                                                     // saved
                                                                     // histPeriod
                if (histDate != null) {
                    getStartDateComboBox().setSelectedDate(
                        ServletUtil.parseDateStringLiberally((dateFormat.format(histDate)).toString())); // set
                                                                                                         // to
                                                                                                         // saved
                                                                                                         // histDate
                    setStartDate(getStartDateComboBox().getSelectedDate());
                } else {
                    CTILogger.debug("Historical date it null, what should we do???");
                }
            } else {
                getTimePeriodComboBox().setSelectedItem(ServletUtil.EVENT);
            }
        }

        // -- Put the action listener back on the timePeriodComboBox
        getTimePeriodComboBox().addActionListener(this);
        getStartDateComboBox().addActionListener(this);
        updateTimePeriod();
    }

    /**
     * Display a printer job dialog, prints the currently viewed tab.
     */
    public void print() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            PageFormat pf = new PageFormat();

            try {
                Paper paper = new Paper();
                if (getTabbedPane().getSelectedComponent() == getGraphTabPanel()) {
                    pf.setOrientation(PageFormat.LANDSCAPE);
                    paper.setImageableArea(30, 40, 552, 712);
                    pf.setPaper(paper);
                    pj.setPrintable(getFreeChartPanel(), pf);
                } else if (getTabbedPane().getSelectedComponent() == getTabularTabScrollPane()) {
                    pf.setOrientation(PageFormat.PORTRAIT);
                    paper.setImageableArea(72, 36, 468, 720);
                    pf.setPaper(paper);
                    pj.setPrintable(getTabularEditorPane(), pf);
                } else if (getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane()) {
                    pf.setOrientation(PageFormat.PORTRAIT);
                    paper.setImageableArea(40, 40, 542, 712);
                    pf.setPaper(paper);
                    pj.setPrintable(getSummaryEditorPane(), pf);
                }
                pj.print();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace(System.out);
            } catch (java.awt.print.PrinterException ex) {
                ex.printStackTrace();
            }
        }
        // FIX to keep the GraphClient frame on top after calling the
        // printDialog.
        // JDK1.4 should have fixed the issue but I(SN) have still seen
        // inconsistencies with focus.
        getGraphParentFrame().toFront();// keeps the main frame in front focus
    }

    /**
     * Add action listeners to each JMenuItem in menu.
     * 
     * @param menu JMenu
     */
    public void addMenuItemActionListeners(JMenu menu) {
        JMenuItem item;

        for (int i = 0; i < menu.getItemCount(); i++) {
            item = menu.getItem(i);

            if (item != null) {
                menu.getItem(i).addActionListener(this);
                if (item instanceof JMenu) {
                    for (int j = 0; j < ((JMenu) item).getItemCount(); j++) {
                        if (((JMenu) item).getItem(j) != null) {
                            ((JMenu) item).getItem(j).addActionListener(this);
                        }
                    }
                }
            }

        }
    }

    /**
     * Returns HTML string buffer for summary, usage or tabular displays.
     * 
     * @param htmlBuffer HTMLBuffer
     * @return StringBuffer.toString()
     */
    private String buildHTMLBuffer(HTMLBuffer htmlBuffer) {
        StringBuffer returnBuffer = null;

        try {
            returnBuffer = new StringBuffer("<html><center>");
            TrendModel tModel = getTrendModel();
            {
                htmlBuffer.setModel(tModel);

                if (htmlBuffer instanceof TabularHtml) {
                    ((TabularHtml) htmlBuffer).setTabularStartDate(tModel.getStartDate());
                    ((TabularHtml) htmlBuffer).setTabularEndDate(tModel.getStopDate());
                    ((TabularHtml) htmlBuffer).setResolution(getTrendProperties().getResolutionInMillis());

                    formatDateRangeSlider(tModel, (TabularHtml) htmlBuffer);
                }
                htmlBuffer.getHtml(returnBuffer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return returnBuffer.toString();
    }

    /**
     * Writes the current application state to a file for convenient default
     * startup display. Creation date: (9/25/2001 11:12:24 AM)
     */
    public void exit() {
        Preferences prefs = Preferences.userNodeForPackage(GraphClient.class);
        prefs.put("LAST_X", new Integer(this.getGraphParentFrame().getX()).toString());
        prefs.put("LAST_Y", new Integer(this.getGraphParentFrame().getY()).toString());
        prefs.put("LAST_WIDTH", new Integer(this.getGraphParentFrame().getWidth()).toString());
        prefs.put("LAST_HEIGHT", new Integer(this.getGraphParentFrame().getHeight()).toString());
        getTrendProperties().writeDefaultsFile();
        System.exit(0);
    }

    private int getTrendCount() {
        TreeModel model = getTreeViewPanel().getTree().getModel();
        Object parent = getTreeViewPanel().getTree().getModel().getRoot();
        return model.getChildCount(parent);
    }

    /**
     * Format the tabular view Slider. Returns the currently selected value from
     * the slider.
     * 
     * @param model
     * @param htmlData
     * @return valueSelected
     */
    private int formatDateRangeSlider(TrendModel model, TabularHtml htmlData) {
        String timePeriod = getGraph().getPeriod();
        int valueSelected = Integer.MIN_VALUE; // some number not -1 or greater

        setSliderKeysAndValues(model.getStartDate(), model.getStopDate());
        int valuesCount = sliderValuesArray.length; // number of days in time
                                                    // period

        if (timePeriod.equalsIgnoreCase(ServletUtil.ONEDAY)
            || timePeriod.equalsIgnoreCase(ServletUtil.TODAY)
            || (getTrendProperties().getOptionsMaskSettings() & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK
            || (getTrendProperties().getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK) // 1
                                                                                                                         // day
        {
            // With load duration and event, we show all values at the same
            // time.
            getSliderPanel().setVisible(false);
        } else {
            if (timeToUpdateSlider == true) // update button pushed
                                            // ("new start date selected")
            {
                // SET SLIDER LABELS
                if (valuesCount > 7) {
                    setSliderLabels(0, 7);
                } else {
                    // One week or less.
                    setSliderLabels(0, (valuesCount - 1)); // always start with
                                                           // the min day, max
                                                           // is length - 1
                }

                // SET CURRENT SLIDER VALUE Selected.
                if (getCurrentRadioButton().isSelected()) // today and backwards
                {
                    getTabularSlider().getModel().setValue(valuesCount - 1);
                } else if (getHistoricalRadioButton().isSelected()) // some day
                                                                    // and
                                                                    // forward
                {
                    getTabularSlider().getModel().setValue(0);
                }

                timeToUpdateSlider = false;
            }

            // Set up the Slider labels and size, this method also calls
            // setSliderLabels
            valueSelected =
                setLabelData(getTabularSlider().getMinimum(), getTabularSlider().getMaximum(),
                    getTabularSlider().getValue());
            getTabularSlider().setValue(valueSelected);

            GregorianCalendar cal = new GregorianCalendar();
            // If a date is indicated on the slider, just do that day....
            if (valueSelected > Integer.MIN_VALUE) {
                // temporarily set the end date for only one day's data
                cal.setTime((Date) (sliderValuesArray[valueSelected]).clone());
                cal.add(Calendar.DATE, 1);

                htmlData.setTabularEndDate(cal.getTime());

                // temporarily set the start date for only one day's data
                cal.setTime((sliderValuesArray[valueSelected]));
                htmlData.setTabularStartDate(cal.getTime());

            }

            getSliderPanel().setVisible(true);
        }

        return valueSelected;
    }

    private JRadioButton getCurrentRadioButton() {
        if (ivjCurrentRadioButton == null) {
            try {
                ivjCurrentRadioButton = new JRadioButton();
                ivjCurrentRadioButton.setName("CurrentRadioButton");
                ivjCurrentRadioButton.setText("Current");
                ivjCurrentRadioButton.setSelected(true);
                ivjCurrentRadioButton.setMinimumSize(new Dimension(35, 22));
                ivjCurrentRadioButton.setMargin(new Insets(2, 2, 2, 2));
                ivjCurrentRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);

                ivjCurrentRadioButton.addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCurrentRadioButton;
    }

    /**
     * A button group for current and historical radio button exclusive
     * selection.
     * 
     * @return ButtonGroup dataViewButtonGroup
     */
    private ButtonGroup getDataViewButtonGroup() {
        if (dataViewButtonGroup == null) {
            dataViewButtonGroup = new ButtonGroup();
            dataViewButtonGroup.add(getCurrentRadioButton());
            dataViewButtonGroup.add(getHistoricalRadioButton());
        }
        return dataViewButtonGroup;
    }

    /**
     * A menu listing FILE type functions.
     * 
     * @return FileMenu fileMenu
     */
    private FileMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new FileMenu();
            addMenuItemActionListeners(fileMenu);
        }
        return fileMenu;
    }

    /**
     * A jFreeChart for containing the graph image.
     * 
     * @return JFreeChart getGraph().getFreeChart()
     */
    public JFreeChart getFreeChart() {
        return getGraph().getFreeChart();
    }

    /**
     * A special panel for containing a jfreeChart.
     * 
     * @return YukonChartPanel freeChartPanel
     */
    private YukonChartPanel getFreeChartPanel() {
        if (freeChartPanel == null) {
            freeChartPanel = new YukonChartPanel(getFreeChart());
            freeChartPanel.setVisible(true);
            freeChartPanel.setPopupMenu(null); // DISABLE popup properties menu
        }

        // turn all zoom on
        freeChartPanel.setDomainZoomable(true);
        freeChartPanel.setRangeZoomable(true);

        return freeChartPanel;
    }

    /**
     * An instance of the Graph class.
     * 
     * @return com.cannontech.graph.Graph graphClass
     */
    public Graph getGraph() {
        if (graphClass == null) {
            graphClass = new Graph();
        }
        return graphClass;
    }

    private JFrame getGraphParentFrame() {
        return graphClientFrame;
    }

    private JPanel getGraphTabPanel() {
        if (ivjGraphTabPanel == null) {
            try {
                ivjGraphTabPanel = new JPanel();
                ivjGraphTabPanel.setName("GraphTabPanel");
                ivjGraphTabPanel.setLayout(new java.awt.BorderLayout());

                ivjGraphTabPanel.add(getFreeChartPanel());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjGraphTabPanel;
    }

    /**
     * A menu listing HELP type functions.
     * 
     * @return HelpMenu helpMenu
     */
    private HelpMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new HelpMenu();
            addMenuItemActionListeners(helpMenu);
        }
        return helpMenu;
    }

    private JRadioButton getHistoricalRadioButton() {
        if (ivjHistoricalRadioButton == null) {
            try {
                ivjHistoricalRadioButton = new JRadioButton();
                ivjHistoricalRadioButton.setName("HistoricalRadioButton");
                ivjHistoricalRadioButton.setText("Historical");
                ivjHistoricalRadioButton.setMinimumSize(new Dimension(35, 22));
                ivjHistoricalRadioButton.setMargin(new Insets(2, 2, 2, 2));

                ivjHistoricalRadioButton.addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjHistoricalRadioButton;
    }

    /**
     * A split pane to separate the treeviewPanel and the tabbed pane/trend
     * setup information. Return the JSplitPane1 property value.
     * 
     * @return JSplitPane ivjLeftRightSplitPane
     */
    private JSplitPane getLeftRightSplitPane() {
        if (ivjLeftRightSplitPane == null) {
            try {
                ivjLeftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                ivjLeftRightSplitPane.setName("LeftRightSplitPane");
                ivjLeftRightSplitPane.setDividerSize(4);
                ivjLeftRightSplitPane.setDividerLocation(206);
                getLeftRightSplitPane().add(getTreeViewPanel(), "left");
                getLeftRightSplitPane().add(getTrendDisplayPanel(), "right");

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLeftRightSplitPane;
    }

    /**
     * A menu bar for containing the menu lists.
     */
    private JMenuBar getMenuBar() {
        if (menuBar == null) {
            try {
                menuBar = new JMenuBar();
                menuBar.add(getFileMenu());
                menuBar.add(getTrendMenu());
                menuBar.add(getViewMenu());
                menuBar.add(getOptionsMenu());
                menuBar.add(getHelpMenu());
            } catch (Throwable ivjExc) {
                CTILogger.info(" Throwable Exception in getMenuBar()");
                ivjExc.printStackTrace();
            }
        }
        return menuBar;
    }

    /**
     * A menu listing OPTIONAS type functions.
     */
    private OptionsMenu getOptionsMenu() {
        if (optionsMenu == null) {
            optionsMenu =
                new OptionsMenu(getTrendProperties().getOptionsMaskSettings(),
                    getTrendProperties().getResolutionInMillis());
            addMenuItemActionListeners(optionsMenu);
        }
        return optionsMenu;
    }

    private JButton getRefreshButton() {
        if (ivjRefreshButton == null) {
            try {
                ivjRefreshButton = new JButton();
                ivjRefreshButton.setName("RefreshButton");
                ivjRefreshButton.setText("Refresh");
                ivjRefreshButton.setMaximumSize(new Dimension(75, 25));
                ivjRefreshButton.setMinimumSize(new Dimension(45, 25));
                ivjRefreshButton.setMargin(new Insets(2, 12, 2, 12));

                ivjRefreshButton.addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRefreshButton;
    }

    private JPanel getSliderPanel() {
        if (ivjSliderPanel == null) {
            try {
                ivjSliderPanel = new JPanel();
                ivjSliderPanel.setName("SliderPanel");
                ivjSliderPanel.setLayout(new java.awt.FlowLayout());
                ivjSliderPanel.setBackground(new java.awt.Color(255, 255, 255));
                getSliderPanel().add(getTabularSlider(), getTabularSlider().getName());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSliderPanel;
    }

    private Date getStartDate() {
        return getGraph().getStartDate();
    }

    private void setStartDate(Date newStartDate) {
        getGraph().setStartDate(newStartDate);
    }

    private DateComboBox getStartDateComboBox() {
        if (ivjStartDateComboBox == null) {
            try {
                ivjStartDateComboBox = new DateComboBox();
                ivjStartDateComboBox.setName("StartDateComboBox");
                ivjStartDateComboBox.setMinimumSize(new Dimension(50, 23));

                setStartDate(ivjStartDateComboBox.getSelectedDate());
                CTILogger.info(" DAY -> " + ivjStartDateComboBox.getSelectedDate() + " and  " + getStartDate());
                ivjStartDateComboBox.addActionListener(this);
                ivjStartDateComboBox.setEnabled(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjStartDateComboBox;
    }

    private JLabel getEventLabel() {
        if (eventLabel == null) {
            eventLabel = new JLabel("Number of Events:");
        }
        return eventLabel;
    }

    private JSpinner getEventJSpinner() {
        if (eventSpinner == null) {
            eventSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 50, 1));
            eventSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    getGraph().setNumberOfEvents(((Integer) eventSpinner.getValue()).intValue());
                    getGraph().setUpdateTrend(true);
                }
            });
        }
        return eventSpinner;
    }

    private JLabel getStartDateLabel() {
        if (ivjStartDateLabel == null) {
            try {
                ivjStartDateLabel = new JLabel();
                ivjStartDateLabel.setName("StartDateLabel");
                ivjStartDateLabel.setText("Start Date:");
                ivjStartDateLabel.setMinimumSize(new Dimension(30, 14));

                ivjStartDateLabel.setEnabled(false);
                // user code end
            } catch (Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjStartDateLabel;
    }

    private JEditorPanePrintable getSummaryEditorPane() {
        if (ivjSummaryEditorPane == null) {
            try {
                ivjSummaryEditorPane = new JEditorPanePrintable();
                ivjSummaryEditorPane.setName("SummaryEditorPane");
                ivjSummaryEditorPane.setBounds(0, 0, 861, 677);
                ivjSummaryEditorPane.setContentType("text/html");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSummaryEditorPane;
    }

    private JScrollPane getSummaryTabScrollPane() {
        if (ivjSummaryTabScrollPane == null) {
            try {
                ivjSummaryTabScrollPane = new JScrollPane();
                ivjSummaryTabScrollPane.setName("SummaryTabScrollPane");
                getSummaryTabScrollPane().setViewportView(getSummaryEditorPane());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSummaryTabScrollPane;
    }

    private JTabbedPane getTabbedPane() {
        if (ivjTabbedPane == null) {
            try {
                ivjTabbedPane = new JTabbedPane();
                ivjTabbedPane.setName("TabbedPane");
                ivjTabbedPane.setMinimumSize(new Dimension(200, 135));
                ivjTabbedPane.insertTab("Graph", null, getGraphTabPanel(), null, 0);
                ivjTabbedPane.insertTab("Tabular", null, getTabularTabScrollPane(), null, 1);
                ivjTabbedPane.insertTab("Summary", null, getSummaryTabScrollPane(), null, 2);

                if (getTrendProperties().getViewType() == GraphRenderers.TABULAR) {
                    ivjTabbedPane.setSelectedIndex(1);
                } else if (getTrendProperties().getViewType() == GraphRenderers.SUMMARY) {
                    ivjTabbedPane.setSelectedIndex(2);
                } else {
                    ivjTabbedPane.setSelectedIndex(0);
                }

                ivjTabbedPane.addChangeListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTabbedPane;
    }

    private JEditorPanePrintable getTabularEditorPane() {
        if (ivjTabularEditorPane == null) {
            try {
                ivjTabularEditorPane = new JEditorPanePrintable();
                ivjTabularEditorPane.setName("TabularEditorPane");
                ivjTabularEditorPane.setEditable(false);
                ivjTabularEditorPane.setContentType("text/html");

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTabularEditorPane;
    }

    private JSlider getTabularSlider() {
        if (ivjTabularSlider == null) {
            try {
                ivjTabularSlider = new JSlider();
                ivjTabularSlider.setName("TabularSlider");
                ivjTabularSlider.setPaintLabels(true);
                ivjTabularSlider.setBackground(new java.awt.Color(255, 255, 255));
                ivjTabularSlider.setPaintTicks(true);
                ivjTabularSlider.setForeground(new java.awt.Color(0, 0, 0));
                ivjTabularSlider.setValue(0);
                ivjTabularSlider.setMajorTickSpacing(1);
                ivjTabularSlider.setSnapToTicks(true);

                setSliderSize(0, 0, 1); // default initialize
                ivjTabularSlider.addChangeListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTabularSlider;
    }

    private JPanel getTabularTabPanel() {
        if (ivjTabularTabPanel == null) {
            try {
                ivjTabularTabPanel = new JPanel();
                ivjTabularTabPanel.setName("TabularTabPanel");
                ivjTabularTabPanel.setLayout(new java.awt.BorderLayout());
                ivjTabularTabPanel.setBounds(0, 0, 873, 688);
                getTabularTabPanel().add(getTabularEditorPane(), "Center");
                getTabularTabPanel().add(getSliderPanel(), "North");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTabularTabPanel;
    }

    private JScrollPane getTabularTabScrollPane() {
        if (ivjTabularTabScrollPane == null) {
            try {
                ivjTabularTabScrollPane = new JScrollPane();
                ivjTabularTabScrollPane.setName("TabularTabScrollPane");
                getTabularTabScrollPane().setViewportView(getTabularTabPanel());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTabularTabScrollPane;
    }

    private JComboBox<String> getTimePeriodComboBox() {
        if (ivjTimePeriodComboBox == null) {
            try {
                ivjTimePeriodComboBox = new JComboBox<>();
                ivjTimePeriodComboBox.setName("TimePeriodComboBox");
                ivjTimePeriodComboBox.setToolTipText("Select a Time Span");
                ivjTimePeriodComboBox.setMinimumSize(new Dimension(50, 23));
                for (int i = 0; i < ServletUtil.currentPeriods.length; i++) {
                    ivjTimePeriodComboBox.addItem(ServletUtil.currentPeriods[i]);
                }
                ivjTimePeriodComboBox.addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimePeriodComboBox;
    }

    private JLabel getTimePeriodLabel() {
        if (ivjTimePeriodLabel == null) {
            try {
                ivjTimePeriodLabel = new JLabel();
                ivjTimePeriodLabel.setName("TimePeriodLabel");
                ivjTimePeriodLabel.setText("Time Period:");
                ivjTimePeriodLabel.setMinimumSize(new Dimension(30, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimePeriodLabel;
    }

    private TreeViewPanel getTreeViewPanel() {
        if (ivjTreeViewPanel == null) {
            try {
                ivjTreeViewPanel = new TreeViewPanel();
                ivjTreeViewPanel.setName("TreeViewPanel");
                ivjTreeViewPanel.setMinimumSize(new Dimension(50, 10));
                ivjTreeViewPanel.setTreeModels(new com.cannontech.database.model.LiteBaseTreeModel[] { new GraphDefinitionTreeModel() });
                ivjTreeViewPanel.addTreeSelectionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTreeViewPanel;
    }

    private JPanel getTrendDisplayPanel() {
        if (ivjTrendDisplayPanel == null) {
            try {
                ivjTrendDisplayPanel = new JPanel();
                ivjTrendDisplayPanel.setName("TrendDisplayPanel");
                ivjTrendDisplayPanel.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsTabbedPane = new GridBagConstraints();
                constraintsTabbedPane.gridx = 0;
                constraintsTabbedPane.gridy = 1;
                constraintsTabbedPane.fill = GridBagConstraints.BOTH;
                constraintsTabbedPane.weightx = 1.0;
                constraintsTabbedPane.weighty = 1.0;
                getTrendDisplayPanel().add(getTabbedPane(), constraintsTabbedPane);

                GridBagConstraints constraintsTrendSetupPanel = new GridBagConstraints();
                constraintsTrendSetupPanel.gridx = 0;
                constraintsTrendSetupPanel.gridy = 0;
                constraintsTrendSetupPanel.fill = GridBagConstraints.BOTH;
                constraintsTrendSetupPanel.weightx = 1.0;
                constraintsTrendSetupPanel.weighty = 0.05;
                getTrendDisplayPanel().add(getTrendSetupPanel(), constraintsTrendSetupPanel);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTrendDisplayPanel;
    }

    private TrendMenu getTrendMenu() {
        if (trendMenu == null) {
            trendMenu = new TrendMenu();
            addMenuItemActionListeners(trendMenu);
        }
        return trendMenu;
    }

    private JPanel getTrendSetupPanel() {
        if (ivjTrendSetupPanel == null) {
            try {
                ivjTrendSetupPanel = new JPanel();
                ivjTrendSetupPanel.setName("TrendSetupPanel");
                ivjTrendSetupPanel.setLayout(new java.awt.GridBagLayout());
                ivjTrendSetupPanel.setMinimumSize(new Dimension(200, 35));

                GridBagConstraints constraintsRefreshButton = new GridBagConstraints();
                constraintsRefreshButton.gridx = 0;
                constraintsRefreshButton.gridy = 1;
                constraintsRefreshButton.fill = GridBagConstraints.HORIZONTAL;
                constraintsRefreshButton.weightx = 0.5;
                constraintsRefreshButton.insets = new Insets(5, 5, 5, 5);
                getTrendSetupPanel().add(getRefreshButton(), constraintsRefreshButton);

                GridBagConstraints constraintsCurrentRadioButton = new GridBagConstraints();
                constraintsCurrentRadioButton.gridx = 1;
                constraintsCurrentRadioButton.gridy = 1;
                constraintsCurrentRadioButton.fill = GridBagConstraints.HORIZONTAL;
                constraintsCurrentRadioButton.anchor = GridBagConstraints.EAST;
                constraintsCurrentRadioButton.weightx = 0.5;
                constraintsCurrentRadioButton.insets = new Insets(5, 20, 5, 5);
                getTrendSetupPanel().add(getCurrentRadioButton(), constraintsCurrentRadioButton);

                GridBagConstraints constraintsHistoricalRadioButton = new GridBagConstraints();
                constraintsHistoricalRadioButton.gridx = 2;
                constraintsHistoricalRadioButton.gridy = 1;
                constraintsHistoricalRadioButton.fill = GridBagConstraints.HORIZONTAL;
                constraintsHistoricalRadioButton.anchor = GridBagConstraints.WEST;
                constraintsHistoricalRadioButton.weightx = 0.5;
                constraintsHistoricalRadioButton.insets = new Insets(5, 5, 5, 5);
                getTrendSetupPanel().add(getHistoricalRadioButton(), constraintsHistoricalRadioButton);

                GridBagConstraints constraintsTimePeriodLabel = new GridBagConstraints();
                constraintsTimePeriodLabel.gridx = 3;
                constraintsTimePeriodLabel.gridy = 1;
                constraintsTimePeriodLabel.fill = GridBagConstraints.HORIZONTAL;
                constraintsTimePeriodLabel.anchor = GridBagConstraints.EAST;
                constraintsTimePeriodLabel.insets = new Insets(5, 20, 5, 5);
                getTrendSetupPanel().add(getTimePeriodLabel(), constraintsTimePeriodLabel);

                GridBagConstraints constraintsTimePeriodComboBox = new GridBagConstraints();
                constraintsTimePeriodComboBox.gridx = 4;
                constraintsTimePeriodComboBox.gridy = 1;
                constraintsTimePeriodComboBox.fill = GridBagConstraints.HORIZONTAL;
                constraintsTimePeriodComboBox.anchor = GridBagConstraints.WEST;
                constraintsTimePeriodComboBox.weightx = 1.0;
                constraintsTimePeriodComboBox.insets = new Insets(5, 5, 5, 5);
                getTrendSetupPanel().add(getTimePeriodComboBox(), constraintsTimePeriodComboBox);

                GridBagConstraints constraintsStartDateLabel = new GridBagConstraints();
                constraintsStartDateLabel.gridx = 5;
                constraintsStartDateLabel.gridy = 1;
                constraintsStartDateLabel.fill = GridBagConstraints.HORIZONTAL;
                constraintsStartDateLabel.anchor = GridBagConstraints.WEST;
                constraintsStartDateLabel.insets = new Insets(5, 20, 5, 5);
                getTrendSetupPanel().add(getStartDateLabel(), constraintsStartDateLabel);

                GridBagConstraints constraintsStartDateComboBox = new GridBagConstraints();
                constraintsStartDateComboBox.gridx = 6;
                constraintsStartDateComboBox.gridy = 1;
                constraintsStartDateComboBox.fill = GridBagConstraints.HORIZONTAL;
                constraintsStartDateComboBox.weightx = 1.0;
                constraintsStartDateComboBox.insets = new Insets(5, 5, 5, 5);
                getTrendSetupPanel().add(getStartDateComboBox(), constraintsStartDateComboBox);

                getTrendSetupPanel().add(
                    getEventLabel(),
                    new GridBagConstraints(7, 1, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5), 0, 0));
                getTrendSetupPanel().add(
                    getEventJSpinner(),
                    new GridBagConstraints(8, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5), 0, 0));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTrendSetupPanel;
    }

    private ViewMenu getViewMenu() {
        if (viewMenu == null) {
            viewMenu = new ViewMenu(getTrendProperties().getViewType());
            addMenuItemActionListeners(viewMenu);
        }
        return viewMenu;
    }

    public com.cannontech.graph.model.TrendProperties getTrendProperties() {
        return getGraph().getTrendProperties();
    }

    public TrendModel getTrendModel() {
        return getGraph().getTrendModel();
    }

    public AdvancedOptionsPanel getAdvOptsPanel() {
        if (advOptsPanel == null) {
            advOptsPanel = new AdvancedOptionsPanel(getTrendProperties());
        }
        return advOptsPanel;
    }

    @Override
    public void dbChangeReceived(final DBChangeMsg msg) {
        if (!msg.getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // limit to only refresh device/point tree on create/edit panel for paos and points
                    if (msg.getDatabase() == DBChangeMsg.CHANGE_POINT_DB
                        || msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB) {

                        CTILogger.info(" ## DBChangeMsg ##\n" + msg);

                        // Refreshes the device trees in the createGraphPanel if
                        // that's the panel that is open panel.
                        if (createPanel != null) {
                            ((AbstractDeviceTreeModel) createPanel.getTreeViewPanel().getTree().getModel()).update();
                        }
                    }
                    // limit to only refresh trend tree for trend db changes.
                    if (msg.getDatabase() == DBChangeMsg.CHANGE_GRAPH_DB) {
                        // Refreshes the device tree panel in the GraphClient. (Main
                        // Frame)
                        Object sel = getTreeViewPanel().getSelectedItem();
                        getTreeViewPanel().refresh();

                        if (sel != null) {
                            getTreeViewPanel().selectByString(sel.toString());
                        }
                    }
                }
            });
        }
    }

    private void handleException(Throwable exception) {
        exception.printStackTrace(System.out);
    }

    private void initialize() {
        try {
            // Setup Role Property to create/edit trends.
            ClientSession session = ClientSession.getInstance();
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
            boolean graphEdit =
                rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION, session.getUser());
            if (!graphEdit) {
                getTrendMenu().getCreateMenuItem().setEnabled(false);
                getTrendMenu().getEditMenuItem().setEnabled(false);
            }

            histDate = com.cannontech.util.ServletUtil.getToday();
            com.cannontech.util.ServletUtil.getToday();
            // user code end
            setName("GraphClient");
            setLayout(new java.awt.GridBagLayout());
            setSize(1086, 776);
            setMinimumSize(new Dimension(0, 0));

            GridBagConstraints constraintsLeftRightSplitPane = new GridBagConstraints();
            constraintsLeftRightSplitPane.gridx = -1;
            constraintsLeftRightSplitPane.gridy = -1;
            constraintsLeftRightSplitPane.fill = GridBagConstraints.BOTH;
            constraintsLeftRightSplitPane.weightx = 1.0;
            constraintsLeftRightSplitPane.weighty = 1.0;
            constraintsLeftRightSplitPane.insets = new Insets(4, 4, 4, 4);
            add(getLeftRightSplitPane(), constraintsLeftRightSplitPane);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void initializeSwingComponents() {
        getDataViewButtonGroup(); // make sure that the radio buttons get
                                  // grouped.

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new CTIKeyEventDispatcher() {
            @Override
            public boolean handleKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                    return true;
                } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                    // FORCE THE TREND TO UPDATE
                    getGraph().setUpdateTrend(true);
                    refresh();
                }

                // its this the last handling of the KeyEvent in this
                // KeyboardFocusManager?
                return false;
            }
        });

        boolean found = getTreeViewPanel().selectByString(getTrendProperties().getGdefName());
        // Construct a mouse listener that will allow double clicking selection
        // in the tree
        getTreeViewPanel().getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    valueChanged((TreeSelectionEvent) null);
                }

            }
        });

        StyleSheet styleSheet = new StyleSheet();
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) (kit.createDefaultDocument());

        getTabularEditorPane().setEditorKit(kit);
        getTabularEditorPane().setDocument(doc);
        try {
            FileReader reader = new FileReader("c:/yukon/client/config/CannonStyle.css");
            styleSheet.loadRules(reader, new java.net.URL("file:c:/yukon/client/config/CannonStyle.css"));
        } catch (IOException e) {
            CTILogger.info(e);
        }

        kit.setStyleSheet(styleSheet);

        trendDataAutoUpdater = new TrendDataAutoUpdater();
        trendDataAutoUpdater.start();

        AsyncDynamicDataSource dataSource = (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
        dataSource.addDBChangeListener(this);

        if (getTrendProperties().getViewType() != GraphRenderers.TABULAR
            && getTrendProperties().getViewType() != GraphRenderers.SUMMARY) {
            // not tabular or summary
            savedViewType = getTrendProperties().getViewType();
        }

        if ((found &&(getTrendProperties().getOptionsMaskSettings() & GraphRenderers.EVENT_MASK) == GraphRenderers.EVENT_MASK)) {
            getGraph().setPeriod(ServletUtil.EVENT);
            Date tomorrow = ServletUtil.getTomorrow();
            getGraph().setStartDate(tomorrow);
            getStartDateComboBox().setSelectedDate(tomorrow);
            getStartDateComboBox().setEnabled(true);
            getHistoricalRadioButton().setSelected(true);
        }

        if (found) { // found a gdefName to start with and display data
            refresh();
        }
    }

    public static void main(String[] args) {
        try {
            ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
            clientStartupHelper.setAppName(ApplicationId.TRENDING);
            clientStartupHelper.setRequiredRole(YukonRole.TRENDING.getRoleId());
            clientStartupHelper.setSplashUrl(CtiUtilities.TRENDING_SPLASH);

            JFrame mainFrame = new JFrame() {
                // creates a stupid anonymous class to create a unique class name
                // for the main frame
                // the ClientStartupHelper will use this for picking a Preference
                // node
            };
            clientStartupHelper.setParentFrame(mainFrame);
            mainFrame.setIconImages(getIconsImages());
            mainFrame.setTitle("Yukon Trending");

            clientStartupHelper.doStartup();

            GraphClient gc = new GraphClient(mainFrame);
            mainFrame.setContentPane(gc);
            mainFrame.setJMenuBar(gc.getMenuBar());
            gc.updateTimePeriodComboBox();
            mainFrame.setVisible(true);
            // Add the Window Closing Listener.
            mainFrame.addWindowListener(gc);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public void setGraphClientFrame(JFrame frame) {
        graphClientFrame = frame;
    }

    /**
     * @param minDay int
     * @param maxDay int
     * @param cal GregorianCalendar This method is used in setting up which max
     *        and min time period will be used in setting up the slider.
     */
    private int setLabelData(int minIndex, int maxIndex, int value) {
        int next = currentWeek * 7;
        int prev = ((currentWeek - 2) * 7);

        String graphPeriod = getGraph().getPeriod();

        if (getCurrentRadioButton().isSelected()) {
            setSliderLabels(minIndex, maxIndex);
            currentWeek = NO_WEEK;
        } else if (graphPeriod.equalsIgnoreCase(ServletUtil.ONEDAY)
            || graphPeriod.equalsIgnoreCase(ServletUtil.THREEDAYS)
            || graphPeriod.equalsIgnoreCase(ServletUtil.FIVEDAYS) || graphPeriod.equalsIgnoreCase(ServletUtil.ONEWEEK)) {
            // 2 to 7 days (index of array not a value)
            setSliderLabels(minIndex, maxIndex);
            currentWeek = NO_WEEK;
        } else {
            // 8 or more days
            if (value == next) {
                // week "NEXT"
                currentWeek++;
                setSliderLabels(next, maxIndex);
            } else if (value == prev + 6) {
                // week "PREV"
                currentWeek--;
                setSliderLabels(prev, maxIndex);
            }
        } // end else 8 or more days in the period

        return value;
    }

    public void setSliderKeysAndValues(Date start, Date end) {
        Vector<Date> temp = new Vector<>();
        GregorianCalendar tempCal = new GregorianCalendar();
        tempCal.setTime(start);
        while (tempCal.getTime().compareTo(end) < 0) {
            temp.add(new Date(tempCal.getTimeInMillis()));
            tempCal.add(Calendar.DATE, 1);
        }
        sliderValuesArray = new Date[temp.size()];
        temp.toArray(sliderValuesArray);
    }

    /**
     * Passed in the min, max time period lengths and a current calendar Create
     * a hashtable of values/labels for the date Slider. Depending on length and
     * current position of the time period, different labels are created
     */
    public void setSliderLabels(int minIndex, int maxIndex) {
        Hashtable<Integer, Serializable> sliderLabelTable = new Hashtable<>();

        final int weekPrevOrNext = 8;
        final int weekPrevAndNext = 9;

        if (currentWeek == NO_WEEK) {
            // at most 1, 2, 3 or 7 days
            setSliderSize(minIndex, maxIndex, (maxIndex - minIndex));
        }

        else if (currentWeek == FIRST_WEEK) {
            maxIndex = minIndex + 6;
            setSliderSize(minIndex, maxIndex + 1, weekPrevOrNext);
            sliderLabelTable.put(new Integer(maxIndex + 1), new JLabel("Next"));
        } else if ((currentWeek == FOURTH_WEEK && getGraph().getPeriod().equalsIgnoreCase(ServletUtil.FOURWEEKS))
            || currentWeek == FIFTH_WEEK) {
            // farthest we can go on slider, no next
            if ((minIndex + 6) < sliderValuesArray.length) {
                maxIndex = minIndex + 6;
            } else {
                maxIndex = sliderValuesArray.length - 1;
            }
            setSliderSize(minIndex - 1, maxIndex, weekPrevOrNext);
            sliderLabelTable.put(new Integer(minIndex - 1), new JLabel("Prev"));
        } else {
            // not the first nor last week, we can go prev or next from here
            maxIndex = minIndex + 6;
            setSliderSize(minIndex - 1, maxIndex + 1, weekPrevAndNext);
            sliderLabelTable.put(new Integer(minIndex - 1), new JLabel("Prev"));
            sliderLabelTable.put(new Integer(maxIndex + 1), new JLabel("Next"));
        }

        // Label up to 7 days
        for (int i = minIndex; i <= maxIndex; i++) {
            java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
            tempCal.setTime(sliderValuesArray[i]);

            // month count is 0 to 11
            int month = tempCal.get(Calendar.MONTH) + 1;
            int day = tempCal.get(Calendar.DAY_OF_MONTH);
            sliderLabelTable.put(new Integer(i), new JLabel((month + "/" + day)));
        }

        getTabularSlider().setLabelTable(sliderLabelTable);
    }

    public void setSliderSize(int min, int max, int numTicks) {
        getTabularSlider().setPreferredSize(new Dimension(((numTicks) * 60), 48));
        getTabularSlider().setMinimum(min);
        getTabularSlider().setMaximum(max);

    }

    public void showPopupMessage(String message, int messageType) {
        JFrame popupFrame = new JFrame();
        popupFrame.setIconImages(getIconsImages());
        JOptionPane.showMessageDialog(popupFrame, message, "Yukon Trending", messageType);
        return;
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        Cursor savedCursor = null;
        try {
            // set the cursor to a waiting cursor
            savedCursor = this.getCursor();
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (getTabbedPane().getSelectedComponent() == getGraphTabPanel()) {
                getGraph().setViewType(savedViewType);
                // Has no parent, therefore isthe root.
                if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                    // Only tell the user to select a trend if there are
                    // trends to select
                    if (getTrendCount() > 0) {
                        showPopupMessage("Please Select a Trend From the list", JOptionPane.WARNING_MESSAGE);
                    }
                }
                getGraph().update(null, null);
                getFreeChartPanel().setChart(getFreeChart());
            } else if (getTabbedPane().getSelectedComponent() == getTabularTabScrollPane()) {
                getGraph().setViewType(GraphRenderers.TABULAR);
                if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                    getTabularEditorPane().setText("<CENTER>Please Select a Trend from the list");
                    getSliderPanel().setVisible(false);
                    return;
                }
                if (!getTabularSlider().getModel().getValueIsAdjusting()) {
                    getTabularSlider().removeChangeListener(this);
                    getGraph().update(null, null);
                    StringBuffer buf = new StringBuffer();
                    buf.append(buildHTMLBuffer(new TabularHtml()));
                    getTabularEditorPane().setText(buf.toString());
                    getTabularEditorPane().setCaretPosition(0);
                    getGraph().setHtmlString(buf);
                    getTabularSlider().addChangeListener(this);
                }
            } else if (getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane()) {
                getGraph().setViewType(GraphRenderers.SUMMARY);

                if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                    getSummaryEditorPane().setText("<CENTER>Please Select a Trend from the list");
                } else {
                    StringBuffer buf = new StringBuffer();
                    getGraph().update(null, null);
                    buf.append(buildHTMLBuffer(new PeakHtml()));
                    buf.append(buildHTMLBuffer(new UsageHtml()));
                    getSummaryEditorPane().setText(buf.toString());
                    getSummaryEditorPane().setCaretPosition(0);
                    getGraph().setHtmlString(buf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.setCursor(savedCursor);
        }
    }

    public void updateCurrentPane() {
        synchronized (GraphClient.class) {
            trendDataAutoUpdater.ignoreAutoUpdate = true;
        }

        Object selectedItem = getTreeViewPanel().getSelectedItem();
        if (selectedItem != null) {
            if (getTabbedPane().getSelectedComponent() == getGraphTabPanel()) {
                getGraph().setViewType(savedViewType);
                getGraph().update(null, null);
                getFreeChartPanel().setChart(getFreeChart());
            } else if (getTabbedPane().getSelectedComponent() == getTabularTabScrollPane()) {
                getTabularSlider().removeChangeListener(this);
                getGraph().setViewType(GraphRenderers.TABULAR);
                getGraph().update(null, null);
                StringBuffer buf = new StringBuffer();
                buf.append(buildHTMLBuffer(new TabularHtml()));

                buf.append("</CENTER></HTML>");
                getTabularEditorPane().setText(buf.toString());
                getTabularEditorPane().setCaretPosition(0);
                getGraph().setHtmlString(buf);
                getTabularSlider().addChangeListener(this);
            } else if (getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane()) {
                StringBuffer buf = new StringBuffer();

                getGraph().setViewType(GraphRenderers.SUMMARY);
                getGraph().update(null, null);
                buf.append(buildHTMLBuffer(new PeakHtml()));
                buf.append(buildHTMLBuffer(new UsageHtml()));
                buf.append("</CENTER></HTML>");

                getSummaryEditorPane().setText(buf.toString());
                getGraph().setHtmlString(buf);
                getSummaryEditorPane().setCaretPosition(0);
            }
            getTrendModel().setNumberOfEvents(((Integer) getEventJSpinner().getValue()).intValue());
        } else {
            showPopupMessage("Please select a Trend from the List", JOptionPane.WARNING_MESSAGE);
        }

        synchronized (GraphClient.class) {
            trendDataAutoUpdater.ignoreAutoUpdate = false;
        }

    }

    /**
     * Selections on the left hand tree generate and event that appears here.
     * Creation date: (6/22/00 10:57:11 AM)
     * 
     * @param event TreeSelectionEvent
     */
    @Override
    public void valueChanged(TreeSelectionEvent event) {
        Cursor savedCursor = null;

        if (getTreeViewPanel() == null) {
            return;
        }

        try {
            // Current cursor set to waiting during the update.
            savedCursor = this.getCursor();
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // Find the selected graph definition and display it
            Object item = getTreeViewPanel().getSelectedItem();
            if (item == null || !(item instanceof LiteBase)) {
                return;
            }

            // Item is an instance of LiteBase...(from previous statement)
            getGraph().setGraphDefinition(((LiteBase) item).getLiteID());

            // *Note: An update will only occur when event is null.
            // Null events are passed from the mouse Listener (in
            // displayGraph..)
            // and only occur after a double click has happened on a tree node.
            if (event == null) {
                updateCurrentPane();
                getGraphParentFrame().setTitle(
                    "Yukon Trending - ( Updated " + extendedDateTimeformat.format(new Date(System.currentTimeMillis()))
                        + " )");
                getRefreshButton().requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.setCursor(savedCursor);
        }
    }

    @Override
    public void windowClosing(WindowEvent event) {
        exit();
    }

    @Override
    public void windowActivated(WindowEvent event) { /* nothing */
    }

    @Override
    public void windowClosed(WindowEvent event) { /* nothing */
    }

    @Override
    public void windowDeactivated(WindowEvent event) { /* nothing */
    }

    @Override
    public void windowDeiconified(WindowEvent event) { /* nothing */
    }

    @Override
    public void windowIconified(WindowEvent event) { /* nothing */
    }

    @Override
    public void windowOpened(WindowEvent event) { /* nothing */
    }
}
