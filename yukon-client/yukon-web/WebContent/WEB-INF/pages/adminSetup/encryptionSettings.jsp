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
                enable(${route.paobjectId})
            } else {
                eraseAndDisable(${route.paobjectId});
            }

        </c:forEach>  
        
        Event.observe('cancelBtn', 'click', function() {
            window.location = "view";
        });
    });
    
        function hideById(id) {
            $(id).hide();
        }
    
        function eraseAndDisable(inputId) {
            $("keyid_"+inputId).clear();
            $("keyid_"+inputId).type = "hidden";
            $("dummy_"+inputId).type = "text";
            $("enableLbl_"+inputId).show();
            $("disableLbl_"+inputId).hide();
        }
        
        function enable(inputId) {
            $("keyid_"+inputId).type = "text";
            $("dummy_"+inputId).type = "hidden";
            $("keyid_"+inputId).focus();
            $("enableLbl_"+inputId).hide();
            $("disableLbl_"+inputId).show();
        }
    </script>
    
    <form id="encryptionForm" action="save" method="POST">
    <table class="resultsTable">
        <tr>
            <th><i:inline key=".paoNameLbl"/></th>
            <th><i:inline key=".paoTypeLbl"/></th>
            <th><i:inline key=".actionsLbl"/></th>
            <th><i:inline key=".CPSkeyLbl"/></th>
        </tr>
        
        <c:forEach var="route" items="${encryptedRoutes}">
            <tr>
                <td>${route.paoName}</td>
                <td>${route.type}</td>
                <td>
                    <span id="disableLbl_${route.paobjectId}" style="display:none">
                        <cti:labeledImg  key="addDisabled"/>
                        <cti:labeledImg  key="delete" href="javascript:eraseAndDisable('${route.paobjectId}')"/>
                    </span>
                    <span id="enableLbl_${route.paobjectId}" style="display:none">
                        <cti:labeledImg  key="add" href="javascript:enable('${route.paobjectId}')"/>
                        <cti:labeledImg  key="deleteDisabled"/>
                    </span>
                </td>
                <td>
                    <input id="keyid_${route.paobjectId}" name="value" type="hidden" value="${route.value}" size ="50"/>
                    <input id="dummy_${route.paobjectId}" type="hidden" value="<i:inline key=".disabledMsg"/>" size="50" disabled/>
                    <input name="paobjectId" type="hidden" value="${route.paobjectId}"/>
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
        <br>
        <c:if test="${not empty encryptedRoutes}">
            <cti:button key="saveBtn" type="submit"/>
            <cti:button key="cancelBtn" id="cancelBtn"/>
        </c:if>
        <c:if test="${empty encryptedRoutes}">
            <cti:button key="saveBtn" disabled="true"/>
            <cti:button key="cancelBtn" disabled="true"/>
        </c:if>        
    </form>

</cti:standardPage>
