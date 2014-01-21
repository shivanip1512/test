<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="validation.review">
    <cti:includeScript link="/JavaScript/veeReview.js"/>

    <cti:msg2 var="reloading" key=".reloading"/>
    <form id="reloadForm" action="/common/veeReview/home" method="get" data-reloadmsg="${reloading}">
           <c:if test="${!noPoints}">
               <input type="hidden" name="afterPaoId" value="${afterPaoId}">
           </c:if>
    </form>
    <c:choose>

    <c:when test="${fn:length(groupedExtendedReviewPoints) == 0}">
        <span class="empty-list"><i:inline key=".emptyList" /></span>
    </c:when>

    <c:otherwise>

    <form id="saveForm" action="/common/veeReview/save" method="post">
        <input type="hidden" name="afterPaoId" value="${nextPaoId}">
        <input type="hidden" id="checkAllState" value="">

        <c:if test="${!noPoints}">
            <div class="column-8-16 stacked clearfix">
                <div class="column one">
                    <tags:sectionContainer2 nameKey="displayTypes">
                        <cti:dataGrid cols="2" tableClasses="compact-results-table" tableStyle="width:90%;">
                            <c:forEach var="displayType" items="${displayTypes}">
                                <cti:dataGridCell>
                                    <label>
                                        <input type="checkbox" name="${displayType.rphTag}" <c:if test="${displayType.checked}">checked</c:if>>
                                        <cti:logo key="${displayType.rphTag.logoKey}"/>
                                        <cti:msg key="${displayType.rphTag.formatKey}"/>
                                        (${displayType.count})
                                    </label>
                                </cti:dataGridCell>
                            </c:forEach>
                        </cti:dataGrid>
                        <div class="action-area">
                            <cti:button id="reloadButton" nameKey="reload" type="button" onclick="Yukon.veeReview.reloadForm();" busy="true"/>
                        </div>
                    </tags:sectionContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:sectionContainer2 nameKey="instructions">
                        <div class="column-12-12">
                            <div class="column one">
                                <h4>
                                    <i:inline key=".delete"/>
                                </h4>
                                <i:inline key=".deleteInfo"/>
                            </div>
                            <div class="column two nogutter">
                                <h4>
                                    <i:inline key=".accept"/>
                                </h4>
                                <i:inline key=".acceptInfo"/>
                            </div>
                        </div>
                    </tags:sectionContainer2>
                </div>
            </div>
        </c:if>

        <%-- REVIEW TABLE --%>
        <table class="compact-results-table row-highlighting stacked">
            <tr>
                <th><i:inline key=".device"/></th>
                <th><i:inline key=".previous"/></th>
                <th><i:inline key=".flagged"/></th>
                <th><i:inline key=".next"/></th>
                <th align="center">
                    <i:inline key=".delete"/>
                    <cti:button renderMode="image" icon="icon-cross" onclick="Yukon.veeReview.checkUncheckAll('DELETE');"/>
                </th>
                <th align="center" >
                    <i:inline key=".accept"/>
                    <cti:button renderMode="image" icon="icon-tick" onclick="Yukon.veeReview.checkUncheckAll('ACCEPT');"/>
                </th>
            </tr>

            <c:forEach var="entry" items="${groupedExtendedReviewPoints}">
                <c:set var="pList" value="${entry.value}"/>
                <c:forEach var="p" items="${pList}" varStatus="status">
                    <c:set var="changeId" value="${p.reviewPoint.changeId}"/>
                    <tr>
                        <c:if test="${status.count == 1}">
                            <td rowspan="${fn:length(pList)}" style="vertical-align:top;">
                                <cti:paoDetailUrl yukonPao="${p.reviewPoint.displayablePao}">
                                    ${fn:escapeXml(entry.key)}
                                </cti:paoDetailUrl>
                            </td>
                        </c:if>
                        <td>
                            <cti:pointValueFormatter value="${p.prevPointValue}" format="FULL"/>
                        </td>
                        <td title="${p.reviewPoint.changeId}">
                            <div style="float:left;">
                                <cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="FULL"/> 
                            </div>
                            <div style="float:right;padding-right:10px;">
                                <c:forEach var="otherTag" items="${p.otherTags}">
                                    <cti:logo key="${otherTag.logoKey}"/>
                                </c:forEach>
                                <cti:logo key="${p.reviewPoint.rphTag.logoKey}"/>
                            </div>
                        </td>

                        <td><cti:pointValueFormatter value="${p.nextPointValue}" format="FULL"/></td>

                        <td align="center" class="ACTION_TD pointer">
                            <cti:button id="ACTION_DELETE_IMG_${changeId}" icon="icon-cross disabled" renderMode="image"/>
                        </td>

                        <td align="center" class="ACTION_TD pointer">
                            <cti:button id="ACTION_ACCEPT_IMG_${changeId}" icon="icon-tick disabled" renderMode="image"/>
                            <input id="ACTION_${changeId}" name="ACTION_${changeId}" type="hidden" value="">
                        </td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </table>

        <c:if test="${fn:length(groupedExtendedReviewPoints) > 0}">
            <div class="action-area">
            <cti:button type="submit" nameKey="saveAndContinue" classes="f-disable-after-click" busy="true"/>
            </div>
        </c:if>
    </form>
    </c:otherwise>
    </c:choose>
</cti:standardPage>