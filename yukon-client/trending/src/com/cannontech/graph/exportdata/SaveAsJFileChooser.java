package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (8/21/2002 9:48:51 AM)
 * @author: 
 */
public class SaveAsJFileChooser extends javax.swing.JFileChooser implements com.cannontech.graph.GraphDefines, java.beans.PropertyChangeListener 
{
//	private int exportPane = 0;		//Trend Pane index.  Specifies which types are valid for each tab.
		
//	private String fileName = "";
//	private static String extension = "";
	private static java.io.File file = new java.io.File("C:/yukon/client/export/Chart.csv");
	
//	private static Object exportObject = null;
	
	//Object items that are only valid if csv exportting is selected.
//	private static com.cannontech.graph.model.TrendModel trendModel = null;	
//	private static String csvExportType = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES;
//	private static int csvColumnLength = 0;
//	private static int csvRowLength = 0;

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
	
	public SaveAsJFileChooser(String currentDirectory, int selectedPane, Object expObj, String newFileName)
	{
		super(currentDirectory);
		
		eDataFile = new ExportDataFile(selectedPane, expObj, newFileName);
		initialize();
	}

	public SaveAsJFileChooser(String currentDirectory, int selectedPane, Object expObj, String newFileName, com.cannontech.graph.model.TrendModel tModel)
	{
		super(currentDirectory);

		eDataFile = new ExportDataFile( selectedPane, expObj, newFileName, tModel);
		initialize();
	}
	
	private String getCurrentPathAndFilename()
	{
		String pathName = getCurrentDirectory().getPath() + "/" + eDataFile.getFileNameAndExtension();
		return pathName;
	}
	public void initialize()
	{
		removeChoosableFileFilter(getFileFilter());
			
		if( eDataFile.getExportPane() == GRAPH_PANE)
		{
//			eDataFile.setCSVExportMask( com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK);
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("csv", "Comma Separated"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("jpeg", "JPEG Image Format"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("png", "Portable Network Graphics"));			
//			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("gif", "GIF Image Format"));
//			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("svg", "Scalable Vector Graphics"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("pdf", "Portable Document Format"));
		}
		else if ( eDataFile.getExportPane() == TABULAR_PANE)
		{
//			eDataFile.setCSVExportMask( com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK);
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("html", "Web Page HTML"));
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("csv", "Comma Separated"));			
		}
		else if ( eDataFile.getExportPane() == SUMMARY_PANE)
		{
			addChoosableFileFilter(new com.cannontech.common.util.FileFilter("html", "Web Page HTML"));			
		}
	
		eDataFile.setExtension( ((com.cannontech.common.util.FileFilter)getFileFilter()).getFirstExt());
		setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
	
		addPropertyChangeListener("fileFilterChanged", this);
			
		int status = showSaveDialog(this);
		if( status == APPROVE_OPTION )
		{
			file = getSelectedFile();
			eDataFile.writeFile(file);
		}
		return;
	
	}
	public void propertyChange(java.beans.PropertyChangeEvent event)
	{
		if( getSelectedFile() != null)
		{
			String selectedFile = getSelectedFile().getName();
			eDataFile.setFileName( selectedFile.substring(0, selectedFile.lastIndexOf('.') + 1));

			eDataFile.setExtension(((com.cannontech.common.util.FileFilter)(event.getNewValue())).getFirstExt());
			setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
		}
		else
		{
			eDataFile.setExtension(((com.cannontech.common.util.FileFilter)(event.getNewValue())).getFirstExt());
			setSelectedFile(new java.io.File(getCurrentPathAndFilename()));
		}
	}
	
}
