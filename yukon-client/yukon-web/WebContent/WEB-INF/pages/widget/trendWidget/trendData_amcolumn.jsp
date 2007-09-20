<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<chart>

	<series>
		<c:forEach var="xValue" items="${xAxisValues}" >
			<value xid="${xValue.id}">${xValue.formattedValue}</value>
		</c:forEach>		
	</series>
	
	<graphs>
		
		<c:forEach var="graph" items="${graphList}" >
			
			<graph                                           
		      
		      type="column"                                                      
		      title="${graph.seriesTitle}"                                     
		                                            
		                                                 
		      gradient_fill_colors=""  
		              
		      balloon_color="FFCC00"                        
		      balloon_alpha="45"                           
		      balloon_text_color="#000000"              
		      
		      
		     >
		     
			<c:forEach var="dataValue" items="${graph.chartData}">
				<value xid="${dataValue.id}" color="#009933" description="${dataValue.description}">${dataValue.formattedValue}</value>
			</c:forEach>

			</graph>
			
			
		</c:forEach>

	</graphs>
	
</chart>

