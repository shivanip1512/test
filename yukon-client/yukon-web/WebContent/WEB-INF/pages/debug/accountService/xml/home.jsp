<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Account Service XML Test Page" module="debug">
    <cti:standardMenu menuSelection="accountService|xml" />
    
    <h2>Account Service XML Test Page</h2>
    <br>
    
    <form action="/spring/debug/accountService/xml/home" method="post">

        <textarea>
XML
        </textarea>
    
    </form>
    
</cti:standardPage>