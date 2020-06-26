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
            <span class="js-create-attribute-span">
                <cti:msg2 var="attributePlaceholder" key=".attributeName"/>
                <input type="text" name="name" placeholder="${attributePlaceholder}" maxlength="60" size="40"/>
                <cti:button nameKey="create" classes="js-create-attribute fn vam" icon="icon-plus-green"/>
                <div class="error"></div>
            </span>
            
            <div style="width:60%;">
                <table class="compact-results-table row-highlighting has-actions js-attributes-table">
                    <thead>
                        <tr>
                            <th><i:inline key=".attributeName"></i:inline>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="attribute" items="${attributes}">
                            <tr>
                                <td>
                                    <span class="js-view-attribute-${attribute.id}">
                                        ${fn:escapeXml(attribute.name)}
                                    </span>
                                    <span class="js-edit-attribute-${attribute.id} dn">
                                        <input type="hidden" name="savedName" value="${attribute.name}"/>
                                        <input type="text" name="name" maxlength="60" size="50" value="${attribute.name}"/>
                                        <div class="button-group">
                                            <cti:button renderMode="buttonImage" icon="icon-disk" classes="js-save-edit-attribute" 
                                                data-attribute-id="${attribute.id}"/>
                                            <cti:button renderMode="buttonImage" icon="icon-delete" classes="js-cancel-edit-attribute" 
                                                data-attribute-id="${attribute.id}"/>
                                        </div>
                                        <div class="error"></div>
                                    </span>
                                </td>
                                <td>
                                    <cm:dropdown icon="icon-cog" triggerClasses="js-view-attribute-${attribute.id}">
                                        <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-attribute" data-attribute-id="${attribute.id}"/>
                                        <cm:dropdownOption id="delete-attribute-${attribute.id}" key=".delete" icon="icon-cross" 
                                            data-ok-event="yukon:attribute:delete" classes="js-hide-dropdown" data-attribute-id="${attribute.id}"/>
                                        <d:confirm on="#delete-attribute-${attribute.id}" nameKey="confirmDelete" argument="${attribute.name}"  />
                                        <cti:url var="deleteUrl" value="/admin/config/attribute/${attribute.id}/delete"/>
                                        <form:form id="delete-attribute-form-${attribute.id}" action="${deleteUrl}" method="DELETE">
                                            <cti:csrfToken/>
                                            <input type="hidden" name="name" value="${attribute.name}"/>
                                        </form:form>
                                    </cm:dropdown>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
     
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="attributeAssignments">
        
        </tags:sectionContainer2>
        
    </cti:msgScope>

    <cti:includeScript link="/resources/js/pages/yukon.adminSetup.attributes.js" />

</cti:standardPage>