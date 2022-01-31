<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="security">
    <div id="honeywellPublicKeyDownloadStatus" style="display:none"></div>
    <input type="hidden" class="js-show-dialog" value="${showDialog}"/>
	
    <div class="clearfix box">
        <div class="category fl">
            <cti:url var="viewUrl" value="/admin/config/security/view"/>
            <cti:button renderMode="appButton" icon="icon-app icon-app-32-key" href="${viewUrl}"/>
            <div class="box fl meta">
                <div><a class="title" href="${viewUrl}"><i:inline key="yukon.common.setting.subcategory.SECURITY"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.SECURITY.description"/></div>
            </div>
        </div>
    </div><br/>
	
	<div id="honeywellPublicKeyStatus" class="dn">
		<tags:alertBox key='.honeywellPublicKey.notCreated'/>
	</div>
    
    <cti:msg2 key=".addKeyDialog.title" var="addDialogTitle"/>
    <div class="dn" id="addNewKeyDialog" data-dialog data-title="${addDialogTitle}" data-event="addKeyFormSubmit">
     <form:form method="POST" modelAttribute="encryptionKey" action="saveNewKey" autocomplete="off">
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
    </div>
    
    <div id="importKeyDialog" class="dn" data-dialog
        data-title="<cti:msg2 key=".importKeyFileDialog.title"/>" 
        data-event="importKeyFileFormSubmit" >
        <jsp:include page="importKey.jsp"/>
    </div>
    
    <cti:msg2 key=".importHoneywellKeyFileDialog.title" var="importHoneywellKeyDialogTitle"/>
    <div class="dn" id="importHoneywellKeyDialog" data-dialog data-title="${importHoneywellKeyDialogTitle}" data-event="importHoneywellKeyFileFormSubmit">
        <form:form method="POST" modelAttribute="honeywellFileImportBindingBean" action="importHoneywellKeyFile" autocomplete="off" enctype="multipart/form-data">
            <cti:csrfToken/>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".importKeyFile">
                    <tags:bind path="file">
                        <tags:file name="keyFile"/>
                    </tags:bind>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </form:form>
    </div>
        
    <cti:msg2 key=".viewPublicKeyDialog.title" var="viewPublicKeyDialogTitle"/>
    <div class="dn" id="viewPublicKeyDialog" data-dialog data-title="${viewPublicKeyDialogTitle}" data-event="generatePublicKey" data-ok-text="<cti:msg2 key=".generateKeyFileBtn.label"/>">
        <div id="publicKeyStatus">
            <span class="js-no-key-exists dn"><i:inline key='.publicKey.noKeyExists'/></span>
            <span class="js-key-expired dn"><i:inline key='.publicKey.keyExpired'/></span>
            <span class="js-key-expires dn"><i:inline key='.security.expires'/><span class="js-expiration"></span></span>
            <span class="js-key-generation-failed dn"><i:inline key='.publicKey.keyGenerationFailed'/></span>
        </div>
        <div id="publicKeyExpiration"></div>
        <div id="publicKeyText">
            <p><i:inline key=".currentPublicKey" /></p>
            <textarea id="publicKeyTextArea" rows="17" cols="70"
                readonly="readonly"></textarea>
        </div>
    </div>
    
    <cti:msg2 key=".confirmGenerateEcobeeZeusKey.title" var="confirmGenerateEcobeeZeusKeyDialogTitle"/>
    <div class="dn" id="generateEcobeeZeusKeyDialog" data-dialog data-title="${confirmGenerateEcobeeZeusKeyDialogTitle}"  data-event="yukon:admin:security:generateEcobeeZeusKey" data-width="500" data-height="auto" >
        <div id="generateEcobeeZeusKeyWarning">
            <tags:alertBox type="warning" key=".confirmGenerateEcobeeZeusKey.warning"/>
        </div>
        
        <div id="generateEcobeeZeusKeyMessage">
            <p><i:inline key=".confirmGenerateEcobeeZeusKey.message" /></p>
        </div>
    </div>
    
    <cti:msg2 key=".confirmRegisterConfigurationEcobeeZeusKey.title" var="confirmRegisterConfigurationEcobeeZeusKeyDialogTitle"/>
    <div class="dn" id="registerEcobeeZeusDialog" data-dialog data-title="${confirmRegisterConfigurationEcobeeZeusKeyDialogTitle}"  data-event="yukon:admin:security:registerEcobeeZeusKey" data-dialog data-ok-text="<cti:msg2 key=".ecobeeZeusKeyRegisterBtn.label"/>" data-width="500" data-height="auto">
        <div id="registerEcobeeZeusWarning">
            <tags:alertBox type="warning" key=".confirmRegisterConfigurationEcobeeZeusKey.warning"/>  
        </div>
        
        <div id="registerEcobeeZeusMessage">
            <p><i:inline key=".confirmRegisterConfigurationEcobeeZeusKey.message" /></p>
        </div>
    </div>
    
    <cti:msg2 key=".confirmViewEcobeeZeusKey.title" var="confirmViewEcobeeZeusKeyDialogTitle"/>
    <div class="dn" id="viewEcobeeZeusKeyDialog" data-dialog data-title="${confirmViewEcobeeZeusKeyDialogTitle}" data-load-event="yukon:admin:security:viewEcobeeZeusPublicKey">
        <div id="ecobeeZeusErrorMessage"></div>
        
        <div id="ecobeeZeuPublicKeyText">
            <p><i:inline key=".currentPublicKey" /></p>
            <textarea id="ecobeeZeuPublicKeyArea" rows="17" cols="70" readonly="readonly"></textarea>
        </div>
    </div>
    
    <div class="column-12-12">
        <div class="column one">
            <tags:boxContainer2 nameKey="routesBox" styleClass="largeContainer">
                <table id="routesBoxTable" class="compact-results-table no-stripes">
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
                            <cti:url var="actionUrl" value="${route.encrypted ? 'remove' : 'add'}"/>
                            <tr>
                                <td>${fn:escapeXml(route.paoName)}</td>
                                <td>${fn:escapeXml(route.type.paoTypeName)}</td>
                                <form:form id="routes_${route.paobjectId}" modelAttribute="encryptedRoute" method="POST" autocomplete="off" action="${actionUrl}">
                                    <cti:csrfToken/>
                                    <c:set var="btnAction" value="${route.encrypted ? 'remove' : 'add'}"/>
                                    <td>
                                        <form:input path="paoName" type="hidden" value="${route.paoName}" /> 
                                        <form:input path="type" type="hidden" value="${route.type}" /> 
                                        <form:input path="paobjectId" type="hidden" value="${route.paobjectId}" />
                                        <c:if test="${route.encrypted}">
                                            <form:input path="encryptionKeyId" type="hidden" value="${route.encryptionKeyId}" />
                                            <form:input path="encryptionKeyName" type="hidden" value="${route.encryptionKeyName}" />
                                            <d:confirm on="#remove_EncryptionBtn_${route.paobjectId}" nameKey="confirmRemove" argument="${fn:escapeXml(route.paoName)}" />
                                            <select disabled="disabled" style="width: 85%">
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
                                        <cti:button id="${btnAction}_EncryptionBtn_${route.paobjectId}" nameKey="${btnAction}" 
                                            data-ok-event="yukon:admin:security:submitForm" data-form-id="routes_${route.paobjectId}" disabled="${fn:length(encryptionKeys) <= 0}" />
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
            <tags:boxContainer2 nameKey="secretsBox" styleClass="largeContainer">
                <c:if test="${!empty secretExpirationError}">
                    <tags:alertBox type="warning">${secretExpirationError}</tags:alertBox> 
                </c:if>
                <form:form id="refreshSecretForm" method="POST" action="refreshSecret">
                	<cti:csrfToken/>
                	<input type="hidden" id="secretNumber" name="secretNumber"/>
                </form:form>
                <cti:msg2 var="secretNotFoundMessage" key=".secretsBox.secretNotFound"/>
            	<div class="PB10" style="line-height:40px;">
            		<i:inline key=".secretsBox.secret1Expiration"/>:&nbsp;
            		<cti:formatDate type="DATEHMS_12" value="${brightlayerSecretKeyExpiration.expiryTime1}" nullText="${secretNotFoundMessage}"/>
            		<cti:button nameKey="refresh" classes="fr js-refresh-secret" data-secret-number="1"/>
            	</div>
            	<div style="line-height:40px;">
            		<i:inline key=".secretsBox.secret2Expiration"/>:&nbsp;
            		<cti:formatDate type="DATEHMS_12" value="${brightlayerSecretKeyExpiration.expiryTime2}" nullText="${secretNotFoundMessage}"/>
            		<cti:button nameKey="refresh" classes="fr js-refresh-secret" data-secret-number="2"/>
            	</div>
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
                                        <td class="dib">
                                            <c:choose>
                                                <c:when test="${key.currentlyUsed}">
                                                    <cti:msg2 key=".deleteKeyBtnDisabledTitle" var="title"/>
                                                    <cti:button renderMode="buttonImage" disabled="true" title="${title}" icon="icon-delete" />
                                                </c:when>
                                                <c:otherwise>
                                                    <cti:msg2 key=".deleteKeyBtnTitle" var="title"/>
                                                    <cti:button renderMode="buttonImage" id="deleteKeyBtn_${key.encryptionKeyId}" title="${title}" 
                                                        icon="icon-delete" data-ok-event="yukon:admin:security:submitForm" data-form-id="keys_${key.encryptionKeyId}"/>
                                                    <d:confirm on="#deleteKeyBtn_${key.encryptionKeyId}" nameKey="confirmDelete" argument="${key.name}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </form:form>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <div class="page-action-area">
                    <cti:button id="addNewKeyBtn" nameKey="addKeyBtn" disabled="${blockingError}" data-popup="#addNewKeyDialog" />
                    <cti:button id="importKeyFileBtn" nameKey="importKeyFileBtn" disabled="${blockingError}" data-popup="#importKeyDialog"/>
                    <cti:button id="viewPublicKeyBtn" nameKey="viewPublicKeyBtn" classes="js-blocker2" data-popup="#viewPublicKeyDialog"/>
                </div>
            </tags:boxContainer2>
            <cti:checkRolesAndProperties value="HONEYWELL_SUPPORT_ENABLED">
              <tags:boxContainer2 nameKey="honeywellKeyBox" styleClass="largeContainer">
                <d:confirm on="#generateHonewellKeyFileBtn" nameKey="confirmGenerate" argument="Honeywell Key"/>
                  <c:choose>
                      <c:when test="${fn:length(honeywellPublicKey) <= 0}">
                            <i:inline key=".noKeysAvailable" />
                            <c:set var="isPublicKeyGenerated" value="false" />
                      </c:when>
                      <c:otherwise>
                          <c:set var="isPublicKeyGenerated" value="true" />
                          <div id="honeywellPublicKeyText">
            		          <p><i:inline key=".currentPublicKey" /></p>
            		                 <textarea id="honeywellPublicKeyTextArea" rows="6" cols="54" 
                		              readonly="readonly">${honeywellPublicKey}</textarea>
        		          </div>
                      </c:otherwise>
                  </c:choose>
                
                  <div class="page-action-area">
                      <cti:button id="generateHonewellKeyFileBtn" nameKey="generateHonewellKeyFileBtn" />
                      <cti:button id="importHoneywellKeyFileBtn" nameKey="importKeyFileBtn" data-popup="#importHoneywellKeyDialog"/>
                      <form:form method="POST" action="generateHoneywellCertificate" autocomplete="off" enctype="multipart/form-data">
                         <cti:csrfToken/>
                         <cti:button id="generateCertificate" nameKey="generateCertificate" type="submit" disabled="${!isPublicKeyGenerated}" classes="MT5"/>
                      </form:form>
                  </div>
              </tags:boxContainer2>
           </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="SHOW_ECOBEE">
                <tags:boxContainer2 nameKey="ecobeeZeusKeyBox">
                    <cti:msg2 key=".ecobeeZeusURL.blank" var="ecobeeZeusUrlBlank"/>
                    <input type="hidden" class="js-ecobee-url" value="${ecobeeZeusUrlBlank}"/>
                    <div id="ecobee-zeus-js-message"></div>
                    <c:if test="${empty reportingUrl}">
                       <div id="js-url-message">
                        <tags:alertBox type="warning">${ecobeeZeusUrlBlank}</tags:alertBox> 
                       </div>
                    </c:if>
                    <c:set var="keyRegisteredClass" value="${!empty ecobeeZeusRegisteredDateTime ? '' : 'dn'}"/>
                    <span class="js-ecobee-zeus-key-registered ${keyRegisteredClass}">
                        <span class="js-ecobee-zeus-key-register-date-time">${ecobeeZeusRegisteredDateTime}</span><br>
                    </span>
                    <c:set var="keyGeneratedClass" value="${!empty ecobeeKeyZeusGeneratedDateTime ? '' : 'dn'}"/>
                    <span class="js-ecobee-zeus-key-generated ${keyGeneratedClass}">
                        <i:inline key=".ecobeeZeusKeyGenerated"/>&nbsp;<span class="js-ecobee-zeus-key-date-time">${ecobeeKeyZeusGeneratedDateTime}</span>
                    </span>
                    <c:set var="keyNotGeneratedClass" value="${!empty ecobeeKeyZeusGeneratedDateTime ? 'dn' : ''}"/>
                    <span class="js-ecobee-zeus-key-not-generated ${keyNotGeneratedClass}"><i:inline key=".ecobeeZeusNoKeyGenerated"/></span>
                    
                    <div id="reportingUrl">
                        <span class="js-url"><b> <cti:msg2 key=".ecobeeZeusURL"/></b></span>
                        <span class="js-value">${reportingUrl}</span>
                    </div>
                    <div class="page-action-area">
	                    <div class="column-12-12 clearfix">
	                       <div class="column one">
	                           <cti:button id="generateEcobeeZeusKey" nameKey="generateEcobeeKey" data-popup="#generateEcobeeZeusKeyDialog" /><br/><br/>
	                           <c:choose>
	                               <c:when test="${empty reportingUrl}">
                                       <cti:button id="registerEcobeeZeusKey" nameKey="registerConfigurationEcobeeZeusKey" disabled="${empty ecobeeKeyZeusGeneratedDateTime}" data-ok-event="yukon:admin:security:registerEcobeeZeusKey"/>
                                    </c:when>
                                    <c:otherwise>
	                                    <cti:button id="registerConfigurationEcobeeZeusKey" nameKey="registerConfigurationEcobeeZeusKey" disabled="${empty ecobeeKeyZeusGeneratedDateTime}" data-popup="#registerEcobeeZeusDialog" />
                                    </c:otherwise>
	                           </c:choose>
	                        </div>
	                        <div class="column two nogutter">
	                            <cti:button id="viewEcobeeZeusKey" nameKey="viewEcobeeZeusKey" disabled="${empty ecobeeKeyZeusGeneratedDateTime}" data-popup="#viewEcobeeZeusKeyDialog" /><br/><br/>
		                        <cti:button id="checkRegistrationEcobeeZeusKey" nameKey="checkRegistrationEcobeeZeusKey" disabled="${empty ecobeeKeyZeusGeneratedDateTime}" data-ok-event="yukon:admin:security:checkRegistrationEcobeeZeusKey" />
	                        </div>
	                    </div>
                    </div>
                </tags:boxContainer2>
            </cti:checkRolesAndProperties>
            
            <tags:boxContainer2 nameKey="itronKeyBox">
                <c:set var="keyGeneratedClass" value="${!empty itronKeyGeneratedDateTime ? '' : 'dn'}"/>
                <span class="js-itron-key-generated ${keyGeneratedClass}">
                    <i:inline key=".itronKeyGenerated"/>&nbsp;<span class="js-itron-key-date-time">${itronKeyGeneratedDateTime}</span>
                </span>
                <c:set var="keyNotGeneratedClass" value="${!empty itronKeyGeneratedDateTime ? 'dn' : ''}"/>
                <span class="js-itron-key-not-generated ${keyNotGeneratedClass}"><i:inline key=".itronNoKeyGenerated"/></span>
                <div class="page-action-area">
                    <form:form method="GET" action="downloadItronKey">
                        <cti:button nameKey="downloadItronKey" type="submit" />
                    </form:form>
                    <cti:button id="generateItronKey" nameKey="generateItronKey" data-ok-event="yukon:admin:security:generateItronKey"/>
                    <d:confirm on="#generateItronKey" nameKey="confirmGenerateItronKey" />
                </div>
            </tags:boxContainer2>
        </div>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.admin.security.js"/>

</cti:standardPage>
