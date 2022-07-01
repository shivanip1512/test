<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="statusBackgroundColorClass" type="java.lang.String" description="The class to use that specifies the background color for the Status Stripe." %>
<%@ attribute name="dateTime" type="java.lang.Object" description="The Date/Time to display." %>
<%@ attribute name="title" type="java.lang.String" required="true" description="The title to display." %>
<%@ attribute name="subTitle" type="java.lang.String" description="The subtitle to display." %>
<%@ attribute name="tagText" type="java.lang.String" description="The text to display in the tag." %>
<%@ attribute name="tagColor" type="java.lang.String" description="The class to use that specifies the background color for the tag." %>

<div class="info-list-item" style="width:100%;border-bottom:1px solid #ccc;display:inline-flex;">
    <span class="${statusBackgroundColorClass}" style="width:1.2%;margin-right:5px"></span>
    <span style="width:18%;font-size:12px;align-self:center">
        <b><cti:formatDate value="${dateTime}" type="TIME"/></b><br/>
        <cti:formatDate value="${dateTime}" type="DATE"/>
    </span>
    <span style="width:70%;align-self:center">
        <div><b>${title}</b></div>
        <div style="font-size:12px">${subTitle}</div>
    </span>
    <span style="align-self:center">
        <tags:infoListTag text="${tagText}" backgroundColor="${tagColor}"/>
    </span>

</div>
