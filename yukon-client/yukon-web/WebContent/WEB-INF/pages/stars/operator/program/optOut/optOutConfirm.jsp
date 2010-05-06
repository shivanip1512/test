<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<cti:standardPage module="operator" page="optOut.confirm">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/optOut.css"/>

    <script type="text/javascript">
    function createJSON() {
        var hash = $H();

        $$('INPUT').each(function(input) {
            var item = createItem(input);
            if (item) {
                var index = item['index'];
                var hash2 = hash[index];
                if (!hash2) {
                    hash2 = $H();
                    hash[index] = hash2;
                }
                hash2[item['type']] = item['value'];
            }
        }); 
    
        var inputElement = document.createElement('input');
        inputElement.type = 'hidden';
        inputElement.name = 'jsonQuestions';
        inputElement.value = hash.toJSON();
    
        $('form').appendChild(inputElement);
    }    

    function createItem(element) {
        var id = $(element).id;
        if (id) {
            if (id.startsWith('question_') || id.startsWith('answer_')) {
                var split = id.split('_');
            
                var hash = $H();
                hash['index'] = split[1];
                hash['type'] = split[0];
                hash['value'] = element.value;
                return hash;                    
            }
        }
    }
    </script>


    <tags:formElementContainer nameKey="optOuts">
        <form:form id="form" commandName="optOutBackingBean" 
                   action="/spring/stars/operator/program/optOut/update" 
                   method="POST">

            <form:hidden path="durationInDays" />
            <form:hidden path="startDate" />

            <input type="hidden" name="accountId" value="${accountId}" />
            <input type="hidden" name="inventoryIds" value="${inventoryIds}" />

            <c:set var="index" value="0"/> 
            <table id="questionTable">
                <c:forEach var="question" items="${questions}">
                    <c:set var="index" value="${index + 1}"/>
                    <tr>
                        <td>
                            <div class="optOutQuestion">${question}</div>
                            <input type="hidden" id="question_${index}" value="${question}"/>
                            <input type="text" id="answer_${index}" size="80"></input>
                        </td>
                    </tr>
                </c:forEach>
            </table>

            <br>
            <tags:slowInput2 key="save" formId="form" onsubmit="createJSON()"/>
            <button type="submit" name="cancel">
                <i:inline key=".cancel"/>
            </button>

        </form:form>
    </tags:formElementContainer>
</cti:standardPage>