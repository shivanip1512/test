package com.cannontech.dbeditor.wizard.tags;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.ColorComboBoxCellRenderer;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.tags.Tag;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (1/15/2004 2:31:36 PM)
 * @author: 
 */
public class TagsBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements DataInputPanelListener{
	private javax.swing.JTextField ivjLevelTextField = null;
	private javax.swing.JLabel ivjTagLevelLabel = null;
	private javax.swing.JLabel ivjTagNameLabel = null;
	private javax.swing.JComboBox<YukonColorPalette> ivjColorComboBox = null;
	private javax.swing.JLabel ivjColorLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjImageButton = null;
	private javax.swing.JLabel ivjImageLabel = null;
	private javax.swing.JCheckBox ivjInhibitCheckBox = null;
	private javax.swing.JTextField ivjNameTexField = null;
	
    private YukonColorPalette[] tagColors = new YukonColorPalette[] {
            YukonColorPalette.GREEN,
            YukonColorPalette.RED,  // do not use WINE in this enum, as it's text will conflict. Both use "Red".
            YukonColorPalette.WHITE,
            YukonColorPalette.YELLOW,
            YukonColorPalette.BLUE,
            YukonColorPalette.TEAL,
            YukonColorPalette.BLACK,
            YukonColorPalette.ORANGE,
            YukonColorPalette.SAGE,
            YukonColorPalette.GRAY,
            YukonColorPalette.PURPLE
            };

    class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == TagsBasePanel.this.getImageButton())
                TagsBasePanel.this.imageButton_ActionPerformed(e);
            if (e.getSource() == TagsBasePanel.this.getInhibitCheckBox())
                TagsBasePanel.this.fireInputUpdate();
        };

        public void caretUpdate(javax.swing.event.CaretEvent e) {
            if (e.getSource() == TagsBasePanel.this.getNameTextField() ||
                e.getSource() == TagsBasePanel.this.getLevelTextField())
                TagsBasePanel.this.fireInputUpdate();
        };

        public void itemStateChanged(java.awt.event.ItemEvent e) {
            if (e.getSource() == TagsBasePanel.this.getColorComboBox())
                TagsBasePanel.this.fireInputUpdate();
        };
    };
/**
 * TagsBasePanel constructor comment.
 */
public TagsBasePanel() {
	super();
	initialize();
}
/**
 * Return the ColorComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox<YukonColorPalette> getColorComboBox() {
	if (ivjColorComboBox == null) {
		try {
			ivjColorComboBox = new javax.swing.JComboBox<YukonColorPalette>();
			ivjColorComboBox.setName("ColorComboBox");
			// user code begin {1}
			ivjColorComboBox.setRenderer(new ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorComboBox;
}
/**
 * Return the ColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorLabel() {
	if (ivjColorLabel == null) {
		try {
			ivjColorLabel = new javax.swing.JLabel();
			ivjColorLabel.setName("ColorLabel");
			ivjColorLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjColorLabel.setText("Color: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorLabel;
}
/**
 * Return the ImageButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getImageButton() {
	if (ivjImageButton == null) {
		try {
			ivjImageButton = new javax.swing.JButton();
			ivjImageButton.setName("ImageButton");
			ivjImageButton.setText("Image");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageButton;
}
/**
 * Return the ImageLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getImageLabel() {
	if (ivjImageLabel == null) {
		try {
			ivjImageLabel = new javax.swing.JLabel();
			ivjImageLabel.setName("ImageLabel");
			ivjImageLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjImageLabel.setText("Image Select: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageLabel;
}
/**
 * Return the InhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getInhibitCheckBox() {
	if (ivjInhibitCheckBox == null) {
		try {
			ivjInhibitCheckBox = new javax.swing.JCheckBox();
			ivjInhibitCheckBox.setName("InhibitCheckBox");
			ivjInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjInhibitCheckBox.setText("Inhibit Control");
			ivjInhibitCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjInhibitCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			getInhibitCheckBox().setSelected(true);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInhibitCheckBox;
}
/**
 * Return the LevelTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLevelTextField() {
	if (ivjLevelTextField == null) {
		try {
			ivjLevelTextField = new javax.swing.JTextField();
			ivjLevelTextField.setName("LevelTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLevelTextField;
}
/**
 * Return the NameJTexField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTexField == null) {
		try {
			ivjNameTexField = new javax.swing.JTextField();
			ivjNameTexField.setName("NameTexField");
			ivjNameTexField.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_60));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTexField;
}
/**
 * Return the TagLevelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTagLevelLabel() {
	if (ivjTagLevelLabel == null) {
		try {
			ivjTagLevelLabel = new javax.swing.JLabel();
			ivjTagLevelLabel.setName("TagLevelLabel");
			ivjTagLevelLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTagLevelLabel.setText("Tag Level: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTagLevelLabel;
}
/**
 * Return the TagNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTagNameLabel() {
	if (ivjTagNameLabel == null) {
		try {
			ivjTagNameLabel = new javax.swing.JLabel();
			ivjTagNameLabel.setName("TagNameLabel");
			ivjTagNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTagNameLabel.setText("Tag Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTagNameLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	com.cannontech.database.db.tags.Tag aTag = (com.cannontech.database.db.tags.Tag)o;
	
	if(o == null)
		aTag = new Tag();
	
	aTag.setTagName(getNameTextField().getText());
	aTag.setTagLevel(Integer.valueOf(getLevelTextField().getText()));
	if(getInhibitCheckBox().isSelected())
		aTag.setInhibit(Character.valueOf('Y'));
	else
		aTag.setInhibit(Character.valueOf('N'));
		
	aTag.setColorID(Integer.valueOf(getColorComboBox().getSelectedIndex()));	

   	Integer yukImgId = 
				(getImageButton().getClientProperty("LiteYukonImage") == null)
				? Integer.valueOf(com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID)
				: Integer.valueOf( ((LiteYukonImage)getImageButton().getClientProperty("LiteYukonImage")).getImageID() );
				
	aTag.setImageID(yukImgId);
      
   	return aTag;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
public void imageButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	javax.swing.JButton button = (javax.swing.JButton)actionEvent.getSource();
	
   	LiteYukonImage[] yukonImages = null;
   	IDatabaseCache cache = 
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
    
   	synchronized (cache) {
        List<LiteYukonImage> imgList = new ArrayList<>();
        
        for (LiteYukonImage image : cache.getImages().values()) {
            if (image.getImageValue() != null) {
                imgList.add(image);
            }
        }
        yukonImages = imgList.toArray(new LiteYukonImage[imgList.size()]);
    }

   	final javax.swing.JDialog d = new javax.swing.JDialog();

   	com.cannontech.common.gui.YukonImagePanel yPanel =
		new com.cannontech.common.gui.YukonImagePanel( yukonImages )
   	{
	  	public void disposePanel()
	  	{
			d.setVisible(false);
	  	}
   	};
  
  	yPanel.addDataInputPanelListener( this );
	
	//get our selected image id with the JButton
   	LiteYukonImage liteImg = (LiteYukonImage)button.getClientProperty("LiteYukonImage");
   	if( liteImg != null )
		yPanel.setSelectedLiteYukonImage( liteImg );
         
   	d.setModal( true );      
   	d.setTitle("Image Selection");
   	d.getContentPane().add( yPanel );
   	d.setSize(800, 600);

   	//set the location of the dialog to the center of the screen
   	d.setLocation( (getToolkit().getScreenSize().width - d.getSize().width) / 2,
				  (getToolkit().getScreenSize().height - d.getSize().height) / 2);
   	d.show();   

   	if( yPanel.getReturnResult() == yPanel.OK_OPTION )
   	{
		setImageButton( button, yPanel.getSelectedImageIcon(), yPanel.getSelectedLiteImage() );
   	}

	fireInputUpdate();
	return;
}

private void setImageButton(javax.swing.JButton button, javax.swing.ImageIcon img, LiteYukonImage liteYuk )
{
   if( img == null || liteYuk == null )
   {
	  button.setText("Image...");
	  button.setIcon( null );
      
	  liteYuk = LiteYukonImage.NONE_IMAGE;
   }
	else
   {
	  //strange, this will preserve the size of the button
	  int width = (int)button.getPreferredSize().getWidth() - 12;
      
	  //strange, this will preserve the size of the button
	  int height = (int)button.getPreferredSize().getHeight() - 9;
   
      
	  //javax.swing.ImageIcon ico = new javax.swing.ImageIcon(
	  img.setImage(
		 img.getImage().getScaledInstance( 
			   width,
			   height,
			   java.awt.Image.SCALE_AREA_AVERAGING ) );
   
	  button.setText(null);
	  button.setIcon( img );
   }

   //store our selected image id with the JButton
   button.putClientProperty(
		 "LiteYukonImage",
		 (LiteYukonImage)liteYuk );

}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(ivjEventHandler);
	getLevelTextField().addCaretListener(ivjEventHandler);
	getImageButton().addActionListener(ivjEventHandler);
	getColorComboBox().addItemListener(ivjEventHandler);
	getInhibitCheckBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
        for (YukonColorPalette color : tagColors) {
            getColorComboBox().addItem(color);
        }
		// user code end
		setName("TagWizardPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsTagNameLabel = new java.awt.GridBagConstraints();
		constraintsTagNameLabel.gridx = 1; constraintsTagNameLabel.gridy = 1;
		constraintsTagNameLabel.ipadx = 6;
		constraintsTagNameLabel.ipady = 3;
		constraintsTagNameLabel.insets = new java.awt.Insets(14, 16, 8, 1);
		add(getTagNameLabel(), constraintsTagNameLabel);

		java.awt.GridBagConstraints constraintsNameTexField = new java.awt.GridBagConstraints();
		constraintsNameTexField.gridx = 2; constraintsNameTexField.gridy = 1;
		constraintsNameTexField.gridwidth = 3;
		constraintsNameTexField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTexField.weightx = 1.0;
		constraintsNameTexField.ipadx = 198;
		constraintsNameTexField.ipady = 4;
		constraintsNameTexField.insets = new java.awt.Insets(14, 1, 6, 51);
		add(getNameTextField(), constraintsNameTexField);

		java.awt.GridBagConstraints constraintsTagLevelLabel = new java.awt.GridBagConstraints();
		constraintsTagLevelLabel.gridx = 1; constraintsTagLevelLabel.gridy = 2;
		constraintsTagLevelLabel.ipadx = 8;
		constraintsTagLevelLabel.ipady = 3;
		constraintsTagLevelLabel.insets = new java.awt.Insets(7, 16, 12, 1);
		add(getTagLevelLabel(), constraintsTagLevelLabel);

		java.awt.GridBagConstraints constraintsLevelTextField = new java.awt.GridBagConstraints();
		constraintsLevelTextField.gridx = 2; constraintsLevelTextField.gridy = 2;
		constraintsLevelTextField.gridwidth = 2;
		constraintsLevelTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLevelTextField.weightx = 1.0;
		constraintsLevelTextField.ipadx = 61;
		constraintsLevelTextField.ipady = 4;
		constraintsLevelTextField.insets = new java.awt.Insets(7, 1, 10, 38);
		add(getLevelTextField(), constraintsLevelTextField);

		java.awt.GridBagConstraints constraintsInhibitCheckBox = new java.awt.GridBagConstraints();
		constraintsInhibitCheckBox.gridx = 4; constraintsInhibitCheckBox.gridy = 2;
		constraintsInhibitCheckBox.ipadx = 23;
		constraintsInhibitCheckBox.ipady = -5;
		constraintsInhibitCheckBox.insets = new java.awt.Insets(7, 1, 12, 52);
		add(getInhibitCheckBox(), constraintsInhibitCheckBox);

		java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
		constraintsColorLabel.gridx = 1; constraintsColorLabel.gridy = 3;
		constraintsColorLabel.ipadx = 12;
		constraintsColorLabel.ipady = -5;
		constraintsColorLabel.insets = new java.awt.Insets(12, 16, 11, 26);
		add(getColorLabel(), constraintsColorLabel);

		java.awt.GridBagConstraints constraintsColorComboBox = new java.awt.GridBagConstraints();
		constraintsColorComboBox.gridx = 1; constraintsColorComboBox.gridy = 3;
		constraintsColorComboBox.gridwidth = 3;
		constraintsColorComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsColorComboBox.weightx = 1.0;
		constraintsColorComboBox.ipadx = 4;
		constraintsColorComboBox.insets = new java.awt.Insets(10, 68, 4, 2);
		add(getColorComboBox(), constraintsColorComboBox);

		java.awt.GridBagConstraints constraintsImageLabel = new java.awt.GridBagConstraints();
		constraintsImageLabel.gridx = 1; constraintsImageLabel.gridy = 4;
		constraintsImageLabel.gridwidth = 2;
		constraintsImageLabel.ipadx = 9;
		constraintsImageLabel.ipady = 1;
		constraintsImageLabel.insets = new java.awt.Insets(5, 16, 210, 0);
		add(getImageLabel(), constraintsImageLabel);

		java.awt.GridBagConstraints constraintsImageButton = new java.awt.GridBagConstraints();
		constraintsImageButton.gridx = 3; constraintsImageButton.gridy = 4;
		constraintsImageButton.ipadx = 16;
		constraintsImageButton.insets = new java.awt.Insets(5, 0, 205, 1);
		add(getImageButton(), constraintsImageButton);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TagsBasePanel aTagsBasePanel;
		aTagsBasePanel = new TagsBasePanel();
		frame.setContentPane(aTagsBasePanel);
		frame.setSize(aTagsBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	Tag youAreIt;
	
	if( o != null )
		youAreIt = (Tag)o;
	else
		youAreIt = new Tag();
	
	String name = youAreIt.getTagName();
	if( name != null )
	{
		getNameTextField().setText(name);
	}
	
	Integer temp = youAreIt.getTagLevel();
	if( temp != null )
	{
		getLevelTextField().setText( temp.toString() );
		temp = null;
	}		
	
	Character elChar = youAreIt.getInhibit();
	
	if( elChar != null )
	{
		if(elChar.compareTo(Character.valueOf('N')) == 0)
			getInhibitCheckBox().setSelected(false);
		elChar = null;
	}
	
	temp = youAreIt.getColorID();
	if( temp != null )
	{
		getColorComboBox().setSelectedIndex(temp.intValue() );
	}

	//grab that image for the button
	if(youAreIt.getImageID() != null)
	{
		int yukImgID = youAreIt.getImageID().intValue();
		if( yukImgID != com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID )
		{
			IDatabaseCache cache = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
          
			LiteYukonImage liteYukImg = cache.getImages().get(yukImgID);
         
			//be sure we have found a matching LiteYukonImage
			if( liteYukImg != null )
			{
				setImageButton( 
					getImageButton(),
					new javax.swing.ImageIcon(liteYukImg.getImageValue()),
					liteYukImg );
			}
		}
	}
}
	



public void inputUpdate(PropertyPanelEvent event) {
	// Auto-generated method stub

}

}
