package com.example.kursovaya.data;

public class UserProfile {
    public String nick, email, type, place;
    public int age;
    public Stats stats = new Stats();

    public static class Stats {
        public int total = 0;
        public int done  = 0;
        public double rate = 0;
    }

    public UserProfile() {}
    public UserProfile(String nick, String email, int age, String type, String place){
        this.nick = nick; this.email = email; this.age = age;
        this.type = type; this.place = place;
    }
}
