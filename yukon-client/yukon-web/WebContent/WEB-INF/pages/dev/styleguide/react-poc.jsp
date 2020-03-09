<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="reactPoc">
    <tags:styleguide page="reactPoc">
    
    <div id="root"></div>

    </tags:styleguide>
    
    <script src="<c:url value="/resources/js/lib/Babel/babel.min.js"/>"></script>
    <script src="<c:url value="/resources/js/lib/axios/axios.min.js"/>"></script>
    <script src="<c:url value="/resources/js/lib/React/react.development.js"/>"></script>
    <script src="<c:url value="/resources/js/lib/React/react-dom.development.js"/>"></script>
    <script src="<c:url value="/resources/js/pages/yukon.dev.reactPoc.js"/>" type="text/babel"></script>
</cti:standardPage>