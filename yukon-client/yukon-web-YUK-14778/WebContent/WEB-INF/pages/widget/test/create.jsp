<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage module="blank" title="Widget Test">
<cti:standardMenu/>

<ct:widgetContainer>
<div>
<ct:widget width="50%" bean="${param.type}" identify="true" paramMap="${param}"/>
</div>
</ct:widgetContainer>

(Widgets are displayed at 50% width.)

</cti:standardPage>