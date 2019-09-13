<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="picker">

<div id="${id}" data-picker="${id}">
    <label class="dib M0"> 
        <i:inline key="yukon.common.search" /> 
        <input type="text" id="picker-${id}-ss" name="ss" class="js-picker-search-field" style="width:150px;">
        <cti:button renderMode="buttonImage" icon="icon-cross-gray"
            classes="dn fr right vat M0 js-picker-show-all" id="picker-${id}-show-all-link" />
    </label>
    
    <span id="inline-picker-paging" class="fr" style="font-size:12px;padding-top:5px;" data-current-page="${currentPage}">
        <a href="javascript:yukon.pickers['${id}'].previous()" class="previous-page" style="margin-right:5px;">
            <cti:icon icon="icon-resultset-previous-gray" classes="PR0 MR0"/>
            <span class="fl"><cti:msg2 key="yukon.common.paging.previous"/></span>
        </a>
        <span class="page-num-text"></span>
        <span class="fr" style="margin-left:5px;">
            <a href="javascript:yukon.pickers['${id}'].next()" class="next-page">
                <span class="fl"><cti:msg2 key="yukon.common.paging.next"/></span>
                <cti:icon icon="icon-resultset-next-gray" classes="PL0 ML0"/>
            </a>
        </span>
    </span>

    <div id="picker-${id}-nothing-selected" style="display:none" class="error"><i:inline key=".nothingSelected"/></div>
    <div id="picker-${id}-too-many-selections" style="display:none;" class="error"><i:inline key=".tooManySelections" arguments="${maxNumSelections}"/></div>
    <div class="js-block-this">
        <div id="picker-${id}-results"></div>
        <div id="picker-${id}-no-results" style="display: none" class="buffered empty-list">
            <i:inline key=".noResults"/>
        </div>
    </div>
    
    <c:if test="${multiSelectMode}">
        <div class="action-area">
            <input type="checkbox" id="picker-${id}-select-all" onclick="javascript:yukon.pickers['${id}'].selectAll()">
            <label for="picker-${id}-select-all" class="cp"><i:inline key=".selectAll"/></label>
            &nbsp;&nbsp;
            <span style="display: none">
                <i:inline key=".pageSelected"/>&nbsp;&nbsp;
                <a id="picker-${id}-select-all-pages" href="javascript:yukon.pickers['${id}'].selectAllPages()">place holder</a>
            </span>
            <span style="display: none">
                <i:inline key=".selectionCleared"/>&nbsp;&nbsp;
                <a id="picker-${id}-clear-entire-selection"
                    href="javascript:yukon.pickers['${id}'].clearEntireSelection()"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span style="display: none">
                <span id="picker-${id}-all-pages-selected">place holder</span>&nbsp;&nbsp;
                <a href="javascript:yukon.pickers['${id}'].clearEntireSelection()"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span id="picker-${id}-entire-selection-cleared" style="display: none">
                <i:inline key=".entireSelectionCleared"/>
            </span>
        </div>
    </c:if>
</div>

</cti:msgScope>