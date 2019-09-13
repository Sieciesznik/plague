package plague;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.*;

class EventManager{

    ArrayList<String> events = new ArrayList<>();
    //File file = new File("wydarzenia.txt");

    EventManager(){
        try {
            FileInputStream fstream_school = new FileInputStream("wydarzenia.txt");
            DataInputStream data_input = new DataInputStream(fstream_school);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
            String str_line;

            while ((str_line = buffer.readLine()) != null) {
                str_line = str_line.trim();
                if ((str_line.length() != 0)) {
                    events.add(str_line);
                }
            }
        }catch(Exception e) {
            System.out.print("Problem z plikiem wydarzenia!!!");
        }
    }

    String getEvent(){
        Random r = new Random();
        return events.get(r.nextInt(events.size()));
    }
}

class EventControl {

    EventManager eventManager = new EventManager();
    Media media = new Media(new File("dzwon.mp3").toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    Label label;

    EventControl(Label label){
        this.label = label;
    }

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void eventForAnMinute() {

        final Runnable event = new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(eventManager.getEvent());
                    }
                });
            }
        };
        final ScheduledFuture<?> eventHandle = scheduler.scheduleAtFixedRate(event, 60, 60, SECONDS);
    }
}

public class Main extends Application {

    StackPane root = new StackPane();
    Media media = new Media(new File("ambient.mp3").toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    Label label = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception{

        label.setFont(Font.font("Calibri", 40));
        label.setWrapText(true);
        label.setMaxWidth(600);

        root.setId("pane");
        primaryStage.setTitle("Dżuma");

        //mediaPlayer.setVolume(0.5);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        //mediaPlayer.play();
        EventControl control = new EventControl(label);
        control.eventForAnMinute();

        Scene scene = new Scene(root, 800, 400);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        root.getChildren().add(label);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
