package components;

import java.io.Serializable;

public class Cell implements Serializable {
    private int row, col;
    private int x, y;
    private int owner;
    private String roomid;
    public Cell(){}
    public Cell(int row, int col, int x, int y, int owner, String room){
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.roomid = room;
    }
    public void setRow(int r){
        this.row = r;
    }
    public void setCol(int c){
        this.col = c;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setOwner(int o){
        this.owner = o;
    }
    public void setRoom(String room){
        this.roomid = room;
    }

    public int getRow(){
        return this.row;
    }
    public int getCol(){
        return this.col;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getOwner(){
        return this.owner;
    }
    public String getRoom(){
        return this.roomid;
    }
}
