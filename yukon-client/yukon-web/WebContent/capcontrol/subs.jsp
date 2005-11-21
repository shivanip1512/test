<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	SubBus[] areaSubs =
		capControlCache.getSubsByArea( cbcSession.getLastArea() );

	boolean hasControl = CBCWebUtils.hasControlRights(session);
%>

<html>
<head>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>

<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<title>Substations</title>
</head>


<body onload="callBack();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
    <%@include file="cbc_header.jspf"%>
    </td>
  </tr>

    <tr><td> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
        <tr>
          <td valign="top">
	          <div class="lAlign">
				<cti:breadCrumb>
					<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
					<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
				</cti:breadCrumb>
	          </div>
          </td>
        
          <td valign="top">
			<div class="rAlign">
				<form id="findForm" action="results.jsp" method="post">
					<div class="main">Find: <input type="text" name="<%=CBCSessionInfo.STR_LAST_SEARCH%>">
					<input type="image" name="Go" src="images/GoButton.gif" alt="Find"></div>
				</form>
			</div>
          </td>

        </tr>
      </table>

      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images/Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort">Substation Buses In Area:  <%=cbcSession.getLastArea()%></td>
          <td class="cellImgFill"><img src="images/Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images/Side_left.gif"></td>
          <td>
          
          <div class="scrollLarge">
                <form id="subForm" action="feeders.jsp" method="post">
            <input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
          
            <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>
				<input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubs');"/>
				Sub Name</td>
                <td>State</td>
                <td>Target</td>
                <td>VAR Load / Est.</td>
                <td>Date/Time</td>
                <td>PFactor / Est.</td>
                <td>Watts</td>
                <td>Daily / Max Ops</td>
              </tr>

<%
String css = "tableCell";
for( int i = 0; i < areaSubs.length; i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	SubBus subBus = areaSubs[i];
%>
	        <tr class="<%=css%>">
				<td>
				<input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>" />
				<a href="#" class="<%=css%>" onclick="postMany('subForm', '<%=CBCSessionInfo.STR_SUBID%>', <%=subBus.getCcId()%>)">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
				</a></td>
				
				<td>
			<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
				<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
					style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
					href="javascript:void(0);"
				    onmouseover="overlib(
						createIFrame('subCmd.jsp?subId=<%=subBus.getCcId()%>', 135, 90, 'if1', 0),
						STICKY, WIDTH,135, HEIGHT,90,
						MOUSEOFF, FULLHTML);"
				    onmouseout="nd();">

			<% } else { %>
				<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
			<% } %>
			<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
			</a>
				</td>

				<td><a type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN)%></a>
				</td>
				<td><a type="param2" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN)%></a>
				</td>
				<td><a type="param3" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN)%></a>
				</td>
				<td><a type="param4" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN)%></a>
				</td>
				<td><a type="param5" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN)%></a>
				</td>
				<td><a type="param6" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN)%></a>
				</td>
			</tr>
<% } %>

            </table>
			</form>
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
      
    </td>
    </tr>
  </table>

</body>

<%@include file="cbc_footer.jspf"%>

</html>