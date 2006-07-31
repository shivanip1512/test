<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<div id="pointPicker_popup">

<label>Query:<input name="query" id="query" type="text" onkeyup="pointPicker_doKeyUp(this);false;"></label>
<span class="pointPicker_commandLinks"><a href="javascript:pointPicker_showAll(0)">Show all</a> | <a href="javascript:pointPicker_cancel()">Cancel</a></span>
<span id="pointPicker_indicator" style="display:none"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
<div id="pointPicker_results">
  <jsp:include page="results.jsp"/>
</div>

</div>