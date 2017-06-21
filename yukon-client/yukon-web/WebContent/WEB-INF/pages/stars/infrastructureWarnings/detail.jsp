<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="infrastructureWarnings">

<cti:msgScope paths="widgets.infrastructureWarnings">

    <div class="column-12-12">
        <div class="column one">
            <cti:url var="action" value="/stars/infrastructureWarnings/detail" />
            <form action="${action}" method="GET">
            <tags:boxContainer2 nameKey="filters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".deviceTypes">
                        <c:forEach var="type" items="${deviceTypes}">
                            <c:set var="selected" value="${false}"/>
                            <c:if test="${fn:contains(selectedTypes, type)}">
                                <c:set var="selected" value="${true}"/>
                            </c:if>
                            <tags:check name="types" classes="M0" value="${type}" label="${type.paoTypeName}" checked="${selected}"></tags:check>
                        </c:forEach>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            
                <div class="action-area">
                    <cti:button nameKey="filter" classes="primary action" type="submit"/>
                </div>
            
            </tags:boxContainer2>
            </form>
        </div>
    </div>

    <cti:url var="dataUrl" value="/stars/infrastructureWarnings/detail">
        <c:forEach var="type" items="${selectedTypes}">
            <cti:param name="types" value="${type}"/>
        </c:forEach>
    </cti:url>
    <div data-url="${dataUrl}" data-static>
    <table class="compact-results-table row-highlighting">
        <tags:sort column="${name}" />                
        <tags:sort column="${type}" />                
        <tags:sort column="${status}" />                
    
        <c:forEach var="warning" items="${warnings.resultList}">
            <tr>
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
            </tr>
        </c:forEach>
    </table>
    <tags:pagingResultsControls result="${warnings}" adjustPageCount="true"/>
    
    </cti:msgScope>
    
</cti:standardPage>