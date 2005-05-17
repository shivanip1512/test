<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int bankid = ParamUtil.getInteger(request, "bankid");
	SubBus[] allSubs = capControlCache.getAllSubBuses();		
	CapBankDevice capBank = capControlCache.getCapBankDevice( new Integer(bankid) );
	int oldfdrid = 0;
	
	if( capBank == null )
	{
		allSubs = new SubBus[0];
	}
	else
	{
		oldfdrid = capBank.getParentID();
	}
	
%>

<html>

<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>

<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" >

<TITLE>Temp CapBank Move</TITLE>
</HEAD>


<body>

 <!--sort of a hack on REDIRECTURL input for now, but I can not figure out how to get around the use of the proxy in XmlHTTP calls-->
 <form id="frmCapBankMove" action="/servlet/CBCServlet" method="post">
	<input type="hidden" name="redirectURL" value="/capcontrol/feeders.jsp">
	<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
	<input type="hidden" name="paoID" value="<%=bankid%>">
	<input type="hidden" name="cmdID" value="<%=CBCCommand.CMD_BANK_TEMP_MOVE%>">
	<input type="hidden" name="opt" value="<%=oldfdrid%>">  <!--Old Feeder ID-->
	<input type="hidden" name="opt"> <!--New Feeder ID-->
	<input type="hidden" name="opt"> <!--Control Order-->

  <table id="chartTable" width="65%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td class="cellImgFill"><img src="images/Header_left.gif" class="cellImgFill"></td>
      <td class="trimBGColor cellImgShort">CapBank Temp Move (Pick feeder by clicking on name)</td>      
      <td class="cellImgFill"><img src="images/Header_right.gif" class="cellImgFill"></td>
    </tr>
    <tr>
      <td class="cellImgFill lAlign" background="images/Side_left.gif"></td>
      <td>
      
      <div class="scrollSmall">
        <table id="innerTable" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<td>Name</td>
				<td>State</td>
				<td>VAR Load / Est.</td>
				<td>PFactor / Est.</td>
				<td class="rAlign">New Control Order:
				<input type="text" id="txtCntrlOrder" class="tableCell" size="1" maxlength="3" value="1">
				</td>
			</tr>

<%
for( int i = 0; i < allSubs.length; i++ )
{
	SubBus subBus = allSubs[i];	
	Feeder[] feeders = capControlCache.getFeedersBySub(subBus.getCcId());
	
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
%>

			<tr class="<%=css%>">
				<td>
				<input type="checkbox" id="chkBxSub" onclick="showRowElems('subId<%=i%>', this);"/>
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>				
			</tr>

			<a id="subId<%=i%>">
<%
	for( int j = 0; j < feeders.length; j++ )
	{
		Feeder feeder = feeders[j];
		
		//do not include the current feeder this bank belongs to
		if( feeder.getCcId().intValue() == oldfdrid )
			continue;
%>
				<tr class="<%=css%>" style="display: none;">
					<td><a href="#" class="<%=css%> lIndent"
						onclick="postMany('frmCapBankMove', 'opt[1]', <%=feeder.getCcId()%>, 'opt[2]', frmCapBankMove.txtCntrlOrder.value);">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></a>
					</td>
					<td>
					<font color="<%=CBCDisplay.getHTMLFgColor(feeder)%>">
						<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
					</font>
					</td>
					<td>
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN)%></a>
					</td>
					<td>
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN)%></a>
					</td>

					<td>
					</td>
				</tr>
<%	} %>
			</a>
<% } %>

        </table>
    </div>

      </td>

      <td class="cellImgFill rAlign" background="images/Side_right.gif"></td>
    </tr>
    <tr>
      <td class="cellImgShort"><img src="images/Bottom_left.gif"></td>
      <td class="cellImgShort" background="images/Bottom.gif"></td>
      <td class="cellImgShort"><img src="images/Bottom_right.gif"></td>
    </tr>
  </table>
  
</form>

</body>
</html>
