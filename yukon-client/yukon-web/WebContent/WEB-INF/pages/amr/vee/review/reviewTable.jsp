<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.validation.review">

    <c:choose>
        <c:when test="${totalTagCount > 0}">
            <div class="action-area">
                <cti:button id="accept-all" icon="icon-tick" classes="middle" nameKey="acceptAll"/>
                <cti:button id="delete-all" icon="icon-cross" classes="left" nameKey="deleteAll"/>
            </div>
            <table class="compact-results-table row-highlighting stacked has-alerts">
                <thead>
                    <th></th>
                    <th><i:inline key="yukon.web.modules.consumer.alternateEnrollment.device"/></th>
                    <th><i:inline key=".previous"/></th>
                    <th><i:inline key=".flagged"/></th>
                    <th><i:inline key=".next"/></th>
                    <th></th>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${groupedExtendedReviewPoints}">
                        <c:set var="pList" value="${entry.value}"/>
                        <c:forEach var="p" items="${pList}" varStatus="status">
                            <c:set var="changeId" value="${p.reviewPoint.changeId}"/>
                            <tr>
                                <td>
                                    <c:forEach var="otherTag" items="${p.otherTags}">
                                        <cti:icon icon="${otherTag.iconClass}" classes="fn"/>
                                    </c:forEach> 
                                    <cti:icon icon="${p.reviewPoint.rphTag.iconClass}" classes="fn"/>
                                </td>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${p.reviewPoint.displayablePao}">
                                        ${fn:escapeXml(entry.key)}
                                    </cti:paoDetailUrl></td>
                                <td>
                                    <div>
                                        <cti:pointValueFormatter value="${p.prevPointValue}" format="VALUE_UNIT"/>
                                    </div>
                                    <div>
                                        <cti:pointValueFormatter value="${p.prevPointValue}" format="DATE_QUALITY"/>
                                    </div>
                                </td>
                                <td>
                                    <div class="error">
                                        <cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="VALUE_UNIT"/>
                                    </div>
                                    <div class="error">
                                        <cti:pointValueFormatter value="${p.reviewPoint.pointValue}" format="DATE_QUALITY"/>
                                    </div>
                                </td>
                                <td>
                                    <div>
                                        <cti:pointValueFormatter value="${p.nextPointValue}" format="VALUE_UNIT"/>
                                    </div>
                                    <div>
                                        <cti:pointValueFormatter value="${p.nextPointValue}" format="DATE_QUALITY"/>
                                    </div>
                                </td>

                                <td class="ACTION_TD pointer">
                                    <div class="fr">
                                        <cti:button id="ACTION_DELETE_${changeId}" icon="icon-cross" renderMode="buttonImage" classes=" ACTION_BTN left"/>
                                        <cti:button id="ACTION_ACCEPT_${changeId}" icon="icon-tick" renderMode="buttonImage" classes="ACTION_BTN middle"/>
                                        <input id="ACTION_${changeId}" name="ACTION_${changeId}" type="hidden" value="">
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </tbody>
                <tfoot></tfoot>
            </table>
            <tags:pagingResultsControls result="${result}" adjustPageCount="true"/>
            <div class="action-area">
                <cti:button id="saveButton" type="submit" nameKey="saveAndContinue" classes="primary action" busy="true"/>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".emptyList"/></span>
        </c:otherwise>
    </c:choose>
</cti:msgScope>