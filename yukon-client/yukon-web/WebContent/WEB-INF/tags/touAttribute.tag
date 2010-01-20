<%@ attribute name="headerKey" required="true" %>
<%@ attribute name="usageAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>
<%@ attribute name="peakAttribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="${headerKey}" var="headerName" />

<c:if test="${not empty usageAttribute || not empty peakAttribute}">
	<table class="compactResultsTable">
		<tr>
            <th colspan="2">${headerName}</th>
        </tr>
		<tr>
			<td>
				<ct:nameValueContainer>
					<c:if test="${not empty usageAttribute}" >
						<ct:nameValue name="${usageAttribute.description}" nameColumnWidth="200px">
							<ct:attributeValue device="${meter}" attribute="${usageAttribute}" />
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</c:if>
					<c:if test="${not empty peakAttribute}" >
						<ct:nameValue name="${peakAttribute.description}" nameColumnWidth="200px">
							<ct:attributeValue device="${meter}" attribute="${peakAttribute}" />
						</ct:nameValue>
						<ct:nameValueGap gapHeight="6px" />
					</c:if>
				</ct:nameValueContainer>
			</td>
		</tr>
	</table>
	<br />
	
</c:if>