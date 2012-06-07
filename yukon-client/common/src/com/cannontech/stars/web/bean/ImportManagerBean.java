package com.cannontech.stars.web.bean;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class ImportManagerBean {
	private Collection errorList;
	private int pageSize;
	private int page;
	
	public ImportManagerBean() {
		pageSize = 25;
		page = 1;
	}
		
	public String getHtml() {
		final int listSize = errorList.size();
		int endIndex = (getPage() * getPageSize());
		int startIndex = endIndex - getPageSize();
		int maxPageNo = (int) Math.ceil(listSize * 1.0 / getPageSize());
		if (endIndex > listSize) endIndex = listSize;
			
		
		final StringBuffer sb = new StringBuffer();
		
		sb.append("<table width='95%' border='0' cellspacing='0' cellpadding='3'>\n");
		sb.append("<tr>\n");
		sb.append("<td>\n");
		sb.append("<table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>\n");
		sb.append("<tr>\n");
		sb.append("<td align='left'><input type='button' style='font:11px; margin-bottom:-1px' value='Back' onclick='location.href=\"ImportAccount.jsp\"'>");
		sb.append("</td>\n");
		sb.append("<td align='right'>Entries per Page:");
		sb.append("            <input type='text' id='PageSize' style='border:1px solid #666699; font:11px' size='1' value='" + getPageSize() +"'>\n");
		sb.append("            <input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"ImportManagerView.jsp?pageSize=\" + document.getElementById(\"PageSize\").value;'>\n");
		sb.append("</td>\n");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td align='left'>" + (startIndex+1) + "-" + (endIndex)+ " of " + listSize +" | ");
		
		if (getPage() == 1) {
			sb.append("<font color='#CCCCCC'>First</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=1'>First</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == 1) {
			sb.append("<font color='#CCCCCC'>Previous</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + (getPage()-1)+ "'>Previous</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == maxPageNo || maxPageNo == 0) {
			sb.append("<font color='#CCCCCC'>Next</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + (getPage()+1) + "'>Next</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == maxPageNo || maxPageNo == 0) {
			sb.append("<font color='#CCCCCC'>Last</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + maxPageNo + "'>Last</a>\n");
		}
		
		sb.append("</td>\n");
		sb.append("<td align='right'>Page(" + getPage() + "-" + maxPageNo + "):\n");
		sb.append("<input type='text' id='GoPage' style='border:1px solid #666699; font:11px' size='1' value='" + getPage() +"'>\n");
		sb.append("<input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href=\"ImportManagerView.jsp?page=\" + document.getElementById(\"GoPage\").value;'>\n");
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");
		sb.append("</td>\n");
		sb.append("</tr>\n");
		
		sb.append("<tr>\n");
		sb.append("<td>\n");
		sb.append("<table width=\'100%\' border=\'1\' cellspacing=\'0\' cellpadding=\'3\'>\n");
		sb.append("<tr>\n");
		sb.append("<td nowrap class=\'HeaderCell\'>Line #</td>");
		sb.append("<td nowrap class=\'HeaderCell\'>Error Message</td>");
		sb.append("<td nowrap class=\'HeaderCell\'>Import Account</td>");
		sb.append("</tr>\n");
		
		Pattern pattern = Pattern.compile("^\\[line:\\s+(\\d+)\\s+error:\\s+(.+)\\]$");
		Object[] array = errorList.toArray();
		for (int x = startIndex; x < endIndex; x++) {
			if (array[x] instanceof String[]) {
				String[] value = (String[]) array[x];
				if (value.length > 1 && value[1] != null) {
					Matcher m = pattern.matcher(value[1]);
					if (m.matches()) {
						sb.append("<tr>\n");
						sb.append("<td class=\'TableCell\'>" + m.group(1) + "</td>\n");
						sb.append("<td class=\'TableCell\'>" + m.group(2) + "</td>\n");
						sb.append("<td class=\'TableCell\'>" + StringEscapeUtils.escapeHtml(value[0]) + "</td>\n");
						sb.append("</tr>\n");
					}
				}
			}
		}

		sb.append("</table>\n");
		
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("<tr>\n");
		sb.append("<td>\n");
		sb.append("<table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>\n");
		sb.append("<tr>\n");
		sb.append("<td align='left'>" + (startIndex+1) + "-" + endIndex + " of " + listSize + " | ");
        
		if (getPage() == 1) {
			sb.append(" <font color='#CCCCCC'>First</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=1'>First</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == 1) {
			sb.append("<font color='#CCCCCC'>Previous</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + (getPage()-1)+ "'>Previous</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == maxPageNo || maxPageNo == 0) {
			sb.append("<font color='#CCCCCC'>Next</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + (getPage()+1) + "'>Next</a>");
		}
		
		sb.append(" | ");
		
		if (getPage() == maxPageNo || maxPageNo == 0) {
			sb.append("<font color='#CCCCCC'>Last</font>");
		} else {
			sb.append("<a class='Link1' href='ImportManagerView.jsp?page=" + maxPageNo + "'>Last</a>\n");
		}
		
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();
	}
	
	public void setErrorList(final Collection errorList) {
		this.errorList = errorList;
	}
	
	public Collection getErrorList() {
		return errorList;
	}
	
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPage(final int page) {
		this.page = page;
	}
	
	public int getPage() {
		return page;
	}
}
