package dev.numerouno.importer;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CD extends Product {
    private List<String> label = new ArrayList<>();
    private String date;
    private List<MusicTitle> titles = new ArrayList<>();
    private List<Person> artists = new ArrayList<>();

    public CD(String asin) {
        super(asin);
    }

    public List<MusicTitle> getTitles() {
        return titles;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Person> getArtists() {
        return artists;
    }

    public void setArtists(List<Person> artists) {
        this.artists = artists;
    }

    public void setTitles(List<MusicTitle> titles) {
        this.titles = titles;
    }
}
