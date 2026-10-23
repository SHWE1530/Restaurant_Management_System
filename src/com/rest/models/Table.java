package com.rest.models;

public class Table {
    public enum Availability {
        occupy, free
    }
    private int id;
    private int capacity;
    private Availability status;

    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.status = Availability.free;
    }

    public Table(int id, int capacity, Availability status) {
        this.id = id;
        this.capacity = capacity;
        this.status = status != null ? status : Availability.free;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Availability getstatus() {
        return status; 
    }

    public Availability getStatus() {
        return status;
    }

    public void setStatus(Availability status) {
        this.status = status;
    }

    public void booking() {
        status = Availability.occupy;
    }

    public void freeNow() {
        status = Availability.free;
    }

    @Override
    public String toString() {
        return "Table #" + id + " (Capacity: " + capacity + ", Status: " + status + ")";
    }
}