/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet.worker;

import javax.swing.table.TableModel;

import org.jfree.report.JFreeReport;
import org.jfree.report.ReportInitialisationException;
import org.jfree.report.ext.servletdemo.AbstractTableReportServletWorker;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StaticTableReportServletWorker extends AbstractTableReportServletWorker
{
	private JFreeReport report; 

	public StaticTableReportServletWorker(JFreeReport report, TableModel data)
	{
		super();
		this.report = report;
		this.report.setData(data);
	} 

	protected JFreeReport createReport () throws ReportInitialisationException
	{
		return report;
	}

}
