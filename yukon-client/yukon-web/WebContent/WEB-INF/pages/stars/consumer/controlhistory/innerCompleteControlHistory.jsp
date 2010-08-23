<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:msgScope paths="yukon.dr.consumer.innercompletecontrolhistory">
	<dr:controlHistory displayableControlHistoryMap="${displayableControlHistoryMap}" consumer="${consumer}"/>
</cti:msgScope>