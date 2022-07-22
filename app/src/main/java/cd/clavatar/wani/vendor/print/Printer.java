package cd.clavatar.wani.vendor.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import cd.clavatar.wani.data.model.CompactPaiement;


public class Printer {


    public static void print(Context mContext, String text) {
        // 1: Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            Toast.makeText(mContext, "Mettez en route le bluetoth SVP!", Toast.LENGTH_LONG).show();

            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {
        Toast.makeText(mContext, "Soyez assuré d'être connecté au bluetooth de l'imprimante!",
                Toast.LENGTH_LONG).show();
            return;
        }
        // 3: Generate a order data
        byte[] data = ESCUtil.generateMockData(text);


        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


    }

    public static void printQr(Context mContext, String text) {
        // 1: Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            Toast.makeText(mContext, "Mettez en route le bluetoth SVP!", Toast.LENGTH_LONG).show();

            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {
            Toast.makeText(mContext, "Soyez rassuré d'être connecté au bluetooth de l'imprimante!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // 3: Generate a order data
        byte[] data = ESCUtil.getPrintQRCode(text, 8,3);
        //byte[] data = ESCUtil.getPrintDoubleQRCode(text,text,8,3);
        //



        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


    }


    public static void printPaiement(Context mContext, CompactPaiement paiement) {
        // 1: Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            Toast.makeText(mContext, "Mettez en route le bluetoth SVP!", Toast.LENGTH_LONG).show();

            return;
        }
        // 2: Get Sunmi's InnerPrinter BluetoothDevice
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter);
        if (device == null) {
            Toast.makeText(mContext, "Soyez rassuré d'être connecté au bluetooth de l'imprimante!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // 3: Generate a order data
        byte[] data = ESCUtil.generatePaiementData(paiement);


        // 4: Using InnerPrinter print data
        BluetoothSocket socket = null;
        try {
            socket = BluetoothUtil.getSocket(device);
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


}