package com.cannontech.common.gui;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.image.ImageChooser;
import com.cannontech.common.gui.util.DialogUtil;
import com.cannontech.common.gui.wizard.state.GroupStateNamePanel;
import com.cannontech.common.gui.wizard.state.ImagePopup;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class YukonImagePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.CTICallBack, java.awt.event.ActionListener, javax.swing.event.ListSelectionListener, javax.swing.event.PopupMenuListener {

   class ImageMouseAdapter extends com.cannontech.clientutils.popup.PopUpMenuShower //java.awt.event.MouseAdapter
   {
      public ImageMouseAdapter( javax.swing.JPopupMenu popupMenu ) 
      {
         super(popupMenu);
      }
      
      @Override
    public void mousePressed(java.awt.event.MouseEvent me) 
      {
         javax.swing.JLabel imageLabel = (javax.swing.JLabel)me.getSource();
         
         if( me.isControlDown() ) {
            unselectLabel( imageLabel );
        } else {
            selectLabel(imageLabel, false);
        }
      }
   }
   public static final int CANCEL_OPTION = 0;
   public static final int OK_OPTION = 1;
   private int retResult = CANCEL_OPTION;
   private static final String ALL_CATEGORIES_STRING = "(All Images)";
   // Set up some borders to (re)-use
   private static javax.swing.border.Border backgroundBorder =
      new javax.swing.border.MatteBorder(4, 4, 4, 4, java.awt.Color.white);
   private static javax.swing.border.Border highlightBorder =
      new javax.swing.border.MatteBorder(4, 4, 4, 4, java.awt.Color.red);
   private static javax.swing.border.Border unselectedBorder =
      new javax.swing.border.CompoundBorder(backgroundBorder, backgroundBorder);
   private static javax.swing.border.Border selectedBorder =
      new javax.swing.border.CompoundBorder(highlightBorder, backgroundBorder);
   private ImagePopup imagePopupMenu = null;
   // currently selected JLabel
   private javax.swing.JLabel selectedLabel;
	private javax.swing.JButton ivjJButtonAddImages = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	private javax.swing.JScrollPane ivjJScrollPaneImages = null;
	private javax.swing.JPanel ivjJPanelImages = null;
	private java.awt.FlowLayout ivjJPanelImagesFlowLayout = null;
	private javax.swing.JList ivjJListCategories = null;
	private javax.swing.JLabel ivjJLabelCategory = null;
	private javax.swing.JScrollPane ivjJScrollPaneCategory = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonImagePanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/21/2002 11:41:09 AM)
 * @param images java.awt.Image[]
 */
public YukonImagePanel(LiteYukonImage[] images) 
{
	this();
   setUpImages( images );
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonAddImages()) {
        connEtoC1(e);
    }
	if (e.getSource() == getJButtonOk()) {
        connEtoC2(e);
    }
	if (e.getSource() == getJButtonCancel())
     {
        connEtoC3(e);
	// user code begin {2}
	// user code end
    }
}


/**
 * connEtoC1:  (JButtonAddImages.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonAddImages_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jButtonAddImages_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JListCategories.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> YukonImagePanel.jListCategories_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jListCategories_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


@Override
public void ctiCallBackAction( java.beans.PropertyChangeEvent pEvent )
{
   if( pEvent.getSource() == getImagePopupMenu() )
   {   	

		if( "DeleteChange".equalsIgnoreCase(pEvent.getPropertyName()) )
		{
			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_DELETE,
						LiteFactory.createDBPersistent( (LiteYukonImage)pEvent.getNewValue()) ) );	

			setUpImages(null);
			//setSelectedLiteYukonImage( null );
			unselectLabel( selectedLabel );
		}
      else if( "RefreshPanelWithLite".equalsIgnoreCase(pEvent.getPropertyName()) )
      {         
			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_UPDATE,
						LiteFactory.createDBPersistent( (LiteYukonImage)pEvent.getNewValue()) ) );

			setUpImages(null);      
         setSelectedLiteYukonImage( (LiteYukonImage)pEvent.getNewValue() );
      }
      
   }

}

private void disposePanelInternal() {
    getJPanelImages().removeAll();
    disposePanel();
}

/* Override me to do things when this panel is dismissed */
protected void disposePanel()
{
    
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2002 11:41:09 AM)
 */
private void doImagePanelLayout()
{
   getJPanelImages().revalidate();
   getJPanelImages().repaint();         
   
   javax.swing.SwingUtilities.invokeLater( new Runnable()
   {   
      @Override
    public void run()
      {
         java.awt.Component currComp = null;
         int currPoint = 0;
         int nmembers = getJPanelImages().getComponentCount();
         
         //find the lowest component so we can set our scrollbar correctly
         for (int i = 0 ; i < nmembers ; i++) 
         {                        
            java.awt.Component m = getJPanelImages().getComponent(i);         
            int mPoint = (int)m.getLocation().getY() + m.getHeight();
            
            if( m.isVisible()
                && (currComp == null
                    || mPoint >= currPoint) ) 
            {
               currComp = m;            
               currPoint = mPoint;
            }
            
         }
         

         if( currComp != null ) {
            getJPanelImages().setPreferredSize( new java.awt.Dimension(
                  (int)getJPanelImages().getPreferredSize().getWidth(),
                  currPoint + getJPanelImagesFlowLayout().getHgap() ) );
        }

         getJPanelImages().revalidate();
         getJPanelImages().repaint();
      }
   });

   
}

	/**
	 * Returns the imagePopupMenu.
	 * @return ImagePopup
	 */
	private ImagePopup getImagePopupMenu()
	{
      if( imagePopupMenu == null )
      {
         imagePopupMenu = new ImagePopup(this);
         
      }
      
		return imagePopupMenu;
	}


/**
 * Return the JButtonAddImages property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAddImages() {
	if (ivjJButtonAddImages == null) {
		try {
			ivjJButtonAddImages = new javax.swing.JButton();
			ivjJButtonAddImages.setName("JButtonAddImages");
			ivjJButtonAddImages.setMnemonic('a');
			ivjJButtonAddImages.setText("Add Image(s)...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAddImages;
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}


/**
 * Return the JLabelSortBy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCategory() {
	if (ivjJLabelCategory == null) {
		try {
			ivjJLabelCategory = new javax.swing.JLabel();
			ivjJLabelCategory.setName("JLabelCategory");
			ivjJLabelCategory.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCategory.setText("Category:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCategory;
}

/**
 * Return the JListCategories property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListCategories() {
	if (ivjJListCategories == null) {
		try {
			ivjJListCategories = new javax.swing.JList();
			ivjJListCategories.setName("JListCategories");
			ivjJListCategories.setBounds(0, 0, 102, 153);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListCategories;
}

/**
 * Return the JPanelButtons property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setBorder(new com.cannontech.common.gui.util.TitleBorder());
			ivjJPanelButtons.setLayout(getJPanelButtonsFlowLayout());
			getJPanelButtons().add(getJButtonAddImages(), getJButtonAddImages().getName());
			getJPanelButtons().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelButtons().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}


/**
 * Return the JPanelButtonsFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelButtonsFlowLayout() {
	java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelButtonsFlowLayout = new java.awt.FlowLayout();
		ivjJPanelButtonsFlowLayout.setHgap(15);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelButtonsFlowLayout;
}


/**
 * Return the JPanelImages property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelImages() {
	if (ivjJPanelImages == null) {
		try {
			ivjJPanelImages = new javax.swing.JPanel();
			ivjJPanelImages.setName("JPanelImages");
			ivjJPanelImages.setPreferredSize(new java.awt.Dimension(160, 120));
			ivjJPanelImages.setLayout(getJPanelImagesFlowLayout());
			ivjJPanelImages.setBounds(0, 0, 160, 120);
			ivjJPanelImages.setMaximumSize(new java.awt.Dimension(160, 32767));
			ivjJPanelImages.setMinimumSize(new java.awt.Dimension(160, 120));
			// user code begin {1}
         
         ivjJPanelImages.setBackground( java.awt.Color.white );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelImages;
}


/**
 * Return the JPanelImagesFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelImagesFlowLayout() {
	java.awt.FlowLayout ivjJPanelImagesFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelImagesFlowLayout = new java.awt.FlowLayout();
		ivjJPanelImagesFlowLayout.setVgap(8);
		ivjJPanelImagesFlowLayout.setHgap(8);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelImagesFlowLayout;
}


/**
 * Return the JScrollPaneCategory property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneCategory() {
	if (ivjJScrollPaneCategory == null) {
		try {
			ivjJScrollPaneCategory = new javax.swing.JScrollPane();
			ivjJScrollPaneCategory.setName("JScrollPaneCategory");
			getJScrollPaneCategory().setViewportView(getJListCategories());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneCategory;
}


/**
 * Return the JScrollPaneImages property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneImages() {
	if (ivjJScrollPaneImages == null) {
		try {
			ivjJScrollPaneImages = new javax.swing.JScrollPane();
			ivjJScrollPaneImages.setName("JScrollPaneImages");
			ivjJScrollPaneImages.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneImages.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneImages().setViewportView(getJPanelImages());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneImages;
}


public int getReturnResult()
{
   return retResult;
}


public javax.swing.ImageIcon getSelectedImageIcon()
{
   if( selectedLabel != null ) {
    return (javax.swing.ImageIcon)selectedLabel.getIcon();
} else {
    return null;
}
}


/* Return a null image if there is nothing selected */
public LiteYukonImage getSelectedLiteImage() 
{
   if( selectedLabel == null ) {
    return null;
} else {
    return (LiteYukonImage)selectedLabel.getClientProperty("YukonImage");
}
}


/**
 * getValue method comment.
 */
@Override
public Object getValue(Object val) 
{

	return null;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.error("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( "Throwable caught", exception );
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
   
   getImagePopupMenu().addPopupMenuListener( this );      
   
	// user code end
	getJButtonAddImages().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJButtonCancel().addActionListener(this);
	getJListCategories().addListSelectionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("YukonImageEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(462, 388);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.gridwidth = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJPanelButtons.ipadx = 138;
		constraintsJPanelButtons.insets = new java.awt.Insets(3, 5, 5, 6);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJScrollPaneImages = new java.awt.GridBagConstraints();
		constraintsJScrollPaneImages.gridx = 2; constraintsJScrollPaneImages.gridy = 2;
		constraintsJScrollPaneImages.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneImages.weightx = 2.0;
		constraintsJScrollPaneImages.weighty = 2.0;
		constraintsJScrollPaneImages.ipadx = 319;
		constraintsJScrollPaneImages.ipady = 287;
		constraintsJScrollPaneImages.insets = new java.awt.Insets(1, 3, 2, 6);
		add(getJScrollPaneImages(), constraintsJScrollPaneImages);

		java.awt.GridBagConstraints constraintsJLabelCategory = new java.awt.GridBagConstraints();
		constraintsJLabelCategory.gridx = 1; constraintsJLabelCategory.gridy = 1;
		constraintsJLabelCategory.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCategory.ipadx = 31;
		constraintsJLabelCategory.ipady = -2;
		constraintsJLabelCategory.insets = new java.awt.Insets(6, 6, 0, 13);
		add(getJLabelCategory(), constraintsJLabelCategory);

		java.awt.GridBagConstraints constraintsJScrollPaneCategory = new java.awt.GridBagConstraints();
		constraintsJScrollPaneCategory.gridx = 1; constraintsJScrollPaneCategory.gridy = 2;
		constraintsJScrollPaneCategory.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJScrollPaneCategory.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneCategory.ipadx = 81;
		constraintsJScrollPaneCategory.ipady = 287;
		constraintsJScrollPaneCategory.insets = new java.awt.Insets(1, 6, 2, 3);
		add(getJScrollPaneCategory(), constraintsJScrollPaneCategory);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * Comment
 */
public void jButtonAddImages_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   ImageChooser.getInstance().setDialogTitle("Select Image File");
   ImageChooser.getInstance().setMultiSelectionEnabled(true);
	int res = ImageChooser.getInstance().showOpenDialog(this);

   if( res == javax.swing.JFileChooser.APPROVE_OPTION )
   {
      if(ImageChooser.getInstance().getSelectedFile() != null )
      {
         java.io.File[] files = ImageChooser.getInstance().getSelectedFiles();
      
         for( int i = 0; i < files.length; i++ )
         {
            com.cannontech.database.db.state.YukonImage dbImg = 
                  new com.cannontech.database.db.state.YukonImage();
            
            try
            {
               byte[] byteArray = new byte[ (int)files[i].length() ];
               new java.io.FileInputStream(files[i]).read( byteArray, 0, byteArray.length );
                              
               String name = files[i].getName();
                                       
               dbImg.setImageCategory( com.cannontech.common.util.CtiUtilities.STRING_NONE );  //not sure if this is right!
               dbImg.setImageName( (name.length() >= 80 ? name.substring(0,80) : name) );
               dbImg.setImageValue( byteArray );
               
               dbImg.setImageID( new Integer(dbImg.getNextImageID(
                     com.cannontech.database.PoolManager.getInstance().getConnection(
                              com.cannontech.common.util.CtiUtilities.getDatabaseAlias()))) );

               //insert the new image
//               com.cannontech.database.Transaction.createTransaction(
//                  com.cannontech.database.Transaction.INSERT, dbImg ).execute();
                  

					fireInputDataPanelEvent( 
						new PropertyPanelEvent(
									this,
									PropertyPanelEvent.EVENT_DB_INSERT,
									dbImg) );                  
            }
            catch( Exception e )
            {
               com.cannontech.clientutils.CTILogger.error( "Exception occurred when saving image file to database", e );
            }
         
         }
      }
      
   }

   //the null setting tells us to get a new list of YukonImages from the DB
   setUpImages( null );

	return;
}


/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   disposePanelInternal();

	return;
}


/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   retResult = OK_OPTION;
   disposePanelInternal();
   
	return;
}


/**
 * Comment
 */
public void jListCategories_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) 
{
   if( !listSelectionEvent.getValueIsAdjusting()
        && getJListCategories().getSelectedValue() != null )
   {
      //just in case, remove all the JLabels
      getJPanelImages().removeAll();

      IDatabaseCache cache = 
         com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
       
      synchronized( cache )
      {
         java.util.List imgList = cache.getAllYukonImages();
         String selCategory = getJListCategories().getSelectedValue().toString();

         //create and add the image as a JLabel   
         for( int i = 0; i < imgList.size(); i++ )
         {
            LiteYukonImage lImage = (LiteYukonImage)imgList.get(i);
            if( lImage.getImageValue() != null
                && ( selCategory.equalsIgnoreCase(ALL_CATEGORIES_STRING)
                     || lImage.getImageCategory().equalsIgnoreCase(selCategory)) )
            {
               createImageLabel( (LiteYukonImage)imgList.get(i) );
            }
            
         }
         
      }   

      //relayout our display
      doImagePanelLayout();
      
      setSelectedLiteYukonImage( getSelectedLiteImage() );
   }

	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupStateNamePanel aGroupStateNamePanel;
		aGroupStateNamePanel = new GroupStateNamePanel();
		frame.setContentPane(aGroupStateNamePanel);
		frame.setSize(aGroupStateNamePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}


   @Override
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e)
   {
   }


   @Override
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e)
   {  
   }


   @Override
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
   {
      
      if( e.getSource() == getImagePopupMenu() )
      {
         if( selectedLabel != null ) {
            getImagePopupMenu().setSelectedLiteImage(
                  (LiteYukonImage)selectedLabel.getClientProperty("YukonImage") );
        } else {
            getImagePopupMenu().setSelectedLiteImage(null);
        }

      }
         
   }


private void selectLabel(final javax.swing.JLabel label, final boolean scrollToLabel) 
{
   javax.swing.SwingUtilities.invokeLater(new Runnable() 
   {
      @Override
    public void run() 
      {
         if (selectedLabel != null) {
            selectedLabel.setBorder(unselectedBorder);
        }

         label.setBorder(selectedBorder);
         selectedLabel = label;
         
         if( scrollToLabel ) {
            getJScrollPaneImages().getViewport().scrollRectToVisible(
               new java.awt.Rectangle(label.getLocation(), label.getSize()) );
        }
      }
   });

}


public void setSelectedLiteYukonImage( LiteYukonImage liteImg )
{
   for( int i = 0; i < getJPanelImages().getComponentCount(); i++ )
   {
      if( !(getJPanelImages().getComponent(i) instanceof javax.swing.JLabel) ) {
        continue;
    }

      javax.swing.JLabel imgLabel = (javax.swing.JLabel)getJPanelImages().getComponent(i);
      LiteYukonImage labelImage = (LiteYukonImage)imgLabel.getClientProperty("YukonImage");

      if( labelImage != null && liteImg != null ) {
        if( labelImage.getImageID() == liteImg.getImageID() )
         {
            selectLabel( imgLabel, true );
            return;
         }
    }   
   }

}


protected void setUpImages( LiteYukonImage[] images )
{
   //just in case, remove all the JLabels
   getJPanelImages().removeAll();

   if( images == null )
   {
      IDatabaseCache cache = 
         com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
       
      synchronized( cache )
      {
         java.util.List imgList = cache.getAllYukonImages();
         images = new LiteYukonImage[ imgList.size() ];
   
         for( int i = 0; i < imgList.size(); i++ ) {
            if( ((LiteYukonImage)imgList.get(i)).getImageValue() != null ) {
                images[i] = (LiteYukonImage)imgList.get(i);
            }
        }
      }   
   }

   //put our images in order by their category
	java.util.Arrays.sort( images, com.cannontech.database.data.lite.LiteComparators.liteYukonImageCategoryComparator );
	java.util.Vector catVector = new java.util.Vector(images.length / 3);
	catVector.add( ALL_CATEGORIES_STRING );
	String currCategory = null;
	
   for (int i = 0; i < images.length; i++) 
   {
      LiteYukonImage image = images[i];
      
      //create and add the image as a JLabel
      createImageLabel( image );

      if( currCategory == null || !image.getImageCategory().equalsIgnoreCase(currCategory) )
      {
			currCategory = image.getImageCategory();

         if( !currCategory.equalsIgnoreCase(com.cannontech.common.util.CtiUtilities.STRING_NONE) ) {
            catVector.add( currCategory );
        }
		}
      
   }

  	getJListCategories().setListData( catVector );
   
   doImagePanelLayout();
}

   private void createImageLabel( LiteYukonImage image )
   {
      ImageIcon scaledImage = DialogUtil.scaleImage(new ImageIcon(image.getImageValue()), 20, 20);
      JLabel imageLabel = new JLabel( image.getImageName(), scaledImage,SwingConstants.CENTER);
      
      imageLabel.setHorizontalTextPosition( javax.swing.SwingConstants.CENTER );
      imageLabel.setVerticalAlignment( javax.swing.SwingConstants.BOTTOM );
      imageLabel.setVerticalTextPosition( javax.swing.SwingConstants.BOTTOM );
      
      imageLabel.setToolTipText("CTRL+CLICK to unselect image");
      imageLabel.setBorder(unselectedBorder);
      imageLabel.putClientProperty( "YukonImage", image );
      
      imageLabel.addMouseListener( new ImageMouseAdapter(getImagePopupMenu()) );

      getJPanelImages().add(imageLabel);
   }


   /**
    * setValue method comment.
    */
   @Override
public void setValue(Object val) {
   }


   private void unselectLabel(final javax.swing.JLabel label) 
   {
      if( label != selectedLabel ) {
        return;
    }
   
      javax.swing.SwingUtilities.invokeLater(new Runnable() 
      {
         @Override
        public void run()
         {
            if (selectedLabel != null) {
                selectedLabel.setBorder(unselectedBorder);
            }
   
            label.setBorder(unselectedBorder);
            selectedLabel = null;
         }
      });
   
   }


/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJListCategories())
     {
        connEtoC5(e);
	// user code begin {2}
	// user code end
    }
}
}