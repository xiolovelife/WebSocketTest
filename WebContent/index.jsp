<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>WebSocket Test</title>
<style>

#cen{
	margin:50px auto;
	border:solid 1px black;
	width: 80%;
}

</style>
</head>
<body>
<div id="cen"></div>
<div>
	<button onclick="doOpen()">连接</button>
	<button onclick="doClose()">退出</button>
</div>
<div>
	<textarea id="text" rows="" cols=""></textarea><br>
	<button onclick="send()">发送</button>
</div>
<script type="text/javascript" src="/WebSocketTest/js/jquery.js"></script>
<script>
var socket = new WebSocket("ws://192.168.0.55:8080/WebSocketTest/websocket");

//主动连接
function doOpen(){
	socket = new WebSocket("ws://192.168.0.55:8080/WebSocketTest/websocket");
}

//主动关闭
function doClose(){
	socket.close();
}

socket.onerror = function(event) {
	sendErrorMsg("连接异常，请稍后再试");
};

socket.onopen = function (){
	sendOpenMsg("连接成功...");
}

//接收到消息的回调方法
socket.onmessage = function(event){
	sendMsg(event.data);
}

//连接关闭的回调方法
socket.onclose = function(){
	senErrorMsg("退出连接...");
}

//发送消息
function send(){
	var message = $("#text").val();
	socket.send(message);
}

window.onbeforeunload = function(){
	socket.close();
}

//普通消息
function sendMsg(msg){
	var html = '<p>'+msg+'<p/>';
	$("#cen").append(html);
}

//异常消息
function sendErrorMsg(msg){
	var html = '<p syle="color:red">'+msg+'<p/>';
	$("#cen").append(html);
}

//提示消息
function sendOpenMsg(msg){
	var html = '<p syle="color:#008000">'+msg+'<p/>';
	$("#cen").append(html);
}

</script>
</body>
</html>