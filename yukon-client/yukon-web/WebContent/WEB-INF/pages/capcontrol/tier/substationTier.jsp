<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

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
        <tr>
            <th>Sub Name</th>
            <th>State</th>
            <th>Available<br> kVARS</th>
            <th>Unavailable <br>kVARS</th>
            <th>Closed <br>kVARS</th>
            <th>Tripped <br>kVARS</th>
            <th>PFactor / Est.</th>
	  </tr>

		<c:forEach var="subStation" items="${subStations}">
	        <tr class="<ct:alternateRow odd="" even="altRow"/>">
				<td>
                    <input type="hidden" id="paoId_${subStation.ccId}" value="${subStation.ccId}">
                    <a class="tierIconLink" href="/editor/cbcBase.jsf?type=2&amp;itemid=${subStation.ccId}&amp;ignoreBookmark=true">
                        <img class="tierImg" src="${editInfoImage}" alt="">
                    </a>
                    <c:if test="${hasEditingRole}">
                        <a class="tierIconLink" href="/editor/deleteBasePAO.jsf?value=${subStation.ccId}">
                            <img class="tierImg" src="/WebConfig/yukon/Icons/delete.gif" alt="">
                        </a>
                    </c:if>

				    <cti:url value="/spring/capcontrol/tier/feeders" var="feederLink">
				    	<cti:param name="areaId" value="${areaId}"/>
				    	<cti:param name="subStationId" value="${subStation.ccId}"/>
				    	<cti:param name="isSpecialArea" value="${isSpecialArea}"/>
				    </cti:url>
				    
				    <a href="${feederLink}" id="anc_${subStation.ccId}">${subStation.ccName}</a>
				    
				    <span class="errorRed">
                        <cti:capControlValue paoId="${subStation.ccId}" type="SUBSTATION" format="SA_ENABLED" />
                    </span>
				</td>
                
				<td>
                    <capTags:warningImg paoId="${subStation.ccId}" type="SUBSTATION"/>
                    <a id="substation_state_${subStation.ccId}"
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