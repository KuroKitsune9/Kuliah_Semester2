package kuliah.sems_2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class HotelBookingLoopStyle extends Application {

    String[] roomTypes = {"Standard", "Deluxe", "Suite"};
    int[] prices = {500000, 1000000, 2000000};
    boolean[] available = {true, true, true};

    TextArea outputArea;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hotel Booking - Versi Rapi");

        Label label = new Label("Selamat datang di Hotel Kami");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200); // Biar tidak terlalu pendek
        outputArea.setPrefWidth(400);

        // Semua tombol
        Button lihatBtn = new Button("1. Lihat daftar kamar");
        Button pesanBtn = new Button("2. Pesan kamar");
        Button tambahFileBtn = new Button("3. Tambah ke TextFile");
        Button tampilFileBtn = new Button("4. Tampilkan dari TextFile");
        Button sortBtn = new Button("5. Urutkan kamar");
        Button searchBtn = new Button("6. Cari kamar");
        Button keluarBtn = new Button("7. Keluar");

        // Atur ukuran tombol
        int buttonWidth = 190;
        lihatBtn.setPrefWidth(buttonWidth);
        pesanBtn.setPrefWidth(buttonWidth);
        tambahFileBtn.setPrefWidth(buttonWidth);
        tampilFileBtn.setPrefWidth(buttonWidth);
        sortBtn.setPrefWidth(buttonWidth);
        searchBtn.setPrefWidth(buttonWidth);
        keluarBtn.setPrefWidth(buttonWidth * 2 + 10); // Lebih panjang dikit, sesuai lebar 2 tombol

        // Susun tombol-tombol
        HBox baris1 = new HBox(10, lihatBtn, pesanBtn);
        HBox baris2 = new HBox(10, tambahFileBtn, tampilFileBtn);
        HBox baris3 = new HBox(10, sortBtn, searchBtn);

        baris1.setAlignment(Pos.CENTER);
        baris2.setAlignment(Pos.CENTER);
        baris3.setAlignment(Pos.CENTER);

        VBox tombolBox = new VBox(15, baris1, baris2, baris3, keluarBtn);
        tombolBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, label, outputArea, tombolBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 450, 550);
        stage.setScene(scene);
        stage.show();

        // Event handler
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
    }

    // (Method lainnya seperti tampilkanDaftarKamar(), pesanKamar(), dll tetap sama)

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
        // Bisa isi method sorting harga (seperti yang sudah dibuat sebelumnya)
    }

    void cariKamar() {
        // Bisa isi method searching kamar berdasarkan nama
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
