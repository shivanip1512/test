<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<cti:standardPage title="${mainTitle}" module="capcontrol" page="substations">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
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
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
    
    <tags:boxContainer hideEnabled="false" title="${containerTitle}">
        
        <table id="subTable" class="tierTable">
            <thead>
                <tr>
                    <th>Sub Name</th>
                    <th>Actions</th>
                    <th>State</th>
                    <th>Available<br>kVARS</th>
                    <th>Unavailable<br>kVARS</th>
                    <th>Closed<br>kVARS</th>
                    <th>Tripped<br>kVARS</th>
                    <th>PFactor / Est.</th>
                </tr>
            </thead>
    
            <c:forEach var="subStation" items="${subStations}">
            
                <!-- Setup Variables -->
                <c:set var="substationId" value="${subStation.ccId}"/>
                
                <c:set var="menuEvent" value="getSubstationMenu('${substationId}', event)"/> 
                
                <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                    <cti:param name="itemid" value="${substationId}"/>
                    <cti:param name="type" value="2"/>
                </cti:url>
                
                <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                    <cti:param name="value" value="${substationId}"/>
                </cti:url>
                
                <cti:url value="/spring/capcontrol/tier/feeders" var="feederLink">
                    <cti:param name="areaId" value="${areaId}"/>
                    <cti:param name="subStationId" value="${substationId}"/>
                    <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                </cti:url>
            
    	        <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                
    				<td>
                        <input type="hidden" id="paoId_${substationId}" value="${substationId}">
    				    <a href="${feederLink}" id="anc_${substationId}">
                            <spring:escapeBody>${subStation.ccName}</spring:escapeBody>
                        </a>
    				    <span class="errorRed textFieldLabel">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED" />
                        </span>
    				</td>
                    
                    <td>
                        <cti:img nameKey="${editKey}" href="${editUrl}" styleClass="tierIconLink"/>
                        <c:if test="${hasEditingRole}">
                            <cti:img nameKey="remove" href="${deleteUrl}" styleClass="tierIconLink"/>
                        </c:if>
                    </td>
                    
    				<td>
                        <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                        <a id="substation_state_${substationId}" <c:if test="${hasSubstationControl}">href="javascript:void(0);" onclick="${menuEvent}"</c:if>>
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE" />
                        </a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${substationId}')" initialize="true" value="SUBSTATION/${substationId}/STATE"/>
                	</td>
                    
    				<td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" /></td>
                </tr>
            </c:forEach>
    		
        </table>
    
    </tags:boxContainer>

    <script type="text/javascript">
        Event.observe(window, 'load', checkPageExpire);
    </script>
    
</cti:standardPage>