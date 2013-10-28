<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="security">

    <div class="clearfix box">
        <div class="category fl">
            <a href="weather" class="icon icon-32 fl icon-32-key"></a>
            <div class="box fl meta">
                <div><a class="title" href="/adminSetup/config/security/view"><i:inline key="yukon.common.setting.subcategory.SECURITY"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.SECURITY.description"/></div>
            </div>
        </div>
    </div>

    <dialog:inline nameKey="addKeyDialog" okEvent="addKeyFormSubmit" on="#addNewKeyBtn">
        <tags:nameValueContainer2>
            <form:form method="POST" commandName="encryptionKey" action="saveNewKey" autocomplete="off">
                <h3><i:inline key=".addNewKeyHeading" /></h3>
                <tags:nameValue2 nameKey=".keyName">
                    <tags:input path="name" size="30" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".key">
                    <tags:input path="value" size="30" />
                </tags:nameValue2>
            </form:form>
        </tags:nameValueContainer2>
    </dialog:inline>

    <dialog:inline nameKey="importKeyFileDialog" okEvent="importKeyFileFormSubmit" on="#importKeyFileBtn">
        <tags:nameValueContainer2>
            <form:form method="POST" commandName="fileImportBindingBean" action="importKeyFile" autocomplete="off" enctype="multipart/form-data">
                <tags:nameValue2 nameKey=".importKeyFile">
                    <tags:bind path="file">
                        <input type="file" name="keyFile">
                    </tags:bind>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".keyName">
                    <tags:input path="name" size="50" />
                </tags:nameValue2>
            </form:form>
        </tags:nameValueContainer2>
    </dialog:inline>
    
    <dialog:inline nameKey="viewPublicKeyDialog" okEvent="none" 
        on="#viewPublicKeyBtn" options="{width: 600, 'buttons': [{text: 'Generate New Key', 'class': 'f-blocker2', click: function() { loadPublicKey(true);}},
                                        {text: 'Cancel', click: function() { jQuery(this).dialog('close'); } }]}">
        <div id="publicKeyStatus"></div>
        <div id="publicKeyExpiration"></div>
        <div id="publicKeyText">
            <p><i:inline key=".currentPublicKey" /></p>
            <textarea id="publicKeyTextArea" rows="17" cols="70"
                readonly="readonly"></textarea>
        </div>
    </dialog:inline>

    <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="routesBox" styleClass="largeContainer">
                <table id="routesBoxTable" class="compactResultsTable rowHighlighting">
                    <thead>
                        <tr>
                            <th><i:inline key=".paoName" /></th>
                            <th><i:inline key=".paoType" /></th>
                            <th style="width: 50%"><i:inline key=".CPSkey" /></th>
                            <th><i:inline key=".action" /></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
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
                                        <cti:button id="${btnAction}_EncryptionBtn_${route.paobjectId}" nameKey="${btnAction}" href="javascript:submitForm('routes_${route.paobjectId}')" disabled="${fn:length(encryptionKeys) <= 0}" />
                                    </td>
                                </form:form>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty encryptedRoutes}">
                            <tr>
                                <td colspan="4"><i:inline key=".noRoutesMsg" /></td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </tags:boxContainer2>
        </cti:dataGridCell>
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="keyBox" styleClass="largeContainer">
                <c:if test="${fn:length(encryptionKeys) <= 0}">
                    <div><i:inline key=".noKeysAvailable" /></div>
                </c:if>
                <c:if test="${fn:length(encryptionKeys) > 0}">
                    <table id="keyBoxTable" class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".key" /> </th>
                            <th><i:inline key=".status" /> </th>
                            <th><i:inline key=".assigned" /> </th>
                            <th class="removeColumn"><i:inline key=".remove" /></th>
                        </tr>
                        <c:forEach items="${encryptionKeys}" var="key">
                            <dialog:confirm on="#deleteKeyBtn_${key.encryptionKeyId}" nameKey="confirmDelete" argument="${key.name}" />
                            <form:form id="keys_${key.encryptionKeyId}" method="POST" action="deleteKey" autocomplete="off">
                                <input type="hidden" name="encryptionKeyId" value="${key.encryptionKeyId}" />
                                <tr>
                                    <td>${fn:escapeXml(key.name)}</td>
                                    <td id="keyStatus_${key.encryptionKeyId}">
                                        <c:if test="${key.isValid}">
                                            <span class="successMessage"><i:inline key=".validKey" /></span>
                                        </c:if>
                                        <c:if test="${not key.isValid}">
                                            <span class="errorMessage"><i:inline key=".invalidKey" /></span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${key.currentlyUsed}">
                                                <i:inline key=".assignedKey" />
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key=".unassignedKey" />
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="removeColumn">
                                        <c:choose>
	                                        <c:when test="${key.currentlyUsed}">
												<a class="button naked" disabled="disabled" title="<cti:msg2 key=".deleteKeyBtnDisabledTitle"/>">
                                                    <i class="icon icon-cross"></i>
                                                </a>
											</c:when>
	                                        <c:otherwise>
												<a type="submit" id="deleteKeyBtn_${key.encryptionKeyId}"
													href="javascript:submitForm('keys_${key.encryptionKeyId}')"
													class="button naked"
													title="<cti:msg2 key=".deleteKeyBtnTitle"/>">
													<i class="icon icon-cross"></i>
                                                </a>
											</c:otherwise>
                                        </c:choose>
                                </tr>
                            </form:form>
                        </c:forEach>
                    </table>
                </c:if>
                <div class="pageActionArea">
	                <cti:button id="addNewKeyBtn" nameKey="addKeyBtn" disabled="${blockingError}" />
	                <cti:button id="importKeyFileBtn" nameKey="importKeyFileBtn" disabled="${blockingError}" />
	                <cti:button id="viewPublicKeyBtn" nameKey="viewPublicKeyBtn"  classes="f-blocker2" />
                </div>
            </tags:boxContainer2>
        </cti:dataGridCell>
    </cti:dataGrid>
    
    <script type="text/javascript">
    jQuery(function(){
        
        if ("${showDialog}" == "addKey") {
            jQuery('#addNewKeyBtn').trigger(jQuery.Event("click")); // Opens up addKey Dialog
        } else if ("${showDialog}" == "importKey") {
            jQuery('#importKeyFileBtn').trigger(jQuery.Event("click")); // Opens up importKey Dialog
        }
    });
    
    function submitForm(formId) {
        jQuery(".ui-dialog").css("display","none");
        Yukon.ui.blockPage();
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
                jQuery("#publicKeyStatus").html("<i:inline key='.security.expires'/>"+publicKeyObj.expiration);
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
    
    jQuery(document).bind('addKeyFormSubmit', function(event) {
        Yukon.ui.blockPage();
        jQuery('#encryptionKey').submit();
    });
    
    jQuery(document).bind('importKeyFileFormSubmit', function(event) {
        Yukon.ui.blockPage();
        jQuery('#fileImportBindingBean').submit();
    });
    
    </script>
</cti:standardPage>
