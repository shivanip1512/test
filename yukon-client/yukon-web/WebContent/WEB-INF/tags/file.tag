<%@ tag dynamic-attributes="attrs" body-content="empty" 
    description="Creates a dropdown menu option inside a dropdown.tag element." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="name" description="Name of the file input. Default: 'dataFile'." %>

<%@ attribute name="classes" description="Class attribute applied to the 'span' container element." %>
<%@ attribute name="icon" description="Icon class name.  Defaut: icon-upload" %>
<%@ attribute name="id" description="Id html attribute applied to the file input element." %>
<%@ attribute name="disabled" %>

<%@ attribute name="buttonKey" 
    description="i18n object to use for the button text. Note: key can be an i18n key String, 
                 MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
                 Default: 'yukon.common.upload'." %>
<%@ attribute name="promptKey" 
    description="i18n object to use for the prompt text. Note: key can be an i18n key String, 
                 MessageSourceResolvable, Displayable, DisplayableEnum, or ResolvableTemplate.
                 Default: 'yukon.common.chooseFile'." %>

<cti:msgScope paths=", yukon.common">

<cti:default var="name" value="dataFile"/>
<cti:default var="icon" value="icon-upload"/>
<cti:default var="buttonKey" value=".upload"/>
<cti:default var="promptKey" value=".chooseFile"/>

<c:set var="classes" value="file-upload ${pageScope.classes}"/>
<c:set var="disabled" value="${not empty pageScope.disabled && disabled == 'true' ? 'disabled' : ''}"/>

<span class="${classes}">
    <div class="button M0">
        <cti:icon icon="${icon}"/>
        <span class="b-label"><cti:msg2 key="${buttonKey}"/></span>
        <input type="file" name="${name}" <c:if test="${not empty pageScope.id}">id="${id}"</c:if> 
            <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    </div>&nbsp;<span class="file-name form-control"><cti:msg2 key="${promptKey}"/></span>
</span>
</cti:msgScope>