<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage module="blank" title="Widget Test">
<cti:standardMenu/>

<ct:widgetContainer>
<div style="height: 400px; width: 400px">
<ct:widget height="300px" bean="${param.type}" identify="true" paramMap="${param}"/>
</div>
</ct:widgetContainer>

</cti:standardPage>