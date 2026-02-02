package com.example.gfm_pronuntiapp_appfinale_esame;

public class Exercise {
    private int id;
    private String name;
    private String date;
    private String correction;

    public Exercise(int id, String name, String date, String correction) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.correction = correction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCorrection() {
        return correction;
    }
}


