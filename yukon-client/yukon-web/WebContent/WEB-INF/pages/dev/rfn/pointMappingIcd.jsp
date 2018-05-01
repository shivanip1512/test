<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest">

    <tags:sectionContainer title="Point Mapping ICD">
       <h2>Manufacturers</h2>
       <a data-show-hide="#manufacturers">Toggle</a>
       <pre id="manufacturers">${manufacturers}</pre>
       
       <h2>Models</h2>
       <a data-show-hide="#models">Toggle</a>
       <pre id="models">${models}</pre>
       
       <h2>Units</h2>
       <a data-show-hide="#units">Toggle</a>
       <pre id="units">${units}</pre>
       
       <h2>Metrics</h2>
       <a data-show-hide="#metrics">Toggle</a>
       <pre id="metrics">${metrics}</pre>
       
   <c:forEach var="point" items="${points}">
       <h2>${point.title}</h2>
       <a data-show-hide="#${point.id}">Toggle</a>
       <pre id="${point.id}">${point.content}</pre>
   </c:forEach>
       
       <h2>rfnPointMapping</h2>
       <a data-show-hide="#rfnPointMapping">Toggle</a>
       <pre id="rfnPointMapping">${rfnPointMapping}</pre> 
       
       <h2>ICD JSON</h2>
       <a data-show-hide="#icdjson">Toggle</a>
       <pre id="icdjson" class="dn">${icd}</pre>
    </tags:sectionContainer>

</cti:standardPage>