<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
<link href="tweets4discovery.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<!-- script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script type="text/javascript" src="tweets4discovery.js"></script>
<script>
	$(function() {
		$( "#since" ).datepicker({ minDate: -7, maxDate: 0, dateFormat: "yy-mm-dd"});
		$( "#until" ).datepicker({ minDate: -1, maxDate: 0, dateFormat: "yy-mm-dd"});

		$( '#since' ).change(function() { 
			$( "#until" ).datepicker('option', 'minDate', $( '#since' ).val());
		});		
			
		// initialize the date range
		var day = new Date();
		$( "#until" ).datepicker("setDate", day);
		day.setDate(day.getDate() - 1);
		$( "#since" ).datepicker("setDate", day);

		$( '#queryString' ).change(function() {
			if ( $( '#queryString' ).val() == null || $( '#queryString' ).val().trim() == "" ) {
				$( "#searchButton" ).attr('disabled','disabled');
				/* $( "#getJsonButton" ).attr('disabled','disabled');
				$( "#getDocxButton" ).attr('disabled','disabled');
				$( "#queryString" ).css({background: 'red'}); */
			} else {
				$( "#searchButton" ).removeAttr('disabled');
				/* $( "#getJsonButton" ).removeAttr('disabled');
				$( "#getDocxButton" ).removeAttr('disabled');
				$( "#queryString" ).css({background: 'none'}); */
			}
		});
		
		$(document).ready(function() {
			setDisplayTemplate(0);
		});
	});
</script>
<title>Twitter 4 Discovery</title>
</head>
<body>
	<div id="queryDiv">
		<!-- h2>Query</h2 -->
		<!-- <form name="form_sessions" action="json" method="GET"> -->
		<p><img alt="Discovery Channel" src="http://www.discoveryuk.com/localresources/interestgroups/images/global/discovery-logo.png"/></p>
		<p>
			Search Twitter:	<input type="text" name="query" id="queryString" value="@discovery"/>
			From: 			<input type="text" id="since" size="12" />
			To: 			<input type="text" id="until" size="12" />			
			<button id="searchButton" onclick="doSearch();">Search</button>
			<!-- button id="getJsonButton" onclick="getJson();">Text results</button -->
			<!--  button id="getDocxButton" onclick="getDocx();">Word document</button -->
			<!-- <input type="submit" name="submit" id="submitButton" value="JSON results" accept="" /> -->
		</p>
		<hr size="1" />
	</div>
	<div id="spinnerDiv">
		<h2>Just a few seconds...</h2>
		<div id="imageDiv">
			<img class="centred" src="spinner_blue.gif"/>
		</div>	
		<hr size="1" />
	</div>
	<div id="resultsDiv">
		<h2>Results</h2>
		<p><a id="docxLink"></a></p>
		<table id="resultsTable"><tbody></tbody></table>
		<hr size="1" />
	</div>		
	<div id="errorDiv">
		<h2>Error</h2>
		<p id="error"></p>
		<hr size="1" />
	</div>		
	<div id="creditsDiv">
		<!-- Version: <c:out value="${requestScope['VERSION']}" /> | <c:out value="${requestScope['REVISION']}" /> | <c:out value="${requestScope['DATE']}" /> | -->
		by <i>Vilmos Zsombori</i> and <i>Michael Frantzis</i>
	</div>		
</html>