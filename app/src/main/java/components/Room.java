package components;

import java.io.Serializable;

public class Room implements Serializable {
    private String roomID;
    private int Players;
    public Room(){}
    public Room(String roomID, int players){
        this.roomID = roomID;
        this.Players = players;
    }
    public void setRoomID(String roomID){
        this.roomID = roomID;
    }
    public void setPlayers(int players){
        this.Players = players;
    }
    public String getRoomID(){
        return this.roomID;
    }
    public int getPlayers(){
        return this.Players;
    }
}
