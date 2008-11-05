<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Load Control Service XML Test Page" module="debug">
    <cti:standardMenu menuSelection="loadControlService|xml" />
    
    <h2>Load Control Service XML Test Page</h2>
    <br>
    
    <form action="/spring/debug/loadControlService/xml/home" method="post">

        <textarea>
XML
        </textarea>
    
    </form>
    
</cti:standardPage>