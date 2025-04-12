package kuliah.sems_2;

public class Class {
//    Atribut
    private String nama;
    private int umur;

//    Konstruktor
    public Class(String nama, int umur) {
        this.nama = nama;
        this.umur = umur;
    }

//    Method
    public String tampilkanInfo() {
        return "Nama: " + nama + "\nUmur: " + umur;
    }
}
