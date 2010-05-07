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
                        <i:inline key=".showAll"/>
                    </a>
                </td>
                <tags:nextPrevLinks previousUrl="javascript:${id}.previous()"
                    nextUrl="javascript:${id}.next()" mode="javascript"/>
            </tr>
        </table>
    </form>

    <div id="picker_${id}_nothingSelected" style="display:none" class="errorMessage"><i:inline key=".nothingSelected"/></div>
    <div id="picker_${id}_results" class="pickerResults"></div>
    <div id="picker_${id}_noResults" style="display: none" class="pickerResults"><i:inline key=".noResults"/></div>

    <table class="actionArea">
        <tr>
            <c:if test="${multiSelectMode}">
                <td class="leftActions">
                    <input class="notButton" id="picker_${id}_selectAll" type="checkbox"
                        onclick="javascript:${id}.selectAll()">
                    <label for="picker_${id}_selectAll"><i:inline key=".selectAll"/></label>
                </td>
            </c:if>
            <td class="rightActions">
                <c:if test="${!immediateSelectMode}">
                    <input type="button" onclick="${id}.okPressed()"
                        value="<cti:msg2 key=".ok"/>"/>
                </c:if>
                <input type="button" onclick="${id}.hide()"
                    value="<cti:msg2 key=".cancel"/>"/>
            </td>
        </tr>
    </table>

</tags:simplePopup>
</cti:msgScope>
