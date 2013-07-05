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
<div title="${dialogTitle}" id="${id}" class="dn pickerDialog" role="dialog">
    <div class="clearfix">
	    <div class="fl">
	        <div class="stacked">
	            <label class="wsnw"><i:inline key=".query"/><input type="text" id="picker_${id}_ss" name="ss" onkeyup="${id}.doKeyUp.call(${id});false;"/></label>
	            <a id="picker_${id}_showAllLink" href="javascript:${id}.showAll.call(${id})" style="margin-left: 3px;"><i:inline key=".showAll"/></a>
	        </div>
	    </div>
	    <div class="fr pagingArea">
	        <tags:nextPrevLinks previousUrl="javascript:${id}.previous.call(${id})" nextUrl="javascript:${id}.next.call(${id})" mode="javascript"/>
	    </div>
    </div>
    
    <div id="picker_${id}_nothingSelected" style="display:none" class="errorMessage"><i:inline key=".nothingSelected"/></div>
    <div class="f-block_this">
        <div id="picker_${id}_results" class="pickerResults"></div>
        <div id="picker_${id}_noResults" style="display: none" class="pickerResults"><i:inline key=".noResults"/></div>
    </div>

    <c:if test="${multiSelectMode}">
	    <div class="actionArea">
            <input type="checkbox" id="picker_${id}_selectAll" onclick="javascript:${id}.selectAll.call(${id})">
            <label for="picker_${id}_selectAll" class="simpleLink pointer"><i:inline key=".selectAll"/></label>
            &nbsp;&nbsp;
            <span style="display: none">
                <i:inline key=".pageSelected"/>&nbsp;&nbsp;
                <a id="picker_${id}_selectAllPages" href="javascript:${id}.selectAllPages.call(${id})">place holder</a>
            </span>
            <span style="display: none">
                <i:inline key=".selectionCleared"/>&nbsp;&nbsp;
                <a id="picker_${id}_clearEntireSelection"
                    href="javascript:${id}.clearEntireSelection.call(${id})"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span style="display: none">
                <span id="picker_${id}_allPagesSelected">place holder</span>&nbsp;&nbsp;
                <a href="javascript:${id}.clearEntireSelection.call(${id})"><i:inline key=".clearEntireSelection"/></a>
            </span>
            <span id="picker_${id}_entireSelectionCleared" style="display: none">
                <i:inline key=".entireSelectionCleared"/>
            </span>
	    </div>
    </c:if>
</div>
</cti:msgScope>