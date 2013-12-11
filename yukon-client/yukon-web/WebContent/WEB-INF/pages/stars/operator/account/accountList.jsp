<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="accountList">

    <cti:checkEnergyCompanyOperator showError="true" >
        <div class="column-12-12 stacked">
            <div class="column one">
                <tags:widget bean="operatorAccountSearchWidget" container="section"/>
            </div>
        </div>
    
        <%-- RESULTS --%>
        <c:if test="${accountSearchResultHolder.accountSearchResults.hitCount > 0}">
        
            <cti:msg2 var="resultBoxtitleText" key=".resultBoxtitle" arguments="${searchResultTitleArguments}"/>
            <tags:pagedBox searchResult="${accountSearchResultHolder.accountSearchResults}" baseUrl="search" title="${resultBoxtitleText}" styleClass="clear">
            
                <table class="compact-results-table row-highlighting">
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
                    
                            <tr style="vertical-align:top;">
                            
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
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>