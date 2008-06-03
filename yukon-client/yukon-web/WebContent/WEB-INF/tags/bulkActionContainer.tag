<%@ attribute name="titleKey" required="true" type="java.lang.String"%>
<%@ attribute name="noteLabelKey" required="false" type="java.lang.String"%>
<%@ attribute name="noteLabelStyle" required="false" type="java.lang.String"%>
<%@ attribute name="noteTextKey" required="false" type="java.lang.String"%>
<%@ attribute name="noteTextArguments" required="false" type="java.lang.String"%>
<%@ attribute name="deviceCollection" required="false" type="com.cannontech.common.bulk.collection.DeviceCollection"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="headerTitle" key="${titleKey}"/>
<tags:boxContainer title="${headerTitle}" id="collectionActionsContainer" hideEnabled="false">

    <%-- SELECTED DEVICES POPUP, NOTE TO USER --%>
    <table cellpadding="2">
    
        <c:if test="${not empty deviceCollection}">
        <tr>
            <td valign="top" colspan="2" class="smallBoldLabel">
                <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
            </td>
        </tr>
        </c:if>
        
        <%-- NOTE --%>
        <c:if test="${not empty noteLabelKey && not empty noteTextKey}">
            <tr>
                <td valign="top" class="smallBoldLabel" <c:if test="${not empty noteLabelStyle}">style="${noteLabelStyle}"</c:if>><cti:msg key="${noteLabelKey}"/></td>
                <td style="font-size:11px;">
                    <cti:msg key="${noteTextKey}" arguments="${noteTextArguments}" />
                </td>
            </tr>
        </c:if>
    </table>
    <br>
    
<jsp:doBody/>
    
</tags:boxContainer>