<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.cbc.web.CapControlUserOwnerDAO" %>
<jsp:directive.page import="com.cannontech.database.data.capcontrol.CapControlArea"/>
<jsp:directive.page import="com.cannontech.database.data.capcontrol.CapControlSpecialArea"/>
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
    String nd = "\"return nd();\"";
    LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);			
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover"; 
    
	CapControlUserOwnerDAO userOwner = new CapControlUserOwnerDAO (capControlCache, user);
	Integer areaId = cbcSession.getLastAreaId();
	String area = cbcSession.getLastArea();
	SubStation[] areaSubs = userOwner.getSubstationsByArea(areaId);
    boolean hasControl = CBCWebUtils.hasControlRights(session);
    boolean special = userOwner.isSpecialCBCArea(areaId);
%>

<cti:standardMenu/>
<cti:breadCrumbs>
<%if(special){ %>
  <cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
  <%} else{ %>
  <cti:crumbLink url="subareas.jsp" title="Substation Areas" />
  <%} %>
  <cti:crumbLink url="substations.jsp" title="Substations" />
</cti:breadCrumbs>
  
<script type="text/javascript">
	
 Event.observe(window, 'load', function () {								
 								callBack();
 								});
</script>

<cti:titledContainer title="<%="Substation In Area:  " + cbcSession.getLastArea()%>" id="last_titled_container">
          
		<%if (areaSubs.length == 0) {%>
		<!-- 
		<form id="subForm" action="redirect.jsp" method="post">
		<input type="hidden" name="reason" value="No subs were found. "/>
		<input type="hidden" name="message" value = "Subs.jsp <i>might<i> be defined in db_editor. ">
		<input type="hidden" name="redirectUrl" value="/capcontrol/subareas.jsp"/>
		<script type="text/javascript">
			$('subForm').submit();
		</script>
		</form>
		-->
		<%} else  {
		
		%> 
		
	          
            <form id="subForm" action="feeders.jsp" method="post">
            <input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
          
            <table id="subHeaderTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
              <tr class="columnHeader lAlign">              
                <td>
                <input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubs');"/>
                Sub Name</td>
                <td>State</td>
                <td>Available<br/> kVARS</td>
                <td>Unavailable <br/>kVARS</td>
                <td>Closed <br/>kVARS</td>
                <td>Tripped <br/>kVARS</td>
                <td>PFactor / Est.</td>
              </tr>
			</table>
<div>
<table id="subTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
<%
String css = "tableCell";
for( int i = 0; i < areaSubs.length; i++ ) {
    css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
    SubStation substation = areaSubs[i];
    
	String varsAvailable = CBCUtils.format( CBCUtils.calcVarsAvailable(substation));
	String varsDisabled =  CBCUtils.format (CBCUtils.calcVarsDisabled(substation));
	String closedVars = CBCUtils.format( CBCUtils.calcClosedVARS(capControlCache.getCapBanksBySubStation(substation)));
	String trippedVars = CBCUtils.format( CBCUtils.calcTrippedVARS(capControlCache.getCapBanksBySubStation(substation)));

%>

	        <tr class="<%=css%>">
				<td>
				<input type="checkbox" name="cti_chkbxSubs" value="<%=substation.getCcId()%>" />
				<a href="#" class="<%=css%>" onclick="postMany('subForm', '<%=CBCSessionInfo.STR_SUBID%>', <%=substation.getCcId()%>)" id="anc_<%=substation.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_NAME_COLUMN)%>
				</a>
				
				</td>
				<td>
				
			<%
							if( hasControl ) {
							%>
				<!--Create  popup menu html-->
				<input id="cmd_sub_<%=substation.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />
				<a type="state" name="cti_dyn" id="<%=substation.getCcId()%>"
					style="color: <%=CBCDisplay.getHTMLFgColor(substation)%>;"
					href="javascript:void(0);"
				    <%=popupEvent%> ="return overlib(
						$F('cmd_sub_<%=substation.getCcId()%>'),
						STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,
						MOUSEOFF, FULLHTML);"
				    onmouseout= <%=nd%> >

			<%
			} else {
			%>
				<a type="state" name="cti_dyn" id="<%=substation.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(substation)%>;" >
			<%
			}
			%>
			<%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
			</a>
				</td>
				<td><%=varsAvailable %> </td>
                <td><%=varsDisabled %> </td>
                <td><%=closedVars %> </td>
                <td><%=trippedVars %> </td>
                <td><a type="param4" name="cti_dyn" id="<%=substation.getCcId()%>">
                <%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_POWER_FACTOR_COLUMN)%></a>
                </td>
            </tr>
<% } %>

            </table>
	</div>
	<input type="hidden" id="lastUpdate" value="" />
		        
</form>
<%}%>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('subTable','subHeaderTable');});
</script>
</cti:titledContainer>
<div style = "display:none" id = "outerDiv">
<cti:titledContainer title="Current Status">
    <div id="cmd_msg_div" />
</cti:titledContainer>
</div>
</cti:standardPage>