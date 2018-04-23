<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="rtuList">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/rtu/create" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="create-option" href="${createUrl}"/>
    </div>

    <hr/>

    <cti:url var="dataUrl" value="/stars/rtu-list" />
    <form action="${dataUrl}" method="get" id="filter-rtu-form">
        <i:inline key="yukon.common.filterBy"/>
        <select id="rtuType" name="rtuType">
            <option value="AllTypes" label="<i:inline key="yukon.web.modules.operator.rtu.allTypes"/>"/>
            <c:forEach var="rtuType" items="${rtuTypes}">
                <c:set var="isSelected" value=""/>
                <c:if test="${rtuType == selectedRtuType}">
                    <c:set var="isSelected" value="selected"/>
                </c:if>
                <option value="${rtuType}" label="<i:inline key="${rtuType}"/>" ${isSelected}/>
            </c:forEach>
        </select>
        <cti:button type="submit" classes="action primary fn vab" nameKey="filter" />
    </form>

    <hr/>
        
    <!-- RTU Table -->
    <c:choose>
        <c:when test="${rtus.hitCount > 0}">
            <div id="rtu-list-container" data-url="${dataUrl}" data-static>
                <table class="compact-results-table has-actions row-highlighting">
                    <thead>
                        <tr>
                            <tags:sort column="${name}" />
                            <tags:sort column="${type}" />
                            <tags:sort column="${status}" />
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="rtu" items="${rtus.resultList}">
                            <c:set var="cssClass" value="success" />
                            <cti:msg2 var="rtuStatus" key="yukon.common.enabled"/>
                            <c:if test="${rtu.disableFlag == 'Y'}">
                                <c:set var="cssClass" value="error" />
                                <cti:msg2 var="rtuStatus" key="yukon.common.disabled"/>
                            </c:if>
                            <tr>
                                <c:choose>
                                    <c:when test="${rtu.paoType == 'RTU_DNP'}">
                                        <cti:url var="rtuDnpUrl" value="/stars/rtu/${rtu.paoIdentifier.paoId}"/>
                                        <td><a href="${rtuDnpUrl}">${fn:escapeXml(rtu.paoName)}</a></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>${rtu.paoName}</td>
                                    </c:otherwise>
                                </c:choose>
                                <td><i:inline key="${rtu.paoType}"/></td>
                                <td class="${cssClass}">${fn:escapeXml(rtuStatus)}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${rtus}" adjustPageCount="true"/>
            </div>
            
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:otherwise>
    </c:choose>

    <cti:includeScript link="/resources/js/pages/yukon.assets.rtu.js" />

</cti:standardPage>