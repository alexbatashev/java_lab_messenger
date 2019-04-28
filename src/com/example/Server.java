package com.example;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static class ClientThread extends Thread {

        private Socket mSocket;
        private int mId;
        private ArrayList<ClientThread> allThreads;

        public DataInputStream dis;
        public DataOutputStream dos;

        ClientThread(Socket socket, int id, ArrayList<ClientThread> all) {
            mSocket = socket;
            mId = id;
            allThreads = all;
        }

        @Override
        public void run() {
            try {
                dis = new DataInputStream(mSocket.getInputStream());
                dos = new DataOutputStream(mSocket.getOutputStream());

                dos.writeUTF(String.valueOf(mId));

                while (mSocket.isConnected()) {
                    String id = dis.readUTF();

                    String message = dis.readUTF();

                    allThreads.get(Integer.parseInt(id)).dos.writeUTF(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ServerThread extends Thread {
        private int mThreadCounter = 0;
        @Override
        public void run() {
            super.run();

            ArrayList<ClientThread> threads = new ArrayList<>();

            try {
                ServerSocket serverSocket = new ServerSocket(4004, 0, InetAddress.getLocalHost());
                while (mThreadCounter < 2) {
                    Socket c = serverSocket.accept();
                    ClientThread t = new ClientThread(c, mThreadCounter, threads);
                    mThreadCounter++;
                    t.start();
                    threads.add(t);
                    System.out.println("Client " + mThreadCounter + " connected");
                }

                while (!threads.isEmpty()) {
//                    if (threads.get(0).isAlive()) {
                    threads.get(0).join();
                    System.out.println("Thread joined");
//                    }
                    threads.remove(0);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void start() throws InterruptedException {
        ServerThread t = new ServerThread();
        t.start();
        t.join();
    }
}
