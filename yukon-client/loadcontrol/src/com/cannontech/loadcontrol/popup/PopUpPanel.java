package com.cannontech.loadcontrol.popup;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.cannontech.common.gui.panel.ManualChangeJPanel;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.gui.manualentry.ConstraintResponsePanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectChangeGearJPanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;
import com.cannontech.loadcontrol.gui.manualentry.MultiSelectProg;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

/**
 * Helps control handling with program constraints 
 * 
 */
public class PopUpPanel {

	//the popup GUI item that is using this control class
	private JPopupMenu thisPopup = null;
	
	public PopUpPanel( JPopupMenu currentPopUp ) {
		super();
		
		if( currentPopUp == null )
			throw new IllegalArgumentException(
				"Unable to create instance with null argument");

		thisPopup = currentPopUp;
	}
	
	
	
	/**
	 * Single method to allow program constraints to be dealt with on the GUI
	 * 
	 */
	public void showDirectManualEntry( final int panelMode,
			LMProgramBase[] prgArray ) 
	{
		final JDialog d = new JDialog(SwingUtil.getParentFrame(thisPopup.getInvoker()));

		DirectControlJPanel panel = new DirectControlJPanel()
		{
			public void exit()
			{
				d.dispose();
			}

			public void setParentWidth( int x )
			{
				d.setSize( d.getWidth() + x, d.getHeight() );
			}
		};


		d.setTitle(
			panelMode == DirectControlJPanel.MODE_START_STOP 
			? "Start Program(s)"
			: "Stop Program(s)" );
			
		d.setModal(true);
		d.setContentPane(panel);
		d.setSize( 350, 500 );
		d.setLocationRelativeTo(thisPopup);

		
		if( panel.setMultiSelectObject( prgArray ) )
		{
	        panel.setMode( panelMode );
	        d.show();
		
			//destroy the JDialog
			d.dispose();

			if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
			{
				MultiSelectProg[] selected = panel.getMultiSelectObject();
		
				if( selected != null )
				{
					ResponseProg[] programResp =
						new ResponseProg[ selected.length ];
					
					//if we have at least 1 check constraint set, then lets
					// always show a result box
					boolean isCheckConstraints = false;

					for( int i = 0; i < selected.length; i++ )
					{
						LMManualControlRequest lmCntrlMsg =
							panel.createMessage(
									selected[i].getBaseProgram(),
									selected[i].getGearNum());

						isCheckConstraints |=
							lmCntrlMsg.getConstraintFlag() ==
								LMManualControlRequest.CONSTRAINTS_FLAG_CHECK;
						
						programResp[i] = new ResponseProg(
								lmCntrlMsg,
								selected[i].getBaseProgram() );

					}
					
					boolean success = LCUtils.executeSyncMessage( programResp );

					if( !success || isCheckConstraints )
					{
						final ConstraintResponsePanel constrPanel = new ConstraintResponsePanel();
						OkCancelDialog diag = new OkCancelDialog(SwingUtil.getParentFrame(thisPopup.getInvoker()),
							"Results of Constraint Check", true, constrPanel);

						//add a "Successful check" output if there are not
						// any constraints violated
						if( isCheckConstraints ) {
							for( int i = 0; i < programResp.length; i++ ) {

								if( programResp[i].getViolations().size() <= 0 ) {
									programResp[i].setAction(
										ResponseProg.NO_VIOLATION_ACTION );

									programResp[i].setNoViolationsMessage(
										" No Constraints Violated");
								}
								
							}						
						}

						//set our responses
						constrPanel.setValue( programResp );
						
						diag.setOkButtonText( "Resubmit" );
						diag.setResizable( true );
						diag.setSize( 800, 350 );
						diag.setLocationRelativeTo( thisPopup );

						diag.show();

						ResponseProg[] respArr = 
							(ResponseProg[])constrPanel.getValue( null );
							
						if( diag.getButtonPressed() == OkCancelDialog.OK_PRESSED
							&& respArr.length > 0 )
						{
							LCUtils.executeSyncMessage( respArr );
						}

						diag.dispose();

					}
					
				}
			}
		}
		else
		{
			JOptionPane.showMessageDialog(
				thisPopup,
				"No programs are found for your current selection, try again",
				"Unable to Control Program(s)",
				JOptionPane.WARNING_MESSAGE );
			
		}
		
	}

    public void showChangeGearOptions( LMProgramDirect currentProgram ) {
        final JDialog d = new JDialog(SwingUtil.getParentFrame(thisPopup.getInvoker()));

        DirectChangeGearJPanel panel = new DirectChangeGearJPanel() {
            public void exit() {
                d.dispose();
            }
            public void setParentWidth( int x ) {
                d.setSize( d.getWidth() + x, d.getHeight() );
            }
        };
        panel.setGearList(((IGearProgram)currentProgram).getDirectGearVector());
        d.setTitle("Choose Next Gear");
        d.setModal(true);
        d.setContentPane(panel);
        d.setSize( 250, 200 );
        d.setLocationRelativeTo(thisPopup);
        d.show();
    
        //destroy the JDialog
        d.dispose();
        if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE ) {
            Integer newCurrentGearNumber = panel.getSelectedGearNumber();
    
            if( newCurrentGearNumber != null ) {
                LMManualControlRequest lmCntrlMsg = panel.createMessage(currentProgram, newCurrentGearNumber);
                lmCntrlMsg.setConstraintFlag(LMManualControlRequest.CONSTRAINTS_FLAG_USE);
                lmCntrlMsg.setCommand(LMManualControlRequest.CHANGE_GEAR);
                ResponseProg currentResponseProg = new ResponseProg(lmCntrlMsg, currentProgram );
                ResponseProg[] responseWrapper = { currentResponseProg };
                
                boolean success = LCUtils.executeSyncMessage( responseWrapper );

                if( !success )
                {
                    final ConstraintResponsePanel constrPanel = new ConstraintResponsePanel();
                    OkCancelDialog diag = new OkCancelDialog(SwingUtil.getParentFrame(thisPopup.getInvoker()),
                        "Results of Constraint Check", true, constrPanel);

                    //add a "Successful check" output if there are not
                    // any constraints violated
                    if( currentResponseProg.getViolations().size() <= 0 ) {
                        currentResponseProg.setAction( ResponseProg.NO_VIOLATION_ACTION );
                        currentResponseProg.setNoViolationsMessage("No Constraints Violated");
                    }

                    //set our responses
                    constrPanel.setValue( responseWrapper );
                    
                    diag.setOkButtonText( "Resubmit" );
                    diag.setResizable( true );
                    diag.setSize( 800, 350 );
                    diag.setLocationRelativeTo( thisPopup );

                    diag.show();

                    ResponseProg[] respArr = (ResponseProg[])constrPanel.getValue( null );
                        
                    if( diag.getButtonPressed() == OkCancelDialog.OK_PRESSED
                            && respArr.length > 0 ) {
                        LCUtils.executeSyncMessage( respArr );
                    }

                    diag.dispose();
                }
            }
        }
    }
}
