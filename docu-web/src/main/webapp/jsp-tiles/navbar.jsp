<%@page import="fr.clunven.docu.web.domain.DocuUser"%>

<%@ include file="/jsp-tiles/taglibs.jsp"%>

<header class="header">

	<a href="<c:url value='/home.htm'/>" class="logo" >
	   <img src="<c:url value='img/docus.png'/>" alt="" height="60px" style="padding: 10px; margin-top: -5px" />
	    &nbsp;<b>D O C U S</b>
	</a>

	<!-- Header Navbar: style can be found in header.less -->
	<nav class="navbar navbar-static-top" role="navigation">
		        
		<div class="navbar-right">
        
			<ul class="nav navbar-nav">
			
				<!-- User Account: style can be found in dropdown.less -->
				<li class="user user-menu">
						<a href="<c:url value="logout.htm" />" >
		  					<i class="fa fa-sign-out"></i>
							D&eacute;connexion
						</a>
					</li>
			</ul>
		</div>
	</nav>
	
	<nav style="color:#F0F8FF;background-color:#222222;height:35px;
		top:60px;width:100%;padding-left:10px;border-top:1px solid #888888;
		border-bottom:1px solid #888888;padding:5px">
		<a href="<c:url value='/home.htm'/>" style="font-weight: normal; color: #F0F8FF;padding-right:10px;padding-left:10px;">
		  <i class="fa fa-home" style="color:#3c8dbc"></i>&nbsp;Home
		</a> |
	    <a href="<c:url value='/documentaireByGenre.htm?genre=23'/>" style="font-weight: normal; color: #F0F8FF;padding-right:10px;padding-left:10px;">
			<i class="fa fa-film" style="color:#3c8dbc"></i>&nbsp;Documentaires
	    </a> |	
	    <a href="<c:url value='/seriesByGenre.htm?genre=7'/>" style="font-weight: normal; color: #F0F8FF;padding-right:10px;padding-left:10px;">
		      <i class="fa fa-video-camera" style="color:#3c8dbc"></i>&nbsp;S&eacute;ries
		</a> |
		  <a href="<c:url value='/documentaire.htm'/>" style="font-weight: normal; color: #F0F8FF;padding-right:10px;padding-left:10px;">
		      <i class="fa fa-list" style="color:#3c8dbc"></i>&nbsp;Rechercher
			</a> |
			 <a href="<c:url value='/batchSynchro.htm'/>"  style="font-weight: normal; color: #F0F8FF;padding-right:10px;padding-left:10px;">
		       <i class="fa fa-users" style="color:#3c8dbc"></i>&nbsp;Batch Synchro
			</a>

	</nav>
</header>