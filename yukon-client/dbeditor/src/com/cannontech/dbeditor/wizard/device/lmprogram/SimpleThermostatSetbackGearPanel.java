package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.LineLabel;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.db.device.lm.LMThermostatGear;
import com.cannontech.dbeditor.wizard.device.lmgroup.ExpressComCellRenderer;

public class SimpleThermostatSetbackGearPanel extends GenericGearPanel implements com.cannontech.common.gui.util.DataInputPanelListener {
    private static final Calendar zeroCal = Calendar.getInstance();
    private JPanel statEditorPanel;
    private JSpinner randomStartTimeSpinner;
    private JSpinner preCoolTempSpinner;
    private JSpinner preCoolTimeSpinner;
    private JSpinner preCoolHoldSpinner;
    private JSpinner rampSpinner;
    private JSpinner maxSpinner;
    private JSpinner restoreTimeSpinner;
    private JSpinner maxRuntimeSpinner;
    private JSpinner resendRateSpinner;
    private JCheckBox heatModeCheckBox;
    private JCheckBox coolModeCheckBox;
    private JComboBox changeGearBox;
    private ChangeListener customChangeListener;

    static {
        zeroCal.set(Calendar.HOUR_OF_DAY, 0);
        zeroCal.set(Calendar.MINUTE, 0);
        zeroCal.set(Calendar.SECOND, 0);
    }

    public SimpleThermostatSetbackGearPanel() {
        initialize();
    }

    public static void main(String[] args)  {
        javax.swing.JFrame frame = new javax.swing.JFrame();

        SimpleThermostatSetbackGearPanel panel = new SimpleThermostatSetbackGearPanel();
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private JSpinner createTimeSpinner(final int min, final int max) {
        SpinnerDateModel model = new SpinnerDateModel(zeroCal.getTime(), null, null, Calendar.MINUTE) {
            @Override
            public void setValue(Object value) {
                if (value instanceof Date) {
                    Date date = (Date) value;
                    Calendar tempCal = (Calendar) zeroCal.clone();
                    tempCal.setTime(date);

                    int hours = tempCal.get(Calendar.HOUR_OF_DAY);
                    int minutes = tempCal.get(Calendar.MINUTE);
                    if ((hours < min) ||
                        (hours > max) ||
                        (hours == max && minutes > 0)) {
                        return;
                    }
                    super.setValue(date);
                }
            }
        };
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        spinner.addChangeListener(customChangeListener);
        return spinner;
    }

    private JSpinner getRandomStartTimeSpinner() {
        if (randomStartTimeSpinner == null) {
            randomStartTimeSpinner = createTimeSpinner(0, 2);
        }
        return randomStartTimeSpinner;
    }

    private JSpinner getResendRateSpinner() {
        if (resendRateSpinner == null) {
            resendRateSpinner = createTimeSpinner(0, 3);
        }
        return resendRateSpinner;    
    }

    private JSpinner getPreCoolTempSpinner() {
        if (preCoolTempSpinner == null) {
            SpinnerNumberModel model = new SpinnerNumberModel(0, -20, 20, 1); 
            preCoolTempSpinner = new JSpinner(model);
            preCoolTempSpinner.addChangeListener(customChangeListener);
        }
        return preCoolTempSpinner;
    }

    private JSpinner getPreCoolTimeSpinner() {
        if (preCoolTimeSpinner == null) {
            preCoolTimeSpinner = createTimeSpinner(0, 5);
        }
        return preCoolTimeSpinner;
    }
    private JSpinner getPreCoolHoldSpinner() {
        if (preCoolHoldSpinner == null) {
            preCoolHoldSpinner = createTimeSpinner(0, 5);
        }
        return preCoolHoldSpinner;
    }

    private JSpinner getRampSpinner() {
        if (rampSpinner == null) {
            SpinnerNumberModel model = new SpinnerNumberModel(0.0, -9.9, 9.9, 0.1);
            rampSpinner = new JSpinner(model);
            rampSpinner.addChangeListener(customChangeListener);
        }
        return rampSpinner;
    }

    private JSpinner getMaxSpinner() {
        if (maxSpinner == null) {
            SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 20, 1);
            maxSpinner = new JSpinner(model);
            maxSpinner.addChangeListener(customChangeListener);
        }
        return maxSpinner;
    }

    private JSpinner getRestoreTimeSpinner() {
        if (restoreTimeSpinner == null) {
            restoreTimeSpinner = createTimeSpinner(0, 5);
        }
        return restoreTimeSpinner;
    }

    private JSpinner getMaxRuntimeSpinner() {
        if (maxRuntimeSpinner == null) {
            maxRuntimeSpinner = createTimeSpinner(0, 24);
        }
        return maxRuntimeSpinner;
    }

    private JComboBox getChangeGearBox() {
        if (changeGearBox == null) {
            String[] values = new String[]{"Manually Only", "After a Duration", "Priority Change", "Above Trigger"};
            changeGearBox = new JComboBox(values);
            changeGearBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    fireInputUpdate();
                }
            });
        }
        return changeGearBox;
    }
    
    private JPanel getMainPanel() {
        if (statEditorPanel != null) return statEditorPanel;

        statEditorPanel = new JPanel();
        statEditorPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3,3,3,3);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        statEditorPanel.add(new JLabel("Random Start Time"), c);

        c.gridx = 1;
        c.gridy = 0;
        statEditorPanel.add(getRandomStartTimeSpinner(), c);

        c.gridx = 2;
        c.gridy = 0;
        statEditorPanel.add(new JLabel("(0:00 - 2:00)"), c);

        c.gridx = 0;
        c.gridy = 1;
        statEditorPanel.add(new JLabel("PreCool"), c);

        c.gridx = 1;
        c.gridy = 1;
        statEditorPanel.add(new JLabel("Temp"), c);

        c.gridx = 2;
        c.gridy = 1;
        statEditorPanel.add(getPreCoolTempSpinner(), c);

        c.gridx = 3;
        c.gridy = 1;
        statEditorPanel.add(new JLabel("(-20 - 20)"), c);    

        c.gridx = 1;
        c.gridy = 2;
        statEditorPanel.add(new JLabel("Time"), c);    

        c.gridx = 2;
        c.gridy = 2;
        statEditorPanel.add(getPreCoolTimeSpinner(), c);

        c.gridx = 3;
        c.gridy = 2;
        statEditorPanel.add(new JLabel("(0:00 - 5:00)"), c);

        c.gridx = 1;
        c.gridy = 3;
        statEditorPanel.add(new JLabel("Hold"), c);    

        c.gridx = 2;
        c.gridy = 3;
        statEditorPanel.add(getPreCoolHoldSpinner(), c);

        c.gridx = 3;
        c.gridy = 3;
        statEditorPanel.add(new JLabel("(0:00 - 5:00)"), c);

        c.gridx = 0;
        c.gridy = 4;
        statEditorPanel.add(new JLabel("Ramp \u00B0F/Hour"), c);

        c.gridx = 1;
        c.gridy = 4;
        statEditorPanel.add(getRampSpinner(), c);

        c.gridx = 2;
        c.gridy = 4;
        statEditorPanel.add(new JLabel("(-9.9 - 9.9)"), c);

        c.gridx = 0;
        c.gridy = 5;
        statEditorPanel.add(new JLabel("Max \u00B0\u0394"), c);

        c.gridx = 1;
        c.gridy = 5;
        statEditorPanel.add(getMaxSpinner(), c);

        c.gridx = 2;
        c.gridy = 5;
        statEditorPanel.add(new JLabel("(0 - 20)"), c);

        c.gridx = 0;
        c.gridy = 6;
        statEditorPanel.add(new JLabel("Restore Time"), c);

        c.gridx = 1;
        c.gridy = 6;
        statEditorPanel.add(getRestoreTimeSpinner(), c);

        c.gridx = 2;
        c.gridy = 6;
        statEditorPanel.add(new JLabel("(0:00 - 5:00)"), c);

        c.gridx = 0;
        c.gridy = 7;
        statEditorPanel.add(new JLabel("Max Runtime"), c);

        c.gridx = 1;
        c.gridy = 7;
        statEditorPanel.add(getMaxRuntimeSpinner(), c);

        c.gridx = 2;
        c.gridy = 7;
        statEditorPanel.add(new JLabel("(0:00 - 24:00)"), c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        statEditorPanel.add(new JLabel("Resend Rate"), c);

        c.gridx = 1;
        c.gridy = 8;
        statEditorPanel.add(getResendRateSpinner(), c);

        c.gridx = 2;
        c.gridy = 8;
        statEditorPanel.add(new JLabel("(0:00 - 3:00)"), c);

        return statEditorPanel;
    }

    private JCheckBox getHeatModeCheckBox() {
        if (heatModeCheckBox == null) {
            heatModeCheckBox = new JCheckBox("Heat Mode");
            heatModeCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    boolean toggleState = (e.getStateChange() == ItemEvent.SELECTED);
                    coolModeCheckBox.setSelected(!toggleState);
                    fireInputUpdate();
                }
            });
        }
        return heatModeCheckBox;
    }

    private JCheckBox getCoolModeCheckBox() {
        if (coolModeCheckBox == null) {
            coolModeCheckBox = new JCheckBox("Cool Mode");
            coolModeCheckBox.setSelected(true);
            coolModeCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    boolean toggleState = (e.getStateChange() == ItemEvent.SELECTED);
                    heatModeCheckBox.setSelected(!toggleState);
                    fireInputUpdate();
                }
            });
        }
        return coolModeCheckBox;
    }

    private JPanel getImagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2,2,2,2);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        panel.add(getHeatModeCheckBox(), c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        panel.add(getCoolModeCheckBox());

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        panel.add(getCurveTable(), c);

        return panel;
    }

    private JTable getCurveTable() {
        JTable curveTable = new JTable();
        curveTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        curveTable.setColumnSelectionAllowed(false);
        curveTable.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
        curveTable.setGridColor(java.awt.Color.yellow);
        curveTable.setShowHorizontalLines(false);
        curveTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        curveTable.setIntercellSpacing(new java.awt.Dimension(1, 0));
        curveTable.setRowSelectionAllowed(false);
        curveTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        curveTable.setShowVerticalLines(false);

        Dimension size = new Dimension(250,100);
        curveTable.setPreferredSize(size);
        curveTable.setMaximumSize(size);
        curveTable.setMinimumSize(size);

        JLabel c0 = new JLabel("Rand");
        c0.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel c1 = new JLabel("PreCool");
        c1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel time = new JLabel("Time");
        time.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel c2 = new JLabel("Hold");
        c2.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel c6 = new JLabel("RampOut");
        c6.setHorizontalAlignment(SwingConstants.CENTER);

        final LineLabel blank = new LineLabel( LineLabel.NO_LINE );
        final LineLabel bkSlash = new LineLabel( LineLabel.UP_LEFT_BOT_RIGHT );
        final LineLabel bottom = new LineLabel( LineLabel.BOTTOM );

        Object[][] data = new Object[][]{
                {c0,     c1,      c2,     new LineLabel(LineLabel.NO_LINE),             new LineLabel(LineLabel.NO_LINE),             blank,  c6},
                {blank,  time,    time,   new LineLabel(LineLabel.NO_LINE),             new LineLabel(LineLabel.NO_LINE),             bottom, time},
                {bottom, blank,   blank,  new LineLabel(LineLabel.NO_LINE),             new LineLabel( LineLabel.BOT_LEFT_UP_RIGHT ), blank,  bkSlash},
                {blank,  bkSlash, bottom, new LineLabel( LineLabel.BOT_LEFT_UP_RIGHT ), new LineLabel(LineLabel.NO_LINE),             blank,  blank},
                {blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE),             new LineLabel(LineLabel.NO_LINE),             blank,  blank},
                {blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE),             new LineLabel(LineLabel.NO_LINE),             blank,  blank},
                {blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE),             new LineLabel(LineLabel.NO_LINE),             blank,  blank},
        };

        DefaultTableModel model = new DefaultTableModel(data, new Object[7]) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        curveTable.setModel(model);
        curveTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {
                if (value instanceof JComponent) {
                    JComponent c = (JComponent) value;
                    if (column == 3 || column == 4) return c;

                    c.setBorder(ExpressComCellRenderer.yellowBorder);
                    return c;
                }
                return null;
            }
        });

        curveTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        curveTable.getColumnModel().getColumn(1).setPreferredWidth(65);
        curveTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        curveTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        curveTable.getColumnModel().getColumn(4).setPreferredWidth(20);
        curveTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        curveTable.getColumnModel().getColumn(6).setPreferredWidth(75);

        return curveTable;
    }

    private JPanel getChangePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("When to Change:"));
        panel.add(getChangeGearBox());
        return panel;
    }
    
    private void initialize() {
        setName("SimpleThermostatSetbackGearPanel");
        setPreferredSize(new Dimension(402, 430));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);
        setSize(402, 430);
        setLayout(new BorderLayout());
        
        customChangeListener = new CustomChangeListener();

        add(getImagePanel(), BorderLayout.NORTH);
        add(getMainPanel(), BorderLayout.CENTER);
        add(getChangePanel(), BorderLayout.SOUTH);
    }
    
    private Date toDateFromMinutes(final int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        
        Calendar tempCal = (Calendar) zeroCal.clone();
        tempCal.set(Calendar.HOUR_OF_DAY, hour);
        tempCal.set(Calendar.MINUTE, min);
        
        Date date = tempCal.getTime();
        return date;
    }
    
    private boolean isNotEmpty(Number number) {
        boolean result = (number != null && number.intValue() != 0);
        return result;
    }
    
    private Integer toMinutesFromDate(final Object obj) {
        if (obj == null || !(obj instanceof Date)) return Integer.valueOf(0);
        
        Calendar tempCal = (Calendar) zeroCal.clone();
        tempCal.setTime((Date) obj);
        
        int hour = tempCal.get(Calendar.HOUR_OF_DAY);
        int minute = tempCal.get(Calendar.MINUTE);
        
        int totalMinutes = hour * 60 + minute;
        return Integer.valueOf(totalMinutes);
    }
    
    @Override
    public void setValue(Object obj) {
        if (obj == null || !(obj instanceof SimpleThermostatRampingGear)) return;
        
        SimpleThermostatRampingGear gear = (SimpleThermostatRampingGear) obj;
        
        boolean isHeatMode = gear.getSettings().charAt(2) == 'H';
        getHeatModeCheckBox().setSelected(isHeatMode);
        
        boolean isCoolMode = gear.getSettings().charAt(3) == 'I';
        getCoolModeCheckBox().setSelected(isCoolMode);
        
        Integer random = gear.getRandom();
        if (isNotEmpty(random)) getRandomStartTimeSpinner().setValue(toDateFromMinutes(random));
        
        Integer preCoolTemp = gear.getValueB();
        if (isNotEmpty(preCoolTemp)) getPreCoolTempSpinner().setValue(preCoolTemp);
        
        Integer preCoolTime = gear.getValueTb();
        if (isNotEmpty(preCoolTime)) getPreCoolTimeSpinner().setValue(toDateFromMinutes(preCoolTime));
        
        Integer preCoolHold = gear.getValueTc();
        if (isNotEmpty(preCoolHold)) getPreCoolHoldSpinner().setValue(toDateFromMinutes(preCoolHold));
        
        Float rampRate = gear.getRampRate();
        if (isNotEmpty(rampRate)) getRampSpinner().setValue(rampRate);

        Integer maxRampTemp = gear.getValueD();
        if (isNotEmpty(maxRampTemp)) getMaxSpinner().setValue(maxRampTemp);
        
        Integer restoreTime = gear.getValueTf();
        if (isNotEmpty(restoreTime)) getRestoreTimeSpinner().setValue(toDateFromMinutes(restoreTime));
        
        Integer maxRuntime = gear.getValueTa();
        if (isNotEmpty(maxRuntime)) getMaxRuntimeSpinner().setValue(toDateFromMinutes(maxRuntime));
        
        Integer resendRate = gear.getMethodRate();
        if (isNotEmpty(resendRate)) getResendRateSpinner().setValue(toDateFromMinutes(resendRate));
        
        setChangeCondition(getChangeGearBox(), gear.getChangeCondition());
    }
    
    @Override
    public Object getValue(Object obj) {
        if (obj == null || !(obj instanceof LMThermostatGear)) return null;
        
        LMThermostatGear gear = (LMThermostatGear) obj;
        
        boolean isHeatMode = getHeatModeCheckBox().isSelected();
        char heatChar = (isHeatMode) ? 'H' : '-';
        gear.getSettings().setCharAt(2, heatChar);
        
        boolean isCoolMode = getCoolModeCheckBox().isSelected();
        char coolChar = (isCoolMode) ? coolChar = 'I' : '-';
        gear.getSettings().setCharAt(3, coolChar);
        
        Integer random = toMinutesFromDate(getRandomStartTimeSpinner().getValue());
        gear.setRandom(random);
        
        Integer preCoolTemp = Integer.valueOf(((Number) getPreCoolTempSpinner().getValue()).intValue());
        gear.setValueB(preCoolTemp);
        
        Integer preCoolTime = toMinutesFromDate(getPreCoolTimeSpinner().getValue());
        gear.setValueTb(preCoolTime);
        
        Integer preCoolHold = toMinutesFromDate(getPreCoolHoldSpinner().getValue());
        gear.setValueTc(preCoolHold);
        
        Float rampRate = Float.valueOf(((Number) getRampSpinner().getValue()).floatValue());
        gear.setRampRate(rampRate);
        
        Integer maxRampTemp = Integer.valueOf(((Number) getMaxSpinner().getValue()).intValue());
        gear.setValueD(maxRampTemp);
        
        Integer restoreTime = toMinutesFromDate(getRestoreTimeSpinner().getValue());
        gear.setValueTf(restoreTime);
        
        Integer maxRuntime = toMinutesFromDate(getMaxRuntimeSpinner().getValue());
        gear.setValueTa(maxRuntime);
        
        Integer resendRate = toMinutesFromDate(getResendRateSpinner().getValue());
        gear.setMethodRate(resendRate);
        
        gear.setChangeCondition(getChangeCondition(getChangeGearBox().getSelectedItem().toString()));
        
        return gear;
    }
    
    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        this.fireInputUpdate();
    }
    
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();	
    }
    
    private class CustomChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            fireInputUpdate();
        }
    }
    
}
