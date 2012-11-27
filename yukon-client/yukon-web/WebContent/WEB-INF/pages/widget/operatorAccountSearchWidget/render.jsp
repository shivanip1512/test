<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:checkRolesAndProperties value="OPERATOR_ACCOUNT_SEARCH">
    <cti:url var="submitUrl" value="/stars/operator/account/search"/>
    <form action="${submitUrl}" method="get">

    	<div style="padding-top:8px;padding-bottom:8px;">
    		
    		<select name="searchBy" onchange="$('searchValue').value = ''">
    			<c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}" >
    				<option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == searchBy}">selected</c:if>>
    					<i:inline key="${operatorAccountSearchBy.formatKey}"/>
    				</option>
    			</c:forEach>
    		</select>
    		
    		<input type="text" name="searchValue" value="${searchValue}">
    		
            <cti:button nameKey="search" type="submit" styleClass="f_blocker"/>
    	
    	</div>
    	
    </form>
</cti:checkRolesAndProperties>
