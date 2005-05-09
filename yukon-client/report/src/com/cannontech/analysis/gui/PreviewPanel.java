/**
 * ========================================
 * JFreeReport : a free Java report library
 * ========================================
 *
 * Project Info:  http://www.jfree.org/jfreereport/index.html
 * Project Lead:  Thomas Morgner;
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -----------------
 * PreviewFrame.java
 * -----------------
 * (C)opyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Thomas Morgner;
 *
 * $Id: PreviewPanel.java,v 1.3 2005/05/09 16:26:08 stacey Exp $
 *
 * Changes (from 8-Feb-2002)
 * -------------------------
 * 08-Feb-2002 : Updated code to work with latest version of the JCommon class library (DG);
 * 24-Apr-2002 : Corrected the structure to better interact with ReportPane. Uses PropertyChange
 *               Events to pass information from the panel. Zoom and Page-Buttons maintain
 *               their state according to the informations from the ReportPane.
 *               SaveToPDF appends a ".pdf" extension to the filename, if not given.
 * 10-May-2002 : Inner classes deliver default implementations for all actions. Actions are used
 *               directly to create menus and buttons.
 * 16-May-2002 : Line delimiters adjusted
 *               close behaviour unified
 *               reset the mnemonics of the toolBar buttons
 * 17-May-2002 : KeyListener for zooming and navigation
 * 26-May-2002 : Added a statusline to the report to show errors and the current and total page
 *               number.  Printing supports the pageable interface.
 * 08-Jun-2002 : Documentation and the pageFormat property is removed. The pageformat is defined
 *               in the JFreeReport-object and passed to the ReportPane.
 * 06-Sep-2002 : Added Dispose on Component-hide, so that this Frame can be garbageCollected.
 *               Without this Construct, the PreviewFrame would never be GarbageCollected and
 *               would cause OutOfMemoryExceptions when the program runs a longer time.
 * 10-Dec-2002 : Updated Javadocs (DG);
 * 08-Oct-2003 : Removed event mapping from WindowClosing to CloseAction
 */

package com.cannontech.analysis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.jfree.report.modules.gui.base.CloseAction;
import org.jfree.report.modules.gui.base.PreviewBaseModule;
import org.jfree.report.modules.gui.base.PreviewProxy;
import org.jfree.report.modules.gui.base.PreviewProxyBase;

import org.jfree.report.JFreeReport;
import org.jfree.report.ReportProcessingException;
import org.jfree.util.ResourceBundleSupport;

/**
 * A standard print preview frame for any JFreeReport.  Allows the user to page back and
 * forward through the report, zoom in and out, and send the output to the printer. <P>
 * You can also save the report in PDF format (thanks to the iText library).
 * <p/>
 * When including this PreviewFrame in your own programs, you should override the provided
 * createXXXAction methods to include your customized actions.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class PreviewPanel extends JPanel implements PreviewProxy
{
  /**
   * Default 'close' action for the frame.
   */
  private class DefaultCloseAction extends CloseAction
  {
    /**
     * Creates a 'close' action.
     */
    public DefaultCloseAction ()
    {
      super(getResources());
    }

    /**
     * Closes the preview frame if the default close operation is set to dispose so this
     * frame is reusable.
     *
     * @param e The action event.
     */
    public void actionPerformed (final ActionEvent e)
    {
        setVisible(false);
    }
  }

  /**
   * A preview proxy.
   */
  private PreviewProxyBase base;

  /**
   * Localised resources.
   */
  private ResourceBundleSupport resources;

  /**
   * Constructs a PreviewFrame that displays the specified report.
   *
   * @param report the report to be displayed.
   * @throws org.jfree.report.ReportProcessingException
   *          if there is a problem processing the report.
   */
  public PreviewPanel (final JFreeReport report)
          throws ReportProcessingException
  {
    init(report);
  }

  /**
   * Initialise.
   *
   * @param report the report.
   * @throws org.jfree.report.ReportProcessingException
   *          if there is a problem processing the report.
   */
  private void init (final JFreeReport report)
          throws ReportProcessingException
  {
    base = createPreviewProxyBase();
    base.setReport(report);
    
	setLayout(new BorderLayout());	
//	setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); 
	add(base);    
  }

  protected PreviewProxyBase createPreviewProxyBase ()
  {
    return new PreviewProxyBase(this);
  }


  /**
   * Returns the default close action.
   *
   * @return The action.
   */
  public Action createDefaultCloseAction ()
  {
    return new DefaultCloseAction();
  }

  /**
   * Disposes the frame.
   */
  public void dispose ()
  {
    base.dispose();
  }

  /**
   * Shuts down the preview component. Once the component is closed, it cannot be
   * reactivated anymore.
   */
  public void close ()
  {
    base.close();
  }

  /**
   * Retrieves the resources for this PreviewFrame. If the resources are not initialized,
   * they get loaded on the first call to this method.
   *
   * @return this frames ResourceBundle.
   */
  public ResourceBundleSupport getResources ()
  {
    if (resources == null)
    {
      resources = new ResourceBundleSupport(PreviewBaseModule.RESOURCES_BASE_NAME);
    }
    return resources;
  }

  /**
   * Returns the preview proxy.
   *
   * @return The proxy.
   */
  public PreviewProxyBase getBase ()
  {
    return base;
  }

/* (non-Javadoc)
 * @see org.jfree.report.modules.gui.base.PreviewProxy#pack()
 */
public void pack()
{
    // TODO Auto-generated method stub
    
}

/* (non-Javadoc)
 * @see org.jfree.report.modules.gui.base.PreviewProxy#setJMenuBar(javax.swing.JMenuBar)
 */
public void setJMenuBar(JMenuBar bar)
{
    // TODO Auto-generated method stub
    
}

/* (non-Javadoc)
 * @see org.jfree.report.modules.gui.base.PreviewProxy#setTitle(java.lang.String)
 */
public void setTitle(String title)
{
    // TODO Auto-generated method stub
    
}

}
