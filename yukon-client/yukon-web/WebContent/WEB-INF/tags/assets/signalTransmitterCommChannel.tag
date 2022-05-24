<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="items" required="true" type="java.util.List"%>

<cti:msgScope paths="yukon.web.modules.operator.signalTransmitter">
    <tags:nameValue2 nameKey=".commChannel">
         <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
         <tags:selectWithItems items="${items}" path="commChannel.id" defaultItemLabel="${selectLbl}" itemLabel="name" itemValue="id"/>
    </tags:nameValue2>
</cti:msgScope>