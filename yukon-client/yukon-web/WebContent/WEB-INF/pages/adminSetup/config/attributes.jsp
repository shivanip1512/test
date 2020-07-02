<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
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
            <cti:url value="/admin/config/attribute/save" var="createAttributeUrl" />
            <form:form modelAttribute="attribute" action="${createAttributeUrl}" method="POST">
                <cti:csrfToken />
                <div class="column-8-16 clearfix">
                    <div class="column one">
                        <cti:msg2 var="attributePlaceholder" key=".attributeName"/>
                        <tags:input path="name" placeholder="${attributePlaceholder}" maxlength="60" size="40"/>
                    </div>
                    <div class="column two nogutter">
                        <cti:button nameKey="create" type="submit" classes="fn vam ML15" icon="icon-plus-green"/>
                    </div>
                </div>
            </form:form>
            
            <div style="width:60%;" class="PT10">
                <c:set var="tableClass" value="${attributes.size() > 10 ? 'js-attributes-table' : ''}"/>
                <table class="compact-results-table row-highlighting has-actions ${tableClass}">
                    <thead>
                        <tr>
                            <th><i:inline key=".attributeName"></i:inline>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="attr" items="${attributes}">
                            <tr>
                                <td>
                                    <span class="js-view-attribute-${attr.id}">
                                        ${fn:escapeXml(attr.name)}
                                    </span>
                                    <span class="js-edit-attribute-${attr.id} dn">
                                        <input type="hidden" name="savedName" value="${attr.name}"/>
                                        <input type="text" name="name" maxlength="60" size="50" value="${attr.name}"/>
                                        <div class="button-group">
                                            <cti:button renderMode="buttonImage" icon="icon-disk" classes="js-save-edit-attribute" 
                                                data-attribute-id="${attr.id}"/>
                                            <cti:button renderMode="buttonImage" icon="icon-delete" classes="js-cancel-edit-attribute" 
                                                data-attribute-id="${attr.id}"/>
                                        </div>
                                        <div class="error"></div>
                                    </span>
                                </td>
                                <td>
                                    <cm:dropdown icon="icon-cog" triggerClasses="js-view-attribute-${attr.id}">
                                        <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-attribute" data-attribute-id="${attr.id}"/>
                                        <cm:dropdownOption id="delete-attribute-${attr.id}" key=".delete" icon="icon-cross" 
                                            data-ok-event="yukon:attribute:delete" classes="js-hide-dropdown" data-attribute-id="${attr.id}"/>
                                        <d:confirm on="#delete-attribute-${attr.id}" nameKey="confirmDelete" argument="${attr.name}"  />
                                        <cti:url var="deleteUrl" value="/admin/config/attribute/${attr.id}/delete"/>
                                        <form:form id="delete-attribute-form-${attr.id}" action="${deleteUrl}" method="DELETE">
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
        
        <tags:sectionContainer2 nameKey="attributeAssignments">
        
        </tags:sectionContainer2>
        
    </cti:msgScope>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.attributes.js" />

</cti:standardPage>