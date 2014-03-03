<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="amr" page="statusPointMonitorCreator">
    
    <c:set var="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}"/>
    
    <%-- CREATE FORM --%>
    <form:form commandName="statusPointMonitor" id="basicInfoForm" action="/amr/statusPointMonitoring/create" method="post">
    
        <form:hidden path="statusPointMonitorId"/>
                    
        <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="statusPointMonitorName" 
                                    size="50" 
                                    maxlength="50"/>
                
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
        jQuery(function() {
            jQuery('#basicInfoForm input')[1].focus();
        });
    </script>
</cti:standardPage>
