<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<style type="text/css">
    
    table.matrix > th.rtate45 > div {
        -webkit-transform-origin: 130px 38px;
        -moz-transform-origin: 130px 38px;
        -o-transform-origin: 130px 38px;
        -ms-transform-origin: 130px 38px;
        transform-origin: 130px 38px;
        -webkit-transform: rotate(315deg);
        -moz-transform: rotate(315deg);
        -ms-transform: rotate(315deg);
        -o-transform: rotate(315deg);
        transform: rotate(315deg);
        height: 100px;
        width: 20px;
    }
    
    table.matrix th.rtate45 > div >span {
        border-bottom: 1px solid;
        padding-bottom: 3px;
        white-space: nowrap;
    }
    
    table.matrix > th.rtate30 > div {
        -webkit-transform-origin: 175px 5px;
        -moz-transform-origin: 175px 5px;
        -o-transform-origin: 175px 5px;
        -ms-transform-origin: 175px 5px;
        transform-origin: 175px 5px;
        -webkit-transform: rotate(330deg);
        -moz-transform: rotate(330deg);
        -ms-transform: rotate(330deg);
        -o-transform: rotate(330deg);
        transform: rotate(330deg);
        height: 100px;
        width: 25px;
    }
    
    table.matrix th.rtate30 > div >span {
        border-bottom: 1px solid;
        white-space: nowrap;
    }
    
    table.matrix td{
        border-right: 1px solid !important;
        border-bottom: 1px solid !important;
    }
    
    table.matrix{
        border-bottom: 1px solid !important;
    }
    
</style>

<cti:standardPage module="support" page="dataStreamingSupport">
    
    <table class="matrix">
        <tr>
            <th style="height: 120px; width: 150px;"><i:inline key=".devices"/></th>
            <c:forEach var="atrId" items="${attributes}">
                <th class="rtate45"><div><span>${atrId.getDescription() }</span></div></th>
            </c:forEach>
        </tr>
        <c:forEach var="device" items="${devices}">
            <tr>
            <td>${device.getDbString()}</td>
            <c:forEach var="attribute" items="${attributes}">
                <td>${dataStreamingDevices.get(device).contains(attribute)?"X":""}</td>
            </c:forEach>
            </tr>
        </c:forEach>
    </table>
    
</cti:standardPage>
