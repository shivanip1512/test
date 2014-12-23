<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<input id="popup-title" type="hidden" value="${popupTitle}"/>

<cti:url var="saveInPlaceUrl" value="/deviceConfiguration/category/saveInPlace"/>
<form:form id="category-form" commandName="categoryEditBean" action="${saveInPlaceUrl}" cssClass="js-preventSubmitViaEnterKey">
    <cti:csrfToken/>
    <form:hidden path="categoryType"/>
    <form:hidden path="categoryId"/>
    <input type="hidden" name="configId" value="${configId}"/>
    
    <cti:msgScope paths="modules.tools.configs, modules.tools.configs.category">
        <cti:flashScopeMessages/>
        <div class="stacked">
            <span class="warning">
                <i:inline key="yukon.common.warning"/>
            </span>
            <i:inline key=".category.changeWarning"/>
        </div>

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
        
    </cti:msgScope>
    
    <%@ include file="category.jspf" %>
</form:form>