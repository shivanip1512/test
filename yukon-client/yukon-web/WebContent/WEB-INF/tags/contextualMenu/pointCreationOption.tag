<%@ tag body-content="empty" 
        description="Creates a Create Point menu option (if user has correct permissions) that displays a popup to select a point type to create for the specified pao." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="paoId" type="java.lang.String" required="true" description="The id of the pao that will have the point attached." %>

<c:set var="isPointCreate" value="false" />
<cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
    <c:set var="isPointCreate" value="true" />
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="MANAGE_POINTS" level="CREATE">
    <c:set var="isPointCreate" value="true" />
</cti:checkRolesAndProperties>

<c:if test="${isPointCreate}">

    <cm:dropdownOption key="yukon.common.createPoint" icon="icon-plus-green" data-popup="#createPointPopup"/>
    
    <li class="divider"/>
    
    <div id="createPointPopup" class="dn" data-title="<i:inline key="yukon.common.createPoint"/>" data-width="400">
    
        <div class="column-12-12">
            <div class="column one">
            
                <cti:url var="createAnalog" value="/tools/points/Analog/create">
                    <cti:param name="parentId" value="${paoId}"></cti:param>
                </cti:url>
                <a href="${createAnalog}"><i:inline key="yukon.common.point.pointType.Analog.long"/></a>
                
                <div class="PT10">
                    <cti:url var="createCalcAnalog" value="/tools/points/CalcAnalog/create">
                        <cti:param name="parentId" value="${paoId}"></cti:param>
                    </cti:url>
                    <a href="${createCalcAnalog}"><i:inline key="yukon.common.point.pointType.CalcAnalog.long"/></a>
                </div>
                
                <div class="PT10">
                    <cti:url var="createCalcStatus" value="/tools/points/CalcStatus/create">
                        <cti:param name="parentId" value="${paoId}"></cti:param>
                    </cti:url>
                    <a href="${createCalcStatus}"><i:inline key="yukon.common.point.pointType.CalcStatus.long"/></a>
                </div>
            </div>
            <div class="column two nogutter">

                <cti:url var="createDemandAccumulator" value="/tools/points/DemandAccumulator/create">
                    <cti:param name="parentId" value="${paoId}"></cti:param>
                </cti:url>
                <a href="${createDemandAccumulator}"><i:inline key="yukon.common.point.pointType.DemandAccumulator.long"/></a>
                
                <div class="PT10">
                    <cti:url var="createPulseAccumulator" value="/tools/points/PulseAccumulator/create">
                        <cti:param name="parentId" value="${paoId}"></cti:param>
                    </cti:url>
                    <a href="${createPulseAccumulator}"><i:inline key="yukon.common.point.pointType.PulseAccumulator.long"/></a>
                </div>
                
               <div class="PT10">
                    <cti:url var="createStatus" value="/tools/points/Status/create">
                        <cti:param name="parentId" value="${paoId}"></cti:param>
                    </cti:url>
                    <a href="${createStatus}"><i:inline key="yukon.common.point.pointType.Status.long"/></a>
                </div>

            </div>
        </div>
        
    </div>
    
</c:if>