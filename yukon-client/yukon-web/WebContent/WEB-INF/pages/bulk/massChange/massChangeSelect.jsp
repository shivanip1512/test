<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.massChangeSelect.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- mass change options --%>
        <cti:msg var="massChangePageTitle" key="yukon.common.device.bulk.massChangeSelect.pageTitle"/>
        <cti:crumbLink>${massChangePageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function submitForm(selectMassChangeField) {
        
            $('massChangeBulkFieldName').value = selectMassChangeField;
            $('massChangeSelectForm').submit();
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    
    <tags:bulkActionContainer   key="yukon.common.device.bulk.massChangeSelect" 
                                deviceCollection="${deviceCollection}">
    
        <form id="massChangeSelectForm" method="get" action="/bulk/massChangeOptions">
    
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" /> 
    
            <%-- CHANGES BUTTONS --%>
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
            <cti:checkRolesAndProperties value="MASS_CHANGE">
                <input type="hidden" id="massChangeBulkFieldName" name="massChangeBulkFieldName" value="">
                <table cellspacing="10">
            
                    <c:forEach var="bulkField" items="${massChangableBulkFields}">
                    
                        <tr>
                            <td>
                            	<input type="button" id="massChangeTypeButton" value="<cti:msg key="${bulkField.displayKey}"/>" onclick="submitForm('${bulkField.inputSource.field}');" class="full_width"/>
                            </td>
                            <td><cti:msg key="${bulkField.displayKey}.description"/></td>
                        </tr>
                    
                    </c:forEach>
                    
                </table>
            </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
    
        </form>
        
    </tags:bulkActionContainer>
    
    
</cti:standardPage>