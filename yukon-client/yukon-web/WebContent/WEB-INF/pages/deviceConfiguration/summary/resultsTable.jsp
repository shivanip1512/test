<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
    
<cti:msgScope paths="modules.tools.configs.summary">
    
    <span class="fwn"><i:inline key=".filteredResults"/></span>
    <span class="badge">${results.hitCount}</span>&nbsp;<i:inline key=".devices"/>
    <c:choose>
        <c:when test="${results.hitCount > 0}">
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                        <cm:dropdownOption key=".changeConfig" classes="js-collection-action" icon="icon-page-edit" data-collection-action="ASSIGN"/> 
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                        <cm:dropdownOption key=".uploadConfig" classes="js-collection-action" icon="icon-ping" data-collection-action="SEND"/> 
                        <cm:dropdownOption key=".validateConfig" classes="js-collection-action" icon="icon-read" data-collection-action="READ"/> 
                    </cti:checkRolesAndProperties>
                    <cm:dropdownOption key=".download" classes="js-config-download" icon="icon-page-white-excel"/>
                </cm:dropdown>
            </span>
            
            <table id="summary-table" class="compact-results-table row-highlighting has-actions">
                <thead>
                    <tr>
                        <tags:sort column="${deviceName}" />
                        <tags:sort column="${type}" />  
                        <tags:sort column="${deviceConfiguration}" />                 
                        <tags:sort column="${lastAction}" />                  
                        <tags:sort column="${lastActionStatus}" />                  
                        <tags:sort column="${currentState}" />                  
                        <tags:sort column="${lastActionStart}" />                 
                        <tags:sort column="${lastActionEnd}" />                  
                        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <cti:msg2 var="naText" key="yukon.common.na"/>
                    <c:forEach var="detail" items="${results.resultList}">
                        <c:set var="deviceId" value="${detail.device.paoIdentifier.paoId}"/>
                        <tr data-device-id="${deviceId}">
                            <%@ include file="summaryResultRow.jsp" %>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${results}" adjustPageCount="true" thousands="true"/>
        </c:when>
        <c:otherwise>
            <br/><br/><span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span>
        </c:otherwise>
    </c:choose>
    
</cti:msgScope>
