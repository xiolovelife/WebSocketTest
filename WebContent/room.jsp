<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String uri = request.getServerName() + ":" + request.getServerPort() + path;
	String basePath = request.getScheme() + "://" + uri;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="<%=basePath%>/layui/css/layui.css">
<style type="text/css">
	#infoDiv{
		height:230px;
		margin-top: 30px;
		text-align: center;
	}
	#codeDiv{
		display: none;
	}
	#code{
		margin:12px auto;
	}
	#playInfo{
		display: none;
		line-height:230px;
	}
	.my-icon{
		font-size: 30px;
	}
	.online-icon{
		color: #1E9FFF;
	}
	.offline-icon{
		color: #e2e2e2;
	}
	.progressP{
		margin-bottom: 3px;
	}
	p > span{
		margin-left:4px;
		font-size: 16px;
	}
	
</style>
<title>房间</title>
</head>
<body>
	<div class="layui-container">
		
		<div id="infoDiv">
			<div id="codeDiv">
				<h2 id="codeInfo">扫码加进[<span id="roomName"></span>]开始游戏吧</h2>
				<div id="code"></div>
			</div>
			<h1 id="playInfo"></h1>
		</div>
		
		<p class="progressP"><i id="play1Icon" sessionId="" class="layui-icon layui-icon-username my-icon offline-icon"></i><span id="play1Info" sessionId="">等待玩家...</span></p>
		<div id="play1Progress" sessionId="" class="layui-progress layui-progress-big" lay-filter="" lay-showPercent="yes">
			<div class="layui-progress-bar" lay-percent="0%"></div>
		</div>
		<br><br><br>
		<p class="progressP"><i id="play2Icon" sessionId="" class="layui-icon layui-icon-username my-icon offline-icon"></i><span id="play2Info" sessionId="">等待玩家...</span></p>
		<div id="play2Progress" sessionId="" class="layui-progress layui-progress-big" lay-filter="" lay-showPercent="yes">
			<div class="layui-progress-bar layui-bg-orange" lay-percent="0%"></div>
		</div>
	</div>
	
	<script type="text/javascript" src="<%=basePath%>/js/jquery.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/jquery.qrcode.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>/layui/layui.js"></script>
	<script type="text/javascript"></script>
	<script>
	var socket ;
	
	layui.use('layer', function(){
		var layer = layui.layer;
		layer.ready(function (){
			setRoonName();
		});
	});
	
	layui.use('element', function(){
		var element = layui.element;
	});
	
	function setRoonName(){
		layer.prompt({title: '请输入房间名',formType: 0}, function(text, index){
			layer.close(index);
			initSocket(text);
		});
	}
	
	//关闭连接
	function doClose(){
		socket.close();
	}
	
	//发送消息
	function sendMsg(code,msg){
		socket.send(code+":"+msg);
	}
	
	function initSocket(name){
		$("#roomName").text(name);
		socket = new WebSocket("ws://<%=uri%>/room/"+name);
		//接收到消息
		socket.onmessage = function(event){
			var result = event.data.split(":");
			var code = result[0];
			var data = result[1];
			var playId ;
			if( code == "101" ){	//生成手机端入口
				showqrCode(data);
			}else{
				playId = result[2];
				if( code == "102" ){	//手机加入房间
					playerJoin(data,playId);
					console.log( data + " 加入房间 ["+playId+"]" );
				}else if( code == "103" ){
					playShake(data,playId);
					console.log( data + " 摇动手机 ["+playId+"]" );
				}else if( code == "104" ){
					playerExit(playId);
					console.log( data + " 退出房间 ["+playId+"]" );
				}
			} 
		}
	}
	
	//玩家加进房间
	function playerJoin(name,sessionId){
		var index;
		if( !$("#play1Icon").hasClass("online-icon") ){
			index = 1;
		}else if( !$("#play2Icon").hasClass("online-icon") ){
			index = 2;
		}else{
			console.log("玩家已满");
			return;
		}
		
		$("#play"+index+"Icon").removeClass("offline-icon");
		$("#play"+index+"Icon").addClass("online-icon");
		$("#play"+index+"Icon").attr("sessionId",sessionId);
		
		$("#play"+index+"Info").text(name);
		$("#play"+index+"Info").attr("sessionId",sessionId);
		
		$("#play"+index+"Progress").attr("lay-filter",sessionId);
		$("#play"+index+"Progress").attr("sessionId",sessionId);
		
		checkFull();
	}
	
	var intervalIndex;
	var lock = 0;
	
	//检查是否可以开始
	function checkFull(){
		if( $("#play1Icon").hasClass("online-icon") && $("#play2Icon").hasClass("online-icon") ){
			$("#codeDiv").hide();
			$("#playInfo").text("3");
			$("#playInfo").show();
			
			sendMsg("107","START");	//通知玩家开始，手机端计时 
			intervalIndex = window.setInterval("begin();", 1000);
		}
	}
	
	function begin(){
		var i = $("#playInfo").text() * 1;
		if( i == 1 ){
			lock = 1;
			$("#playInfo").text("游戏开始");
			window.clearInterval(intervalIndex);
		}else{
			$("#playInfo").text(i - 1);
		}
	}
	
	var size={};
	
	function playShake(name,sessionId){
		if( lock == 0 ){
			return;
		}
		//TODO 获取目前进度，速度递减
		//目前先摇一次增加一次
		var numStr = $("div[sessionId='"+sessionId+"']").children().children().text();
		console.log("numStr:"+numStr);
		var num = numStr.substring(0,numStr.length-1)*1;
		num++;
		layui.use('element', function(){
			var element = layui.element;
			element.progress(sessionId, num+'%');
		});
		if( num == 100 ){
			lock = 0;
			sendMsg("108",sessionId);	//通知玩家胜利
			$("#playInfo").text("恭喜["+name+"]玩家胜利");
		}
	}
	
	//玩家离开房间
	function playerExit(sessionId){
		
		$("i[sessionId='"+sessionId+"']").removeClass("online-icon");
		$("i[sessionId='"+sessionId+"']").addClass("offline-icon");
		$("i[sessionId='"+sessionId+"']").attr("sessionId","");
		
		$("span[sessionId='"+sessionId+"']").text("等待玩家...");
		$("span[sessionId='"+sessionId+"']").attr("sessionId","");
		
		layui.use('element', function(){
			var element = layui.element;
			var paly1Filter = $("#play1Progress").attr("sessionId");
			var paly2Filter = $("#play2Progress").attr("sessionId");
			
			element.progress(paly1Filter, '0%');	//TODO 全清零
			element.progress(paly2Filter, '0%');	//TODO 全清零
		});
		
		$("div[sessionId='"+sessionId+"']").attr("sessionId","");
		$("div[sessionId='"+sessionId+"']").attr("lay-filter","");
		
		lock = 0;
		$("#playInfo").hide();
		$("#codeDiv").show();
	}
	
	function showqrCode(sessionId){
		$("#code").qrcode({
			width: 180, //宽度
			height:180, //高度
			text: "<%=basePath%>/player.jsp?roomSoketSessionId=" + sessionId
		});
		$("#codeDiv").show();
	}
	
	window.onbeforeunload = function(){
		socket.close();
	}
	
</script>
</body>
</html>