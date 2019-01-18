<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="widgets.infrastructureWarnings">

    
    <cti:url var="dataUrl" value="/stars/infrastructureWarnings/filteredResults">
        <c:forEach var="type" items="${selectedTypes}">
            <cti:param name="types" value="${type}"/>
        </c:forEach>
    </cti:url>
    <div data-url="${dataUrl}">
            
        <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
        <span class="label bg-color-orange">${warnings.hitCount}</span>&nbsp;<i:inline key=".warnings"/>
        
        <c:if test="${warnings.hitCount > 0}">
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <cm:dropdownOption key="yukon.common.collectionActions" icon="icon-cog-go" classes="js-collection-action" data-url="/bulk/collectionActions"/> 
                    <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download-warnings"/>  
                    <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" classes="js-collection-action" data-url="/tools/map"/>
                </cm:dropdown>
            </span>
        </c:if>
        
        <table class="compact-results-table row-highlighting">
            <th class="row-icon" /> 
            <tags:sort column="${name}" />                
            <tags:sort column="${type}" />                
            <tags:sort column="${status}" />
            <tags:sort column="${timestamp}" />
        
            <c:choose>
                <c:when test="${warnings.hitCount > 0}">
                    <c:forEach var="warning" items="${warnings.resultList}">
                        <tr>
                            <td>
                                <c:if test="${notesList.contains(warning.paoIdentifier.paoId)}">
                                    <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                    <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${warning.paoIdentifier.paoId}"/>
                                </c:if>
                            </td>
                            <td class="wsnw">
                                <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                                    <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                                </cti:paoDetailUrl>
                            </td>
                            <td class="wsnw">${warning.paoIdentifier.paoType.paoTypeName}</td>
                            <td>
                                <c:set var="warningColor" value="warning"/>
                                <c:if test="${warning.severity == 'HIGH'}">
                                    <c:set var="warningColor" value="error"/>
                                </c:if>
                                <span class="${warningColor}"><cti:msg2 key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/></td>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${warning.timestamp lt epoch1990}">
                                        <i:inline key="yukon.common.dashes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${warning.timestamp}" type="FULL"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="3">
                            <span class="empty-list"><i:inline key=".noWarnings" /></span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
        <tags:pagingResultsControls result="${warnings}" adjustPageCount="true"/>
    
    </div>

</cti:msgScope>
