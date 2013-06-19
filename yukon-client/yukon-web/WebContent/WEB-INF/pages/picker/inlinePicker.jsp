<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="picker">

<script type="text/javascript">
Picker.alreadySelectedHoverMessage = '<cti:msg2 key=".alreadySelectedHover"/>';
</script>

<div id="${id}">
    <div class="column_12_12">
        <div class="column one">
                    <label>
                        <i:inline key=".query"/> <input type="text" id="picker_${id}_ss" name="ss" onkeyup="${id}.doKeyUp();false;"/>
                    </label>
                    <a id="picker_${id}_showAllLink" href="javascript:${id}.showAll()">
                        <i:inline key=".showAll"/>
                    </a>
        </div>
        <div class="column two nogutter pagingArea">
                    <tags:nextPrevLinks previousUrl="javascript:${id}.previous()" nextUrl="javascript:${id}.next()" mode="javascript"/>
        </div>
    </div>

    <div id="picker_${id}_nothingSelected" style="display:none" class="errorMessage"><i:inline key=".nothingSelected"/></div>
    <div class="f_block_this">
        <div id="picker_${id}_results" class="pickerResults"></div>
        <div id="picker_${id}_noResults" style="display: none" class="pickerResults"><i:inline key=".noResults"/></div>
    </div>
</div>

</cti:msgScope>
