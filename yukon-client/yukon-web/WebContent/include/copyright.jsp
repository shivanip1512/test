<%
	int crStartYear = 2002;
	int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
	String crYears = String.valueOf(currentYear);
	if (crStartYear < currentYear)
		crYears = String.valueOf(crStartYear) + "-" + crYears;
%>
<font face="Arial, Helvetica, sans-serif" size="1">Copyright &copy; <%= crYears %>, 
  Cannon Technologies, Inc. All rights reserved.</font> 
