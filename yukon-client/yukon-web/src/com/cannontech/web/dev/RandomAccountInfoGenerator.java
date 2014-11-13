package com.cannontech.web.dev;

public class RandomAccountInfoGenerator {
    private static final String[][] cityStateZip = { { "Birmingham", "AL", "35203" }, { "Phoenix", "AZ", "85026" },
        { "Los Angeles", "CA", "90052" }, { "Miami", "FL", "33152" }, { "Boston", "MA", "02205" },
        { "Reno", "NV", "89510" } };
    // sublist of popular US names
    private static final String[] name = { "James", "John", "Robert", "Michael", "William", "David", "Richard",
        "Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George", "Kenneth",
        "Steven", "Edward", "Patrick", "Peter", "Harold", "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger",
        "Joe", "Juan", "Jack", "Jimmy", "Antonio", "Mary", "Patricia", "Linda", "Barbara", "Elizabeth", "Jennifer",
        "Maria", "Susan", "Melissa", "Brenda", "Amy", "Anna", "Rebecca", "Virginia", "Kathleen", "Pamela",
        "Martha", "Debra", "Amanda", "Beverly", "Denise", "Tammy", "Irene", "Jane", "Lori", "Rachel", "Marilyn",
        "Andrea", "Kathryn", "Louise", "Diana", "Annie", "Lillian", "Emily", "Robin" };
    private static final String[] streetAbvs = { "Ln", "St", "Cir", "Dr", "Ave" };
    
    private String lastLastName;
    private String lastfirstName;

    public String[] cityStateZip() {
        return getRandom(cityStateZip);
    }
    
    public String streetAddr() {
        return (int) (Math.random() * 9999) + " " + firstName() + " " + getRandom(streetAbvs);
    }
    
    public String streetAddr2() {
        return "c/o " + getRandom(name) +" " + getRandom(name) + "son";
    }

    public String phoneNum() {
        return "555-" + String.format("%04d", (int) (Math.random() * 9999));
    }

    public String firstName() {
        lastfirstName = getRandom(name);
        return lastfirstName;
    }

    public String lastName() {
        lastLastName = getRandom(name) + "son";
        return lastLastName;
    }

    public String email() {
        return lastfirstName + "." + lastLastName + "@" + lastLastName + "sSite.com";
    }

    private <T> T getRandom(T[] items) {
        return items[(int) (Math.random() * items.length)];
    }
}