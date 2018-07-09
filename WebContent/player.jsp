<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String uri = request.getServerName()+":"+request.getServerPort()+path;
	String basePath = request.getScheme()+"://"+uri;
	String sessionId = request.getParameter("roomSoketSessionId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>玩家</title>
</head>
<body>
<div style="text-align: center;margin-top: 50%;">
	<h2 id="info"></h2>
	<h3 id="subInfo"></h3>
</div>

<script type="text/javascript" src="<%=basePath%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=basePath%>/layer/layer.js"></script>
<script type="text/javascript">
	var socket,thisSessionId ;
	var intervalIndex,size ;
	var SHAKE_THRESHOLD = 1800;
    var last_update = 0;
    var x = y = z = last_x = last_y = last_z = 0;
	
	$(function(){
		var system = {};
		var p = navigator.platform;
		system.win = p.indexOf("Win") == 0;
		system.mac = p.indexOf("Mac") == 0;
		system.x11 = (p == "X11") || (p.indexOf("Linux") == 0);
		if (system.win || system.mac || system.xll) {//PC
			layer.alert("请用手机端打开");
		 	/**
		 	window.location.href="about:blank";
		 	window.close();
		 	**/
		} else { //手机
			layer.ready(function (){
				layer.prompt({title: '请输入玩家名',formType: 0}, function(text, index){
					layer.close(index);
					initSocket(text);
					initShake();
				});
			});
		}
	})
	
    function initShake() {
        if (window.DeviceMotionEvent) {
            window.addEventListener('devicemotion', deviceMotionHandler, false);
        } else {
        	layer.alert('抱歉，你的手机不支持');
        }
    }
    
    function deviceMotionHandler(eventData) {
        var acceleration = eventData.accelerationIncludingGravity;
        var curTime = new Date().getTime();
        if ((curTime - last_update) > 100) {
            var diffTime = curTime - last_update;
            last_update = curTime;
            x = acceleration.x;
            y = acceleration.y;
            z = acceleration.z;
            var speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
            if (speed > SHAKE_THRESHOLD) {
            	socket.send("103:1");
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
    
    function initSocket(name){
    	socket = new WebSocket("ws://<%=uri%>/player/<%=sessionId%>/"+name);
    	//手机接收到消息
        socket.onmessage = function(event){
        	var result = event.data.split(":");
    		var code = result[0];
    		var data = result[1];
    		if( code == "102" ){
    			thisSessionId = result[2];
    			infoMsg("加入房间["+data+"]成功");
    			console.log("加入房间["+data+"]成功");
    		}else if( code == "106" ){	//房间解散
    			$("#subInfo").hide();
    			infoMsg("抱歉，房间已解散");
    			socket.close();
    			console.log(data);
    		}else if( code == "107" ){
    			size = 3;
    			$("#subInfo").show();
    			intervalIndex = window.setInterval("begin();", 1000);
    		}else if( code == "108" ){
    			$("#subInfo").hide();
    			if( thisSessionId == data ){
    				infoMsg("恭喜获得胜利");
    			}else{
    				infoMsg("很遗憾，输了~");
    			}
    		}
    		else if( code == "500" ){	//连接上房间出错，断开连接
    			$("#subInfo").hide();
    			infoMsg(data);
    			socket.close();
    			console.log(data);
    		}
        }
    }
    
    function begin(){
    	size = size - 1 ;
    	var info ;
    	if( size > 0 ){
    		info = size;
    	}else{
    		info = "游戏开始，摇动手机吧";
    		window.clearInterval(intervalIndex);
    	}
    	$("#subInfo").text(info);
    }
    
    function infoMsg(msg){
    	$("#info").text(msg);
    }
    
	window.onbeforeunload = function(){
		socket.close();
	}
	
</script>
</body>
</html>