<%@ include file="import_header.jsp" %>

<html>
<head>
<title>Yukon MCT-410 Bulk Importer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= webImp.REFRESH_RATE %> >
<link rel="stylesheet" href="theme/YimpStyle.css" type="text/css">
<STYLE type="text/css">
<!--
INPUT {
	background-color: red;
	font-weight: bold;
	text-align: center
}

.BodyTitleText {
	color: black;
	font-weight: bold;
	font-size: 14pt
}

TH {
	color: black;
	font-weight: bold
}

.HeaderCellCrucial {
	color: white;
	font-weight: bold;
	background-color: #666699
}
-->
</STYLE>
<SCRIPT type="text/javascript"></SCRIPT></head>

<body bgcolor="#666699" leftmargin="0" topmargin="0" text="#CCCCCC" link="#000000" vlink="#000000" alink="#000000">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/importpic.jpg">&nbsp;</td>
		  <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr>
				<td colspan="3" height="74" background="images/Header.gif">&nbsp;</td>
			  </tr>
              <tr bgcolor="#666699"> 
                <td height = "28" class="Header3" width="420">&nbsp;&nbsp;<font color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
                	<font color="#f8f8f8">Next import attempt: <%= webImp.getNextImportTime() %>. </font>
            		</em></font></td>
                <td valign="middle" width="180">&nbsp;</td>
                  
                <td width="57" valign="middle"> 
                  <div align="left"><span ><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">
                  	<font color="#ffffff" size="2" face="Arial, Helvetica, sans-serif">
                  	 </font></a></span></div>
                  </td>
                  
              </tr>
            </table>
          </td>
		  </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr>
          <td width="759" bgcolor="#000000" valign="top"></td>
		  <td width="1" bgcolor="#000000" height="2"></td>
        </tr>
        <tr>
		  <td width="5" bgcolor="#FFFFFF" height="5"></td>
        </tr>
        <tr> 
          <td width="759" valign="top" bgcolor="#FFFFFF"> 
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td></td>
              </tr>
            </table>
				<DIV align="right">
				<TABLE border="0" cellspacing="0" cellpadding="0">
					<TBODY>
						<TR>
							<TD valign="top" class="BodyTitleText" align="center" width="757">
							<TABLE width="738" border="1" align="center" cellpadding="0"
								cellspacing="0">
								<TBODY>
									<TR>
										<TD>
										<TABLE width="735" border="0" cellspacing="0" cellpadding="0"><CAPTION align="bottom"></CAPTION>
											<TBODY>
												<TR class="HeaderCell">
													<TD width="231"><SPAN class="HeaderCellCrucial">&nbsp;Override
													the import wait period: <FONT color="#000000"> </FONT></SPAN></TD>
													<TD width="314"><INPUT type="button" name="Import"
														value="Force Manual Import Event" onclick=<%webImp.forceImport();%>></TD>

													<TD width="191">
													<DIV align="right"><BR>
													<BR>
													</DIV>
													</TD>
												</TR>
											</TBODY>
										</TABLE>
										</TD>
									</TR>
								</TBODY>
							</TABLE>

							<BR>
							RESULTS OF LAST IMPORT<BR>
							<TABLE border="1" width="740">
								<TBODY>
									<TR>
										<TH width="369" align="left" class="HeaderCell">Last import occurred at: <FONT
											color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><FONT
											color="#ff0000"><%=webImp.getLastImportTime()%></FONT></EM></FONT></TH>
									</TR>
									<TR>
										<TH width="369" align="left" class="HeaderCellCrucial"><FONT
											color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><FONT
											color="#ff0000"><%=webImp.getTotalSuccesses()%></FONT></EM></FONT> of <FONT
											color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><FONT
											color="#ff0000"><%=webImp.getTotalAttempts()%></FONT></EM></FONT> entries were
										successfully imported.</TH>
									</TR>
								</TBODY>
							</TABLE>
							
							<BR>
							<TABLE width="740" border="1" cellpadding="2" cellspacing="0">
								<CAPTION align="top" class="HeaderCellCrucial">Failed Entries</CAPTION>
								<TBODY>
									<TR valign="top" class="HeaderCell">
										<TD width="70"><FONT color="#000000"><%= webImp.COLUMN_NAMES[0] %></FONT></TD>
										<TD width="400"><FONT color="#ff0000"><%= webImp.COLUMN_NAMES[1] %></FONT></TD>
										<TD width="70"><FONT color="#000000"><%= webImp.COLUMN_NAMES[2] %></FONT></TD>
									</TR>

						<% 
	                  	for( int i = 0; i < webImp.getFailures().size(); i++ )
	                  	{
	                  	%>

									<TR valign="top">
					
										<TD width="70" class="TableCell"><FONT color="#000000"><%= webImp.getFailName(i)  %></FONT></TD>
										<TD width="400" class="TableCell"><FONT color="#ff0000"><%= webImp.getErrorString(i) %></FONT></TD>
										<TD width="70" class="TableCell"><FONT color="#000000"><%= webImp.getFailureTime(i) %></FONT></TD>
										
									</TR>
						<%
						}
						%>


								</TBODY>
							</TABLE>
							<BR>
							</TD>
						</TR>
					</TBODY>
				</TABLE>
				</DIV>

				</td>
    </tr>
      </table>
    </td>
	</tr>
</table>



</body>
</html>
