<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountImport">
    <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" />
    
    <script type="text/javascript">
        function importFinished() {
            $('cancelButton').disable();

            var params = {'resultId': '${resultId}'};
            new Ajax.Request('/spring/stars/operator/account/importResult', {
                method: 'get',
                parameters: params,
                onSuccess: function(resp, json) {
                    var prescan = ${prescan};
                	if(json.passed == false) {
                        $('errorsLink').show();
                    } else if(prescan == true) {
                        $('importButton').enable();
                    }
                }
            });
        }

        function showErrorsTable() {
            var params = {'resultId': '${resultId}'};
            new Ajax.Updater('importErrorsDiv', '/spring/stars/operator/account/importErrors', {method: 'get', evalScripts: 'true', parameters: params});
            $('importErrorsDiv').show();
        }
    </script>
    
    <cti:dataUpdaterEventCallback function="importFinished" id="ACCOUNT_IMPORT/${resultId}/IS_COMPLETE" />
    
    <c:choose>
        <c:when test="${prescan}">
            <c:set var="containerTitle" value="prescanResultsContainer"/>
        </c:when>
        <c:otherwise>
            <c:set var="containerTitle" value="accountImportResultsContainer"/>
        </c:otherwise>
    </c:choose>
    
    <tags:boxContainer2 nameKey="${containerTitle}">
        
        <tags:nameValueContainer2>
            
            <c:choose>
                <c:when test="${prescan}">
                
                    <tags:nameValue2 nameKey=".prescanStatus">
                        <cti:classUpdater identifier="${resultId}/PRESCAN_STATUS_COLOR" type="ACCOUNT_IMPORT">
                            <cti:dataUpdaterValue identifier="${resultId}/PRESCAN_STATUS" type="ACCOUNT_IMPORT"/>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                    <c:if test="${showCustomerStats}">
                        <tags:nameValue2 nameKey=".accountsToImport">
                            <cti:dataUpdaterValue identifier="${resultId}/ACCOUNTS_PROCESSED" type="ACCOUNT_IMPORT"/>
                        </tags:nameValue2>
                    </c:if>
                    
                    <c:if test="${showHardwareStats}">
                        <tags:nameValue2 nameKey=".hardwareToImport">
                            <cti:dataUpdaterValue identifier="${resultId}/HARDWARE_PROCESSED" type="ACCOUNT_IMPORT"/>
                        </tags:nameValue2>
                    </c:if>
                    
                </c:when>
                <c:otherwise>
                
                    <tags:nameValue2 nameKey=".progressBarLabel">
                        <tags:updateableProgressBar totalCount="${totalCount}" countKey="ACCOUNT_IMPORT/${resultId}/COMPLETED_COUNT" hideCount="true"/>
                    </tags:nameValue2>
                    
                    <c:if test="${showCustomerStats}">
                        <tags:nameValue2 nameKey=".accountsProgress">
                            <cti:dataUpdaterValue identifier="${resultId}/ACCOUNT_STATS" type="ACCOUNT_IMPORT"/>
                        </tags:nameValue2>
                    </c:if>
                    
                    <tags:nameValue2 nameKey=".hardwareProgress">
                        <cti:dataUpdaterValue identifier="${resultId}/HARDWARE_STATS" type="ACCOUNT_IMPORT"/>
                    </tags:nameValue2>
                    
                </c:otherwise>
            </c:choose>
            
            <tags:nameValue2 nameKey=".importErrors">
                <cti:dataUpdaterValue identifier="${resultId}/IMPORT_ERRORS" type="ACCOUNT_IMPORT"/>
                <span id="errorsLink" style="display: none;"><a href="javascript:showErrorsTable()"><i:inline key=".errorsLink"/></a></span>
            </tags:nameValue2>
        
        </tags:nameValueContainer2>
        
        <br>
        
        <c:choose>
            <c:when test="${prescan}">
                <c:set var="action" value="/spring/stars/operator/account/doAccountImport"/>
            </c:when>
            <c:otherwise>
                <c:set var="action" value="/spring/stars/operator/account/accountImport"/>
            </c:otherwise>
        </c:choose>
        
        <form action="${action}" id="importForm">
            <input type="hidden" value="${resultId}" name="resultId">
              
            <c:if test="${prescan}"><cti:button type="submit" id="importButton" nameKey="import" disabled="disabled"/></c:if>
            <cti:button type="submit" id="cancelButton" name="cancelImport" nameKey="cancel"/>
            
            <input type="hidden" name="prescan" value="${prescan}">
        </form>
        
    </tags:boxContainer2>
    
    <br>
    <br>
    
    <a href="/spring/stars/operator/account/accountImport"><i:inline key=".backLink"/></a>
    
    <br>
    <br>
    
    <div id="importErrorsDiv" style="display: none;"></div>
    
</cti:standardPage>