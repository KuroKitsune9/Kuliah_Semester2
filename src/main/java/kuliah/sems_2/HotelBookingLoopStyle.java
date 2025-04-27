package kuliah.sems_2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class HotelBookingLoopStyle extends Application {

    String[] roomTypes = {"Standard", "Deluxe", "Suite"};
    int[] prices = {500000, 1000000, 2000000};
    boolean[] available = {true, true, true};

    TextArea outputArea;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hotel Booking - Versi Update");

        Label label = new Label("Selamat datang di Hotel Kami");
        outputArea = new TextArea();
        outputArea.setEditable(false);

        // Semua button
        Button lihatBtn = new Button("1. Lihat daftar kamar");
        Button pesanBtn = new Button("2. Pesan kamar");
        Button tambahFileBtn = new Button("3. Tambah ke TextFile");
        Button tampilFileBtn = new Button("4. Tampilkan dari TextFile");
        Button sortBtn = new Button("5. Urutkan kamar");
        Button searchBtn = new Button("6. Cari kamar");
        Button keluarBtn = new Button("7. Keluar");

        // Ukuran tombol
        lihatBtn.setPrefWidth(180);
        pesanBtn.setPrefWidth(180);
        tambahFileBtn.setPrefWidth(180);
        tampilFileBtn.setPrefWidth(180);
        sortBtn.setPrefWidth(180);
        searchBtn.setPrefWidth(180);
        keluarBtn.setPrefWidth(370);

        // Layout button
        HBox baris1 = new HBox(10, lihatBtn, pesanBtn);
        HBox baris2 = new HBox(10, tambahFileBtn, tampilFileBtn);
        HBox baris3 = new HBox(10, sortBtn, searchBtn);

        VBox root = new VBox(15, label, outputArea, baris1, baris2, baris3, keluarBtn);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        // Event button
        lihatBtn.setOnAction(e -> tampilkanDaftarKamar());
        pesanBtn.setOnAction(e -> pesanKamar());
        tambahFileBtn.setOnAction(e -> tambahKeFile());
        tampilFileBtn.setOnAction(e -> tampilkanDariFile());
        sortBtn.setOnAction(e -> urutkanKamar());
        searchBtn.setOnAction(e -> cariKamar());
        keluarBtn.setOnAction(e -> {
            Alert keluar = new Alert(Alert.AlertType.INFORMATION, "Terima kasih telah menggunakan layanan kami!");
            keluar.showAndWait();
            stage.close();
        });

        Scene scene = new Scene(root, 450, 550);
        stage.setScene(scene);
        stage.show();
    }

    void tampilkanDaftarKamar() {
        StringBuilder sb = new StringBuilder("Daftar Kamar:\n");
        for (int i = 0; i < roomTypes.length; i++) {
            sb.append(i + 1).append(". ")
                    .append(roomTypes[i])
                    .append(" - Rp").append(prices[i])
                    .append(" - ").append(available[i] ? "Tersedia" : "Dipesan")
                    .append("\n");
        }
        outputArea.setText(sb.toString());
    }

    void pesanKamar() {
        TextInputDialog pilihKamar = new TextInputDialog();
        pilihKamar.setHeaderText("Masukkan nomor kamar yang ingin dipesan:");
        pilihKamar.showAndWait().ifPresent(input -> {
            try {
                int nomor = Integer.parseInt(input) - 1;

                if (nomor < 0 || nomor >= roomTypes.length) {
                    showAlert("Nomor kamar tidak valid!");
                    return;
                }

                if (!available[nomor]) {
                    showAlert("Kamar sudah dipesan!");
                    return;
                }

                TextInputDialog malamDialog = new TextInputDialog();
                malamDialog.setHeaderText("Berapa malam Anda akan menginap?");
                malamDialog.showAndWait().ifPresent(malamStr -> {
                    try {
                        int malam = Integer.parseInt(malamStr);
                        int total = malam * prices[nomor];

                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setHeaderText("Total harga: Rp" + total);
                        confirm.setContentText("Ingin melanjutkan?");
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                available[nomor] = false;
                                showAlert("Pesanan berhasil!");
                                tampilkanDaftarKamar();
                            }
                        });
                    } catch (NumberFormatException e) {
                        showAlert("Input malam tidak valid.");
                    }
                });

            } catch (NumberFormatException e) {
                showAlert("Input nomor kamar tidak valid.");
            }
        });
    }

    void tambahKeFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("kamar.txt", true))) {
            for (int i = 0; i < roomTypes.length; i++) {
                writer.println(roomTypes[i] + "," + prices[i] + "," + available[i]);
            }
            showAlert("Data berhasil ditambahkan ke kamar.txt!");
        } catch (IOException e) {
            showAlert("Gagal menambahkan ke file!");
        }
    }

    void tampilkanDariFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("kamar.txt"))) {
            StringBuilder sb = new StringBuilder("Isi file kamar.txt:\n");
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            outputArea.setText(sb.toString());
        } catch (IOException e) {
            showAlert("Gagal membaca file!");
        }
    }

    void urutkanKamar() {
        Integer[] idx = {0, 1, 2};
        Arrays.sort(idx, Comparator.comparingInt(i -> prices[i]));

        String[] newRoomTypes = new String[roomTypes.length];
        int[] newPrices = new int[prices.length];
        boolean[] newAvailable = new boolean[available.length];

        for (int i = 0; i < idx.length; i++) {
            newRoomTypes[i] = roomTypes[idx[i]];
            newPrices[i] = prices[idx[i]];
            newAvailable[i] = available[idx[i]];
        }

        roomTypes = newRoomTypes;
        prices = newPrices;
        available = newAvailable;

        showAlert("Kamar berhasil diurutkan berdasarkan harga!");
        tampilkanDaftarKamar();
    }

    void cariKamar() {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setHeaderText("Masukkan nama kamar yang dicari:");
        Optional<String> result = searchDialog.showAndWait();

        if (result.isPresent()) {
            String keyword = result.get().toLowerCase();
            boolean found = false;
            StringBuilder sb = new StringBuilder("Hasil pencarian:\n");

            for (int i = 0; i < roomTypes.length; i++) {
                if (roomTypes[i].toLowerCase().contains(keyword)) {
                    sb.append(i + 1).append(". ")
                            .append(roomTypes[i])
                            .append(" - Rp").append(prices[i])
                            .append(" - ").append(available[i] ? "Tersedia" : "Dipesan")
                            .append("\n");
                    found = true;
                }
            }

            if (found) {
                outputArea.setText(sb.toString());
            } else {
                showAlert("Kamar tidak ditemukan.");
            }
        }
    }

    void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
