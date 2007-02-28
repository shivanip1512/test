<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Point Picker Test" module="blank">

<h2>This should show all points</h2>
<input id="blah" type="hidden" value="0"> 
<span id="deviceNameSpan">POINT NAME</span> / <span id="pointNameSpan">POINT NAME</span>
<cti:pointPicker pickerId="funWithPointPicker" pointIdField="blah" constraint="" pointNameElement="pointNameSpan" deviceNameElement="deviceNameSpan">Select point...</cti:pointPicker><br>

<hr>
 some other content 
 <br>
 <br>
 <br>
 <br>
 <br>
<hr>
<h2>This should show KW Points</h2>
<input id="blah2" type="text" value="0"> 
<cti:pointPicker pickerId="kWPointPicker" pointIdField="blah2" constraint="com.cannontech.common.search.criteria.KWCriteria">Select point...</cti:pointPicker><br>

</cti:standardPage>
