package com.cannontech.common.gui.util;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Displays any number of categories of images and allows
 * the user to select one.
 * 
 * @author alauinger
 */
public class CategoryImageChooserPanel extends JPanel implements ListSelectionListener {

	private CategoryImageModel model;
	
	//remember these to do the switching cards
	//based on the selection in the list
	private JList categoryList;	
	private JPanel imageCards;
	private CardLayout cardLayout;
		
	// key = Category (String), value = ImageSelectorPanel
	private HashMap categoryPanelMap = new HashMap();
	
	// currently selected label, can be used to lookup category
	private JLabel selectedLabel;
	
	/**
	 * Create a CategoryImageChooserPanel given a model.
	 * @param model
	 */
	public CategoryImageChooserPanel(CategoryImageModel model) {
		initialize(model);
	}
	
	/**
	 * Returns the selected image.
	 * @return Image
	 */
	public Image getSelectedImage() {
		String category = getSelectedCategory();
		ImageSelectorPanel p = (ImageSelectorPanel) categoryPanelMap.get(category);
		return p.getSelectedImage();
	}
	
	/**
	 * Returns the selected category.
	 * @return String
	 */
	public String getSelectedCategory() {
		return model.getCategories()[ categoryList.getSelectedIndex()];
	}
	
	private void initialize(CategoryImageModel model) {
		this.model = model;
		String[] categories = model.getCategories();
		
		//left panel
		categoryList = new JList(categories);		
		categoryList.setSelectedIndex(0);
		
		categoryList.addListSelectionListener(this);
		JScrollPane categoryScrollPane = new JScrollPane(categoryList);
		
		//right panel		
		this.imageCards = new JPanel();
		this.cardLayout = new CardLayout();
		imageCards.setLayout(cardLayout);
		
		for( int i = 0; i < categories.length; i++ ) {
			Image[] iArr = model.getImages(categories[i]);
			ImageSelectorPanel isp = new ImageSelectorPanel(iArr);					
			imageCards.add(isp, categories[i]);
			categoryPanelMap.put(categories[i], isp);
		}
		imageCards.setPreferredSize(new Dimension(300,200));		
		//JScrollPane imageScrollPane = new JScrollPane(imageCards);
			
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				
		splitPane.setLeftComponent(categoryScrollPane);
		splitPane.setRightComponent(imageCards);
		
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);			
	}
	
	public static void main(String[] args) {
	
		final CategoryImageModel model = new CategoryImageModel();	
		final HashMap imageFileMap = new HashMap();
	
		File dir = new File("c:/eclipse/workspace/esub-web/esub-demo/images/Buttons");
		File[] files = dir.listFiles();
		Image[] images = new Image[files.length];
		
		
			
		for( int i = 0; i < files.length; i++ ) {
			images[i] = Toolkit.getDefaultToolkit().getImage(files[i].getPath());
			imageFileMap.put(images[i], files[i]);					
		}
		
		model.addCategory(dir.getName(), images);
			
		dir = new File("c:/eclipse/workspace/esub-web/esub-demo/images/OnelineGraphics");		
		files = dir.listFiles();
		images = new Image[files.length];
		
		for( int i = 0; i < files.length; i++ ) {
			images[i] = Toolkit.getDefaultToolkit().getImage(files[i].getPath());
			imageFileMap.put(images[i], files[i]);					
		}
		
		model.addCategory(dir.getName(), images);
		
		final CategoryImageChooserPanel p = new CategoryImageChooserPanel(model);
		
		JFrame f = new JFrame();
		
		f.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.out.println("Category: " + p.getSelectedCategory() );
				System.out.println("Image: " + p.getSelectedImage());
				System.out.println("Image: " + imageFileMap.get(p.getSelectedImage()));
				System.exit(0);
			}
		});
		
		f.getContentPane().add(p); 

		f.setSize(800,600);
		f.show();				
	}
	
	/**
	 * Makes the correct image panel visible in response to a 
	 * list selection event.
	 * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {		
		String selectedCategory = model.getCategories()[ ((JList)e.getSource()).getSelectedIndex()];
		cardLayout.show(imageCards, selectedCategory);
	}

}
