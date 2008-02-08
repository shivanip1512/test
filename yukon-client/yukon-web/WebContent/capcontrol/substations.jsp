<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<%@ page import="com.cannontech.common.constants.LoginController" %>
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
    final CBCDisplay cbcDisplay = new CBCDisplay(user);
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
    String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover"; 
    
	Integer areaId = ccSession.getLastAreaId();
	String area = paoDao.getYukonPAOName(areaId);
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
  <cti:crumbLink url="subareas.jsp" title="Home" /> 
  <cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
  <%
  } else{
  %>
  <cti:crumbLink url="subareas.jsp" title="Home" /> 
  <cti:crumbLink url="subareas.jsp" title="Substation Areas" />
  <%
  }
  %>
  <cti:crumbLink url="substations.jsp" title="Substations" />
</cti:breadCrumbs>
  
<script type="text/javascript">
    // handles analysis links (which are not functional for a substation - show error alert)
	function loadPointChartGreyBox(title, url) {
		alert(title + ' is not available for a Substation.\n\nChoose specific Substation Bus or Feeder within a Substation');
		return void(0);
	}
</script>

<cti:titledContainer title="<%="Substation In Area:  " + area%>" id="last_titled_container">
          
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
          
            <table id="subHeaderTable" width="100%" cellspacing="0" cellpadding="0" >
              <tr class="columnHeader lAlign">              
                <td>
                <input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubStation');"/>
                Sub Name</td>
                <td width="2%"></td>
                <td>State</td>
                <td>Available<br/> kVARS</td>
                <td>Unavailable <br/>kVARS</td>
                <td>Closed <br/>kVARS</td>
                <td>Tripped <br/>kVARS</td>
                <td>PFactor / Est.</td>
              </tr>
			</table>
<div>
<table id="subTable" width="100%" cellspacing="0" cellpadding="0" >
<%
String css = "tableCell";
for( int i = 0; i < areaSubs.size(); i++ ) {
    css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
    SubStation substation = areaSubs.get(i);
%>
            <c:set var="thisSubStationId" value="<%=substation.getCcId()%>"/>
            <input type="hidden" id="paoId_${thisSubStationId}" value="${thisSubStationId}"></input>
            
	        <tr class="<%=css%>">
				<td>
				    <input type="checkbox" name="cti_chkbxSubStation" value="${thisSubStationId}" />
				    <a href="javascript:postMany('subForm', '<%=CCSessionInfo.STR_SUBID%>', ${thisSubStationId});" 
                       class="<%=css%>" 
                       id="anc_${thisSubStationId}">
				        <%=substation.getCcName()%>
				    </a>
				    <% if (substation.getSpecialAreaEnabled()){
				        String spcAreaName = paoDao.getYukonPAOName(substation.getSpecialAreaId()); %>
					   <font color="red">SA <%=spcAreaName%></font>
				    <% } %>
				</td>
                
                <td>
                    <ct:warningImg paoId="${thisSubStationId}" type="SUBSTATION"/>
                </td>
                
				<td>
                    <a id="substation_state_${thisSubStationId}" 
                       style="<%=css%>"
                    <% if( hasControl ) { %>
					   href="javascript:void(0);"
					   <%=popupEvent%>="getSubstationMenu('${thisSubStationId}');"
                    <% } %> >
                        <cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="STATE" />
                    </a>
                    <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${thisSubStationId}')" initialize="true" value="SUBSTATION/${thisSubStationId}/STATE"/>
            	</td>
                
				<td><cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="KVARS_AVAILABLE" /></td>
                <td><cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" /></td>
                <td><cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="KVARS_CLOSED" /></td>
                <td><cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="KVARS_TRIPPED" /></td>
                <td><cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="PFACTOR" /></td>
            </tr>
<% } %>

            </table>
	</div>
</form>
<%}%>
<script type="text/javascript">
    Event.observe(window, 'load', function() { new CtiNonScrollTable('subTable','subHeaderTable');});
    Event.observe(window, 'load', checkPageExpire);
</script>

</cti:titledContainer>

<ct:commandMsgDiv/>

    <ct:dataUpdateEnabler disableHighlight="true"/>
</cti:standardPage>