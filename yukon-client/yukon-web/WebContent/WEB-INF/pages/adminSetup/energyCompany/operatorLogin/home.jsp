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
            var anchor = jQuery(event.currentTarget);
            var icon = anchor.find('.icon');
            var originalClasses = icon.attr('class');
            icon.addClass("icon-loading");
            jQuery.ajax({
                type: "POST",
                url: anchor.attr("href"),
                datatype: "json",
                success: function(jsonResponse) {
                    icon.removeClass("icon-accept");
                    icon.removeClass("icon-delete");
                    icon.removeClass("icon-loading");
                    icon.addClass(jsonResponse.icon);
                    icon.attr("title",jsonResponse.loginStatus);
                },
                error: function() {
                    icon.removeClass("icon-loading");
                    icon.attr("class", originalClasses);
                }
            });
        });
    });
</script>

    <div style="min-width:400px;" class="fl">
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
                                <c:choose>
	                                <c:when test="${login.loginStatus == 'DISABLED'}">
	                                    <c:set var="cssClass" value="icon-delete"/>
	                                </c:when>
	                                <c:otherwise>
	                                   <c:set var="cssClass" value="icon-accept"/>
	                                </c:otherwise>
                                </c:choose>
                                <a href="${operatorLoginUpdateUrl}" class="button naked toggle_status" title="${login.loginStatus}"><i class="icon ${cssClass}"></i></a>
                            </c:if>
                            <c:if test="${currentUserId == login.userID}">
                                <a class="button naked" disabled="disabled" title="<i:inline key=".unableToDeleteCurrentUser"/>"><i class="icon icon-accept"></i></a>
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
            <cti:button nameKey="create" icon="icon-plus-green" href="${operatorLoginCreateUrl}"/>
        </div>
    </tags:boxContainer2>
    </div>
    
</cti:standardPage>