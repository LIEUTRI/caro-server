package com.liutri.carochess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import components.Cell;
import components.Room;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {

    static public TextView textViewLogs;
    static TextView textViewServerIP;
    Switch aSwitch;
    public static int PORT = 1998;
    static boolean isServerON = false;
    private String serverAddress;
    public static String status = "";
    static String ID = "";
    static ArrayList<Socket> clientList;
    static ArrayList<Room> rooms;
    ServerTask serverTask;
    static Boolean setRoom = false;
    static Button btnChangePort;
    static EditText editPort;
    static Button btnChangeSLphong;
    static EditText editSLphong;
    static int SLphong = 10;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLogs = findViewById(R.id.textViewLogs);
        textViewServerIP = findViewById(R.id.textViewServerIP);
        aSwitch = findViewById(R.id.switchServer);
        btnChangePort = findViewById(R.id.btnChangePort);
        editPort = findViewById(R.id.editPort);
        btnChangeSLphong = findViewById(R.id.btnChangeSLphong);
        editSLphong = findViewById(R.id.editSLphong);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wm != null;
        serverAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        textViewServerIP.setText("Private IP: " + serverAddress);

        loadGameSetting();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    serverTask = new ServerTask();
                    textViewLogs.append("Server is listening....\n");
                    isServerON = true;
                    serverTask.execute();
                    if (serverTask.getStatus() == AsyncTask.Status.RUNNING) {
                        System.out.println("server was started!");
                        Toast.makeText(MainActivity.this, "Server was started!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    textViewLogs.append("Server was stopped!\n");
                    isServerON = false;
                    serverTask.cancel(true);
                    if (serverTask.isCancelled()) {
                        System.out.println("server was stopped!");
                        Toast.makeText(MainActivity.this, "Server was stopped!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnChangePort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PORT = Integer.parseInt(editPort.getText().toString());
                doSave();
                Toast.makeText(MainActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangeSLphong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SLphong = Integer.parseInt(editSLphong.getText().toString());
                doSave();
                Toast.makeText(MainActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doSave()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("PORT", PORT);
        editor.putInt("PHONG", SLphong);
        editor.apply();
    }
    @SuppressLint("SetTextI18n")
    private void loadGameSetting()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {
            PORT = sharedPreferences.getInt("PORT", PORT);
            SLphong = sharedPreferences.getInt("PHONG", SLphong);
            editPort.setText(PORT + "");
            editSLphong.setText(SLphong + "");
        } else {
            editPort.setText(PORT + "");
            editSLphong.setText(SLphong + "");
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ServerTask extends AsyncTask<Void, Cell, Cell> {
        ServerSocket ss = null;
        Socket s = null;

        @Override
        protected Cell doInBackground(Void... voids) {
            MainActivity.clientList = new ArrayList<>();
            MainActivity.rooms = new ArrayList<>();
            for (int i = 0; i < SLphong; i++) {
                MainActivity.rooms.add(new Room(i + "", 0));
            }
            try {
                ss = new ServerSocket(MainActivity.PORT);
                while (MainActivity.isServerON) {
                    s = ss.accept();
                    MainActivity.setRoom = false;
                    MainActivity.clientList.add(s);
                    ObjectOutputStream listPlayer = new ObjectOutputStream(s.getOutputStream());
                    listPlayer.writeObject(MainActivity.rooms);
                    MainActivity.status = "Connected to " + s.getRemoteSocketAddress();
                    System.out.println(MainActivity.status);
                    publishProgress();
                    ReadData readData = new ReadData(s);
                    readData.start();
                }
                s.close();
                ss.close();
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }
            return new Cell(0, 0, 0, 0, 0, "");
        }

        @Override
        protected void onProgressUpdate(Cell... values) {
            super.onProgressUpdate(values);
            MainActivity.textViewLogs.append(MainActivity.status + "\n");
        }

        @Override
        protected void onPostExecute(Cell cell) {
            super.onPostExecute(cell);

        }
    }
}