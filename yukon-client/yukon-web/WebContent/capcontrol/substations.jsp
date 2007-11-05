<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.database.data.capcontrol.CapControlArea" %>
<%@ page import="com.cannontech.database.data.capcontrol.CapControlSpecialArea" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.core.dao.PaoDao" %>

<cti:standardPage title="Substations" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
    LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
    String nd = "\"return nd();\"";
    String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover"; 
    
	Integer areaId = ccSession.getLastAreaId();
	String area = ccSession.getLastArea();
	List<SubStation> areaSubs;
	boolean special = filterCapControlCache.isSpecialCBCArea(areaId);
	if(special){
		areaSubs = filterCapControlCache.getSubstationsBySpecialArea(areaId);
	}else{
		areaSubs = filterCapControlCache.getSubstationsByArea(areaId);
	}
    boolean hasControl = CBCWebUtils.hasControlRights(session);
    
%>

<cti:standardMenu/>
<cti:breadCrumbs>
<%
if(special){
%>
  <cti:crumbLink url="subareas.jsp" title="Substation Areas" /> 
  <cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
  <%
  } else{
  %>
  <cti:crumbLink url="subareas.jsp" title="Substation Areas" /> 
  <cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
  <%
  }
  %>
  <cti:crumbLink url="substations.jsp" title="Substations" />
</cti:breadCrumbs>
  
<script type="text/javascript">
	
 Event.observe(window, 'load', function () {								
 								callBack();
 								});
</script>

<cti:titledContainer title="<%="Substation In Area:  " + ccSession.getLastArea()%>" id="last_titled_container">
          
		<%
          		if (areaSubs.size() == 0) {
          		%>
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
		<%
		} else  {
		%> 
		
	          
            <form id="subForm" action="feeders.jsp" method="post">
            <input type="hidden" name="<%=CCSessionInfo.STR_SUBID%>" />
          
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
for( int i = 0; i < areaSubs.size(); i++ ) {
    css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
    SubStation substation = areaSubs.get(i);
    
	String varsAvailable = CBCUtils.format( CBCUtils.calcVarsAvailableForSubStation(substation, user) );
	String varsDisabled =  CBCUtils.format (CBCUtils.calcVarsUnavailableForSubStation(substation, user));
	String closedVars = CBCUtils.format( CBCUtils.calcVarsClosedForCapBanks(filterCapControlCache.getCapBanksBySubStation(substation), user));
	String trippedVars = CBCUtils.format( CBCUtils.calcVarsTrippedForCapBanks(filterCapControlCache.getCapBanksBySubStation(substation), user));
%>

	        <tr class="<%=css%>">
				<td>
				<input type="checkbox" name="cti_chkbxSubs" value="<%=substation.getCcId()%>" />
				<a href="#" class="<%=css%>" onclick="postMany('subForm', '<%=CCSessionInfo.STR_SUBID%>', <%=substation.getCcId()%>)" id="anc_<%=substation.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_NAME_COLUMN)%>
				</a>
				<% if(substation.getSpecialAreaEnabled()){
					String spcAreaName = paoDao.getYukonPAOName(substation.getSpecialAreaId()); %>
					 <font color="red">SA <%=spcAreaName%></font>
				<%}%>
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
                <td><a type="param2" name="cti_dyn" id="<%=substation.getCcId()%>">
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