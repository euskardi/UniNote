package com.example.uninote.models;

import java.util.Random;

public class GeneratorId {

    public static String get() {
        final Random r = new Random();
        return Integer.toHexString(r.nextInt());
    }
}
