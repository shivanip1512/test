<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="Temp Move Report" module="capcontrol">

<%@include file="cbc_inc.jspf"%>

<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.yukon.cbc.CapBankDevice" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.cannontech.cbc.util.CBCDisplay" %>

<cti:url var="movedCapBanksUrl" value="/capcontrol/movedCapBanks.jsp"/>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu menuSelection="view|recentcapbankmoves"/>

<cti:breadCrumbs>
	<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
</cti:breadCrumbs>

<%
String nd = "\"return nd();\"";
CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);

HashMap<String, String[]> parameters = new HashMap<String, String[]>();

final CBCDisplay cbcDisplay = new CBCDisplay(user);

String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
if (popupEvent == null) {
	popupEvent = "onmouseover";
}

List<CCArea> areas = capControlCache.getCbcAreas();
List<CapBankDevice> movedCaps = new ArrayList<CapBankDevice>(10);   
for (CCArea area : areas) {
	List<CapBankDevice> capBanks = capControlCache.getCapBanksByArea(area.getPaoId());
	for (CapBankDevice capBank : capBanks) {
		if (capBank.isBankMoved()) {
			movedCaps.add(capBank);
		}
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
			<table id="movedCBTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<%
String css = "tableCell";
for (int i = 0; i < movedCaps.size(); i++) {
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	CapBankDevice cap = (CapBankDevice)movedCaps.get(i);
	String rowColor = ((i % 2) == 0) ? "#eeeeee" : "white";
%>
                <c:set var="thisCapBankId" value="<%=cap.getCcId()%>"/>
				<tr id="tr_cap_${thisCapBankId}" class="<%=css%>">
					<td id="<%=cap.getCcName()%>">
			          	<a href="javascript:void(0);"
			          	   class="warning"
                           <%=popupEvent%>="getCapBankTempMoveBack('${thisCapBankId}', '${movedCapBanksUrl}')"
                           onmouseout = "hidePopupHiLite('tr_cap_${thisCapBankId}', '<%=rowColor%>');"
                        > 
                            <%=cbcDisplay.getCapBankValueAt(cap, CBCDisplay.CB_PARENT_COLUMN)%>
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
