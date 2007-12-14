<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="itemPicker_popup">
<!--  fix for IE6 bug (see itemPicker.css for more info) -->
<!--[if lte IE 6.5]><iframe></iframe><![endif]-->

<div class="query">
	<label>Query:<input name="itemPicker_query" id="itemPicker_query" type="text" onkeyup="javascript:${pickerId}.doKeyUp(this);false;"></label>
</div>
<div class="itemPicker_commandLinks">
  <input type="hidden" id="sameParentItemLink" /> 
  <a id="showAllLink" href="javascript:${pickerId}.showAll()">Show all</a> | 
  <a href="javascript:${pickerId}.cancel()">Cancel</a>
  <span id="itemPicker_indicator" style="visibility:hidden"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
</div>
<div style="clear:both"></div>

<div class="multiItemPicker_commandLinks">
  	<a href="javascript:${pickerId}.itemSelectionComplete()">${selectionLinkName}</a>
</div>
<div style="clear:both"></div>

<div id="itemPicker_results">
</div>

</div>

