<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="optOutUrl" value="/operator/Consumer/OptOut.jsp" />
<cti:url var="actionUrl" value="/stars/operator/optout/update" />

<script type="text/javascript">
function createJSON() {
    var hash = $H();

    $$('INPUT').each(function(input) {
        var item = createItem(input);
        if (item) {
            var index = item.get('index');
            var hash2 = hash.get(index);
            if (!hash2) {
                hash2 = $H();
                hash.set(index, hash2);
            }
            hash2.set(item.get('type'), item.get('value'));
        }
    }); 
    
    var inputElement = document.createElement('input');
    inputElement.type = 'hidden';
    inputElement.name = 'jsonQuestions';
    inputElement.value = Object.toJSON(hash);
    
    $('form').appendChild(inputElement);
}    

/**
 * @returns {Hash|undefined(implied)}
 */
function createItem(element) {
    var id = $(element).id;
    if (id) {
        if (id.startsWith('question_') || id.startsWith('answer_')) {
            var split = id.split('_');

            return $H({
                'index': split[1],
                'type': split[0],
                'value': element.value
            });                    
        }
    }
}
</script>    
    
    <h3><cti:msg key="yukon.dr.operator.optoutconfirm.header"/></h3>
    
        <cti:msg key="yukon.dr.operator.optoutconfirm.description"/>
        
        <br>
        <br>
        
        <form id="form" action="${actionUrl}" method="POST" onsubmit="createJSON();">
            <c:set var="index" value="0"/> 
            <table id="questionTable">
                <c:forEach var="question" items="${questions}">
                    <c:set var="index" value="${index + 1}"/>
                    <tr>
                        <td>
                            <div style="padding-bottom: 0.3em; text-align: left;">${question}</div>
                            <input type="hidden" id="question_${index}" value="${question}"/>
                            <input type="text" id="answer_${index}" size="80"></input>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
            <div>
                <br>
                <span style="padding-right: 0.5em;">
                    <input type="submit" value="<cti:msg key='yukon.dr.operator.optoutconfirm.save'/>"></input>
                </span>    
                <input type="button" value="<cti:msg key='yukon.dr.operator.optoutconfirm.cancel'/>"
                       onclick="javascript:location.href='${optOutUrl}';"></input>
            </div>
    
            <input type="hidden" name="durationInDays" value="${durationInDays}"></input>
            <input type="hidden" name="startDate" value="${startDate}"></input>
            <input type="hidden" name="jsonInventoryIds" value="${jsonInventoryIds}"></input>
        </form>
