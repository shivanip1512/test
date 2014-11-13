<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="dev" page="uberLog">

   <script>
    yukon.namespace('yukon.dev.uberLog');

    yukon.dev.uberLog = (function() {
        var _initialized = false,
        _download = function(sendData) {
            var uberLogFormData = $('#uberLogForm').serialize();
            window.location = 'download-uber-log?' + uberLogFormData;
        },
        
        mod = {
            init : function() {
                if (_initialized) return;
                
                $('#download-button').click(_download);
                
                _initialized = true;
            }
        };
        return mod;
    }());

    $(function() {
        yukon.dev.uberLog.init();
    });
    </script>

    <form id='uberLogForm'>
        <tags:sectionContainer title="Download Uber Log">
            <tags:nameValueContainer2>
                <tags:nameValue2 argument="Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <date:dateTime name="start" value="${start}"/>
                </tags:nameValue2>
                <tags:nameValue2 argument="Stop" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <date:dateTime name="stop" value="${stop}"/>
                </tags:nameValue2>
                <tags:nameValue2 argument="Logs" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select multiple name="logs" size="${fn:length(logs)}">
                        <c:forEach var="log" items="${logs}">
                            <option value="${log}">${log.label}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer>
        <div class="page-action-area">
            <cti:button id="download-button" label="Download" classes="primary action"/>
         </div>
     </form>
</cti:standardPage>