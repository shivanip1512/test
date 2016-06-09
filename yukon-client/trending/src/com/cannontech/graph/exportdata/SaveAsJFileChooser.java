package com.cannontech.graph.exportdata;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;

import com.cannontech.database.db.graph.GraphRenderers;

/**
 * Insert the type's description here.
 * Creation date: (8/21/2002 9:48:51 AM)
 * @author: 
 */
public class SaveAsJFileChooser extends javax.swing.JFileChooser implements com.cannontech.graph.GraphDefines, java.beans.PropertyChangeListener 
{
	private static java.io.File file = new java.io.File("C:/yukon/client/export/Chart.csv");
	
	private ExportDataFile eDataFile = null;
	/**
	 * SaveAsJFileChooser constructor comment.
	 */
	public SaveAsJFileChooser() {
		super();
	}
	/**
	 * SaveAsJFileChooser constructor comment.
	 * @param currentDirectory java.io.File
	 */
	
	public SaveAsJFileChooser(String currentDirectory, int viewType, Object expObj, String newFileName)
	{
		super(currentDirectory);
		
		eDataFile = new ExportDataFile(viewType, expObj, newFileName);
		initialize();
	}

	public SaveAsJFileChooser(String currentDirectory, int viewType, Object expObj, String newFileName, com.cannontech.graph.model.TrendModel tModel)
	{
		super(currentDirectory);

		eDataFile = new ExportDataFile( viewType, expObj, newFileName, tModel);
		initialize();
	}
	
	private String getCurrentPathAndFilename()
	{
        String pathName;
        if (getSelectedFile() != null) {
            pathName = getCurrentDirectory().getPath() + "/" + getSelectedFile().getName();
        } else {
            pathName = getCurrentDirectory().getPath() + "/" + eDataFile.getFileNameAndExtension();
        }
		return pathName;
	}
	public void initialize()
	{
		removeChoosableFileFilter(getFileFilter());
			
		if( eDataFile.getViewType() == GraphRenderers.TABULAR)
		{
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("html", "Web Page HTML"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("csv", "Comma Separated"));			
		}
		else if( eDataFile.getViewType() == GraphRenderers.SUMMARY)
		{
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("html", "Web Page HTML"));			
		}
		else
		{
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("csv", "Comma Separated"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("jpeg", "JPEG Image Format"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("png", "Portable Network Graphics"));			
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("pdf", "Portable Document Format"));
		}
	
		eDataFile.setExtension( ((com.cannontech.common.util.FileFilter)getFileFilter()).getFirstExt());
		setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
		addPropertyChangeListener(this);
			
        while(true){
            int status = showSaveDialog(this);
            if (status == APPROVE_OPTION) {
                // Set the selectedFile one more time in case the extension has been
                // removed from the filename
                setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
            
                file = getSelectedFile();
                if (file.exists()) {
                    int exitStatus = JOptionPane.showConfirmDialog(this,
                                                                   file.toString() + "\n Do you want to overwrite this file?");
                    if (exitStatus == APPROVE_OPTION) {
                        eDataFile.writeFile(file);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    eDataFile.writeFile(file);
                    break;
                }
            } else {
                break;
            }
        }
	}

    public void propertyChange(java.beans.PropertyChangeEvent event)
	{
		if (event.getPropertyName() == JFileChooser.FILE_FILTER_CHANGED_PROPERTY)
		{
			final BasicFileChooserUI ui = (BasicFileChooserUI) getUI();
			eDataFile.setExtension(((com.cannontech.common.util.FileFilter)(event.getNewValue())).getFirstExt());
			String fileName = ui.getFileName(); 
			int endIndex = fileName.length();
			if (fileName.indexOf('.') > 0)
				 endIndex = fileName.lastIndexOf('.');
			eDataFile.setFileName( fileName.substring(0, endIndex));

			setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
		}	
	}
	
	/**
	 * This method is overriden because of a bug in the JDK 1.4 and greater.
	 *  The bug sets the filename to null whenever the filefilter changes.
	 *  It is somewhat of a hack to set the fileName again this way, 
	 *   but what else do you do, right? (Tom...no comments, please)
	 */
	public void setFileFilter(FileFilter filter) {
		super.setFileFilter(filter);
        
		if (!(getUI() instanceof BasicFileChooserUI)) {
			return;
		}

		final BasicFileChooserUI ui = (BasicFileChooserUI) getUI();
		final String name = ui.getFileName().trim();

		if ((name == null) || (name.length() == 0)) {
			return;
		}
		
		EventQueue.invokeLater(new Thread() {
			public void run() {
				String currentName = ui.getFileName();
				if ((currentName == null) || (currentName.length() == 0)) {
					ui.setFileName(name);
				}
			}
		});
	}
}
