<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="encryption">

    <dialog:inline nameKey="addKeyDialog" okEvent="yukonDialogSubmit"
        on="#addNewKeyBtn">
        <tags:nameValueContainer2>
            <form:form method="POST" commandName="encryptionKey"
                action="saveNewKey" autocomplete="off">
                <i:inline key=".addNewKeyHeading" />
                <tags:nameValue2 nameKey=".keyNameLbl">
                    <tags:input path="name" size="50" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".keyLbl">
                    <tags:input path="value" size="50" />
                </tags:nameValue2>
            </form:form>
        </tags:nameValueContainer2>
    </dialog:inline>

    <dialog:inline nameKey="importKeyFileDialog"
        okEvent="yukonDialogSubmit" on="#importKeyFileBtn">
        <tags:nameValueContainer2>
            <form:form method="POST" commandName="fileImportBindingBean"
                action="importKeyFile" autocomplete="off"
                enctype="multipart/form-data">
                <tags:nameValue2 nameKey=".importKeyFileLbl">
                    <tags:bind path="file">
                        <input type="file" name="keyFile">
                    </tags:bind>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".keyNameLbl">
                    <tags:input path="name" size="50" />
                </tags:nameValue2>
            </form:form>
        </tags:nameValueContainer2>
    </dialog:inline>
    
    <dialog:inline nameKey="viewPublicKeyDialog" okEvent="none" 
        on="#viewPublicKeyBtn" options="{width: 600, 'buttons': [{text: 'Generate New Key', class: 'f_blocker2', click: function() { loadPublicKey(true);}},
                                        {text: 'Cancel', click: function() { jQuery(this).dialog('close'); } }]}">
        <div id="publicKeyStatus"></div>
        <div id="publicKeyExpiration"></div>
        <div id="publicKeyText">
            <p><i:inline key=".currentPublicKeyLbl" /></p>
            <textarea id="publicKeyTextArea" rows="17" cols="70"
                readonly="readonly"></textarea>
        </div>
    </dialog:inline>

    <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="routesBox" styleClass="largeContainer">
                <table id="routesBoxTable"
                    class="compactResultsTable rowHighlighting">
                    <tr>
                        <th><i:inline key=".paoNameLbl" />
                        </th>
                        <th><i:inline key=".paoTypeLbl" />
                        </th>
                        <th style="width: 50%"><i:inline key=".CPSkeyLbl" />
                        </th>
                        <th><i:inline key=".actionLbl" />
                        </th>
                    </tr>
                    <c:forEach var="route" items="${encryptedRoutes}">
                        <c:set var="actionUrl" value="add" />
                        <c:if test="${route.encrypted}">
                            <c:set var="actionUrl" value="remove" />
                        </c:if>
                        <tr>
                            <td>${fn:escapeXml(route.paoName)}</td>
                            <td>${fn:escapeXml(route.type.paoTypeName)}</td>
                            <form:form id="routes_${route.paobjectId}" commandName="encryptedRoute" method="POST" autocomplete="off" action="${actionUrl}">
                                <c:set var="btnAction" value="add" />
                                <c:if test="${route.encrypted}">
                                    <c:set var="btnAction" value="remove" />
                                </c:if>
                                <td>
                                    <form:input path="paoName" type="hidden" value="${route.paoName}" /> 
                                    <form:input path="type" type="hidden" value="${route.type}" /> 
                                    <form:input path="paobjectId" type="hidden" value="${route.paobjectId}" />
                                    <c:if test="${route.encrypted}">
                                        <form:input path="encryptionKeyId" type="hidden" value="${route.encryptionKeyId}" />
                                        <form:input path="encryptionKeyName" type="hidden" value="${route.encryptionKeyName}" />
                                        <dialog:confirm on="#remove_EncryptionBtn_${route.paobjectId}" nameKey="confirmRemove" argument="${fn:escapeXml(route.paoName)}" />
                                        <select disabled="disabled" style="width: 100%">
                                            <option>${fn:escapeXml(route.encryptionKeyName)}</option>
                                        <select>
                                    </c:if> 
                                    <c:if test="${!route.encrypted}">
                                        <c:if test="${fn:length(encryptionKeys) > 0}">
                                            <dialog:confirm on="#add_EncryptionBtn_${route.paobjectId}" nameKey="confirmAdd" argument="${fn:escapeXml(route.paoName)}" />
                                            <form:select id="keyNameSelect${route.paobjectId}" path="encryptionKeyId" style="width:100%">
                                                <c:forEach items="${encryptionKeys}" var="key">
                                                    <form:option value="${key.encryptionKeyId}">${fn:escapeXml(key.name)}</form:option>
                                                </c:forEach>
                                            </form:select>
                                        </c:if>
                                        <c:if test="${fn:length(encryptionKeys) <= 0}">
                                            <i:inline key=".noKeysAvailable" />
                                        </c:if>
                                    </c:if>
                                </td>
                                <td>
                                    <cti:button id="${btnAction}_EncryptionBtn_${route.paobjectId}" nameKey="${btnAction}EncryptionBtn" href="javascript:submitForm('routes_${route.paobjectId}')" disabled="${fn:length(encryptionKeys) <= 0}" />
                                </td>
                            </form:form>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty encryptedRoutes}">
                        <tr>
                            <td colspan="4"><i:inline key=".noRoutesMsg" /></td>
                        </tr>
                    </c:if>
                </table>
            </tags:boxContainer2>
        </cti:dataGridCell>
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="keyBox" styleClass="largeContainer">
                <c:if test="${fn:length(encryptionKeys) <= 0}">
                    <i:inline key=".noKeysAvailable" />
                </c:if>
                <c:if test="${fn:length(encryptionKeys) > 0}">
                    <table id="keyBoxTable" class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".keyLbl" /> </th>
                            <th><i:inline key=".statusLbl" /> </th>
                            <th><i:inline key=".assignedLbl" /> </th>
                            <th class="removeColumn">Remove</th>
                        </tr>
                        <c:forEach items="${encryptionKeys}" var="key">
                            <dialog:confirm on="#deleteKeyBtn_${key.encryptionKeyId}" nameKey="confirmDelete" argument="${key.name}" />
                            <form:form id="keys_${key.encryptionKeyId}" method="POST" action="deleteKey" autocomplete="off">
                                <input type="hidden" name="encryptionKeyId" value="${key.encryptionKeyId}" />
                                <tr>
                                    <td>${fn:escapeXml(key.name)}</td>
                                    <td id="keyStatus_${key.encryptionKeyId}">
                                        <c:if test="${key.isValid}">
                                            <span class="ConfirmMsg"><i:inline key=".validKey" /></span>
                                        </c:if>
                                        <c:if test="${not key.isValid}">
                                            <span class="errorMessage"><i:inline key=".invalidKey" /></span>
                                        </c:if>
                                    </td>
                                    <td id="keyAssigned_${key.encryptionKeyId}">
                                        <i:inline key=".unassignedKey" />
                                    </td>
                                    <td class="removeColumn"><cti:button type="submit" id="deleteKeyBtn_${key.encryptionKeyId}" nameKey="deleteKeyBtn" href="javascript:submitForm('keys_${key.encryptionKeyId}')" styleClass="pointer icon icon_remove" />
                                    </td>
                                </tr>
                            </form:form>
                        </c:forEach>
                    </table>
                </c:if>
                <cti:button id="addNewKeyBtn" nameKey="addKeyBtn" disabled="${blockingError}" />
                <cti:button id="importKeyFileBtn" nameKey="importKeyFileBtn" disabled="${blockingError}" />
                <cti:button id="viewPublicKeyBtn" nameKey="viewPublicKeyBtn"  styleClass="f_blocker2" />
            </tags:boxContainer2>
        </cti:dataGridCell>
    </cti:dataGrid>
    <script type="text/javascript">
    jQuery(function(){
        
        // Add the border to last row
        if ("${showDialog}" == "addKey") {
            jQuery('#addNewKeyBtn').trigger(jQuery.Event("click"));
        } else if ("${showDialog}" == "importKey") {
            jQuery('#importKeyFileBtn').trigger(jQuery.Event("click"));
        }
        <c:forEach var="route" items="${encryptedRoutes}">
        if (${route.encrypted}) {
            jQuery("#keyAssigned_${route.encryptionKeyId}").html('<i:inline key=".assignedKey"/>');
            jQuery("#deleteKeyBtn_${route.encryptionKeyId}").attr("hidden", "hidden");
        } 
        </c:forEach>
    });
    
    function submitForm(formId) {
        jQuery("#"+formId).submit();
    }
    
    jQuery("#viewPublicKeyBtn").click(function() {
        loadPublicKey(false);
    });
    
    function loadPublicKey(generateNewKey) {
        Yukon.ui.blockPage();
        jQuery.ajax({ 
            url: "getPublicKey", 
            type: "POST",
            data: { "generateNewKey": generateNewKey},
            cache: false,
            dataType: "json" 
        }).done(function(jsonResponse) {
            var publicKeyObj = jQuery.parseJSON(jsonResponse);
            if(!publicKeyObj.doesExist) {
                // No public Key exists
                jQuery("#publicKeyText").hide();
                jQuery("#publicKeyStatus").html("<i:inline key='.publicKey.noKeyExists'/>").show('fade',{},200);
            } else if (publicKeyObj.isExpired) {
                // A key exists but it's expired
                jQuery("#publicKeyText").hide();
                jQuery("#publicKeyStatus").html("<i:inline key='.publicKey.keyExpired'/>").show('fade',{},200);
            } else {
                // A valid key is found
                jQuery("#publicKeyStatus").html("<i:inline key='.encryption.expiresLbl'/>"+publicKeyObj.expiration);
                jQuery("#publicKeyTextArea").val(publicKeyObj.publicKey);
                jQuery("#publicKeyText").show('fade',{},200);
            }
            Yukon.ui.unblockPage();
        }).fail(function() {
            jQuery("#publicKeyText").hide();
            jQuery("#publicKeyStatus").html("<i:inline key='.publicKey.keyGenerationFailed'/>").show('fade',{},200);
            Yukon.ui.unblockPage();
        });
    }
    
    </script>
</cti:standardPage>
]