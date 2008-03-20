/*
 * Created on Feb 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.importer.ImportData;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.yimp.util.DBFuncs;

/**
 * @author jdayton
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImporterServlet extends HttpServlet {
	// this doesn't have to do a whole lot yet
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		// Flag to force an import event
		String forceImport = req.getParameter("forceImp");
		if (forceImport != null) {
			DBFuncs.forceImport();
			// show status of currently running on JSP
			DBFuncs.writeNextImportTime(new java.util.Date(), true);
		} else {
			HttpSession session = req.getSession(false);
			boolean isMultiPart = ServletFileUpload.isMultipartContent(req);
			if (isMultiPart) {
				try {
					List items = null;
					FileItemFactory factory = new DiskFileItemFactory();
			        	ServletFileUpload upload = new ServletFileUpload(factory);
					items = upload.parseRequest(req);
					FileItem dataFile = null;
					for (int i = 0; i < items.size(); i++) {
						FileItem item = (FileItem) items.get(i);
						if (item.getFieldName().equals("dataFile")) {
							if (!item.getName().equals("")) {
								dataFile = item;
								break;
							}
						}
					}
					if (dataFile == null)
						session.setAttribute("LOAD_IMPORTDATA_ERROR",
								"No file provided.");

					saveFileData(dataFile, session);
				} catch (FileUploadException e) {
					CTILogger.error(e);
					session.setAttribute("LOAD_IMPORTDATA_ERROR",
							"Unable to read file.");
				}
			}
		}

		resp.sendRedirect(req.getContextPath() + "/bulk/importer.jsp");

		/*
		 * There will be a lot more here down the road!!!
		 */
	}

	public void saveFileData(FileItem dataFile, HttpSession session) {
		try {
			if (dataFile != null) {
				File file = new File(dataFile.getName());
				if (file.exists() && file.canRead()) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(dataFile.getInputStream()));
					String line = null;
					int lineNo = 0;
					while ((line = reader.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						// assume headings on columns
						if (lineNo == 1)
							continue;
						String[] columns = StarsUtils.splitString(line, ",");
						if (columns.length < ImportData.SETTER_COLUMNS.length - 2)
							session.setAttribute("LOAD_IMPORTDATA_ERROR",
									"Incorrect number of fields.");
						ImportData currentEntry = new ImportData();
						currentEntry.setAddress(((String) columns[0]).trim());
						currentEntry.setName(((String) columns[1]).trim());
						currentEntry.setRouteName(((String) columns[2]).trim());
						currentEntry.setMeterNumber(((String) columns[3])
								.trim());
						currentEntry.setCollectionGrp(((String) columns[4])
								.trim());
						currentEntry.setAltGrp(((String) columns[5]).trim());
						currentEntry.setTemplateName(((String) columns[6])
								.trim());
						currentEntry.setBillingGroup(" ");
						currentEntry.setSubstationName(" ");
						// possibly ignore these non-mandatory columns
						if (columns.length >= ImportData.SETTER_COLUMNS.length - 1)
							currentEntry.setBillingGroup(((String) columns[7])
									.trim());
						if (columns.length == ImportData.SETTER_COLUMNS.length)
							currentEntry
									.setSubstationName(((String) columns[8])
											.trim());

						try {
							Transaction.createTransaction(Transaction.INSERT,
									currentEntry).execute();
							session
									.setAttribute(
											"LOAD_IMPORTDATA_SUCCESS",
											"Successfully inserted "
													+ (lineNo - 1)
													+ " lines into the ImportData table.");
							session.setAttribute("LOAD_IMPORTDATA_ERROR", "");
						} catch (TransactionException e) {
							CTILogger.error(e);
							session.setAttribute("LOAD_IMPORTDATA_ERROR",
									"Unable to insert line " + lineNo + ".");
						}
					}
				} else {
					session.setAttribute("LOAD_IMPORTDATA_SUCCESS", "");
					session.setAttribute("LOAD_IMPORTDATA_ERROR",
							"File does not exsist. Please enter a new file with the csv file extension.");
				}
			} else {
				session.setAttribute("LOAD_IMPORTDATA_SUCCESS", "");
				session.setAttribute("LOAD_IMPORTDATA_ERROR",
						"Please enter a file.");
			}
		} catch (IOException e) {
			CTILogger.error(e);
			session.setAttribute("LOAD_IMPORTDATA_ERROR",
					"Unable to read file.");
		}

	}
}
