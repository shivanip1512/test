<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="yukon.web.modules.tools.bulk.deviceConfigs">

    <cti:msg2 var="helpText" key=".helpText"/>
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.deviceConfigs" deviceCollection="${deviceCollection}" helpText="${helpText}">
    
        <c:if test="${someRF}">
            <tags:alertBox type="warning" key=".sendConfig.rfnWarning" classes="js-send-config"/>
        </c:if>
    
        <cti:url var="configUrl" value="/bulk/config/deviceConfigs" />
        <form method="post" action="${configUrl}">
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".action" nameClass="mw100">
                    <div class="button-group stacked">
                        <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                            <c:if test="${empty configAction}">
                                <c:set var="configAction" value="ASSIGN"/>
                            </c:if>
                            <tags:radio name="action" value="ASSIGN" key=".assign.label" checked="${configAction == 'ASSIGN'}"/>
                            <tags:radio name="action" value="UNASSIGN" key=".unassign.label" checked="${configAction == 'UNASSIGN'}"/>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                            <c:if test="${empty configAction}">
                                <c:set var="configAction" value="SEND"/>
                            </c:if>
                            <tags:radio name="action" value="SEND" key=".send.label" checked="${configAction == 'SEND'}"/>
                            <tags:radio name="action" value="READ" key=".read.label" checked="${configAction == 'READ'}"/>
                        </cti:checkRolesAndProperties>
                        <c:if test="${empty configAction}">
                            <c:set var="configAction" value="VERIFY"/>
                        </c:if>
                        <tags:radio name="action" value="VERIFY" key=".verify.label" checked="${configAction == 'VERIFY'}"/>
                    </div>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".assignConfig.label" rowClass="js-assign-config" nameClass="mw100">
                    <c:choose>
                        <c:when test="${fn:length(existingConfigs) > 0}">
                            <select name="configuration" class="fl" style="margin-left:5px;">
                                <c:forEach var="config" items="${existingConfigs}">
                                    <option value="${config.configurationId}">${fn:escapeXml(config.name)}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                            <span><i:inline key="yukon.common.none"/></span>
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".sendConfig.selectLabel" rowClass="js-send-config" nameClass="mw100">
                    <select name="method" class="fl" style="margin-left:5px;">
                        <option value="Standard"><cti:msg2 key=".sendConfig.standard"/></option>
                        <c:if test="${!someRF}">
                            <option value="Force"><cti:msg2 key=".sendConfig.force"/></option>
                        </c:if>
                    </select>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:button nameKey="assign" classes="primary action js-assign-config js-action-submit" busy="true"/>
                <cti:button nameKey="unassign" classes="primary action js-unassign-config js-action-submit" busy="true"/>
                <cti:button nameKey="send" classes="primary action js-send-config js-action-submit" busy="true"/>
                <cti:button nameKey="read" classes="primary action js-read-config js-action-submit" busy="true"/>
                <cti:button nameKey="verify" classes="primary action js-verify-config js-action-submit" busy="true"/>
            </div>
            
        </form>

    </tags:bulkActionContainer>
       
</cti:msgScope>