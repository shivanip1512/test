<%
	int crStartYear = 2002;
	int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
	String crYears = String.valueOf(currentYear);
	if (crStartYear < currentYear)
		crYears = String.valueOf(crStartYear) + "-" + crYears;
%>
  <font face="Arial, Helvetica, sans-serif" size="1">
      Copyright &copy; <%= crYears %> Eaton Corporation plc.  Yukon is a valuable trademark of Eaton 
      Corporation plc. in the U.S. and other countries. You are not permitted to use Eaton 
      trademarks without prior written consent of Eaton.
  </font>
