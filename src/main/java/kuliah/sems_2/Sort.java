package kuliah.sems_2;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Sort extends Application {

    private ObservableList<String> names = FXCollections.observableArrayList(
            "Muharafi", "Ratu Lebah", "Begal", "Rafi", "Ridwan", "andra", "Muzakki"
    );
    private TableView<String> tableView = new TableView<>();
    private Label resultLabel = new Label();
    private TextField searchField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        // Setup TableView
        TableColumn<String, String> nameColumn = new TableColumn<>("Nama");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        tableView.getColumns().add(nameColumn);
        tableView.setItems(FXCollections.observableArrayList(names)); // tampilan awal
        tableView.setMaxHeight(200);

        // Tombol
        Button ascButton = new Button("Sort Ascending");
        Button descButton = new Button("Sort Descending");

        // Search Field
        searchField.setPromptText("Cari nama...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> linearSearch(newVal));

        ascButton.setOnAction(e -> {
            ArrayList<String> sorted = new ArrayList<>(names);
            bubbleSort(sorted, true);
            names.setAll(sorted);
            tableView.setItems(FXCollections.observableArrayList(names));
            resultLabel.setText("Data diurutkan Ascending (Bubble Sort)");
        });

        descButton.setOnAction(e -> {
            ArrayList<String> sorted = new ArrayList<>(names);
            bubbleSort(sorted, false);
            names.setAll(sorted);
            tableView.setItems(FXCollections.observableArrayList(names));
            resultLabel.setText("Data diurutkan Descending (Bubble Sort)");
        });

        VBox root = new VBox(10, searchField, tableView, ascButton, descButton, resultLabel);
        root.setStyle("-fx-padding: 20px; -fx-font-size: 14px;");

        primaryStage.setTitle("JavaFX - Bubble Sort & Linear Search");
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }

    // Bubble Sort
    private void bubbleSort(ArrayList<String> list, boolean ascending) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String a = list.get(j).toLowerCase();
                String b = list.get(j + 1).toLowerCase();

                boolean shouldSwap = ascending ? a.compareTo(b) > 0 : a.compareTo(b) < 0;

                if (shouldSwap) {
                    // swap
                    String temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // Linear Search
    private void linearSearch(String query) {
        if (query == null || query.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList(names));
            resultLabel.setText("");
            return;
        }

        List<String> matched = new ArrayList<>();
        for (String name : names) {
            if (name.toLowerCase().contains(query.toLowerCase())) {
                matched.add(name);
            }
        }

        if (matched.isEmpty()) {
            resultLabel.setText("Nama tidak ditemukan.");
        } else {
            resultLabel.setText("Ditemukan " + matched.size() + " hasil.");
        }

        tableView.setItems(FXCollections.observableArrayList(matched));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
