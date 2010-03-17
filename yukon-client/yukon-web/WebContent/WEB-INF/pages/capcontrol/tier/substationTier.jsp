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
        <c:set var="editInfoImage" value="/WebConfig/yukon/Icons/pencil.gif"/>
    </c:when>
    <c:otherwise>
        <c:set var="editInfoImage" value="/WebContent/WebConfig/yukon/Icons/information.gif"/>
    </c:otherwise>
</c:choose>

<ct:abstractContainer type="box" hideEnabled="false" title="${containerTitle}" id="last_titled_container">
          
	<table id="subTable" class="tierTable">
	  <tr class="columnHeader lAlign">              
	    <th style="font-weight:bold;">
	    	<input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'cti_chkbxSubStation');"/>
	    	Sub Name
	    </th>
	    <th width="2%" style="font-weight:bold;"></th>
	    <th style="font-weight:bold;">State</th>
	    <th style="font-weight:bold;">Available<br/> kVARS</th>
	    <th style="font-weight:bold;">Unavailable <br/>kVARS</th>
	    <th style="font-weight:bold;">Closed <br/>kVARS</th>
	    <th style="font-weight:bold;">Tripped <br/>kVARS</th>
	    <th style="font-weight:bold;">PFactor / Est.</th>
	  </tr>

		<c:forEach var="subStation" items="${subStations}">
	        <tr class="<ct:alternateRow odd="" even="altRow"/>">
				<td>
					<input type="hidden" id="paoId_${subStation.ccId}" value="${subStation.ccId}"></input>
				    <input type="checkbox" name="cti_chkbxSubStation" value="${subStation.ccId}" />
                       <a class="editImg" href="/editor/cbcBase.jsf?type=2&itemid=${subStation.ccId}&ignoreBookmark=true" style="text-decoration:none;">
                           <img class="rAlign editImg" src="${editInfoImage}"/>
                       </a>
                       <c:if test="${hasEditingRole}">
                        <a class="editImg" href="/editor/deleteBasePAO.jsf?value=${subStation.ccId}" style="text-decoration:none;">
                            <img class="rAlign editImg" src="/WebConfig/yukon/Icons/delete.gif"/>
                        </a>
                    </c:if>

				    <cti:url value="/spring/capcontrol/tier/feeders" var="myLink">
				    	<cti:param name="areaId" value="${areaId}"/>
				    	<cti:param name="subStationId" value="${subStation.ccId}"/>
				    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
				    </cti:url>
				    
				    <a href="${myLink}" class="" id="anc_${subStation.ccId}"> ${subStation.ccName}</a>
				    
				    <span class="errorRed">
                        <cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="SA_ENABLED" />
                    </span>
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


<script type="text/javascript">
    Event.observe(window, 'load', checkPageExpire);
</script>

</ct:abstractContainer>

<capTags:commandMsgDiv/>

    <ct:disableUpdaterHighlights/>
</cti:standardPage>