package com.xio.domain;


import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.xio.socket.PlayerSocket;
import com.xio.socket.RoomSocket;

public class Room {
	
	private RoomSocket roomSocket;
	private Map<String,PlayerSocket> playerMap = new ConcurrentHashMap<String,PlayerSocket>();
	
	public RoomSocket getRoomSocket() {
		return roomSocket;
	}
	public void setRoomSocket(RoomSocket roomSocket) {
		this.roomSocket = roomSocket;
	}
	
	public Map<String, PlayerSocket> getPlayerMap() {
		return playerMap;
	}
	public void setPlayerMap(Map<String, PlayerSocket> playerMap) {
		this.playerMap = playerMap;
	}
	public Room() {
		super();
	}
	
	public Room(Map<String, PlayerSocket> playerMap) {
		super();
		this.playerMap = playerMap;
	}
	public Room(RoomSocket roomSocket) {
		super();
		this.roomSocket = roomSocket;
	}
	
	
}
