package dev.numerouno.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book extends Product {
    private List<String> verlag = new ArrayList<>();
    private int pages;
    private String releasedate;
    private String isbn;
    private List<Person> people = new ArrayList<>();
    private boolean audiobook = false;

    public boolean isAudiobook() {
        return audiobook;
    }

    public void setAudiobook(boolean audiobook) {
        this.audiobook = audiobook;
    }

    public Book(String asin) {
        super(asin);
    }

    public List<String> getVerlag() {
        return verlag;
    }

    public void setVerlag(List<String> verlag) {
        this.verlag = verlag;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getDate() {
        return releasedate;
    }

    public void setDate(String date) {
        this.releasedate = date;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Person> getAuthors() {
        return people;
    }

    public void setAuthors(List<Person> authors) {
        this.people = authors;
    }
}
