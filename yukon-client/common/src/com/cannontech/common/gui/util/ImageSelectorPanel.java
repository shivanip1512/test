package com.cannontech.common.gui.util;

// here is a hole in the head!
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

// custom hole!

// and another customer hole!
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.cannontech.clientutils.CTILogger;

/**
 * 5
 * Displays an array of images and allows 
 * the user to select one of them.
 * 
 * @author alauinger
 */
public class ImageSelectorPanel extends JPanel {

	// Set up some borders to (re)-use
	private static Border backgroundBorder =
		new MatteBorder(4, 4, 4, 4, Color.WHITE);
	private static Border highlightBorder =
		new MatteBorder(4, 4, 4, 4, Color.RED);

	private static Border unselectedBorder =
		new CompoundBorder(backgroundBorder, backgroundBorder);
	private static Border selectedBorder =
		new CompoundBorder(highlightBorder, backgroundBorder);

	// key = JLabel, value = Image
	private HashMap labelImageMap = new HashMap();

	// key = Image, value = JLabel
	private HashMap imageLabelMap = new HashMap();

	// currently selected JLabel
	private JLabel selectedLabel;

	class ImageMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			JLabel imageLabel = (JLabel) me.getSource();
			selectLabel(imageLabel);
		}
	}

	public ImageSelectorPanel(Image[] images) {
		setImages(images);
	}

   protected ImageSelectorPanel() {
      super();
   }
   
	/**
	 * Returns the selectedImage.
	 * @return Image
	 */
	public Image getSelectedImage() {
		return (Image) labelImageMap.get(selectedLabel);
	}

	/**
	 * Sets the selected image.
	 * @param i
	 */
	public void setSelectedImage(Image i) {
		JLabel imageLabel = (JLabel) imageLabelMap.get(i);
		selectLabel(imageLabel);
	}

	protected void setImages(Image[] images) {

      removeAll();  //just in case we have some images
		setBackground(Color.WHITE);

		for (int i = 0; i < images.length; i++) {
			Image image = images[i];
			JLabel imageLabel = new JLabel(new ImageIcon(image));
			imageLabel.setBorder(unselectedBorder);
			labelImageMap.put(imageLabel, image);
			imageLabelMap.put(image, imageLabel);
			imageLabel.addMouseListener(new ImageMouseAdapter());
			add(imageLabel);
		}
	}

	/**
	 * Updates the currently selected JLabel
	 * @param label
	 */
	private void selectLabel(final JLabel label) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (selectedLabel != null) {
					selectedLabel.setBorder(unselectedBorder);
				}
				label.setBorder(selectedBorder);
				selectedLabel = label;
			}
		});

	}

	public static void main(String[] args) {
		File dir = new File("c:/esub-demo/images");
		File[] all = dir.listFiles();
		Image[] images = new Image[all.length];

		for (int i = 0; i < all.length; i++) {
			images[i] = Toolkit.getDefaultToolkit().getImage(all[i].getPath());
		}

		ImageSelectorPanel p = new ImageSelectorPanel(images);
		//p.setSelectedImage(images[0]);
		JFrame f = new JFrame();
		f.getContentPane().add(p);
		f.setSize(800, 600);
		f.show();
	}
}
