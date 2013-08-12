<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/JavaScript/configurationCategory.js"/>

<dialog:ajaxPage nameKey="createCategory" module="tools" page="configs.config" okEvent="yukonDialogSubmit" 
    id="categoryPopup"
    options="{ 'height' : '600', 'width' : '988' }">
    <form:form commandName="categoryEditBean" action="/deviceConfiguration/category/saveInPlace">
        <form:hidden path="categoryType"/>
        <form:hidden path="categoryId"/>
        <input type="hidden" name="configId" value="${configId}"/>
        
        <cti:msgScope paths=",.category">
            <tags:nameValueContainer2 tableClass="stacked">
                <tags:inputNameValue nameKey=".categoryName" path="categoryName" size="50" maxlength="60"/>
                <tags:nameValue2 nameKey=".type"><i:inline key=".${categoryTemplate.categoryType}.title"/></tags:nameValue2>
                <tags:textareaNameValue nameKey=".description" rows="4" cols="50" path="description" nameClass="vat"/>
            </tags:nameValueContainer2>
        </cti:msgScope>
        
        <%@ include file="category.jspf" %>
    </form:form>
</dialog:ajaxPage>