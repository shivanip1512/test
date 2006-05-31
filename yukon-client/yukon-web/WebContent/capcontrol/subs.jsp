<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Substations" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
    class="com.cannontech.cbc.web.CapControlCache"
    type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    String nd = "\"return nd(5000);\"";
    LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);			
	String popupEvent = AuthFuncs.getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover"; 
    
    SubBus[] areaSubs =
        capControlCache.getSubsByArea( cbcSession.getLastArea() );

    boolean hasControl = CBCWebUtils.hasControlRights(session);
%>


<cti:standardMenu/>
<cti:breadCrumbs>
  <cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
  <cti:crumbLink url="subs.jsp" title="Substations" />
</cti:breadCrumbs>
  
<script type="text/javascript">
  Event.observe(window, 'load', callBack);
</script>

<cti:titledContainer title="<%="Substation Buses In Area:  " + cbcSession.getLastArea()%>">
          
          
            <form id="subForm" action="feeders.jsp" method="post">
            <input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
          
            <table id="subHeaderTable" width="98%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnHeader lAlign">              
                <td>
                <input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubs');"/>
                Sub Name</td>
                <td>State</td>
                <td>Target</td>
                <td>VAR Load / Est.</td>
                <td>Date/Time</td>
                <td>PFactor / Est.</td>
                <td>Watts / Volts</td>
                <td>Daily / Max Ops</td>
              </tr>
			</table>
<div class="scrollLarge">
<table id="subTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
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
				</a>
				
				<% if( subBus.getVerificationFlag().booleanValue() ) { %>
					<span class="popupImg"
						onmouseover="statusMsg(this, 'This SubBus is currently being<br>used in a Verification schedule');" >
					(v)</span>
				<% } %>
				</td>
				
				<td>
			<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
				<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
					style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
					href="javascript:void(0);"
				    <%= popupEvent %> ="return overlib(
						createIFrame('subCmd.jsp?subId=<%=subBus.getCcId()%>', 210, 170 ,'tempIFrame', 0),
						STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,
						 FULLHTML);"
				    onmouseout= <%=nd%> >

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
	</div>
	<input type="hidden" id="lastUpdate" value="">
        </form>

<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('subTable','subHeaderTable');});
</script>
      </cti:titledContainer>

</cti:standardPage>