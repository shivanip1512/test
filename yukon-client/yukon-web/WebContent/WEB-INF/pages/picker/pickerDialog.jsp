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
    <div class="pickerHeader">
        <table>
            <tr>
                <td class="queryStringArea">
                    <label><i:inline key=".query"/> <input type="text" id="picker_${id}_ss" name="ss"
                        onkeyup="${id}.doKeyUp();false;"/></label>
                    <a id="picker_${id}_showAllLink" href="javascript:${id}.showAll()">
                        <i:inline key=".showAll"/>
                    </a>
                </td>
                <td class="pagingArea">
	                <tags:nextPrevLinks previousUrl="javascript:${id}.previous()"
	                    nextUrl="javascript:${id}.next()" mode="javascript"/>
                </td>
            </tr>
        </table>
    </div>

    <div id="picker_${id}_nothingSelected" style="display:none" class="errorMessage"><i:inline key=".nothingSelected"/></div>
    <div id="picker_${id}_results" class="pickerResults f_block_this"></div>
    <div id="picker_${id}_noResults" style="display: none" class="pickerResults"><i:inline key=".noResults"/></div>

    <table class="actionArea">
        <tr>
            <c:if test="${multiSelectMode}">
                <td class="leftActions">
                    <input class="notButton" id="picker_${id}_selectAll" type="checkbox"
                        onclick="javascript:${id}.selectAll()">
                    <label for="picker_${id}_selectAll" class="simpleLink pointer"><i:inline key=".selectAll"/></label>
                    &nbsp;&nbsp;
                    <span style="display: none">
                        <i:inline key=".pageSelected"/>&nbsp;&nbsp;
                        <a id="picker_${id}_selectAllPages" href="javascript:${id}.selectAllPages()">place holder</a>
                    </span>
                    <span style="display: none">
                        <i:inline key=".selectionCleared"/>&nbsp;&nbsp;
                        <a id="picker_${id}_clearEntireSelection"
                            href="javascript:${id}.clearEntireSelection()"><i:inline key=".clearEntireSelection"/></a>
                    </span>
                    <span style="display: none">
                        <span id="picker_${id}_allPagesSelected">place holder</span>&nbsp;&nbsp;
                        <a href="javascript:${id}.clearEntireSelection()"><i:inline key=".clearEntireSelection"/></a>
                    </span>
                    <span id="picker_${id}_entireSelectionCleared" style="display: none">
                        <i:inline key=".entireSelectionCleared"/>
                    </span>
                </td>
            </c:if>
            <td class="rightActions">
                <c:if test="${!immediateSelectMode}">
                    <input type="button" onclick="${id}.okPressed()"
                        value="<cti:msg2 key=".ok"/>"/>
                </c:if>
                <input type="button" onclick="${id}.cancel()"
                    value="<cti:msg2 key=".cancel"/>"/>
            </td>
        </tr>
    </table>

</tags:simplePopup>
</cti:msgScope>
