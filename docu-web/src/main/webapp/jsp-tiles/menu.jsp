<%@ include file="/jsp-tiles/taglibs.jsp"%>

<script type="text/javascript">
$( document ).ready(function() {
  CollapsibleLists.apply();
});
</script>

<aside class="left-side sidebar-offcanvas">
	<section class="sidebar">
		<ul class="sidebar-menu collapsibleList" style="font-weight: normal;">
		 <ul class="sidebar-menu collapsibleList">
		  
		   <li>
		    <a href="<c:url value='/home.htm'/>" style="font-weight: normal; color: #F0F8FF">
		     <i class="fa fa-home" style="color:#3c8dbc"></i>&nbsp;Home
			</a>
		  </li>
		
		  <li>
		    <a href="<c:url value='/documentaireByGenre.htm?genre=23'/>" style="font-weight: normal; color: #F0F8FF">
		     <i class="fa fa-film" style="color:#3c8dbc"></i>&nbsp;Documentaires
			</a>
		  </li>
		  
		  <li>
		    <a href="<c:url value='/seriesByGenre.htm?genre=7'/>" style="font-weight: normal; color: #F0F8FF">
		      <i class="fa fa-video-camera" style="color:#3c8dbc"></i>&nbsp;S&eacute;ries
			</a>
		  </li>
		  
		  <li>
		    <a href="<c:url value='/documentaire.htm'/>" style="font-weight: normal; color: #F0F8FF">
		      <i class="fa fa-list" style="color:#3c8dbc"></i>&nbsp;Rechercher
			</a>
		  </li>
		 
		  <li>
		    <a href="<c:url value='/batchSynchro.htm'/>"  style="font-weight: normal; color: #F0F8FF">
		       <i class="fa fa-users" style="color:#3c8dbc"></i>&nbsp;Batch Synchro
			</a>
		  </li>
		  
	      </ul>
			</ul>
	      
	      
	</section>
	<!-- /.sidebar -->
	

</aside>
