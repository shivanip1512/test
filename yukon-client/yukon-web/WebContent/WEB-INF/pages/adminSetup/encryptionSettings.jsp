<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="adminSetup" page="encryption">

    <script type="text/javascript">
    Event.observe(window, 'load', function() {
        
        <c:forEach var="route" items="${encryptedRoutes}">
        
            if(${route.encrypted}) {
                showEnabled('${route.paobjectId}')
            } else {
                showDisabled('${route.paobjectId}');
            }
            
        </c:forEach>  
    });
    
        function showDisabled(formId) {
            var formChildren = $(formId).childElements();
            formChildren[0].show();
        }
        
        function cancel() {
            window.location = "view";
        }
        
        function showEnabled(formId) {
            var formChildren = $(formId).childElements();
            formChildren[1].show();
        }
        
        function showAddKey(formId) {
            var formChildren = $(formId).childElements();
            formChildren[0].hide();
            formChildren[2].show();
        }
        
        function addKey(formId) {
            Yukon.ui.blockPage();
            $(formId).action = "saveKey"
            $(formId).submit();
        }
        
        function showDeleteConfirm(formId) {
            var formChildren = $(formId).childElements();
            formChildren[1].hide();
            formChildren[3].show();
        }
        
        function confirmDelete(formId) {
            $(formId).reset();
            Yukon.ui.blockPage();
            $(formId).action = "deleteKey"
            $(formId).submit();
        }
    </script>
    
    
    <table class="resultsTable">
        <tr>
            <th><i:inline key=".paoNameLbl"/></th>
            <th><i:inline key=".paoTypeLbl"/></th>
            <th><i:inline key=".CPSkeyLbl"/></th>
        </tr>
        
        <c:forEach var="route" items="${encryptedRoutes}">
            <tr>
                <td>${route.paoName}</td>
                <td>${route.type}</td>
                <td width="50%">
                    <form id="${route.paobjectId}"  method="POST">
                        <span style="display:none">
                            <cti:button key="enableEncryptionBtn" onclick="javascript:showAddKey('${route.paobjectId}')"/>
                        </span>
                        <span style="display:none">
                            <cti:button  key="deleteBtn" onclick="javascript:showDeleteConfirm('${route.paobjectId}')"/>
                            <i:inline key=".keyFoundLbl"/>
                        </span>
                        <span style="display:none">
                            <i:inline key=".keyLbl"/>
                            <input name="value" type="text" value="" size ="50"/>
                            <cti:button key="saveBtn"  onclick="javascript:addKey('${route.paobjectId}')"/>
                            <cti:button key="cancelBtn" onclick="javascript:cancel()"/>
                        </span>
                        <span style="display:none">
                            <i:inline key=".confirmDeleteMsg"/>
                            <cti:button key="confirmDeleteBtn" onclick="javascript:confirmDelete('${route.paobjectId}')"/>
                            <cti:button key="cancelBtn" onclick="javascript:cancel()"/>
                        </span>
                        <input name="paobjectId" type="hidden" value="${route.paobjectId}"/>
                        <input name="type" type="hidden" value="${route.type}"/>
                        <input name="paoName" type="hidden" value="${route.paoName}"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty encryptedRoutes}">
            <tr>
                <td colspan="4">
                    <i:inline key=".noRoutesMsg"/>
                </td>
            </tr>
        </c:if>
        </table>
</cti:standardPage>
