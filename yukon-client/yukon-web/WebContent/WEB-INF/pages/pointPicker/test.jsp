<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<cti:standardPage title="Point Picker Test" module="commercialcurtailment">


<input id="blah" type="text" value="0"> 
<cti:pointPicker pointIdField="blah" constraint="com.cannontech.common.search.criteria.KWCriteria">Select point...</cti:pointPicker><br>

<hr>
 some other content 
 <br>
 <br>
 <br>
 <br>
 <br>
<hr>
<h2>another picker</h2>
<input id="blah2" type="text" value="0"> 
<cti:pointPicker pointIdField="blah2" constraint="com.cannontech.common.search.criteria.KWCriteria">Select point...</cti:pointPicker><br>

</cti:standardPage>
