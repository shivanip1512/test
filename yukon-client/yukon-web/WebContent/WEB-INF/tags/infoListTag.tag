<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="text" type="java.lang.String" required="true" description="The text to display in the tag." %>
<%@ attribute name="backgroundColor" type="java.lang.String" required="true" description="The class to use that specifies the background color for the tag." %>
<%@ attribute name="textColor" type="java.lang.String" description="The class to use that specifies the text color for the tag. Default is white" %>

<cti:default var="textColor" value="txt-color-white"/>

<div class="info-list-tag-item ${backgroundColor}">
    <span class="${textColor}">${text}</span>
</div>
