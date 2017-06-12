<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dashboard">

    <cti:url var="action" value="/dashboards/assignUsers"/>
    <form action="${action}" method="POST">
        <cti:csrfToken/>
        <input type="hidden" id="dashboardId" value="${dashboardId}"/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".dashboard">
                <select id="dashboardId">
                    <c:forEach var="dashboard" items="${dashboards}">
                        <option value="${dashboard.dashboardId}" <c:if test="${dashboardId == dashboard.dashboardId}">selected="selected" </c:if>>${dashboard.name}</option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".dashboardPage">
                <select id="dashboardType">
                    <c:forEach var="pageType" items="${pageTypes}">
                        <option value="${pageType}"><cti:msg2 key=".pageType.${pageType}"/></option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <br/>
        
         <div id="users-tab">  
             <tags:pickerDialog type="userPicker" id="dashboardUsersPicker" container="users-tab" multiSelectMode="true"/>
         </div>

    </form>
<script>
$(function() {             
    yukon.pickers['dashboardUsersPicker'].show(); 
});
</script>
            
</cti:msgScope>

