package dev.numerouno.importer;

import java.util.ArrayList;
import java.util.List;

public class DVD extends Product {
    private String format;
    private int runtime;
    private int regioncode;
    private List<Person> people = new ArrayList<>();

    public DVD(String asin) {
        super(asin);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getRegioncode() {
        return regioncode;
    }

    public void setRegioncode(int regioncode) {
        this.regioncode = regioncode;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "DVD{" +
                "format='" + format + '\'' +
                ", runtime=" + runtime +
                ", regioncode=" + regioncode +
                ", people=" + people +
                "} " + super.toString();
    }
}
