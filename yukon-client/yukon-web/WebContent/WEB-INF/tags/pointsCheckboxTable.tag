<%@ attribute name="deviceType" required="true" type="java.lang.Integer"%>
<%@ attribute name="pointTemplates" required="true" type="java.util.List"%>
<%@ attribute name="columnCount" required="true" type="java.lang.Integer"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<c:set var="columnPercentage" value="${100 / columnCount}"/>
			
<cti:dataGrid cols="${columnCount}" tableClasses="compact-results-table" cellStyle="width:${columnPercentage}%;" orderMode="TOP_TO_BOTTOM">
	<c:forEach var="pointTemplateMask" items="${pointTemplates}">
	
        <cti:dataGridCell>
			<c:set var="disabled" value="${pointTemplateMask.masked ? 'disabled' : ''}"/>
			<c:set var="className" value="${pointTemplateMask.masked ? 'subtle' : ''}"/>
		
			<label>
				<input type="checkbox" ${disabled} class="js-point-checkbox"
                    name="PT:${deviceType}:${pointTemplateMask.pointTemplate.pointIdentifier.type}:${pointTemplateMask.pointTemplate.pointIdentifier.offset}">
			    <span class="${className}">${pointTemplateMask.pointTemplate.name} [#${pointTemplateMask.pointTemplate.pointIdentifier.offset}]</span>
			</label>
        </cti:dataGridCell>    
        
	</c:forEach>
</cti:dataGrid>
<br>
