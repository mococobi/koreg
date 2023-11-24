<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>CSS Template</title>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html;" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
* {
	box-sizing: border-box;
}

html, body {
	margin: 0;
	padding: 0;
	height: 100%;
}

body {
	font-family: Arial, Helvetica, sans-serif;
}

/* Style the header */
.header {
	background-color: #f1f1f1;
	padding: 30px;
	text-align: center;
	/* font-size: 35px; */
}

/* Create three unequal columns that floats next to each other */
.column {
	float: left;
	/* padding: 10px; */
	/* height: 300px; */ /* Should be removed. Only for demonstration */
	height: 100%;
}

/* Left and right column */
.column.side {
	/* width: 20%; */
	width: 280px;
	background-color: #aaa;
}

/* Middle column */
.column.middle {
	/* width: 80%; */
	width: calc(100% - 150px);
	background-color: #bbb;
}

/* Clear floats after the columns */
.row:after {
	content: "";
	display: table;
	clear: both;
}

.row {
	height: calc(100% - 78px);
}

/* Style the footer */
.footer {
	background-color: #f1f1f1;
	padding: 10px;
	text-align: center;
}

/* Responsive layout - makes the three columns stack on top of each other instead of next to each other */
@media ( max-width : 600px) {
	.column.side, .column.middle {
		width: 100%;
	}
}

/*
div#contents {
	position: relative;
	padding-top: 56%;
	width: 100%;
	height: 0;
}

div#contents > iframe#mstrReport {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
}
*/
div#contents {
	width: 100%;
	height: 100%;
}

div#contents>iframe#mstrReport {
	width: 100%;
	height: 100%;
	border: none;
}
</style>

<script type="text/javascript">

/* function getElementHeight(node) {
	const list = [
		'margin-top',
		'margin-bottom',
		'border-top',
		'border-bottom',
		'padding-top',
		'padding-bottom',
		'height'
	]
	
	const style = window.getComputedStyle(node);
	return list.map(k => parseInt(style.getPropertyValue(k), 10)).reduce((prev, cur) => prev + cur)
} */

function resizeAreaHeight() {
	
	console.log("Height: " + window.innerHeight);
	console.log("Width : " + window.innerWidth);
	
	console.log("Header Height : " + document.querySelectorAll(".header")[0].offsetHeight);
	console.log("Footer Height : " + document.querySelectorAll(".footer")[0].offsetHeight);
	//var iHeaderHeight = getElementHeight(document.querySelector(".header"));//document.querySelector(".header").offsetHeight;
	//var iFooterHeight = getElementHeight(document.querySelector(".footer"));//document.querySelector(".footer").offsetHeight;
	var iHeaderHeight = document.querySelector(".header").offsetHeight;
	var iFooterHeight = document.querySelector(".footer").offsetHeight;
	console.log("Header Height2 : " + iHeaderHeight);
	console.log("Footer Height2 : " + iFooterHeight);
	
	document.querySelector(".row").style.height = "calc(100% - " + (iHeaderHeight + iFooterHeight) + "px)";
}

window.onresize = resizeAreaHeight;

function pageInit() {
	if (typeof(Event) === 'function') {
		console.log('modern browsers');
		window.dispatchEvent(new Event('resize'));
	} else {
		console.log('for IE and other old browsers \n causes deprecation warning on modern browsers');
		var evt = window.document.createEvent('UIEvents');
		evt.initUIEvent('resize', true, false, window, 0);
		window.dispatchEvent(evt);
	}
}

window.onload = pageInit;
</script>
</head>
<body>

	<!-- Area. Header -->
	<div class="header">
		<label>Header</label>
	</div>

	<div class="row">
		<!-- Area : Side Menu -->
		<div class="column side">Menu</div>
		<!-- Area : Contents (MSTR Report. iFrame) -->
		<div class="column middle">
			<div id="contents">
				<iframe id="mstrReport" title="contents"
					src="/MicroStrategy/servlet/mstrWeb"></iframe>
			</div>
		</div>
		<!-- <div class="column side" style="background-color:#ccc;">Column</div> -->

		<!-- Area. Footer -->
		<div class="footer">
			<p>Footer</p>
		</div>
	</div>

</body>
</html>