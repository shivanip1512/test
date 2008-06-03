<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:bulkUpdateResultsTable resultsType="${resultsType}"
                            lineCount="${lineCount}"
                            bulkUpdateOperationResults="${bulkUpdateOperationResults}" />