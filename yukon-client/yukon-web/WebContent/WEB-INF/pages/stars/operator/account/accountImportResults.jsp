<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="accountImport">
    <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" />
    
    <script type="text/javascript">
        function importFinished() {
            jQuery('#cancelButton').attr('disabled','disabled');
        
            var params = {'resultId': '${resultId}'};
            jQuery.ajax({
                dataType: "json",
                url: '/stars/operator/account/importResult',
                data: params
            }).done(function(data, status, xhrobj) {
                var jsonResp,
                    prescan;
                prescan = ${prescan};
                // this gets our JSON response
                jsonResp = jQuery.parseJSON(xhrobj.getResponseHeader('X-JSON'));
                try {
                    if(jsonResp.passed === false) {
                        jQuery('#errorsLink').show();
                        jQuery('#importButton').attr('disabled','disabled');
                        jQuery('#importButton').hide();
                    } else if(prescan === true) {
                        jQuery('#importButton').removeAttr('disabled');
                    }
                } catch(badajaxex) {
                }
            });
        }

        function showErrorsTable() {
            var params = {'resultId': '${resultId}'};
            jQuery.ajax({
                url: '/stars/operator/account/importErrors',
                data: params
            }).done(function(data, status, xhrobj) {
                jQuery('#importErrorsDiv').html(data).show();
            });
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
                <c:set var="action" value="/stars/operator/account/doAccountImport"/>
            </c:when>
            <c:otherwise>
                <c:set var="action" value="/stars/operator/account/accountImport"/>
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
    
    <a href="/stars/operator/account/accountImport"><i:inline key=".backLink"/></a>
    
    <br>
    <br>
    
    <div id="importErrorsDiv" style="display: none;"></div>
    
</cti:standardPage>