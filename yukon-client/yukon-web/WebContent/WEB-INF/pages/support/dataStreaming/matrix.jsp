<table class="matrix">
        <tr>
            <th style="width: 130px;" class="firstCol"><cti:msg2 key=".deviceType" /></th>
            <c:forEach var="atrId" items="${attributes}">
                <th class="rtate30"><div><span>${atrId.getDescription() }</span></div></th>
            </c:forEach>
        </tr>
        <c:forEach var="device" items="${devices}">
            <tr>
            <td class="firstCol">${device.getDbString()}</td>
            <c:forEach var="attribute" items="${attributes}">
                <td>${dataStreamingDevices.get(device).contains(attribute)?"X":""}</td>
            </c:forEach>
            </tr>
        </c:forEach>
    </table>