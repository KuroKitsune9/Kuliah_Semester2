package kuliah.sems_2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MethodAndRekursif extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Masukkan angka:");
        TextField inputField = new TextField();
        Button calculateButton = new Button("Hitung Faktor");
        Label resultLabel = new Label();

        calculateButton.setOnAction(e -> {
            try {
                int number = Integer.parseInt(inputField.getText());
                String factors = findFactors(number, number, "");
                resultLabel.setText("Faktor: " + factors);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Masukkan angka yang valid!");
            }
        });

        VBox vbox = new VBox(10, label, inputField, calculateButton, resultLabel);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rekursi Faktor");
        primaryStage.show();
    }

    private String findFactors(int n, int divisor, String result) {
        if (divisor == 0) {
            return result;
        }
        if (n % divisor == 0) {
            result = divisor + " " + result;
        }
        return findFactors(n, divisor - 1, result);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
