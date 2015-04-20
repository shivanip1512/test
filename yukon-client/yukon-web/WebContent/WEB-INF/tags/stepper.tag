<%@ tag description="A component that looks like a select or textfield with a button on each side to switch
                     between options or change content." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="name" description="The name of the checkbox input." %>
<%@ attribute name="path" description="Spring path, used instead of 'name'." %>
<%@ attribute name="disabled" type="java.lang.Boolean" description="If 'true' switch will be disabled.  Default: false." %>

<%@ attribute name="classes" description="CSS class names applied to the outer wrapper element." %>
<%@ attribute name="id" description="The html id attribute of the input." %>

<cti:default var="classes" value=""/>
<cti:default var="disabled" value="${false}"/>
<cti:uniqueIdentifier prefix="stepper-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>

<label class="stepper ${classes}">
    <cti:button classes="stepper-prev left" renderMode="buttonImage" icon="icon-resultset-previous-gray"
            disabled="${disabled}"
    /><select class="stepper-select middle" type="checkbox" name="${name}" 
            id="${id}"<c:if test="${disabled}">disabled</c:if>><jsp:doBody/>
    </select><cti:button classes="stepper-next right" renderMode="buttonImage" icon="icon-resultset-next-gray" 
            disabled="${disabled}"/>
</label>