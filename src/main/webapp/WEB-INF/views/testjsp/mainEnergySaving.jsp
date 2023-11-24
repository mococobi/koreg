<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.mococo.web.util.CustomProperties"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sample - SEVT</title>
<style>
html, body {
	margin: 0;
	padding: 0;
	height: 100%;
}

.header {
	grid-area: hd;
	background-color: #192438;
}

.footer {
	grid-area: ft;
	background-color: #192438;
}

.content {
	grid-area: main;
	background: #192438;
}

.sidebar {
	grid-area: sd;
	background: #1c3363;
}

.container {
	display: grid;
	/* grid-template-columns: repeat(12, 1fr); */
	grid-template-columns: 280px 1fr;
	/*grid-auto-rows: minmax(100px, auto); */
	grid-template-rows: 50px auto 30px;
	/* grid-template-areas:
    "hd   hd   hd   hd   hd   hd   hd   hd   hd   hd   hd   hd"
    "sd   sd   main main main main main main main main main main"
    "sd   sd   ft   ft   ft   ft   ft   ft   ft   ft   ft   ft"; */
	grid-template-areas: "hd   hd" "sd   main" "sd   ft";
}

.no-sidebar {
	/* grid-template-areas:
    "hd   hd   hd   hd   hd   hd   hd   hd   hd   hd   hd   hd"
    "main main main main main main main main main main main main"
    "ft   ft   ft   ft   ft   ft   ft   ft   ft   ft   ft   ft"; */
	grid-template-columns: 1fr;
	grid-template-areas: "hd" "main" "ft";
}
</style>
<script type="text/javascript" charset="UTF-8"
	src="${pageContext.request.contextPath}/plugins/main/javascript/jquery-1.8.3.min.js"></script>
<script type="text/javascript" charset="UTF-8"
	src="${pageContext.request.contextPath}/plugins/main/javascript/common-form.js?v=2020032501"></script>
</head>
<body>
	<div class="container" style="width: 100%; height: 100%;">
		<div class="header" style="line-height: 50px;">
			<div style="width: 280px; float: left; padding-left: 20px;">
				<label
					style="color: #FFF; border-radius: 10px; border: solid 1px #FFF; padding: 0px 20px;">SEVT</label>
			</div>
			<div style="width: calc(100% - 630px); float: left;">
				<label style="font-weight: bold; color: #FFF; font-size: 1.5em;">Energy
					Keeper System</label>
			</div>
			<div
				style="width: 320px; float: right; text-align: right; padding-right: 10px;">
				<label id="clockSevt" style="color: lightgray; font-size: 0.8em;">2024-07-10
					13:27:14</label>
				<button id="btnSetting" style="vertical-align: text-top;">Setting</button>
				<button id="btnUser" style="vertical-align: text-top;">User</button>
			</div>
		</div>
		<div class="sidebar">Sidebar Area</div>
		<div class="content" style="height: 100%;">
			<div id="mainContent" style="width: 100%; height: 100%;">
				<iframe id="mstrReport" name="mstrReport" title="contents"
					src="/MicroStrategy/servlet/mstrWeb"
					style="width: 100%; height: 100%; border: none;"></iframe>
			</div>
		</div>
		<div class="footer" style="text-align: center; line-height: 26px;">
			<label style="color: lightgray; font-size: 0.7em;">SEVt
				Energy Keeper_210812</label>
		</div>
	</div>
	<script type="text/javascript">

function pageInit() {
    console.log("pageInit Event!!!");      
    
    $('#btnSetting').click(function() {
        
        $('.sidebar').toggle();
        $('div.container').toggleClass('no-sidebar');
        /* 
        objectId = "B7AA07FC43092E27D25475B4CA592F55";
        type = 55;
        isvi = true;
        
        formDefs["dossier"].documentID = "B7AA07FC43092E27D25475B4CA592F55";
        
        getPromptInfo(); 
        */
    });
    
    $('#btnUser').click(function() {
        
        objectId = "80C85E8F4F4D0232DE3A62AEE29ADEFD";
        type = 55;
        isvi = true;
        
        formDefs["dossier"].documentID = "80C85E8F4F4D0232DE3A62AEE29ADEFD";
        
        getPromptInfo();
    });
    
    setHeaderClock();
}

window.onload = pageInit;

/* ======= Custom Function ========= */

function setHeaderClock() {
    
    const today = new Date();
    let y = today.getFullYear();
    let m = today.getMonth() + 1;
    let d = today.getDate();
    let h = today.getHours();
    let mi = today.getMinutes();
    let s = today.getSeconds();
    
    m = checkTime(m);
    mi = checkTime(mi);
    s = checkTime(s);
    
    $('#clockSevt').text(y + '-' + m + '-' + d + ' ' + h + ':' + mi + ':' + s);
    setTimeout(setHeaderClock, 1000);
}

function checkTime(i) {
    if (i < 10) { 
        i = "0" + i;
    }
    return i;
}


/* =============== MSTR Report Execute ====================== */

var objectId = "B7AA07FC43092E27D25475B4CA592F55";
var type = 55;
var isvi = true;

var reportInfo = undefined;

/* 리포트정보 및 프롬프트정보의 조회 */
function getPromptInfo() {
    $.ajax({
        type: "post",
        url: "${pageContext.request.contextPath}/app/getReportInfo.json",
        async: true,
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify({
            objectId:objectId, 
            type:type
        }),
        dataType: "json",
        success: function(data, text, request) {
            if (data) {
                reportInfo = data.report;
                
                
                // set Form Definition
                switch (reportInfo["type"]) {
                    case 3:
                        formDefs["report"].reportID = reportInfo["id"];
                        break;
                    case 55:
                       if (isvi == true) {
                           formDefs["dossier"].documentID = reportInfo["id"];
                       } else {
                           formDefs["document"].documentID = reportInfo["id"];
                       }
                        break;
                    default:
                        break;
                }
                
                
                renderPrompt();
            } else {
                alert("리포트정보를 가져올 수 없습니다.");                    
            }
            
            $(window).resize(); // 프롬프트 랜더링이 종료되고 iframe 높이 조정
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR);
        }
    }); 
}   

/* 프롬프트정보를 이용한 랜더링 */
function renderPrompt() {
       if (reportInfo.promptList == undefined) {
        return; 
       }
        
       $.each(reportInfo.promptList, function(i, v) {
        var uiType = undefined;
         
        if (v["exUiType"]) {
            uiType = v["exUiType"];
        } else {
            switch (v["type"]) { // 프롬프트유형을 이용한 UI유형결정
            case 1:
                uiType = "value-default";
                break;
            case 2:   
            case 4:
                uiType = "list-default";
                if ((v["max"] && Number(v["max"]) > 1) || !v["max"]) {
                    uiType = "multiSelect-default";                         
                }
                break;
            default:
                break;
            } 
        }
        
        if (promptRenderer[uiType]) {
            var $wrapper = $("<span class='elem-wrapper'></span>"); 
            $(".prompt-wrapper").append($wrapper);
            promptRenderer[uiType]["label"]($wrapper, v);
            promptRenderer[uiType]["body"]($wrapper, v);
        }
       });
       
       getAnswerXML();
}

function getPromptVal() {
    var elemVal = {};
    $("[prompt-id]").each(function(i, v) {
        var $elem = $(v);
        elemVal[$elem.attr("prompt-id")] = promptRenderer[$elem.attr("ui-type")]["selected"]($elem);
    });
    
    return elemVal;
}

var formDefs = {
    common: {
        server: "<%= CustomProperties.getProperty("mstr.server.name") %>",
        port: "<%= CustomProperties.getProperty("mstr.server.port") %>",
        project: "<%= CustomProperties.getProperty("mstr.default.project.name") %>",
        hiddenSections: "path,header,footer,dockTop,dockLeft",
        promptAnswerMode: "2"
    },
    report: {
        evt: "4001",
        src: "mstrWeb.4001",
        reportID: objectId
    },
    document: {
           evt: "2048001",
           src: "mstrWeb.2048001",
           documentID: objectId
    },
    dossier: {
           evt: "3140",
           src: "mstrWeb.3140",
           documentID: objectId,
           share: "1"
    }
}

function getAnswerXML() { 
    $.ajax({
        type: "post",
        url: "${pageContext.request.contextPath}/app/getAnswerXML.json",
        async: true,
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify({
                objectId: objectId, 
                type: type,
                promptVal: getPromptVal() // 현재 선택된 프롬프트값들을 파라미터로 전달하여 XML형태로 반환 받음.
            }),
        dataType: "json",
        success: function(data, text, request) {
            // 리포트 실행 시 파라미터로 전달될 promptsAnswerXML의 값을 서버로부터 조회 성공
            var inputs = $.extend({}, formDefs["common"]);
            
            switch (type) {
            case 3:
                $.extend(inputs, formDefs["report"]);
                break;
            case 55:
                   if (isvi == true) {
                    $.extend(inputs, formDefs["dossier"]);
                   } else {
                    $.extend(inputs, formDefs["document"]);
                   }
                break;
            default:
            }
            
            $.extend(inputs, {promptsAnswerXML: data["xml"]});
            _submit("${pageContext.request.contextPath}/servlet/mstrWeb", "mstrReport", inputs);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("리포트 실행 중 오류가 발생하였습니다.");
        }
    }); 
}

</script>
</body>
</html>