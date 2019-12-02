<%@ tag body-content="empty" %>

<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="controlBitsLength" required="true" type="java.lang.Integer" %>
<%@ attribute name="restoreBitsLength" required="true" type="java.lang.Integer" %>

<tags:nameValue2 nameKey=".control">
    <form:hidden path="control" cssClass="js-control-value"/>
    <dr:renderCheckboxGroup startIndex="1" endIndex="${controlBitsLength/2}" addressContainerClass="js-control-value_row1"/>
    <dr:renderCheckboxGroup startIndex="${(controlBitsLength/2) + 1}" endIndex="${controlBitsLength}" addressContainerClass="js-control-value_row2"/>
</tags:nameValue2>
<tags:nameValue2 nameKey=".restore">
    <form:hidden path="restore" cssClass="js-restore-value"/>
    <dr:renderCheckboxGroup startIndex="1" endIndex="${restoreBitsLength/2}" addressContainerClass="js-restore-value_row1"/>
    <dr:renderCheckboxGroup startIndex="${(restoreBitsLength/2) + 1}" endIndex="${restoreBitsLength}" addressContainerClass="js-restore-value_row2"/>
</tags:nameValue2>
