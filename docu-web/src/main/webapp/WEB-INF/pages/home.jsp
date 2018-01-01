<%@ include file="/jsp-tiles/taglibs.jsp"%>
<head>
  <title>
    <fmt:message key="home.title" />
  </title>
  <link href="<c:url value='css/datatables/dataTables.bootstrap.css'/>" rel="stylesheet" type="text/css" />
  <link href="<c:url value='css/daterangepicker/daterangepicker-bs3.css'/>" rel="stylesheet" type="text/css" />
</head>
<body>

	<!-- Main content -->
	<section class="content" style="font-weight: normal">
	
	
 	<h3 style="color:#B0C4DE;margin-top:-10px;margin-left:10px">
   		 <i class="fa fa-home"></i> Accueil
   		<hr>
  	</h3>
  	
		<div class="row">
			<div class="col-md-12">

		<p/>
			<div class="col-lg-3 col-xs-6">
				<!-- small box -->
				<div class="small-box" style="background-color:#3c8dbc;color:white">
					<div class="inner">
						<h3>${homeBean.totalDocumentaires}</h3>
						<p>Documentaires</p>
					</div>
					<div class="icon">
						<i class="fa fa-film"></i>
					</div>
					<a href="<c:url value='/documentaireByGenre.htm?genre=23'/>" class="small-box-footer">Afficher Docus <i
						class="fa fa-arrow-circle-right"></i>
					</a>
				</div>
			</div>
			 <div class="col-lg-3 col-xs-6">
                            <!-- small box -->
                            <div class="small-box" style="background-color:#3c8dbc;color:white">
                                <div class="inner">
                                    <h3>${homeBean.totalSeries}</h3>
                                    <p>S&eacute;ries</p>
                                </div>
                                <div class="icon">
                                    <i class="fa fa-video-camera"></i>
                                </div>
                                <a href="#" class="small-box-footer">Afficher Series
                                <i class="fa fa-arrow-circle-right"></i>
                                </a>
                            </div>
                        </div>
			<!-- ./col -->
			<div class="col-lg-3 col-xs-6">
				<!-- small box -->
				<div class="small-box bg-green">
					<div class="inner">
						<h3>${homeBean.totalEpisodes}</h3>
						<p>Episodes</p>
					</div>
					<div class="icon">
						<i class="fa fa-youtube-play"></i>
					</div>
					<div class="small-box-footer">&nbsp;</div>
				</div>
			</div>
			<!-- ./col -->
			<div class="col-lg-3 col-xs-6">
				<!-- small box -->
				<div class="small-box bg-yellow">
					<div class="inner">
						<h3>${homeBean.totalDoublons}</h3>
						<p>Total Doublons</p>
					</div>
					<div class="icon">
						<i class="fa fa-warning"></i>
					</div>
					<div class="small-box-footer">&nbsp;</div>
				</div>
			</div>
			
		</div>
		<!-- /.row -->

		</div>
		</div>
		
	</section>
	

<script src="<c:url value='js/AdminLTE/dashboard.js'/>" type="text/javascript"></script>



</body>