<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mococo.microstrategy.sdk.esm.vo.MstrUser" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Main Category</title>

    <!-- Bootstrap CSS -->
    <link href="${pageContext.request.contextPath}/style/bootstrap/bootstrap.min.css" rel="stylesheet">

<style>
html, body {
  height: 100%;
  background: #ffffff;
}

#container {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  height: 100%;
}

#categoryBox {
  width: 300px;
  text-align: center;
  background-color: #ffffff;
}
</style>
</head>
<body>
<div id="corpBox" style="width: 100%; padding: 30px 30px 0 0; text-align: right; position: fixed;">
    <select id="selLstCorp" style="width: 200px;">
        <option value="" selected>선택하세요.</option>
    </select>
</div>
<div id="container">
    <div id="categoryBox" class="d-grid gap-3" style="display: none;">
      <button id="Equipment" class="btn btn-warning btn-lg" type="button" disabled>Equipment & Product</button>
      <button id="Energy" class="btn btn-success btn-lg" type="button" disabled>Energy Saving</button>
      <button id="Environment" class="btn btn-info btn-lg" type="button" disabled>Environment Safety</button>
    </div>
</div>
<form id="frmCategory"></form>
    <!-- Bootstrap Bundle with Popper -->
<script src="${pageContext.request.contextPath}/javascript/bootstrap/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/javascript/jquery-3.7.0.min.js"></script>
<script type="text/javascript">
//prevent mouse right click..
window.addEventListener('contextmenu', function(e) { e.preventDefault(); }, false); // Not compatible with IE < 9

//set Window Close Event, Mstr Session Clear 
<%-- window.addEventListener("beforeunload", function() {
    try {
        let fd = new FormData();
        fd.append('mstrUserId', "<%=mstrUid%>");
        navigator.sendBeacon("<%=sDomainAddr%>" + "/MicroStrategy/plugins/esm/jsp/sso.jsp", fd);
    } catch (e) {

        $.ajax({
            type: "post",
            url: __contextPath + "/app/login/logoutMstr.jsp",
            async: false,
            data: { mstrUserId: "<%=mstrUid%>" },
            dataType: "json",
            success: function(data, text, request) { },
            error: function(jqXHR, textStatus, errorThrown) { }
        });
    } 
}); --%>
const __contextPath = "${pageContext.request.contextPath}";
const goCorpList = ${corpList};
let gsSelectedCorp = null;
let $frmCategory;
//init
$(document).ready(function() {
    
    console.log(goCorpList);
    
    setElemEvent();
    
    renderCorpList();
    
    
    /*
    iframe 에서 parent 호출
    송신 => windows.postMessage(data, '*') 활용
    수신 => windows.addEventListener("message", receiveMessage, false)
    */
    
    /*
    iframe load 이벤트 활용
    $(iframe).on('load', function(){loaded처리구문});
    */
});

function setElemEvent() {
    
    $('#selLstCorp').change(function() {
        
        // 초기화 버튼
        $('#categoryBox button').each(function (index, item) {
            $(item).attr('disabled', '');
            $(item).attr('data-objid', '');
        });
        
        gsSelectedCorp = $(this).val();
        setCategory(gsSelectedCorp);
    });
    
    $('#Equipment').off('click').on('click', function() {
        console.log("설비 & 제품 클릭!!");
        //window.location.href = "${pageContext.request.contextPath}/app/main/equipmentproduct.do";
        
        $frmCategory = $('#frmCategory');
        $frmCategory.empty();
        $frmCategory.attr("action", __contextPath+"/app/main/equipmentproduct.do");
        $("<input type='hidden'/>").attr('name', 'fldObjId').val($(this).attr("data-objid")).appendTo($frmCategory);
        $frmCategory.attr("method", "post");
        $frmCategory.attr("target", "_self").submit();
        $frmCategory.empty().removeAttr("action","").removeAttr("target","").removeAttr("method","");
        
    });
    $('#Energy').off('click').on('click', function() {
        console.log("에너지 절약 클릭!!");
        //window.location.href = "${pageContext.request.contextPath}/app/main/energysaving.do";
        
        $frmCategory = $('#frmCategory');
        $frmCategory.empty();
        $frmCategory.attr("action", __contextPath+"/app/main/energysaving.do");
        $("<input type='hidden'/>").attr('name', 'fldObjId').val($(this).attr("data-objid")).appendTo($frmCategory);
        $frmCategory.attr("method", "post");
        $frmCategory.attr("target", "_self").submit();
        $frmCategory.empty().removeAttr("action","").removeAttr("target","").removeAttr("method","");
        
    });
    $('#Environment').off('click').on('click', function() {
        console.log("환경 안전 클릭!!");
        window.location.href = "${pageContext.request.contextPath}/app/main/tabtest.do";
    });
}

function renderCorpList() {
    
    let $selCorp = $('#selLstCorp');
    let $optElem = $('<option></option>');
    if (goCorpList.length > 0) {
        let $tmpOptElem;
        for (let i=0; i<goCorpList.length; i++) {
            $tmpOptElem = $optElem.clone();
            
            $tmpOptElem.append(goCorpList[i].name);
            $tmpOptElem.val(goCorpList[i].id);
            
            $selCorp.append($tmpOptElem);
        }
        
        if (goCorpList.length == 1) {
            $('#selLstCorp option:eq(1)').prop("selected", true);
            $selCorp.trigger("change");
        }
    } else {
        alert("사업장 목록이 없습니다!!!");
    }
}

function setCategory(corpId) {
    
    console.log("corpId : " + corpId);
    let aCategoryList;
    let tmpCategoryName;
    for (let i=0; i<goCorpList.length; i++) {
        if (goCorpList[i].id == corpId) {
            aCategoryList = goCorpList[i].child;
            for (let j=0; j< aCategoryList.length; j++) {
                console.log(aCategoryList[j].name);
                tmpCategoryName = aCategoryList[j].name.toUpperCase();
                if (tmpCategoryName.indexOf("EQUIPMENT") > -1) {
                    $('#Equipment', $('#categoryBox')).removeAttr('disabled');
                    $('#Equipment', $('#categoryBox')).attr('data-objid', aCategoryList[j].id);
                }
                
                if (tmpCategoryName.indexOf("ENERGY") > -1) {
                    $('#Energy', $('#categoryBox')).removeAttr('disabled');
                    $('#Energy', $('#categoryBox')).attr('data-objid', aCategoryList[j].id);
                }
                
                if (tmpCategoryName.indexOf("ENVIRONMENT") > -1) {
                    $('#Environment', $('#categoryBox')).removeAttr('disabled');
                    $('#Environment', $('#categoryBox')).attr('data-objid', aCategoryList[j].id);
                }
            }
            
            if (aCategoryList.length > 0) {
                $('#categoryBox').show();
            }
        }
    }
}
</script>
</body>
</html>