<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="security">

    <div class="clearfix box">
        <div class="category fl">
            <cti:url var="viewUrl" value="/admin/config/security/view"/>
            <a href="${viewUrl}" class="icon icon-32 fl icon-32-key"></a>
            <div class="box fl meta">
                <div><a class="title" href="${viewUrl}"><i:inline key="yukon.common.setting.subcategory.SECURITY"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.SECURITY.description"/></div>
            </div>
        </div>
    </div>
	
	<div id="honeywellPublicKeyStatus">
		<tags:alertBox key='.honeywellPublicKey.notCreated'/>
	</div>
	
    <d:inline nameKey="addKeyDialog" okEvent="addKeyFormSubmit" on="#addNewKeyBtn">
     <form:form method="POST" commandName="encryptionKey" action="saveNewKey" autocomplete="off">
        <tags:nameValueContainer2>
                <cti:csrfToken/>
                <h3><i:inline key=".addNewKeyHeading" /></h3>
                <tags:nameValue2 nameKey=".keyName">
                    <tags:input path="name" size="30" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".key">
                    <tags:input path="privateKey" size="30" />
                </tags:nameValue2>
         </tags:nameValueContainer2>
      </form:form>
    </d:inline>

    <d:inline nameKey="importKeyFileDialog" okEvent="importKeyFileFormSubmit" on="#importKeyFileBtn">
            <form:form method="POST" commandName="fileImportBindingBean" action="importKeyFile" autocomplete="off" enctype="multipart/form-data">
               <tags:nameValueContainer2>
                <cti:csrfToken/>
                <tags:nameValue2 nameKey=".importKeyFile">
                    <tags:bind path="file">
                        <tags:file name="keyFile"/>
                    </tags:bind>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".keyName">
                    <tags:input path="name" size="50" />
                </tags:nameValue2>
               </tags:nameValueContainer2>
            </form:form>
    </d:inline>
    
    <d:inline nameKey="importHoneywellKeyFileDialog" okEvent="importHoneywellKeyFileFormSubmit" on="#importHoneywellKeyFileBtn">
            <form:form method="POST" commandName="honeywellFileImportBindingBean" action="importHoneywellKeyFile" autocomplete="off" enctype="multipart/form-data">
                <tags:nameValueContainer2>
                <cti:csrfToken/>
                <tags:nameValue2 nameKey=".importKeyFile">
                    <tags:bind path="file">
                        <tags:file name="keyFile"/>
                    </tags:bind>
                </tags:nameValue2>
                </tags:nameValueContainer2>
            </form:form>
    </d:inline> 
    
    <d:inline nameKey="viewPublicKeyDialog" okEvent="none" 
        on="#viewPublicKeyBtn" options="{width: 600, 'buttons': [{text: 'Generate New Key', 'class': 'js-blocker2', click: function() { loadPublicKey(true);}},
                                        {text: 'Cancel', click: function() { $(this).dialog('close'); } }]}">
        <div id="publicKeyStatus"></div>
        <div id="publicKeyExpiration"></div>
        <div id="publicKeyText">
            <p><i:inline key=".currentPublicKey" /></p>
            <textarea id="publicKeyTextArea" rows="17" cols="70"
                readonly="readonly"></textarea>
        </div>
    </d:inline>

    <div class="column-12-12">
        <div class="column one">
            <tags:boxContainer2 nameKey="routesBox" styleClass="largeContainer">
                <table id="routesBoxTable" class="compact-results-table row-highlighting">
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
                            <cti:url var="actionUrl" value="add"/>
                            <c:if test="${route.encrypted}">
                                <cti:url var="actionUrl" value="remove"/>
                            </c:if>
                            <tr>
                                <td>${fn:escapeXml(route.paoName)}</td>
                                <td>${fn:escapeXml(route.type.paoTypeName)}</td>
                                <form:form id="routes_${route.paobjectId}" commandName="encryptedRoute" method="POST" autocomplete="off" action="${actionUrl}">
                                    <cti:csrfToken/>
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
                                            <d:confirm on="#remove_EncryptionBtn_${route.paobjectId}" nameKey="confirmRemove" argument="${fn:escapeXml(route.paoName)}" />
                                            <select disabled="disabled" style="width: 100%">
                                                <option>${fn:escapeXml(route.encryptionKeyName)}</option>
                                            <select>
                                        </c:if> 
                                        <c:if test="${!route.encrypted}">
                                            <c:if test="${fn:length(encryptionKeys) > 0}">
                                                <d:confirm on="#add_EncryptionBtn_${route.paobjectId}" nameKey="confirmAdd" argument="${fn:escapeXml(route.paoName)}" />
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
        </div>
        <div class="column two nogutter">
            <tags:boxContainer2 nameKey="keyBox" styleClass="largeContainer">
                <c:if test="${fn:length(encryptionKeys) <= 0}">
                    <div><i:inline key=".noKeysAvailable" /></div>
                </c:if>
                <c:if test="${fn:length(encryptionKeys) > 0}">
                    <table id="keyBoxTable" class="compact-results-table row-highlighting">
                        <thead>
                            <tr>
                                <th><i:inline key=".key" /> </th>
                                <th><i:inline key=".status" /> </th>
                                <th><i:inline key=".assigned" /> </th>
                                <th class="remove-column"><i:inline key=".remove" /></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${encryptionKeys}" var="key">
                                <d:confirm on="#deleteKeyBtn_${key.encryptionKeyId}" nameKey="confirmDelete" argument="${key.name}" />
                                <form:form id="keys_${key.encryptionKeyId}" method="POST" action="deleteKey" autocomplete="off">
                                    <cti:csrfToken/>
                                    <tr>
                                        <td><input type="hidden" name="encryptionKeyId" value="${key.encryptionKeyId}" />${fn:escapeXml(key.name)}</td>
                                        <td id="keyStatus_${key.encryptionKeyId}">
                                            <c:if test="${key.isValid}">
                                                <span class="success"><i:inline key=".validKey" /></span>
                                            </c:if>
                                            <c:if test="${not key.isValid}">
                                                <span class="error"><i:inline key=".invalidKey" /></span>
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
                                        <td class="remove-column">
                                            <c:choose>
                                                <c:when test="${key.currentlyUsed}">
                                                    <cti:msg2 key=".deleteKeyBtnDisabledTitle" var="title"/>
                                                    <cti:button renderMode="buttonImage" disabled="true" title="${title}" icon="icon-cross" />
                                                </c:when>
                                                <c:otherwise>
                                                    <cti:msg2 key=".deleteKeyBtnTitle" var="title"/>
                                                    <cti:button renderMode="buttonImage" id="deleteKeyBtn_${key.encryptionKeyId}"
                                                        onclick="submitForm('keys_${key.encryptionKeyId}')"
                                                        title="${title}" icon="icon-cross"/>
                                                </c:otherwise>
                                            </c:choose>
                                    </tr>
                                </form:form>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <div class="page-action-area">
                    <cti:button id="addNewKeyBtn" nameKey="addKeyBtn" disabled="${blockingError}" />
                    <cti:button id="importKeyFileBtn" nameKey="importKeyFileBtn" disabled="${blockingError}" />
                    <cti:button id="viewPublicKeyBtn" nameKey="viewPublicKeyBtn"  classes="js-blocker2" />
                </div>
            </tags:boxContainer2>
            <cti:checkRolesAndProperties value="HONEYWELL_SUPPORT_ENABLED">
              <tags:boxContainer2 nameKey="honeywellKeyBox" styleClass="largeContainer">
                <d:confirm on="#generateHonewellKeyFileBtn" nameKey="confirmGenerate" argument="Honeywell Key"/>
                  <c:choose>
                      <c:when test="${fn:length(honeywellPublicKey) <= 0}">
                            <i:inline key=".noKeysAvailable" />
                      </c:when>
                      <c:otherwise>
                          <div id="honeywellPublicKeyText">
            		          <p><i:inline key=".currentPublicKey" /></p>
            		                 <textarea id="honeywellPublicKeyTextArea" rows="6" cols="60" 
                		              readonly="readonly">${honeywellPublicKey}</textarea>
        		          </div>
                      </c:otherwise>
                  </c:choose>
                
                  <div class="page-action-area">
                      <cti:button id="generateHonewellKeyFileBtn" nameKey="generateHonewellKeyFileBtn" />
                      <cti:button id="importHoneywellKeyFileBtn" nameKey="importKeyFileBtn" />
                      <cti:button id="copyBtn" nameKey="copyBtn" />
                  </div>
              </tags:boxContainer2>
           </cti:checkRolesAndProperties>
        </div>
    </div>
    
    <script type="text/javascript">
    $(function(){
    	$('#honeywellPublicKeyStatus').hide();
        if ("${showDialog}" == "addKey") {
            $('#addNewKeyBtn').trigger($.Event("click")); // Opens up addKey Dialog
        } else if ("${showDialog}" == "importKey") {
            $('#importKeyFileBtn').trigger($.Event("click")); // Opens up importKey Dialog
        }
    });
    
    function submitForm(formId) {
        $(".ui-dialog").css("display","none");
        yukon.ui.blockPage();
        $("#"+formId).submit();
    }
    
    $("#viewPublicKeyBtn").click(function() {
        loadPublicKey(false);
    });
    
    $("#copyBtn").click(function() {
    	 $("#honeywellPublicKeyTextArea").select();
    	    document.execCommand('copy');
    });
    
    function loadPublicKey(generateNewKey) {
        yukon.ui.blockPage();
        $.ajax({ 
            url: "getPublicKey", 
            type: "POST",
            data: { "generateNewKey": generateNewKey},
            cache: false,
            dataType: "json" 
        }).done(function(publicKeyObj) {
            if(!publicKeyObj.doesExist) {
                // No public Key exists
                $("#publicKeyText").hide();
                $("#publicKeyStatus").html("<i:inline key='.publicKey.noKeyExists'/>").show('fade',{},200);
            } else if (publicKeyObj.isExpired) {
                // A key exists but it's expired
                $("#publicKeyText").hide();
                $("#publicKeyStatus").html("<i:inline key='.publicKey.keyExpired'/>").show('fade',{},200);
            } else {
                // A valid key is found
                $("#publicKeyStatus").html("<i:inline key='.security.expires'/>"+publicKeyObj.expiration);
                $("#publicKeyTextArea").val(publicKeyObj.publicKey);
                $("#publicKeyText").show('fade',{},200);
            }
            yukon.ui.unblockPage();
        }).fail(function() {
            $("#publicKeyText").hide();
            $("#publicKeyStatus").html("<i:inline key='.publicKey.keyGenerationFailed'/>").show('fade',{},200);
            yukon.ui.unblockPage();
        });
    }
    
    $(document).on("yukon.dialog.confirm.ok", function(event) {
    	$.ajax({ 
            url: "getHoneywellPublicKey", 
            type: "GET",
        }).done(function(data) {
        	if ( $( "#honeywellPublicKeyTextArea" ).length ){
        		$("#honeywellPublicKeyTextArea").val(data.honeywellPublicKey);
        	}else{
        		location.reload(true);
        	}
        }).fail(function(data) {
        	$('#honeywellPublicKeyStatus').show();
    	});
    });
    
    $(document).on('addKeyFormSubmit', function(event) {
        yukon.ui.blockPage();
        $('#encryptionKey').submit();
    });
    
    $(document).on('importKeyFileFormSubmit', function(event) {
        yukon.ui.blockPage();
        $('#fileImportBindingBean').submit();
    });
    
    $(document).on('importHoneywellKeyFileFormSubmit', function(event) {
        yukon.ui.blockPage();
        $('#honeywellFileImportBindingBean').submit();
    });
    
    </script>
</cti:standardPage>
