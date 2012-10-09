<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="operatorLogin.home">

<script>
    jQuery(function() {
        jQuery("a.toggle_status").click(function(event) {
            event.preventDefault();// We don't want the anchor href to submit a GET request
            var anchor = jQuery(event.target);
            anchor.addClass("loading");
            jQuery.ajax({
                type: "POST",
                url: anchor.attr("href"),
                datatype: "json",
                success: function(jsonResponse) {
                    anchor.removeClass("ENABLED").removeClass("DISABLED").removeClass("loading");
                    anchor.addClass(jsonResponse.loginStatus);
                    anchor.attr("title",jsonResponse.loginStatus);
                },
                error: function() {
                    anchor.removeClass("loading");
                }
            });
        });
    });
</script>

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="pageName" styleClass="operatorLogins">
                <div id="operatorLoginList" class="scroll_y">
                    <table class="compactResultsTable rowHighlighting">
                        <th><i:inline key=".username" /></th>
                        <th class="removeColumn"><i:inline key=".loginEnabled" /></th>
                        <c:forEach items="${operatorLogins}" var="login">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                <td>
                                    <cti:url var="operatorLoginViewUrl" value="${baseUrl}/view">
                                        <cti:param name="ecId" value="${ecId}"/>
                                        <cti:param name="operatorLoginId" value="${login.userID}"/>
                                    </cti:url>
                                    <b><a href="${operatorLoginViewUrl}"><spring:escapeBody htmlEscape="true">${login}</spring:escapeBody></a></b>
                                </td>
                                <td class="removeColumn">
                                <div class="dib">
                                    <cti:url var="operatorLoginUpdateUrl" value="${baseUrl}/toggleOperatorLoginStatus">
                                        <cti:param name="ecId" value="${ecId}"/>
                                        <cti:param name="operatorLoginId" value="${login.userID}"/>
                                    </cti:url>
                                    <c:if test="${currentUserId != login.userID}">
                                        <a href="${operatorLoginUpdateUrl}" class="icon ${login.loginStatus} toggle_status" title="${login.loginStatus}">${login.loginStatus}</a>
                                    </c:if>
                                    <c:if test="${currentUserId == login.userID}">
                                        <a class="icon enable_disabled" title="<i:inline key=".unableToDeleteCurrentUser"/>"><i:inline key=".unableToDeleteCurrentUser"/></a>
                                    </c:if>
                                </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <cti:url var="operatorLoginCreateUrl" value="${baseUrl}/new">
                    <cti:param name="ecId" value="${ecId}"/>
                </cti:url>
                <div class="actionArea">
                    <cti:button nameKey="create" href="${operatorLoginCreateUrl}"/>
                </div>
            </tags:boxContainer2>

        </cti:dataGridCell>
    </cti:dataGrid>
    
</cti:standardPage>