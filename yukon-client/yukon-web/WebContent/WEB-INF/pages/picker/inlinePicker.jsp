<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="picker">

<div id="${id}">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <label>
                <i:inline key=".query"/> <input type="text" id="picker-${id}-ss" name="ss" onkeyup="${id}.doKeyUp.call(${id});false;"/>
            </label>
            <a id="picker-${id}-show-all-link" href="javascript:${id}.showAll.call(${id})">
                <i:inline key=".showAll"/>
            </a>
        </div>
        <div class="column two nogutter paging-area">
            <tags:nextPrevLinks previousUrl="javascript:${id}.previous.call(${id})" nextUrl="javascript:${id}.next.call(${id})"/>
        </div>
    </div>

    <div id="picker-${id}-nothing-selected" style="display:none" class="error"><i:inline key=".nothingSelected"/></div>
    <div class="js-block-this">
        <div id="picker-${id}-results"></div>
        <div id="picker-${id}-no-results" style="display: none"><i:inline key=".noResults"/></div>
    </div>
    <c:if test="${multiSelectMode}">
        <div class="action-area">
            <input type="checkbox" id="picker-${id}-select-all" onclick="javascript:${id}.selectAll.call(${id})">
            <label for="picker-${id}-select-all" class="cp"><i:inline key=".selectAll"/></label>
            &nbsp;&nbsp;
            <span style="display: none">
                <i:inline key=".pageSelected"/>&nbsp;&nbsp;
                <a id="picker-${id}-select-all-pages" href="javascript:${id}.selectAllPages.call(${id})">place holder</a>
            </span>
            <span style="display: none">
                <i:inline key=".selectionCleared"/>&nbsp;&nbsp;
                <a id="picker-${id}-clear-entire-selection"
                    href="javascript:${id}.clearEntireSelection.call(${id})"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span style="display: none">
                <span id="picker-${id}-all-pages-selected">place holder</span>&nbsp;&nbsp;
                <a href="javascript:${id}.clearEntireSelection.call(${id})"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span id="picker-${id}-entire-selection-cleared" style="display: none">
                <i:inline key=".entireSelectionCleared"/>
            </span>
        </div>
    </c:if>
</div>

</cti:msgScope>