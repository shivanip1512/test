<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="items" required="true" type="java.util.List" description="A List of LMDto objects to be displayed in the dropdown."%>

<cti:msgScope paths="yukon.web.modules.operator.signalTransmitter">
    <tags:nameValue2 nameKey=".commChannel">
         <cti:displayForPageEditModes modes="CREATE,EDIT">
            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
            <tags:selectWithItems items="${items}"
                                  path="commChannel.id"
                                  defaultItemLabel="${selectLbl}"
                                  itemLabel="name"
                                  itemValue="id"
                                  inputClass="w300"/>
         </cti:displayForPageEditModes>
         <cti:displayForPageEditModes modes="VIEW">
          <div class = "w300 wrbw dib">  ${fn:escapeXml(signalTransmitter.commChannel.name)}</div>
         </cti:displayForPageEditModes>
    </tags:nameValue2>
</cti:msgScope>