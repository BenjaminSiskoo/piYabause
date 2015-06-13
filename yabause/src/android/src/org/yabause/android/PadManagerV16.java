package org.yabause.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yabause.android.PadEvent;
import org.yabause.android.PadManager;

class PadManagerV16 extends PadManager {
    private ArrayList deviceIds;
    HashMap<Integer,Integer> Keymap;
    final String TAG = "PadManagerV16";

    PadManagerV16() {
        deviceIds = new ArrayList();

        int[] ids = InputDevice.getDeviceIds();
        for (int deviceId : ids) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                if (!deviceIds.contains(deviceId)) {
                    deviceIds.add(deviceId);
                }
            }
        }
        
        Keymap = new HashMap<Integer,Integer>();
        loadSettings();
        
    }

    public boolean hasPad() {
        return deviceIds.size() > 0;
    }

    public PadEvent onKeyDown(int keyCode, KeyEvent event) {
        PadEvent pe = null;

        if (((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
            ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            if (event.getRepeatCount() == 0) {
            	
            	pe = new PadEvent(0, Keymap.get(keyCode));
            }
        }

        return pe;
    }

    public PadEvent onKeyUp(int keyCode, KeyEvent event) {
        PadEvent pe = null;

        if (((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
            ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            if (event.getRepeatCount() == 0) {
            	pe = new PadEvent(1, Keymap.get(keyCode));
            }
        }

        return pe;
    }
    
    void loadDefault(){
    	Keymap.clear();
        Keymap.put(KeyEvent.KEYCODE_DPAD_UP,PadEvent.BUTTON_UP);
        Keymap.put(KeyEvent.KEYCODE_DPAD_DOWN, PadEvent.BUTTON_DOWN);
        Keymap.put(KeyEvent.KEYCODE_DPAD_LEFT, PadEvent.BUTTON_LEFT);
        Keymap.put(KeyEvent.KEYCODE_DPAD_RIGHT, PadEvent.BUTTON_RIGHT);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_L2,PadEvent.BUTTON_LEFT_TRIGGER);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_R2, PadEvent.BUTTON_RIGHT_TRIGGER);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_START, PadEvent.BUTTON_START);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_A, PadEvent.BUTTON_A);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_B, PadEvent.BUTTON_B);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_R1, PadEvent.BUTTON_C);        
        Keymap.put(KeyEvent.KEYCODE_BUTTON_X, PadEvent.BUTTON_X);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_Y, PadEvent.BUTTON_Y);
        Keymap.put(KeyEvent.KEYCODE_BUTTON_L1, PadEvent.BUTTON_Z);   	
    }
    
    void loadSettings(){
        try {
        	
            File yabroot = new File(Environment.getExternalStorageDirectory(), "yabause");
            if (! yabroot.exists()) yabroot.mkdir();
            
            InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory() + "/" +  "yabause/keymap.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
             
            // Json読み込み
            String json = new String(buffer);
            JSONObject jsonObject = new JSONObject(json);
            
            Keymap.clear();
            
            Keymap.put(jsonObject.getInt("BUTTON_UP"),PadEvent.BUTTON_UP);
            Keymap.put(jsonObject.getInt("BUTTON_DOWN"), PadEvent.BUTTON_DOWN);
            Keymap.put(jsonObject.getInt("BUTTON_LEFT"), PadEvent.BUTTON_LEFT);
            Keymap.put(jsonObject.getInt("BUTTON_RIGHT"), PadEvent.BUTTON_RIGHT);
            Keymap.put(jsonObject.getInt("BUTTON_LEFT_TRIGGER"),PadEvent.BUTTON_LEFT_TRIGGER);
            Keymap.put(jsonObject.getInt("BUTTON_RIGHT_TRIGGER"), PadEvent.BUTTON_RIGHT_TRIGGER);
            Keymap.put(jsonObject.getInt("BUTTON_START"), PadEvent.BUTTON_START);
            Keymap.put(jsonObject.getInt("BUTTON_A"), PadEvent.BUTTON_A);
            Keymap.put(jsonObject.getInt("BUTTON_B"), PadEvent.BUTTON_B);
            Keymap.put(jsonObject.getInt("BUTTON_C"), PadEvent.BUTTON_C);        
            Keymap.put(jsonObject.getInt("BUTTON_X"), PadEvent.BUTTON_X);
            Keymap.put(jsonObject.getInt("BUTTON_Y"), PadEvent.BUTTON_Y);
            Keymap.put(jsonObject.getInt("BUTTON_Z"), PadEvent.BUTTON_Z);             
                
             
        } catch (IOException e) {
            e.printStackTrace();
            loadDefault();
        } catch (JSONException e) {
            e.printStackTrace();
            loadDefault();
        }    	
    }

    
}
