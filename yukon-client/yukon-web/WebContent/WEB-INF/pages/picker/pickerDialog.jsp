<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="picker">

<script type="text/javascript">
Picker.alreadySelectedHoverMessage = '<cti:msg2 key=".alreadySelectedHover"/>';
</script>

<cti:msg var="dialogTitle" key="${title}"/>
<tags:simplePopup title="${dialogTitle}" id="${id}" styleClass="pickerDialog">
    <form class="pickerHeader">
        <table>
            <tr>
                <td class="queryStringArea">
                    <label><i:inline key=".query"/> <input type="text" id="picker_${id}_ss" name="ss"
                        onkeyup="${id}.doKeyUp();false;"/></label>
                    <a id="picker_${id}_showAllLink" href="javascript:${id}.showAll()">
                        <cti:msg key="yukon.web.picker.showAll"/>
                    </a>
                </td>
                <tags:nextPrevLinks previousUrl="javascript:${id}.previous()"
                    nextUrl="javascript:${id}.next()" mode="javascript"/>
            </tr>
        </table>
    </form>

    <div class="busyBox"></div>

    <div id="picker_${id}_nothingSelected" style="display:none" class="errorMessage"><i:inline key=".nothingSelected"/></div>
    <div id="picker_${id}_results" class="pickerResults"></div>
    <div id="picker_${id}_noResults" style="display: none" class="pickerResults"><i:inline key=".noResults"/></div>

    <div class="actionArea">
        <c:if test="${!immediateSelectMode}">
            <input type="button" onclick="${id}.okPressed()"
                value="<cti:msg key="yukon.web.picker.ok"/>"/>
        </c:if>
        <input type="button" onclick="${id}.hide()"
            value="<cti:msg key="yukon.web.picker.cancel"/>"/>
    </div>
</tags:simplePopup>
</cti:msgScope>
