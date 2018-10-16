<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.dataStreaming.configure,yukon.web.modules.tools.bulk.dataStreaming">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.configure" deviceCollection="${deviceCollection}">
        <div class="page-action-area">
            <cti:url var="assignUrl"  value="/bulk/dataStreaming/configure" />
            <form:form id="configureForm" method="post" modelAttribute="configuration" action="${assignUrl}">
                <cti:csrfToken/>
                
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <c:choose>
                    <c:when test="${dataStreamingNotSupported}">
                        <div class="user-message error"><i:inline key=".dataStreamingNotSupported"/></div>
                    </c:when>
                    <c:otherwise>
                    
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".configurationType">
                                 <tags:switchButton path="newConfiguration" offNameKey=".existingConfiguration" onNameKey=".newConfiguration" classes="js-configuration-type" color="false"
                                 toggleGroup="existingConfiguration" toggleAction="hide" toggleInverse="true" checked="${config.newConfiguration}"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".configuration" data-toggle-group="existingConfiguration">
                                <tags:selectWithItems id="selectedConfiguration" inputClass="js-existing-configuration" path="selectedConfiguration" items="${existingConfigs}" itemValue="id" itemLabel="name" defaultItemValue="0" defaultItemLabel="Please select"/>
                            </tags:nameValue2>
                            <c:if test="${existingConfigs.size() > 0}">
                                <tags:nameValue2 nameKey=".selectedConfiguration" rowClass="dn js-selected-config">
                                    <c:forEach var="config" varStatus="status" items="${existingConfigs}">
                                        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
                                    </c:forEach>
                                </tags:nameValue2>
                            </c:if>
                            
                            <tags:nameValue2 nameKey=".interval.label" rowClass="dn js-new-configuration">
                                <tags:selectWithItems path="selectedInterval" items="${intervals}" />
                            </tags:nameValue2>
                            
                        </tags:nameValueContainer2>
                        
                        <tags:nameValueContainer2 tableClass="dn js-new-configuration">
                             <cti:url var="helpUrl" value="/support/matrixView"/>
                             <tags:sectionContainer2 nameKey="attributes" styleClass="dn js-new-configuration" helpUrl="${helpUrl}" helpWidth="auto">
                                <div class="empty-list stacked">
                                    <i:inline key=".attributeMessage"/>
                                </div>
                                <div class="user-message info js-too-many-attributes">
                                    <i:inline key=".tooManyAttributesMessage"/>
                                </div>
                                <c:forEach var="attribute" varStatus="status" items="${configuration.attributes}">
                                    <c:set var="idx" value="${status.index}" />
                                    <form:hidden path="attributes[${idx}].attribute" />
                                    <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.${attribute.attribute}">
                                        <tags:switchButton path="attributes[${idx}].attributeOn" offNameKey=".off.label" onNameKey=".on.label" classes="js-attribute"/>
                                    </tags:nameValue2>
                                </c:forEach>
                             </tags:sectionContainer2>
                        </tags:nameValueContainer2>
                        
                    </c:otherwise>
                </c:choose>
                                    
                <div class="page-action-area">
                    <cti:button nameKey="send" disabled="${dataStreamingNotSupported}" classes="primary action js-send-button" busy="true"/>
                </div>
                        
            </form:form>
        </div>
        <div id="verificationDialog" data-dialog data-title="<cti:msg2 key=".verification.header"/>" class="dn"></div>
    </tags:bulkActionContainer>
    
</cti:msgScope>