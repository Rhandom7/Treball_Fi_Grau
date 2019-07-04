package com.example.orientacioeps.estimote;

public class ProximityContent {

    private String title;
    private String subtitle;

    ProximityContent(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    String getTitle() { return title; }

    String getSubtitle() {
        return subtitle;
    }
}
