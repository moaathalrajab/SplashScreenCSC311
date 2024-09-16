package org.example.splashscreen;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

public class Launcher extends Application {
    public static final String APPLICATION_ICON = "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png";
    public static final String SPLASH_IMAGE = "https://fscnow.farmingdale.edu/2024/reimagining-higher-ed/img/reimagining-higher-education-with-flexible-learning-1200x630.jpg";

    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;
    private Stage mainStage;
    private static final int SPLASH_WIDTH = 900;
    private static final int SPLASH_HEIGHT = 400;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void init() {
        ImageView splash = new ImageView(new Image(
                SPLASH_IMAGE));
        splash.setViewport(new Rectangle2D(100,0,SPLASH_WIDTH,SPLASH_HEIGHT));
        loadProgress = new ProgressBar();
        loadProgress.setPrefWidth(SPLASH_WIDTH);
        progressText = new Label("Will find friends for peanuts . . .");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle(
                "-fx-padding: 5; " +
                        "-fx-background-color: cornsilk; " +
                        "-fx-border-width:5; " +
                        "-fx-border-color: " +
                        "linear-gradient(" +
                        "to bottom, " +
                        "chocolate, " +
                        "derive(chocolate, 50%)" +
                        ");");
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage initStage) throws Exception {
        final Task<ObservableList<String>> fscProgramsTask = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws InterruptedException {
                ObservableList<String> foundAcademicPrograms = FXCollections.<String>observableArrayList();
                ObservableList<String> availableStudy = FXCollections.observableArrayList(
                        "CSC", "EET", "CPIS", "BUS", "NUR",
                        "STS", "MTH", "CHM", "PHY",
                        "ENG", "AAR", "MGT", "AIM", "MGM");

                updateMessage("Loading study programs . . .");
                for (int i = 0; i < availableStudy.size(); i++) {
                    Thread.sleep(400);
                    updateProgress(i + 1, availableStudy.size());
                    String nextProgram = availableStudy.get(i);
                    foundAcademicPrograms.add(nextProgram);
                    updateMessage("Loading programs . . . all available " + nextProgram);
                }
                Thread.sleep(400);
                updateMessage("All programs loaded");

                return foundAcademicPrograms;
            }

        };

        showSplash(
                initStage,
                fscProgramsTask,
                () -> showMainStage(fscProgramsTask.valueProperty()));
        new Thread(fscProgramsTask).start();
    }

    private void showMainStage(
            ReadOnlyObjectProperty<ObservableList<String>> prgms) {
        mainStage = new Stage(StageStyle.DECORATED);
        mainStage.setTitle("Farmingdale State College");




        mainStage.setScene(new Scene(new Label("Landing page"), 500, 200));
        mainStage.show();
    }

    private void showSplash(
            final Stage initStage,
            Task<?> task,
            InitCompletionHandler initCompletionHandler) {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress.progressProperty().unbind();
                loadProgress.setProgress(1);
                initStage.toFront();
                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
                fadeSplash.setFromValue(1.0);
                fadeSplash.setToValue(0.0);
                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
                fadeSplash.play();

                initCompletionHandler.complete();
            } // todo add code to gracefully handle other task states.
        });

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        initStage.setScene(splashScene);
        initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setAlwaysOnTop(true);
        initStage.show();
    }

    public interface InitCompletionHandler {
        void complete();
    }

}
