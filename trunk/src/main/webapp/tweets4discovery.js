function getJson() {
	setDisplayTemplate(2);
	$.ajax({
		url : 'json',
		data : {
			query : $("#queryString").val(),
			since : $("#since").val(),
			until : $("#until").val()
		},
		type : "GET",
		async : true,
		success : function(response) {
			if (typeof response.tweets !== 'undefined') {
				$("#resultsTable > tbody").html("");
				$("#docxLink").html("");
				for ( var i in response.tweets) {
					$("#resultsTable > tbody").append(
							'<tr id="' + response.tweets[i].id + '">'
									+ '<td><b>@' + response.tweets[i].user.screenName + '</b></td>'
									+ '<td>' + response.tweets[i].user.name + '</td>'
									+ '<td>' + response.tweets[i].text + '</td>'
									+ '<td>' + response.tweets[i].createdAt + '</td>'
							+ '</tr>');
				}
				setDisplayTemplate(3);
			} else if (typeof response.exception !== 'undefined') {
				$("#error").html(response.exception);
				setDisplayTemplate(1);
				//console.error(response.exception);				
			} else {
				$("#error").html("AJAX error");
				setDisplayTemplate(1);
				//console.error('AJAX error');				
			}
		},
		error : function() {
			console.error('AJAX error');
		}
	});
}

function getDocx() {
	setDisplayTemplate(2);
	$.ajax({
		url : 'docx',
		data : {
			query : $("#queryString").val(),
			since : $("#since").val(),
			until : $("#until").val()
		},
		type : "GET",
		async : true,
		success : function(response) {
			if (typeof response.docx !== 'undefined') {
				console.log(response.docx);
				$("#docxLink").prop("href", "download/" + response.docx);
				$("#docxLink").html("Download Word document");
				$("#resultsTable > tbody").html("");
				setDisplayTemplate(3);
			} else if (typeof response.exception !== 'undefined') {
				$("#error").html(response.exception);
				setDisplayTemplate(1);
				//console.error(response.exception);				
			} else {
				$("#error").html("AJAX error");
				setDisplayTemplate(1);
				//console.error('AJAX error');				
			}
		},
		error : function() {
			$("#error").html("AJAX error");
			setDisplayTemplate(1);
			//console.error('AJAX error');
		}
	});
}

function setDisplayTemplate(mode) {
	/*
	 * 0 -	Show: queryDiv; 
	 * 		Hide: spinnerDiv, resultsDiv, errorDiv.
	 * 1 -	Show: queryDiv, errorDiv; 
	 * 		Hide: spinnerDiv, resultsDiv.
	 * 2 -	Show: queryDiv, spinnerDiv; 
	 * 		Hide: errorDiv, resultsDiv.
	 * 3 -	Show: queryDiv, resultsDiv; 
	 * 		Hide: errorDiv, spinnerDiv.
	 */
	switch (mode) {
	case 0:
		$("#queryDiv").show();
		$("#spinnerDiv").hide();
		$("#resultsDiv").hide();
		$("#errorDiv").hide();
		break;
	case 1:
		$("#queryDiv").show();
		$("#spinnerDiv").hide();
		$("#resultsDiv").hide();
		$("#errorDiv").show();
		break;
	case 2:
		$("#queryDiv").show();
		$("#spinnerDiv").show();
		$("#resultsDiv").hide();
		$("#errorDiv").hide();
		break;
	case 3:
		$("#queryDiv").show();
		$("#spinnerDiv").hide();
		$("#resultsDiv").show();
		$("#errorDiv").hide();
		break;
	default:
	}
}