<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.support.dataStreamingSupport">

<div style="padding-right:100px">
<table class="matrix row-highlighting">
        <tr>
            <th class="firstCol wsnw"><cti:msg2 key=".deviceType" /></th>
            <c:forEach var="atrId" items="${attributes}">
                <th class="rtate30"><div><span>${atrId.getDescription() }</span></div></th>
            </c:forEach>
        </tr>
        <c:forEach var="device" items="${devices}">
            <tr>
            <td class="firstCol wsnw">${device.getDbString()}</td>
            <c:forEach var="attribute" items="${attributes}">
                <td>${dataStreamingDevices.get(device).contains(attribute)?"X":""}</td>
            </c:forEach>
            </tr>
        </c:forEach>
    </table>
    </div>
    </cti:msgScope>