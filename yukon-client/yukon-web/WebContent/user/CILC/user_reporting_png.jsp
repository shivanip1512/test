<table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
  <tr> 
    <td>
      <center>
      <%
		Object stateList = session.getAttribute(session.getAttribute("ReportKey")+"StateList");
        if( stateList != null)
        {
        	for (int i = 0; i < ((org.jfree.report.modules.output.pageable.base.ReportStateList)stateList).size(); i++)
        	{%>
           		<img id = "theReport" src="<%=request.getContextPath()%>/servlet/ReportGenerator?ACTION=PagedReport&ext=png&page=<%=i%>">
			<%}
		}%>
  	  </center>
    </td>
  </tr>
</table>
