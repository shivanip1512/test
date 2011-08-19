<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="supportBundle">
    <script type="text/javascript"">
        Event.observe(window, 'load', function(){
            new Ajax.PeriodicalUpdater("mainDiv", "createBundle/getStatus", {
                frequency : 1
            });
        });
    </script>
    <div id="mainDiv"></div>
</cti:standardPage>
