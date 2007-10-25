<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Temp Move Report" module="capcontrol">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.yukon.cbc.CapBankDevice" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.core.dao.*" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.cbc.util.CBCDisplay" %>
<%@ page import="com.cannontech.cbc.util.CBCUtils" %>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas" />

</cti:breadCrumbs>
<%
String nd = "\"return nd();\"";
CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);			
String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
if (popupEvent == null) popupEvent = "onmouseover";
List areas = capControlCache.getCbcAreas();
List movedCaps = new ArrayList(10);   
   for (Iterator iter = areas.iterator(); iter.hasNext();) {
    CBCArea area = (CBCArea) iter.next();
	CapBankDevice[] capBanks = capControlCache.getCapBanksByArea(area.getPaoID());
	for (int i=0; i < capBanks.length; i++) {
		CapBankDevice capBank = capBanks[i];
		if (capBank.isBankMoved())
			movedCaps.add(capBank);
		}
	}


%>

<cti:titledContainer title="Moved Cap Banks" >
       <table id=movedCBHeaderTable width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr class="columnHeader lAlign">
         <td>Recent Feeder</td>
         <td>Original Feeder </td>
         <td>Cap Bank</td>
         </tr>
       </table>
       <div>
			<table id="movedCBTable" width="98%" border="0" cellspacing="0" cellpadding="0">
<%
String css = "tableCell";
for( int i = 0; i < movedCaps.size(); i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	CapBankDevice cap = (CapBankDevice)movedCaps.get(i);
%>
				<tr class="<%=css%>">
					<td id="<%=cap.getCcName()%>">
			          	<input id="cmd_<%=cap.getCcId()%>" type="hidden" value= "" name="pf_hidden" />
			          	<a href="javascript:void(0);"
			          			class="warning" <%= popupEvent %>="return overlib($F('cmd_<%=cap.getCcId()%>'),
			          			STICKY, WIDTH,155, HEIGHT,50, OFFSETX,-15,OFFSETY,-15, MOUSEOFF, FULLHTML);"

										onmouseout = <%=nd %> >
										<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(cap, CBCDisplay.CB_PARENT_COLUMN)%>
						</a>					
					</td>
					<td><%=DaoFactory.getPaoDao().getYukonPAOName( cap.getOrigFeederID() )%></td>
					<td><%=cap.getCcName()%></td>
				</tr>
				
<%
}
%>				
			</table>
		</div>
<script type="text/javascript">
Event.observe(window, 'load', function() { 	
								new CtiNonScrollTable('movedCBTable','movedCBHeaderTable');
								
});

Event.observe(window, 'load', function() { 	
								getMoveBackMenu();
								
});

</script>		
</cti:titledContainer>
</cti:standardPage>
