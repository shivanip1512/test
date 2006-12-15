<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div id="pointPicker_popup">

<label>Query:<input name="pointPicker_query" id="pointPicker_query" type="text" onkeyup="pointPicker_doKeyUp(this);false;"></label>
<span class="pointPicker_commandLinks">
  <a id="sameDeviceLink" href="javascript:pointPicker_sameDevice()">Same Device</a> | 
  <a id="showAllLink" href="javascript:pointPicker_showAll()">Show all</a> | 
  <a href="javascript:pointPicker_cancel()">Cancel</a>
</span>
<span id="pointPicker_indicator" style="visibility:hidden"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
<div id="pointPicker_results">
</div>

</div>