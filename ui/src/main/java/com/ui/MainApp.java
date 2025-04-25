package com.ui;


import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

public class MainApp extends Application {
    private File fileA;
    private File fileB;
    private final String buttonAText = "Choose signature A";
    private final String buttonBText = "Choose signature B";

    @Override
    public void start(Stage primaryStage) {
        System.out.println(">>> JavaFX start() called!");
        String applicationName = "SignatureGuard";
        primaryStage.setTitle(applicationName);
        Label title = new Label(applicationName);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button chooseA = new Button(buttonAText);
        Button chooseB = new Button(buttonBText);
        Button compareBtn = new Button("Compare Signatures");
        compareBtn.setDisable(true);

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Images", "*.png")
        );

        chooseA.setOnAction(e -> {
            System.out.println("Button A set on action");
           File selected = chooser.showOpenDialog(primaryStage);
           if (selected != null &&
                   selected.getName()
                           .toLowerCase()
                           .endsWith(".png")) {
               this.fileA = selected;
               chooseA.setText("A: " + selected.getName());
           }
           updateCompareState(compareBtn);
        });

        chooseB.setOnAction(e -> {
            File selected = chooser.showOpenDialog(primaryStage);
            if (selected != null &&
                    selected.getName()
                            .toLowerCase()
                            .endsWith(".png")) {
                this.fileB = selected;
                chooseB.setText("B: " + selected.getName());
            }
            updateCompareState(compareBtn);
        });

        compareBtn.setOnAction(e -> {
           if (this.fileA == null || this.fileB == null) {
               messageLabel.setText("Please select both PNG signature files before comparing");
           }
            chooseA.setDisable(true);
            chooseB.setDisable(true);
            compareBtn.setDisable(true);
            messageLabel.setText("Processing...");
            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    try {
                        return uploadSignatures(fileA, fileB);
                    } catch (Exception ex) {
                        return "Error: " + ex.getMessage();
                    }
                }

                @Override
                protected void succeeded() {
                    messageLabel.setText(getValue());
                    resetState(chooseA, chooseB, compareBtn);
                }

                @Override
                protected void failed() {
                    messageLabel.setText("Upload failed.");
                    resetState(chooseA, chooseB, compareBtn);
                }
            };
            new Thread(task).start();
        });

        VBox root = new VBox(10, chooseA, chooseB,
                compareBtn, messageLabel);
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root, 400, 250);
        primaryStage.setScene(scene);

        // ←– Make the window visible!
        primaryStage.show();
    }

    private void updateCompareState(Button compareBtn) {
        compareBtn.setDisable(fileA == null || fileB == null);
    }

    private void resetState(Button aBtn, Button bBtn, Button compareBtn) {
        fileA = null;
        fileB = null;
        aBtn.setText(buttonAText);
        bBtn.setText(buttonBText);
        aBtn.setDisable(false);
        bBtn.setDisable(false);
        compareBtn.setDisable(true);
    }

    private String uploadSignatures(File fileA, File fileB) throws IOException {
        String boundary = UUID.randomUUID().toString();
        String apiUrl = "http://localhost:8080/api/upload-signature";
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            writeFilePart(out, "file1", fileA, boundary);
            writeFilePart(out, "file2", fileB, boundary);
            out.writeBytes("--" + boundary + "--\r\n");
            out.flush();
        }

        int status = conn.getResponseCode();
        InputStream in = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                ? conn.getInputStream() : conn.getErrorStream();
        String response = new String(in.readAllBytes());
        conn.disconnect();
        return response;
    }

    private void writeFilePart(
            DataOutputStream out, String fieldName,
            File file, String boundary) throws IOException {
        String LINE_END = "\r\n";
        String fileName = file.getName();
        out.writeBytes("--" + boundary + LINE_END);
        out.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + LINE_END);
        out.writeBytes("Content-Type: image/png" + LINE_END + LINE_END);
        out.write(Files.readAllBytes(file.toPath()));
        out.writeBytes(LINE_END);
    }

    public static void main(String[] args) {
        launch(args);
    }
}