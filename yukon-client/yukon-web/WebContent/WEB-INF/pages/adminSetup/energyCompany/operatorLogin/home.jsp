<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="operatorLogin.home">

<script>
document.observe("dom:loaded", function() {
    $$("a.toggle_status").each(function(elem){
        elem.observe('click', function(event){
            event.stop();
            event.target.addClassName("loading");
            new Ajax.Request(this.href, {
                onSuccess: function(xhr){
                    event.target.removeClassName("ENABLED").removeClassName("DISABLED").removeClassName("loading");
                    event.target.addClassName(xhr.responseJSON.loginStatus);
                    event.target.title = xhr.responseJSON.loginStatus;
                },
                onFailure: function(xhr){
                    event.target.removeClassName("loading");
                }
            });
        });        
    });
});
</script>

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="pageName" styleClass="operatorLogins">
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
                                <a href="${operatorLoginUpdateUrl}" class="icon ${login.loginStatus} toggle_status" title="${login.loginStatus}">${login.loginStatus}</a>
                            </div>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <cti:url var="operatorLoginCreateUrl" value="${baseUrl}/new">
                    <cti:param name="ecId" value="${ecId}"/>
                </cti:url>
                <div class="actionArea">
                    <cti:button key="create" href="${operatorLoginCreateUrl}"/>
                </div>
            </tags:boxContainer2>

        </cti:dataGridCell>
    </cti:dataGrid>
    
</cti:standardPage>