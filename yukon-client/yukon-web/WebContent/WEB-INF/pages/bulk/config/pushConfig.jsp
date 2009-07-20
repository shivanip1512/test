<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.pushConfig.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- push config --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.pushConfig.header"/>
    <cti:msg var="cancel" key="yukon.common.device.bulk.pushConfig.cancel" />
    <cti:msg var="push" key="yukon.common.device.bulk.pushConfig.push" />
    <cti:msg var="verify" key="yukon.common.device.bulk.pushConfig.verify" />
    <tags:bulkActionContainer   key="yukon.common.device.bulk.pushConfig" deviceCollection="${deviceCollection}">
    
        <form id="pushConfigForm" method="post" action="/spring/bulk/config/doPushConfig">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <table>
                <tr>
                    <td class="smallBoldLabel">
                        <cti:msg key="yukon.common.device.bulk.pushConfig.selectLabel"/>
                    </td>
                    <td>
                        <select id="method" name="method">
                                <option value="Standard">Standard</option>
                                <option value="Force">Force</option>
                                <option value="Read">Read</option>
                        </select>
                    </td>
                    <td><input type="submit" name="pushButton" value="${push}"></td>
                </tr>
            </table>
            <hr width="98%">
            <table>
                <tr>
                    <td class="smallBoldLabel">
                        <cti:msg key="yukon.common.device.bulk.pushConfig.noteLabel"/>
                    </td>
                    <td style="font-size:11px;">
                        <cti:msg key="yukon.common.device.bulk.pushConfig.verifyNoteText"/>
                    </td>
                </tr>
                <tr>
                    <td><input type="submit" name="verifyButton" value="${verify}"></td>
                </tr>
            </table>
            <hr width="98%">
            <input type="submit" name="cancelButton" value="${cancel}">
            
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>