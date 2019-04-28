package com.example;

public class Main {

    public static void main(String[] args) {
        try {
            Server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
