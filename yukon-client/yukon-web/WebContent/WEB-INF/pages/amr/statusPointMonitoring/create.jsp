<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="statusPointMonitorCreator">
    
    <c:set var="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}"/>
    
    <%-- CREATE FORM --%>
    <cti:url var="createUrl" value="/amr/statusPointMonitoring/create"/>
    <form:form modelAttribute="statusPointMonitor" id="basicInfoForm" action="${createUrl}" method="post">
        <cti:csrfToken/>
        <form:hidden path="statusPointMonitorId"/>
                    
        <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:inputNameValue nameKey="yukon.common.name" path="name" size="60" maxlength="60"/>
                
                <%-- state group --%>
                <tags:nameValue2 nameKey=".selectStateGroup">
                    <select name="stateGroup">
                        <c:forEach items="${stateGroups}" var="stateGroup">
                            <option value="${stateGroup}">${fn:escapeXml(stateGroup.stateGroupName)}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
            
        <%-- create / cancel --%>
        <div class="page-action-area">
            <cti:button nameKey="create" type="submit" classes="primary action" busy="true"/>
            <cti:button type="submit" name="cancel" nameKey="cancel"/>
        </div>
        
    </form:form>
    <script type="text/javascript">
        $(function() {
            $('#basicInfoForm input')[1].focus();
        });
    </script>
</cti:standardPage>