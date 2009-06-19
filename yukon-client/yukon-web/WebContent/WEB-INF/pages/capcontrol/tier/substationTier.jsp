<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.core.dao.PaoDao" %>

<%@page import="org.springframework.web.bind.ServletRequestUtils"%>


<cti:standardPage title="${mainTitle}" module="capcontrol">
<%@include file="/capcontrol/cbc_inc.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
<!-- DIV element for the non flyover type popups -->
<ct:simplePopup onClose="closeTierPopup()" title="Comments:" id="tierPopup" styleClass="thinBorder">
    <div id="popupBody"></div>
</ct:simplePopup>

<cti:standardMenu/>
<cti:breadCrumbs>

<c:choose >
	<c:when test="${isSpecialArea}">
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" /> 
		<cti:crumbLink url="/spring/capcontrol/tier/areas?isSpecialArea=${isSpecialArea}" title="Special Substation Areas" />
	</c:when>
	<c:otherwise>
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" /> 
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Substation Areas" />
	</c:otherwise>
</c:choose>

  <cti:crumbLink  title="${areaName}" />
</cti:breadCrumbs>
  
<script type="text/javascript">

	var GB_IMG_DIR = "/editor/css/greybox/";
	GreyBox.preloadGreyBoxImages();

	// handles analysis links (which are not functional for a substation - show error alert)
	function loadPointChartGreyBox(title, url) {
		alert(title + ' is not available for a Substation.\n\nChoose specific Substation Bus or Feeder within a Substation');
		return void(0);
	}
</script>

<c:choose>
    <c:when test="${hasEditingRole}">
        <c:set var="editInfoImage" value="/editor/images/edit_item.gif"/>
    </c:when>
    <c:otherwise>
        <c:set var="editInfoImage" value="/editor/images/info_item.gif"/>
    </c:otherwise>
</c:choose>

<ct:abstractContainer type="box" hideEnabled="false" title="${containerTitle}" id="last_titled_container">
          
	<table id="subHeaderTable" width="100%" cellspacing="0" cellpadding="0" >
	  <tr class="columnHeader lAlign">              
	    <td>
	    	<input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubStation');"/>
	    	Sub Name
	    </td>
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

		<c:forEach var="subStation" items="${subStations}">

		            <input type="hidden" id="paoId_${subStation.ccId}" value="${subStation.ccId}"></input>
		            
			        <tr class="<ct:alternateRow odd="altRow" even=""/>" >
						<td>
						    <input type="checkbox" name="cti_chkbxSubStation" value="${subStation.ccId}" />
	                        <a class="editImg" href="/editor/cbcBase.jsf?type=2&itemid=${subStation.ccId}&ignoreBookmark=true">
	                            <img class="rAlign editImg" src="${editInfoImage}"/>
	                        </a>
	                        <c:if test="${hasEditingRole}">
		                        <a class="editImg" href="/editor/deleteBasePAO.jsf?value=${subStation.ccId}">
		                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
		                        </a>
		                    </c:if>

						    <cti:url value="/spring/capcontrol/tier/feeders" var="myLink">
						    	<cti:param name="areaId" value="${areaId}"/>
						    	<cti:param name="subStationId" value="${subStation.ccId}"/>
						    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
						    </cti:url>
						    
						    <a href="${myLink}" class="" id="anc_${subStation.ccId}"> ${subStation.ccName}</a>
						    
						    <font color="red">
		                        <cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="SA_ENABLED" />
		                    </font>
						</td>
		                
		                <td>
		                    <capTags:warningImg paoId="${subStation.ccId}" type="SUBSTATION"/>
		                </td>
		                
						<td>
		                    <a id="substation_state_${subStation.ccId}" style=""
			                    <c:if test="${hasSubstationControl}">
								   href="javascript:void(0);" ${popupEvent}="getSubstationMenu('${subStation.ccId}', event);"
			                    </c:if> 
			                >
		                        <cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="STATE" />
		                    </a>
		                    <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${subStation.ccId}')" initialize="true" value="SUBSTATION/${subStation.ccId}/STATE"/>
		            	</td>
		                
						<td><cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="KVARS_AVAILABLE" /></td>
		                <td><cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" /></td>
		                <td><cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="KVARS_CLOSED" /></td>
		                <td><cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="KVARS_TRIPPED" /></td>
		                <td><cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="PFACTOR" /></td>
		            </tr>
		</c:forEach>
		
		</table>
	</div>


<script type="text/javascript">
    Event.observe(window, 'load', function() { new CtiNonScrollTable('subTable','subHeaderTable');});
    Event.observe(window, 'load', checkPageExpire);
</script>

</ct:abstractContainer>

<capTags:commandMsgDiv/>

    <ct:disableUpdaterHighlights/>
</cti:standardPage>