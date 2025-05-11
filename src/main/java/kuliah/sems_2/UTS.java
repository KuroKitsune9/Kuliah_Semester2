package kuliah.sems_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import java.io.*;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;

public class UTS extends Application {

    private final String FILE_PATH = "siswa.txt";
    private final ObservableList<Siswa> siswaList = FXCollections.observableArrayList();
    private final TextField namaField = new TextField();
    private final TextField nimField = new TextField();
    private final TextField nilai1Field = new TextField();
    private final TextField nilai2Field = new TextField();
    private final TextField nilai3Field = new TextField();
    private final TextField cariField = new TextField();
    private final Label rataRataLabel = new Label("Rata-rata: ");
    private final Label totalLabel = new Label("Total Mahasiswa: ");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        siswaList.addAll(bacaFile());

        TableView<Siswa> table = new TableView<>(siswaList);
        table.getColumns().addAll(
                kolom("Nama", "nama"),
                kolom("NIM", "nim"),
                kolomDouble("Matematika", "nilai1"),
                kolomDouble("Fisika", "nilai2"),
                kolomDouble("Bahasa", "nilai3"),
                kolomRataRata()
        );

        namaField.setPromptText("Nama");
        nimField.setPromptText("NIM");
        nilai1Field.setPromptText("Nilai Matematika");
        nilai2Field.setPromptText("Nilai Fisika");
        nilai3Field.setPromptText("Nilai Bahasa");
        cariField.setPromptText("Cari Nama atau NIM");

        VBox layout = new VBox(10,
                table,
                new HBox(5, namaField, nimField, nilai1Field, nilai2Field, nilai3Field, btn("Tambah", e -> tambah())),
                new HBox(5, cariField,
                        btn("Cari Nama (Binary)", e -> cariNama()),
                        btn("Cari NIM (Binary)", e -> cariNIM())
                ),
                new HBox(5,
                        btn("Urut Nilai Desc", e -> urutDesc()),
                        btn("Urut Nilai Asc", e -> urutAsc()),
                        btn("Hitung Rata2 & Total", e -> hitung()),
                        btn("Baca dari File", e -> muatUlangDariFile()),
                        btn("Lihat Isi File", e -> tampilkanIsiFile())
                ),
                rataRataLabel, totalLabel
        );
        layout.setPadding(new javafx.geometry.Insets(10));

        stage.setScene(new Scene(layout, 1000, 550));
        stage.setTitle("Aplikasi Nilai Siswa");
        stage.show();
    }

    private TableColumn<Siswa, String> kolom(String nama, String prop) {
        TableColumn<Siswa, String> col = new TableColumn<>(nama);
        col.setCellValueFactory(data -> {
            switch (prop) {
                case "nama": return data.getValue().namaProperty();
                case "nim": return data.getValue().nimProperty();
                default: return null;
            }
        });
        return col;
    }

    private TableColumn<Siswa, Double> kolomDouble(String nama, String prop) {
        TableColumn<Siswa, Double> col = new TableColumn<>(nama);
        col.setCellValueFactory(data -> {
            switch (prop) {
                case "nilai1": return data.getValue().nilai1Property().asObject();
                case "nilai2": return data.getValue().nilai2Property().asObject();
                case "nilai3": return data.getValue().nilai3Property().asObject();
                default: return null;
            }
        });
        return col;
    }

    private TableColumn<Siswa, Double> kolomRataRata() {
        TableColumn<Siswa, Double> col = new TableColumn<>("Rata-rata");
        col.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRataRata()));
        return col;
    }

    private Button btn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button b = new Button(text);
        b.setOnAction(action);
        return b;
    }

    private List<Siswa> bacaFile() {
        List<Siswa> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                var parts = line.split(",");
                if (parts.length == 5)
                    list.add(new Siswa(parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Double.parseDouble(parts[3].trim()),
                            Double.parseDouble(parts[4].trim())));
            }
        } catch (IOException e) {
            alert("Gagal membaca file.");
        }
        return list;
    }

    private void tulisFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Siswa s : siswaList)
                bw.write(s.getNama() + "," + s.getNim() + "," +
                        s.getNilai1() + "," + s.getNilai2() + "," + s.getNilai3() + "\n");
        } catch (IOException e) {
            alert("Gagal menulis file.");
        }
    }

    private void muatUlangDariFile() {
        siswaList.clear();
        siswaList.addAll(bacaFile());
        alert("Data berhasil dimuat ulang dari file.");
    }

    private void tampilkanIsiFile() {
        StringBuilder isi = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                isi.append(line).append ("\n");
            }
        } catch (IOException e) {
            alert("Gagal membaca isi file.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Isi File siswa.txt");
        alert.setHeaderText("Data dari File:");

        TextArea area = new TextArea(isi.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(area);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void tambah() {
        try {
            Siswa s = new Siswa(
                    namaField.getText(), nimField.getText(),
                    Double.parseDouble(nilai1Field.getText()),
                    Double.parseDouble(nilai2Field.getText()),
                    Double.parseDouble(nilai3Field.getText())
            );
            siswaList.add(s);
            tulisFile();
            namaField.clear(); nimField.clear();
            nilai1Field.clear(); nilai2Field.clear(); nilai3Field.clear();
        } catch (Exception e) {
            alert("Input tidak valid!");
        }
    }

    // Tambahan pengurutan ascending dan descending
    private void urutDesc() {
        siswaList.sort((a, b) -> Double.compare(b.getRataRata(), a.getRataRata()));
    }

    private void urutAsc() {
        siswaList.sort((a, b) -> Double.compare(a.getRataRata(), b.getRataRata()));
    }

    private void cariNama() {
        List<Siswa> sorted = new ArrayList<>(siswaList);
        sorted.sort(Comparator.comparing(Siswa::getNama));
        int i = binarySearchNama(sorted, cariField.getText());
        alert(i >= 0 ? "Ditemukan: " + sorted.get(i).getNama() : "Nama tidak ditemukan!");
    }

    private void cariNIM() {
        List<Siswa> sorted = new ArrayList<>(siswaList);
        sorted.sort(Comparator.comparing(Siswa::getNim));
        int i = binarySearchNIM(sorted, cariField.getText());
        alert(i >= 0 ? "Ditemukan: " + sorted.get(i).getNama() + " (NIM: " + sorted.get(i).getNim() + ")" : "NIM tidak ditemukan!");
    }

    private int binarySearchNama(List<Siswa> list, String target) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = list.get(mid).getNama().compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    private int binarySearchNIM(List<Siswa> list, String target) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = list.get(mid).getNim().compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    private void hitung() {
        int totalSiswa = siswaList.size();
        double totalNilai = totalNilai(siswaList.size() - 1);
        totalLabel.setText("Total siswa: " + totalSiswa);
        rataRataLabel.setText("Rata-rata: " + (totalSiswa == 0 ? 0 : String.format("%.2f", totalNilai / totalSiswa)));
    }

    private double totalNilai(int idx) {
        if (idx < 0) return 0;
        return siswaList.get(idx).getRataRata() + totalNilai(idx - 1);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public static class Siswa {
        private final StringProperty nama;
        private final StringProperty nim;
        private final DoubleProperty nilai1;
        private final DoubleProperty nilai2;
        private final DoubleProperty nilai3;

        public Siswa(String nama, String nim, double nilai1, double nilai2, double nilai3) {
            this.nama = new SimpleStringProperty(nama);
            this.nim = new SimpleStringProperty(nim);
            this.nilai1 = new SimpleDoubleProperty(nilai1);
            this.nilai2 = new SimpleDoubleProperty(nilai2);
            this.nilai3 = new SimpleDoubleProperty(nilai3);
        }

        public String getNama() { return nama.get(); }
        public String getNim() { return nim.get(); }
        public double getNilai1() { return nilai1.get(); }
        public double getNilai2() { return nilai2.get(); }
        public double getNilai3() { return nilai3.get(); }

        public double getRataRata() {
            return (getNilai1() + getNilai2() + getNilai3()) / 3;
        }

        public StringProperty namaProperty() { return nama; }
        public StringProperty nimProperty() { return nim; }
        public DoubleProperty nilai1Property() { return nilai1; }
        public DoubleProperty nilai2Property() { return nilai2; }
        public DoubleProperty nilai3Property() { return nilai3; }
    }
}
