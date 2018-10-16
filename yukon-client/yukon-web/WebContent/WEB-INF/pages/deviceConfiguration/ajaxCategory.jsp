<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.configs, modules.tools.configs.category">
<input id="popup-title" type="hidden" value="${popupTitle}"/>

<cti:url var="saveInPlaceUrl" value="/deviceConfiguration/category/saveInPlace"/>
<form:form id="category-form" modelAttribute="categoryEditBean" action="${saveInPlaceUrl}" cssClass="js-no-submit-on-enter">
    <cti:csrfToken/>
    <form:hidden path="categoryType"/>
    <form:hidden path="categoryId"/>
    <input type="hidden" name="configId" value="${configId}"/>
    
        <cti:flashScopeMessages/>

        <table class="stacked-md">
            <tr>
                <td><span class="label label-warning"><i:inline key="yukon.common.warning"/></span></td>
                <td><i:inline key=".category.changeWarning"/></td>
            </tr>
        </table>

        <tags:nameValueContainer2 tableClass="stacked">
            
            <tags:inputNameValue nameKey=".categoryName" path="categoryName" size="50" maxlength="60"/>
            
            <tags:nameValue2 nameKey=".type">
                <i:inline key=".${categoryTemplate.categoryType}.title"/>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".definition">
                <i:inline key=".${categoryTemplate.categoryType}.definition"/>
            </tags:nameValue2>
            
            <tags:textareaNameValue nameKey=".description" rows="4" cols="50" path="description"/>
            
        </tags:nameValueContainer2>
        
    <tags:sectionContainer2 nameKey="categoryFields">
        <%@ include file="category.jspf" %>
    </tags:sectionContainer2>
</form:form>
</cti:msgScope>
