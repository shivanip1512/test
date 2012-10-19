<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="statusPointMonitorCreator">
    
    <c:set var="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}"/>
    
    <%-- CREATE FORM --%>
    <form:form commandName="statusPointMonitor" id="basicInfoForm" action="/spring/amr/statusPointMonitoring/create" method="post">
    
        <form:hidden path="statusPointMonitorId"/>
                    
        <tags:formElementContainer nameKey="setup">
            <tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
            
                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="statusPointMonitorName" 
                                    size="50" 
                                    maxlength="50"/>
                
                <%-- state group --%>
                <tags:nameValue2 nameKey=".selectStateGroup">
                    <select name="stateGroup">
                        <c:forEach items="${stateGroups}" var="stateGroup">
                            <option value="${stateGroup}">${stateGroup.stateGroupName}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
        </tags:formElementContainer>
            
        <%-- create / cancel --%>
        <div class="pageActionArea">
            <cti:button nameKey="create" type="submit" styleClass="f_blocker"/>
            <input type="submit" name="cancel" class="formSubmit" value="<cti:msg2 key=".cancel"/>">
        </div>
        
    </form:form>
    <script type="text/javascript">
        jQuery(function() {
	        $$('#basicInfoForm input')[1].focus();
	    });
    </script>
</cti:standardPage>
