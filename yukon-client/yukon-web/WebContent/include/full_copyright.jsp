<%
	int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
	String crYears = String.valueOf(currentYear);
	if (crStartYear < currentYear)
		crYears = String.valueOf(crStartYear) + "-" + crYears;
%>
  <font face="Arial, Helvetica, sans-serif" size="1">Copyright &copy; <%= crYears %>, 
    Cannon Technologies, Inc. All rights reserved. All text, images, graphics, 
    code, and other materials on this website are subject to the copyrights and 
    other intellectual property rights of Cannon Technologies, Inc. Cannon Technologies, 
    Inc. owns the copyrights in the selection, coordination, and arrangement of 
    the materials on this website. These materials may not be copied for commercial 
    use or distribution, nor may these materials be modified or reposted to other 
    sites. </font>
