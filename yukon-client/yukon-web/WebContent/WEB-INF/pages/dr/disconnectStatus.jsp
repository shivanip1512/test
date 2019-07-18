<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="disconnectStatus">

    <cti:url var="dataUrl" value="/dr/program/disconnectStatus?programId=${programId}"/>
    <div data-url="${dataUrl}" data-static>
        <table class="compact-results-table">
            <thead>
                <tags:sort column="${device}" />
                <tags:sort column="${status}" />
            </thead>
            <tbody>
                <c:forEach var="disconnectStatus" items="${disconnectStatusList}">
                    <c:set var="pao" value="${disconnectStatus.key}"/>
                    <c:set var="pointData" value="${disconnectStatus.value}"/>
                    <tr>
                        <td>
                            <cti:paoDetailUrl yukonPao="${pao}" newTab="true">${fn:escapeXml(pao.paoName)}</cti:paoDetailUrl>
                        </td>
                        <td>                    
                            <cti:pointValueFormatter format="VALUE" value="${pointData}" />&nbsp;
                            <cti:formatDate type="BOTH" value="${pointData.pointDataTimeStamp}"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
</cti:standardPage>