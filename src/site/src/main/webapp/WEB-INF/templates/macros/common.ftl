<#-- @ftlvariable name="locale" type="java.lang.String" -->
<#-- @ftlvariable name="pageTitle" type="java.lang.String" -->
<#-- @ftlvariable name="static" type="java.lang.String" -->
<#setting url_escaping_charset='UTF-8'>
<#macro page><!DOCTYPE html>
<html lang="${locale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="${static}/assets/img/favicon.png">

    <title>${pageTitle}</title>

    <!-- Bootstrap core CSS -->
    <link href="${static}/assets/css/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="${static}/assets/css/main.css" rel="stylesheet">

    <!-- Fonts from Google Fonts -->
    <link href='http://fonts.googleapis.com/css?family=Lato:300,400,900' rel='stylesheet' type='text/css'>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="http://yastatic.net/jquery/1.11.1/jquery.min.js"></script>
    <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
</head>

<body>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-743380-6', 'auto');
  ga('send', 'pageview');

</script>
<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<@link name="IndexPage"/>"><b><span style="font-size:30px;">PBOX<sup>&alpha;</sup></span></b></a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="<@link name="HelpPage"/>">How to use</a></li>
                <li><a href="<@link name="PackagesPage"/>">Packages</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</div>

<#nested/>

<div class="container">
    <hr>
    <p class="centered">PBOX &copy; MikeMirzayanov 2014</p>
</div><!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="${static}/assets/js/bootstrap.min.js"></script>
</body>
</html>
</#macro>

<#macro hasError error=""><#if error?? && (error?length > 0)>has-error</#if></#macro>

<#macro textField for text="">
    <#if text?? && (text?length > 0)>
    <p class="help-block for__${for}">${text}</p>
    </#if>
</#macro>
