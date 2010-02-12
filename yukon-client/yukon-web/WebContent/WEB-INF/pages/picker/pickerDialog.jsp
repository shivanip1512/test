<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="dialogTitle" key="${title}"/>
<tags:simplePopup title="${dialogTitle}" id="${id}" styleClass="pickerDialog">
    <form class="pickerHeader">
        <table width="100%">
            <tr>
                <td class="queryStringArea">
                    <label>Query: <input type="text" id="picker_${id}_ss" name="ss"
                        onkeyup="${id}.doKeyUp();false;"/></label>
                    <a id="picker_${id}_showAllLink" href="javascript:${id}.showAll()">
                        <cti:msg key="yukon.web.modules.picker.showAll"/>
                    </a>
                </td>
                <tags:nextPrevLinks previousUrl="javascript:${id}.previous()"
                    nextUrl="javascript:${id}.next()" mode="javascript"/>
            </tr>
        </table>
    </form>

    <div class="busyBox"></div>

    <div id="picker_${id}_results" class="pickerResults"></div>

    <div class="actionArea">
        <c:if test="${!immediateSelectMode}">
            <input type="button" onclick="${id}.okPressed()"
                value="<cti:msg key="yukon.web.modules.picker.ok"/>"/>
        </c:if>
        <input type="button" onclick="${id}.hide()"
            value="<cti:msg key="yukon.web.modules.picker.cancel"/>"/>
    </div>
</tags:simplePopup>
