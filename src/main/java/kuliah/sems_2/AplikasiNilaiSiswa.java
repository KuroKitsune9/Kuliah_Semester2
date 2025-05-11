package kuliah.sems_2;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;

public class AplikasiNilaiSiswa extends Application {
    private static final String NAMA_FILE = "DataSiswa.txt";
    private TableView<Siswa> table = new TableView<>();
    private ObservableList<Siswa> data = FXCollections.observableArrayList();

    private TextField namaField = new TextField();
    private TextField nimField = new TextField();
    private TextField matField = new TextField();
    private TextField bhsField = new TextField();
    private TextField ipaField = new TextField();

    private TextField cariNamaField = new TextField();
    private TextField cariNimField = new TextField();

    private Label rataLabel = new Label("Rata-rata:");
    private Label totalLabel = new Label("Total siswa:");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Aplikasi Nilai Siswa");

        // Kolom tabel
        TableColumn<Siswa, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));

        TableColumn<Siswa, String> nimCol = new TableColumn<>("NIM");
        nimCol.setCellValueFactory(new PropertyValueFactory<>("nim"));

        TableColumn<Siswa, Double> matCol = new TableColumn<>("Matematika");
        matCol.setCellValueFactory(new PropertyValueFactory<>("nilaiMat"));

        TableColumn<Siswa, Double> bhsCol = new TableColumn<>("Bahasa");
        bhsCol.setCellValueFactory(new PropertyValueFactory<>("nilaiBhs"));

        TableColumn<Siswa, Double> ipaCol = new TableColumn<>("IPA");
        ipaCol.setCellValueFactory(new PropertyValueFactory<>("nilaiIpa"));

        TableColumn<Siswa, Double> rataCol = new TableColumn<>("Rata-rata");
        rataCol.setCellValueFactory(new PropertyValueFactory<>("rataRata"));

        table.setItems(data);
        table.getColumns().addAll(namaCol, nimCol, matCol, bhsCol, ipaCol, rataCol);

        // Input siswa
        namaField.setPromptText("Nama");
        nimField.setPromptText("NIM");
        matField.setPromptText("Nilai Mat");
        bhsField.setPromptText("Nilai Bhs");
        ipaField.setPromptText("Nilai IPA");

        Button tambahBtn = new Button("Tambah");
        tambahBtn.setOnAction(e -> tambahData());

        HBox inputBox = new HBox(10, namaField, nimField, matField, bhsField, ipaField, tambahBtn);

        // Tombol perhitungan
        Button hitungBtn = new Button("Hitung Total & Rata-rata");
        hitungBtn.setOnAction(e -> hitungRataTotal());

        // Pencarian dan pengurutan
        cariNamaField.setPromptText("Cari Nama");
        cariNimField.setPromptText("Cari NIM");

        Button cariNamaBtn = new Button("Cari Nama (Binary Search)");
        cariNamaBtn.setOnAction(e -> cariByNamaBinary());

        Button cariNimBtn = new Button("Cari NIM (Binary Search)");
        cariNimBtn.setOnAction(e -> cariByNimBinary());

        Button urutBtn = new Button("Urutkan Rata-rata");
        urutBtn.setOnAction(e -> urutkanByNilai());

        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> table.setItems(data));

        Button bacaBtn = new Button("Baca dari Dokumen");
        bacaBtn.setOnAction(e -> bacaDariFileManual());

        HBox searchBox = new HBox(10, cariNamaField, cariNamaBtn, cariNimField, cariNimBtn, urutBtn, resetBtn, bacaBtn);

        VBox root = new VBox(10, table, inputBox, searchBox, hitungBtn, rataLabel, totalLabel);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 1000, 550));
        stage.show();

        bacaDariFile(); // baca saat awal dijalankan
    }

    private void tambahData() {
        try {
            String nama = namaField.getText();
            String nim = nimField.getText();
            double mat = Double.parseDouble(matField.getText());
            double bhs = Double.parseDouble(bhsField.getText());
            double ipa = Double.parseDouble(ipaField.getText());

            if (nama.isEmpty() || nim.isEmpty()) {
                showAlert("Nama dan NIM wajib diisi.");
                return;
            }

            data.add(new Siswa(nama, nim, mat, bhs, ipa));
            namaField.clear(); nimField.clear();
            matField.clear(); bhsField.clear(); ipaField.clear();

            simpanKeFile();

        } catch (NumberFormatException e) {
            showAlert("Semua nilai harus berupa angka!");
        }
    }

    private void hitungRataTotal() {
        if (data.isEmpty()) {
            rataLabel.setText("Rata-rata: -");
            totalLabel.setText("Total siswa: 0");
            return;
        }

        double totalSemua = hitungTotalRekursif(data, 0);
        int totalSiswa = data.size();
        double rata = totalSemua / totalSiswa;

        rataLabel.setText("Rata-rata: " + String.format("%.2f", rata));
        totalLabel.setText("Total siswa: " + totalSiswa);
    }

    private double hitungTotalRekursif(ObservableList<Siswa> list, int index) {
        if (index == list.size()) return 0;
        return list.get(index).getRataRata() + hitungTotalRekursif(list, index + 1);
    }

    private void cariByNamaBinary() {
        ObservableList<Siswa> salinan = FXCollections.observableArrayList(data);
        FXCollections.sort(salinan, (a, b) -> a.getNama().compareToIgnoreCase(b.getNama()));

        String target = cariNamaField.getText().toLowerCase();
        int low = 0, high = salinan.size() - 1;
        boolean ketemu = false;

        while (low <= high) {
            int mid = (low + high) / 2;
            Siswa midSiswa = salinan.get(mid);
            int cmp = midSiswa.getNama().toLowerCase().compareTo(target);

            if (cmp == 0) {
                table.setItems(FXCollections.observableArrayList(midSiswa));
                ketemu = true;
                break;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (!ketemu) {
            showAlert("Siswa dengan nama \"" + target + "\" tidak ditemukan.");
        }
    }

    private void cariByNimBinary() {
        ObservableList<Siswa> salinan = FXCollections.observableArrayList(data);
        FXCollections.sort(salinan, (a, b) -> a.getNim().compareTo(b.getNim()));

        String target = cariNimField.getText();
        int low = 0, high = salinan.size() - 1;
        boolean ketemu = false;

        while (low <= high) {
            int mid = (low + high) / 2;
            Siswa midSiswa = salinan.get(mid);
            int cmp = midSiswa.getNim().compareTo(target);

            if (cmp == 0) {
                table.setItems(FXCollections.observableArrayList(midSiswa));
                ketemu = true;
                break;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (!ketemu) {
            showAlert("Siswa dengan NIM " + target + " tidak ditemukan.");
        }
    }

    private void urutkanByNilai() {
        FXCollections.sort(data, (a, b) -> Double.compare(b.getRataRata(), a.getRataRata()));
        table.setItems(data);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Peringatan");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void simpanKeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(NAMA_FILE))) {
            for (Siswa s : data) {
                String baris = String.join(";", s.getNama(), s.getNim(),
                        String.valueOf(s.getNilaiMat()),
                        String.valueOf(s.getNilaiBhs()),
                        String.valueOf(s.getNilaiIpa()));
                writer.write(baris);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Gagal menyimpan data: " + e.getMessage());
        }
    }

    private void bacaDariFile() {
        data.clear();
        Path path = Paths.get(NAMA_FILE);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                String[] bagian = baris.split(";");
                if (bagian.length == 5) {
                    String nama = bagian[0];
                    String nim = bagian[1];
                    double mat = Double.parseDouble(bagian[2]);
                    double bhs = Double.parseDouble(bagian[3]);
                    double ipa = Double.parseDouble(bagian[4]);
                    data.add(new Siswa(nama, nim, mat, bhs, ipa));
                }
            }
        } catch (IOException | NumberFormatException e) {
            showAlert("Gagal membaca data: " + e.getMessage());
        }
    }

    private void bacaDariFileManual() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Data Siswa");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (.txt)", ".txt"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                data.clear(); // Clear data lama
                String baris;
                while ((baris = reader.readLine()) != null) {
                    String[] bagian = baris.split(";");
                    if (bagian.length == 5) {
                        String nama = bagian[0];
                        String nim = bagian[1];
                        double mat = Double.parseDouble(bagian[2]);
                        double bhs = Double.parseDouble(bagian[3]);
                        double ipa = Double.parseDouble(bagian[4]);
                        data.add(new Siswa(nama, nim, mat, bhs, ipa));
                    }
                }
            } catch (IOException | NumberFormatException e) {
                showAlert("Gagal membaca file: " + e.getMessage());
            }
        }
    }

    public static class Siswa {
        private final String nama;
        private final String nim;
        private final double nilaiMat, nilaiBhs, nilaiIpa;

        public Siswa(String nama, String nim, double mat, double bhs, double ipa) {
            this.nama = nama;
            this.nim = nim;
            this.nilaiMat = mat;
            this.nilaiBhs = bhs;
            this.nilaiIpa = ipa;
        }

        public String getNama() { return nama; }
        public String getNim() { return nim; }
        public double getNilaiMat() { return nilaiMat; }
        public double getNilaiBhs() { return nilaiBhs; }
        public double getNilaiIpa() { return nilaiIpa; }

        public double getRataRata() {
            return (nilaiMat + nilaiBhs + nilaiIpa) / 3;
        }
    }
}