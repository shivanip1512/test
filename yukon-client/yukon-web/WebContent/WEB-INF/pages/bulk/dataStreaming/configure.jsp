<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="bulk.dataStreaming.configure">

    <div class="dn js-none-selected warning">
        <i:inline key=".noneSelected"/>
    </div>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.configure" deviceCollection="${deviceCollection}">
        <div class="page-action-area">
            <cti:url var="assignUrl"  value="/bulk/dataStreaming/configure" />
            <form:form id="configureForm" method="post" commandName="configuration" action="${assignUrl}">
                <cti:csrfToken/>
                
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".configurationType">
                         <tags:switchButton path="newConfiguration" offNameKey=".existingConfiguration" onNameKey=".newConfiguration" classes="js-configuration-type" color="false"
                         toggleGroup="existingConfiguration" toggleAction="hide" toggleInverse="true" checked="${config.newConfiguration}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".configuration" data-toggle-group="existingConfiguration">
                        <tags:selectWithItems id="selectedConfiguration" path="selectedConfiguration" items="${existingConfigs}" itemValue="id" itemLabel="name" onchange="showSelectedConfiguration(this);" defaultItemValue="0" defaultItemLabel="Please select"/>
                    </tags:nameValue2>
                    <c:if test="${existingConfigs.size() > 0}">
                        <tags:nameValue2 nameKey=".selectedConfiguration" rowClass="dn js-selected-config">
                            <c:forEach var="config" varStatus="status" items="${existingConfigs}">
                                <table id="configTable_${config.id}" class="compact-results-table dn js-config-table">
                                    <thead>
                                        <tr>
                                            <th><i:inline key=".attribute"/></th>
                                            <th><i:inline key=".interval"/></th>
                                        </tr>
                                    </thead>
                                    <c:forEach var="configAtt" varStatus="status" items="${config.attributes}">
                                    <tr>
                                        <td>${configAtt.attribute.description}</td>
                                        <td>${configAtt.interval}
                                            <c:choose>
                                                <c:when test="${configAtt.interval > 1}">
                                                    <i:inline key=".minutes"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <i:inline key=".minute"/>
                                                </c:otherwise>
                                            </c:choose>
                                       </td>
                                    </tr>
                                    </c:forEach>
                            </c:forEach>
                            </table>
                        </tags:nameValue2>
                    </c:if>
                    
                    <tags:nameValue2 nameKey=".interval.label" rowClass="dn js-new-configuration">
                        <tags:selectWithItems path="selectedInterval" items="${intervals}" />
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <tags:nameValueContainer2 tableClass="dn js-new-configuration">
                     <tags:sectionContainer2 nameKey="attributes" styleClass="dn js-new-configuration">
                        <c:forEach var="attribute" varStatus="status" items="${configuration.attributes}">
                            <c:set var="idx" value="${status.index}" />
                            <form:hidden path="attributes[${idx}].attribute" />
                            <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.${attribute.attribute}">
                                <tags:switchButton path="attributes[${idx}].attributeOn" offNameKey=".off.label" onNameKey=".on.label" classes="js-attribute"/>
                            </tags:nameValue2>
                        </c:forEach>
                     </tags:sectionContainer2>
                </tags:nameValueContainer2>
        
                <div class="page-action-area">
                    <cti:button nameKey="next" name="nextButton" classes="primary action js-next-button"/>
                </div>
            </form:form>
        </div>
    </tags:bulkActionContainer>
    
    <cti:includeScript link="/resources/js/pages/yukon.bulk.dataStreaming.js"/>
    
</cti:standardPage>