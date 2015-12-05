package com.hankarun.piyangoogren;


import java.util.ArrayList;

public class Statics {

    public final static ArrayList<String> menu = new ArrayList<String>() {{
        add("Sayısal");
        add("Super Loto");
        add("Sans Topu");
        add("On Numara");
    }};

    public final static ArrayList<String> menuOriginal = new ArrayList<String>() {{
        add("sayisal");
        add("superloto");
        add("sanstopu");
        add("onnumara");
    }};

    public static int setSayisal(String gametype){
        switch (gametype){
            case "sayisal":
                return 49;
            case "superloto":
                return 54;
            case "sanstopu":
                return 34;
            case "onnumara":
                return 80;
        }
        return 1;
    }
}
