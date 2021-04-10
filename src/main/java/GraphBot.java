import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GraphBot extends Application {

    public GraphBot() {}

    private static FightReport report;

    public static void main(String[] args) {
        launch(args);
    }

    public void graphIt(FightReport report) {
        GraphBot.report = report;
        //launch(new String[] {});
        Application.launch(GraphBot.class, new String[] {});
    }

    @Override
    public void start(Stage stage) throws Exception {

        //root.getStylesheets().add(getClass().getResource("dark-theme.css").toString());

        stage.setTitle("Line Chart Sample");

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Timeline (seconds)");
        yAxis.setLabel("DPS");

        //creating the chart
        LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Squad Damage Output");
        lineChart.setCreateSymbols(false);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: DIMGRAY;");

        //defining a series
        //XYChart.Series series = new XYChart<>().Series();
        //populating the series with data
        Object[] names = report.getDmgMap().keySet().toArray();
        for (int i=0; i < names.length; i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName((String)names[i]);
            Object[] ao = report.getDmgMap().get(names[i]).toArray();
            int lastValue = 0;
            for (int j=0; j < ao.length; j++) {
                int nextValue = (Integer) ao[j];
                series.getData().add(new XYChart.Data(j, nextValue - lastValue));
                lastValue = nextValue;
            }
            lineChart.getData().add(series);
        }

        //series.getData().add(new XYChart.Data(5, 34));
        Scene scene = new Scene(lineChart, 800, 600);

        //scene.getStylesheets().addAll(this.getClass().getResource("dark-theme.css").toExternalForm());
        File f = new File("dark-theme.css");
        String ex = f.toURI().toURL().toExternalForm();
        scene.getStylesheets().add("file:/C:/Users/Drew/IdeaProjects/MzFightReporter/src/dark-theme.css");


        stage.setScene(scene);
        //stage.show();
        lineChart.setAnimated(false);

        saveAsPng(scene, "c:\\Games\\chart.png");
        //stage.setScene(scene);
        //saveAsPng(scene, "c:\\Games\\chart1.png");
        stage.show();

        System.out.println("After show");
        //Platform.exit();
    }

    public void saveAsPng(Scene scene, String path) {
        WritableImage image = scene.snapshot(null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class GraphBot {

    public static void main(String[] args) {

        String chartGenLocation = "C:\\Games";
        new JFXPanel();
        javafx.scene.chart.AreaChart
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Failed", 10),
                        new PieChart.Data("Skipped", 20));
        final PieChart chart = new PieChart(pieChartData);
        chart.setAnimated(false);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(chart, 500, 500);
            stage.setScene(scene);
            WritableImage img = new WritableImage(500, 500);
            scene.snapshot(img);

            File file = new File(Paths.get(chartGenLocation, "a.png").toString());
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
            } catch (IOException e) {
                //logger.error("Error occurred while writing the chart image
            }
        });
    }
}*/