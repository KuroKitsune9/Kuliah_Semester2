package kuliah.sems_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HotelBookingLoopStyle extends Application {

    String[] roomTypes = {"Standard", "Deluxe", "Suite"};
    int[] prices = {500000, 1000000, 2000000};
    boolean[] available = {true, true, true};

    TextArea outputArea;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Hotel Booking - Versi Menu");

        Label label = new Label("Selamat datang di Hotel Kami");
        outputArea = new TextArea();
        outputArea.setEditable(false);

        Button lihatBtn = new Button("1. Lihat daftar kamar");
        Button pesanBtn = new Button("2. Pesan kamar");
        Button keluarBtn = new Button("3. Keluar");

        lihatBtn.setOnAction(e -> tampilkanDaftarKamar());
        pesanBtn.setOnAction(e -> pesanKamar());
        keluarBtn.setOnAction(e -> {
            Alert keluar = new Alert(Alert.AlertType.INFORMATION, "Terima kasih telah menggunakan layanan kami!");
            keluar.showAndWait();
            stage.close();
        });

        VBox root = new VBox(10, label, outputArea, lihatBtn, pesanBtn, keluarBtn);
        Scene scene = new Scene(root, 400, 400);
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

    void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
