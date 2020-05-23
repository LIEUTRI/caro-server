package com.liutri.carochess;

import android.util.Log;
import android.widget.TextView;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import components.Cell;
import components.Room;

public class ReadData extends Thread {
    public Socket s;
    ReadData(Socket s){
        this.s = s;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream = null;
        Cell dataIN=null;
        String ROOMID="";
        try{
            while (MainActivity.isServerON){
                if (s.isBound()){
                    inputStream = new ObjectInputStream(s.getInputStream());
                    dataIN = (Cell) inputStream.readObject();
                    ROOMID=dataIN.getRoom();
                    System.out.println("server got: "+dataIN.getRow()+","+dataIN.getCol()+" | owner: "+dataIN.getOwner()+" | room: "+dataIN.getRoom());
                }

                assert dataIN != null;
                for (Room room: MainActivity.rooms){
                    if (room.getRoomID().equals(dataIN.getRoom()) && !MainActivity.setRoom){
                        MainActivity.setRoom = true;
                        switch (room.getPlayers()){
                            case 0: MainActivity.rooms.get(MainActivity.rooms.indexOf(room)).setPlayers(1); break;
                            case 1: MainActivity.rooms.get(MainActivity.rooms.indexOf(room)).setPlayers(2); break;
                        }
                    }
                }

                for (Socket item: MainActivity.clientList){
                    ObjectOutputStream output = new ObjectOutputStream(item.getOutputStream());
                    output.writeObject(dataIN);
                }
            }
        }catch (EOFException e){
            System.out.println("Error(ReadData): "+e+" | port: "+s.getPort());
            MainActivity.status = s.getRemoteSocketAddress()+" was disconnected!\n";

            for (Room r : MainActivity.rooms) {
                if (r.getRoomID().equals(ROOMID)) {
                    switch (r.getPlayers()) {
                        case 1:
                            MainActivity.rooms.get(MainActivity.rooms.indexOf(r)).setPlayers(0);
                            break;
                        case 2:
                            MainActivity.rooms.get(MainActivity.rooms.indexOf(r)).setPlayers(1);
                            break;
                    }
                }
            }
            try {
                s.close();
            } catch (IOException ex) {
                Log.d("CLOSESOCKET", "ko the close socket");
            }
            MainActivity.clientList.remove(s);
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
