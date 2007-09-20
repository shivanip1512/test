<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<chart>


	<xaxis>
		<c:forEach var="xValue" items="${xAxisValues}" >
			<value xid="${xValue.id}">${xValue.formattedValue}</value>
		</c:forEach>		
	</xaxis>
	
	<graphs>
	
		<c:forEach var="graph" items="${graphList}" >

			<graph
			                                       
		      axis="${graph.YAxis}"                           
		      title="${graph.seriesTitle}"                     
		      color="#009933"                                  		  
		      color_hover="#E77471"                             
		      line_alpha=""                               
		      line_width=""                                                                  
		      fill_alpha=""                               
		      fill_color=""                               
		      balloon_color="FFCC00"                         
		      balloon_alpha="45"                         
		      balloon_text_color="#000000"  
		      bullet="round"                                       
		                                                              
		      bullet_size="4"                             
		      bullet_color=""                           
		      bullet_alpha=""                           
		      hidden=""                                       
		      selected=""  
		                      
		      vertical_lines=""                      
		    
		    >
    
			<c:forEach var="dataValue" items="${graph.chartData}">
				<value xid="${dataValue.id}" description="${dataValue.description}">${dataValue.formattedValue}</value>
			</c:forEach>
			
			</graph>
		
		</c:forEach>
	
	</graphs>
	
</chart>

