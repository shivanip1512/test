<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="config.attributes">
    <cti:msgScope paths="yukon.common,modules.adminSetup.config.attributes">

        <div class="box clear dashboard">
            <div class="clearfix box">
                <div class="category fl">
                    <cti:button renderMode="appButton" icon="icon-app icon-app-32-attributes" href="attributes"/>
                    <div class="box fl meta">
                        <div><a class="title" href="<cti:url value="/admin/config/attributes"/>">
                            <i:inline key="yukon.common.setting.subcategory.ATTRIBUTES"/>
                        </a></div>
                        <div class="detail"><i:inline key="yukon.common.setting.subcategory.ATTRIBUTES.description"/></div>
                    </div>
                </div>
            </div>
        </div>
        
        <tags:sectionContainer2 nameKey="attributeDefinitions">
            <cti:url value="/admin/config/attribute/create" var="createAttributeUrl" />
            <form:form modelAttribute="createAttribute" action="${createAttributeUrl}" method="POST">
                <cti:csrfToken />
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <cti:msg2 var="attributePlaceholder" key=".attributeName"/>
                        <tags:input path="name" placeholder="${attributePlaceholder}" maxlength="60" size="50"/>
                    </div>
                    <div class="column two nogutter">
                        <cti:button nameKey="create" type="submit" classes="fn vam" icon="icon-plus-green"/>
                    </div>
                </div>
            </form:form>
            
            <div style="width:60%;" class="PT10">
                <c:set var="tableClass" value="${attributes.size() > 10 ? 'js-attributes-table' : ''}"/>
                <table class="compact-results-table no-stripes has-actions ${tableClass}">
                    <thead>
                        <tr>
                            <th><i:inline key=".attributeName"></i:inline></th>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="attr" items="${attributes}">
                            <c:set var="attributeId" value="${attr.customAttributeId}"/>
                            <c:set var="enableEdit" value="${enableEditId == attributeId}"/>
                            <c:set var="editClass" value="${enableEdit ? '' : 'dn'}"/>
                            <c:set var="viewClass" value="${enableEdit ? 'dn' : ''}"/>
                            <tr>
                                <td>
                                    <span class="js-view-attribute-${attributeId} ${viewClass}" title="${attr.i18Key}">
                                        ${fn:escapeXml(attr.name)}
                                    </span>
                                    <span class="js-edit-attribute-${attributeId} ${editClass}">
                                        <cti:url value="/admin/config/attribute/edit" var="editAttributeUrl" />
                                        <form:form modelAttribute="editAttribute" action="${editAttributeUrl}" method="POST">
                                            <cti:csrfToken />
                                            <input type="hidden" name="customAttributeId" value="${attributeId}"/>
                                            <input type="hidden" name="savedName" value="${attr.name}"/>
                                            <spring:bind path="name">
                                                <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
                                                <form:input path="name" maxlength="60" size="60" cssClass="${errorClass}"/>
                                            </spring:bind>
                                            <div class="button-group">
                                                <cti:msg2 var="cancelText" key=".cancelChanges"/>
                                                <cti:msg2 var="saveText" key=".save"/>
                                                <cti:button renderMode="buttonImage" icon="icon-disk" type="submit"
                                                    data-attribute-id="${attributeId}" title="${saveText}" classes="ML0"/>
                                                <cti:button renderMode="buttonImage" icon="icon-delete" classes="js-cancel-edit-attribute MR0" 
                                                    data-attribute-id="${attributeId}" title="${cancelText}"/>
                                            </div>
                                            <spring:bind path="name">
                                                <c:if test="${status.error}"><br><form:errors path="name" cssClass="error" /></c:if>
                                            </spring:bind>
                                        </form:form>
                                    </span>
                                </td>
                                <td>
                                    <cm:dropdown icon="icon-cog" triggerClasses="js-view-attribute-${attributeId} ${viewClass}">
                                        <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-attribute" data-attribute-id="${attributeId}"/>
                                        <cm:dropdownOption id="delete-attribute-${attributeId}" key=".delete" icon="icon-cross" 
                                            data-ok-event="yukon:attribute:delete" classes="js-hide-dropdown" data-attribute-id="${attributeId}"/>
                                        <d:confirm on="#delete-attribute-${attributeId}" nameKey="confirmDelete" argument="${attr.name}"  />
                                        <cti:url var="deleteUrl" value="/admin/config/attribute/${attributeId}/delete"/>
                                        <form:form id="delete-attribute-form-${attributeId}" action="${deleteUrl}" method="DELETE">
                                            <cti:csrfToken/>
                                            <input type="hidden" name="name" value="${attr.name}"/>
                                        </form:form>
                                    </cm:dropdown>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <c:if test="${empty attributes}">
                <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
            </c:if>
     
        </tags:sectionContainer2>
        
        <cti:button nameKey="add" classes="fr" icon="icon-add" data-popup=".js-assignment-popup"/>
        <cti:url var="addAssignmentUrl" value="/admin/config/attributeAssignments/popup"/>
        <cti:msg2 var="addAssignmentTitle" key=".addAssignmentTitle"/>
        <cti:msg2 var="saveText" key=".save"/>
        <div class="dn js-assignment-popup ov"
                 data-popup
                 data-dialog
                 data-destroy-dialog-on-close
                 data-title="${addAssignmentTitle}"
                 data-url="${addAssignmentUrl}"
                 data-load-event="yukon:assignment:load"
                 data-ok-text="${saveText}"
                 data-event="yukon:assignment:save">
        </div>
        <tags:sectionContainer2 nameKey="attributeAssignments">
        
            <div class="filter-section stacked-md">
                <cti:url var="filterUrl" value="/admin/config/attributeAssignments/filter"/>
                <form:form id="filter-form" action="${filterUrl}" method="get">
                    <span class="fl">
                        <span class="vat"><i:inline key="yukon.common.filterBy"/></span>
                        
                        <cti:msg2 var="allAttributes" key=".allAttributes"/>&nbsp;
                        <select name="selectedAttributes" class="js-selected-attributes" multiple="multiple" size="1" 
                            data-placeholder="${allAttributes}" style="width:350px;">
                            <c:forEach var="attribute" items="${attributes}">
                                <option value="${attribute.customAttributeId}">${fn:escapeXml(attribute.name)}</option>
                            </c:forEach>
                        </select>
                        
                        <cti:msg2 var="allDeviceTypes" key=".allDeviceTypes"/>&nbsp;
                        <select name="selectedDeviceTypes" class="js-selected-device-types" multiple="multiple" size="1" 
                            data-placeholder="${allDeviceTypes}" size="width:350px;">
                            <c:forEach var="type" items="${deviceTypes}">
                                <option value="${type}"><i:inline key="${type.formatKey}"/></option>
                            </c:forEach>
                        </select>
                    </span>
                                        
                    <cti:button nameKey="filter" classes="js-filter-assignments action primary fn ML15"/>
                </form:form>
            </div>
            <hr/>

            <div id="assignments-container" data-url="${filterUrl}">
                <%@ include file="attributeAssignmentsTable.jsp" %>
            </div>
        
        </tags:sectionContainer2>
        
    </cti:msgScope>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.attributes.js" />

</cti:standardPage>