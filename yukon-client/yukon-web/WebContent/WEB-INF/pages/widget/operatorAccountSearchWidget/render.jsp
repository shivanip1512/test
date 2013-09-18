<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:checkRolesAndProperties value="OPERATOR_ACCOUNT_SEARCH">
    <cti:url var="submitUrl" value="/stars/operator/account/search"/>
    <form action="${submitUrl}" method="get">
        <table class="full_width">
            <tr>
                <td>
            		<select name="searchBy" class="full_width">
            			<c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}">
            				<option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == searchBy}">selected</c:if>>
            					<i:inline key="${operatorAccountSearchBy.formatKey}"/>
            				</option>
            			</c:forEach>
            		</select>
                </td>
                <td>
            		<input id="account-search-field" type="text" name="searchValue" value="${searchValue}" class="full_width">
                </td>
            </tr>
        </table>
    	<div class="actionArea">
            <cti:button nameKey="search" type="submit" classes="f-blocker fr"/>
    	</div>
    	
    </form>
</cti:checkRolesAndProperties>
