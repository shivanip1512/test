<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="tools" page="bulk.assignConfig">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.assignConfig" deviceCollection="${deviceCollection}">
        <c:choose>
            <c:when test="${fn:length(existingConfigs) > 0}">
                <div class="page-action-area">
                    <cti:url var="assignUrl"  value="/bulk/config/doAssignConfig" />
                    <form id="assignConfigForm" method="post" action="${assignUrl}">
                        <cti:csrfToken/>
                        <%-- DEVICE COLLECTION --%>
                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
                        <label>
                            <span class="fl"><i:inline key=".selectLabel"/></span>
                            <select id="configuration" name="configuration" class="fl" style="margin-left:5px;">
                                <c:forEach var="config" items="${existingConfigs}">
                                    <option value="${config.configurationId}">${config.name}</option>
                                </c:forEach>
                            </select>
                        </label>
                        <cti:button nameKey="assign" type="submit" name="assignButton" classes="primary action" busy="true"/>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <span><i:inline key=".none"/></span>
                <div class="page-action-area">
                    <cti:button nameKey="back" onclick="history.back(-1);"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:bulkActionContainer>
    
</cti:standardPage>