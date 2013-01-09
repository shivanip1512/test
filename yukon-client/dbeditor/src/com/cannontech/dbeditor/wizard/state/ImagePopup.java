package com.cannontech.dbeditor.wizard.state;

import javax.swing.JPopupMenu;

import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonImage;
/**
 * @author rneuharth
 * Aug 26, 2002 at 12:05:00 PM
 * 
 * A undefined generated comment
 */
class ImagePopup extends JPopupMenu implements java.awt.event.ActionListener
{
   private LiteYukonImage selectedLiteImage = null;
   private com.cannontech.common.gui.util.CTICallBack caller = null;
   private javax.swing.JMenuItem jMenuItemEdit = null;
   private javax.swing.JMenuItem jMenuItemDelete = null;
   
	/**
	 * Constructor for ImagePopup.
	 */
	public ImagePopup( com.cannontech.common.gui.util.CTICallBack caller_ )
	{
		super();
      caller = caller_;
      initialize();
	}

   private void initialize()
   {
    
      try 
      {
         setName("ImagePopup");
         add(getJMenuItemEdit(), getJMenuItemEdit().getName());
         add(getJMenuItemDelete(), getJMenuItemDelete().getName());

         initConnections();
      } 
      catch (java.lang.Throwable ivjExc) 
      {
         handleException(ivjExc);
      }
      
   }


   /**
    * Method to handle events for the ActionListener interface.
    * @param e java.awt.event.ActionEvent
    */
   public void actionPerformed(java.awt.event.ActionEvent e) 
   {
      if (e.getSource() == getJMenuItemDelete()) 
         jButtonActionPerformed_Delete(e);
      if (e.getSource() == getJMenuItemEdit()) 
         jButtonActionPerformed_Edit(e);
   }
   
   
   private void jButtonActionPerformed_Delete( java.awt.event.ActionEvent ev )
   {
      String stateGroup = 
            DaoFactory.getYukonImageDao().yukonImageUsage( getSelectedLiteImage().getImageID() );

      if( stateGroup != null )
      {
         javax.swing.JOptionPane.showMessageDialog( 
               this,
               "The selected image is used by the State Group named '" + stateGroup + "'",
               "Image Deletion Failed",
               javax.swing.JOptionPane.WARNING_MESSAGE );

         return;
      }
      
      //tell our call back action to execute   
      caller.ctiCallBackAction( new java.beans.PropertyChangeEvent(
            this,
            "DeleteChange",
            getSelectedLiteImage(),
            getSelectedLiteImage()) );      
   }
   
   private void jButtonActionPerformed_Edit( java.awt.event.ActionEvent ev )
   {
      YukonImagePropertyPanel panel = new YukonImagePropertyPanel();
      panel.setSelectedName( getSelectedLiteImage().getImageName() );
      panel.setSelectedCategory( getSelectedLiteImage().getImageCategory() );

      OkCancelDialog d = new OkCancelDialog(SwingUtil.getParentFrame(this), "Image Properties", true, panel);

      d.setSize(350, 200);
      d.setLocationRelativeTo( this );
      d.show();

   
      if( d.getButtonPressed() == d.OK_PRESSED )
      {
         
         try
         {         
            getSelectedLiteImage().setImageName( panel.getSelectedName() );
            getSelectedLiteImage().setImageCategory( panel.getSelectedCategory() );
            
            //update the image
            com.cannontech.database.Transaction.createTransaction(
               com.cannontech.database.Transaction.UPDATE,
               com.cannontech.database.data.lite.LiteFactory.createDBPersistent(getSelectedLiteImage())).execute();
         }
         catch( Exception e )
         {
            com.cannontech.clientutils.CTILogger.error("Unable to update the image named '" + 
                  getSelectedLiteImage().getImageName() + "'.", e );
         }
                  

         //tell our parent it may have changed
         caller.ctiCallBackAction( new java.beans.PropertyChangeEvent(
                  this, 
                  "RefreshPanelWithLite", 
                  getSelectedLiteImage(), 
                  getSelectedLiteImage()) );
      }
                 
   }
   
   private void handleException(java.lang.Throwable exception) 
   {   
      /* Uncomment the following lines to print uncaught exceptions to stdout */
      com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------" + this.getClass());
      com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
   }


   private void initConnections() throws java.lang.Exception 
   {
   
      getJMenuItemDelete().addActionListener(this);
      getJMenuItemEdit().addActionListener(this);
   }

	/**
	 * Returns the jMenuItemDelete.
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemDelete()
	{
      if( jMenuItemDelete == null )
      {
         jMenuItemDelete = new javax.swing.JMenuItem();
         jMenuItemDelete.setName("DeleteButton");
         jMenuItemDelete.setText("Delete");
         jMenuItemDelete.setMnemonic('d');
      }
         
		return jMenuItemDelete;
	}

	/**
	 * Returns the jMenuItemEdit.
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemEdit()
	{
      if( jMenuItemEdit == null )
      {
         jMenuItemEdit = new javax.swing.JMenuItem();
         jMenuItemEdit.setName("EditButton");
         jMenuItemEdit.setText("Edit...");
         jMenuItemEdit.setMnemonic('e');
      }

		return jMenuItemEdit;
	}

	/**
	 * Returns the selectedLiteImage.
	 * @return LiteYukonImage
	 */
	public LiteYukonImage getSelectedLiteImage()
	{
		return selectedLiteImage;
	}

	/**
	 * Sets the selectedLiteImage.
	 * @param selectedLiteImage The selectedLiteImage to set
	 */
	public void setSelectedLiteImage(LiteYukonImage selectedLiteImage)
	{
		this.selectedLiteImage = selectedLiteImage;
      
      getJMenuItemDelete().setEnabled( selectedLiteImage != null );
      getJMenuItemEdit().setEnabled( selectedLiteImage != null );
	}

}
