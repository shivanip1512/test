/*
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet.worker;

import javax.servlet.http.HttpSession;
import javax.swing.table.TableModel;

import org.jfree.report.JFreeReport;
import org.jfree.report.ReportInitialisationException;
import org.jfree.report.ext.servletdemo.AbstractPageableReportServletWorker;


/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class StaticPageableReportServletWorker extends AbstractPageableReportServletWorker 
{ 
	private JFreeReport report; 

	public StaticPageableReportServletWorker(final HttpSession session, final JFreeReport report, final TableModel data)
	{
		super(session);
		this.report = report;
		this.report.setData(data);
	} 

	protected JFreeReport createReport () throws ReportInitialisationException
	{
		return report;
	} 
} 