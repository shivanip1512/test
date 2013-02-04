<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="accountList">

    <cti:checkYukonUserAssociatedWithEC showError="true" >
        <%-- SEACRH WIDGET --%>
        <cti:checkRolesAndProperties value="OPERATOR_ACCOUNT_SEARCH">
            <form id="searchForm" action="/stars/operator/account/search" method="get">
                
                <div style="padding-top:8px;padding-bottom:8px;">
                    
                    <select name="searchBy" onchange="$('accountSearchValue').value = ''">
                        <c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}" >
                            <option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == accountSearchResultHolder.searchBy}">selected</c:if>>
                                <cti:msg2 key="${operatorAccountSearchBy.formatKey}"/>
                            </option>
                        </c:forEach>
                    </select>
                    
                    <input type="text" name="searchValue" id="accountSearchValue" value="${accountSearchResultHolder.searchValue}">
                    
                    <cti:button nameKey="search" type="submit" />
                
                </div>
                
            </form>
            <br>
        </cti:checkRolesAndProperties>
    
        <%-- RESULTS --%>
        <c:if test="${accountSearchResultHolder.accountSearchResults.hitCount > 0}">
        
            <cti:msg2 var="resultBoxtitleText" key=".resultBoxtitle" arguments="${searchResultTitleArguments}"/>
            <tags:pagedBox searchResult="${accountSearchResultHolder.accountSearchResults}" baseUrl="/stars/operator/account/search" title="${resultBoxtitleText}">
            
                <table class="compactResultsTable rowHighlighting">
                    <thead>
                        <tr>
                            <th><i:inline key=".accountNumberHeader"/></th>
                            <th><i:inline key=".nameHeader"/></th>
                            <th><i:inline key=".phoneNumberHeader"/></th>
                            <th><i:inline key=".addressHeader"/></th>
                            <c:if test="${searchMembers}">
                                <th><i:inline key=".energyCompanyHeader"/></th>
                            </c:if>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="accountSearchResult" items="${accountSearchResultHolder.accountSearchResults.resultList}">
                    
                            <tr style="vertical-align:top;" class="<tags:alternateRow odd="" even="altRow"/>">
                            
                                <td>
                                    <cti:url var="accountEditUrl" value="/stars/operator/account/view">
                                        <cti:param name="accountId" value="${accountSearchResult.accountId}"/>
                                    </cti:url>
                                    <a href="${accountEditUrl}">
                                           ${fn:escapeXml(accountSearchResult.accountNumber)}
                                       </a>
                                </td>
                                
                                <td>
                                    ${fn:escapeXml(accountSearchResult.combinedName)}
                                </td>
                                
                                <td>
                                    <tags:homeAndWorkPhone homePhoneNotif="${accountSearchResult.homePhoneNotif}" workPhoneNotif="${accountSearchResult.workPhoneNotif}"/>
                                </td>
                                
                                <td style="padding-bottom:8px;">
                                    <tags:address address="${accountSearchResult.address}"/>
                                </td>
                                
                                <c:if test="${searchMembers}">
                                    <td>
                                        ${fn:escapeXml(accountSearchResult.energyCompanyName)}
                                    </td>
                                </c:if>
                            
                            </tr>
                        
                        </c:forEach>
                    </tbody>
                </table>
            
            </tags:pagedBox>
            
        </c:if>
    </cti:checkYukonUserAssociatedWithEC>
</cti:standardPage>