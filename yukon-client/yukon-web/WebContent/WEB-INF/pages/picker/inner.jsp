<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="itemPicker_popup">
<!--  fix for IE6 bug (see itemPicker.css for more info) -->
<!--[if lte IE 6.5]><iframe></iframe><![endif]-->
<label>Query:<input name="itemPicker_query" id="itemPicker_query" type="text" onkeyup="javascript:${pickerId}.doKeyUp(this);false;"></label>
<span class="itemPicker_commandLinks">
  <a id="sameParentItemLink" href="javascript:${pickerId}.sameParentItem()">${sameItemLink}</a> | 
  <a id="showAllLink" href="javascript:${pickerId}.showAll()">Show all</a> | 
  <a href="javascript:${pickerId}.cancel()">Cancel</a>
</span>
<span id="itemPicker_indicator" style="visibility:hidden"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
<div id="itemPicker_results">
</div>

</div>

