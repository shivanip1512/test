<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.tools.bulk.massChangeSelect">

    <tags:bulkActionContainer key="yukon.common.device.bulk.massChangeSelect" deviceCollection="${deviceCollection}">

        <cti:url var="massChange" value="/bulk/massChangeOptions" />
        <form id="massChangeSelectForm" method="get" action="${massChange}">

            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />

            <%-- CHANGES BUTTONS --%>
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                <cti:checkRolesAndProperties value="MASS_CHANGE">
                    <input type="hidden" id="massChangeBulkFieldName" name="massChangeBulkFieldName">
                    <div class="button-group stacked">
                        <c:forEach var="bulkField" items="${massChangableBulkFields}">
                            <tags:radio name="field" value="${bulkField.inputSource.field}" key="${bulkField.displayKey}" 
                                inputClass="js-mass-change" checked="${bulkField.inputSource.field == 'enable'}"/>
                        </c:forEach>
                    </div>
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
        </form>
        
        <div id="actionInputsDiv2" class="PT10">
        </div>

    </tags:bulkActionContainer>
    
</cti:msgScope>