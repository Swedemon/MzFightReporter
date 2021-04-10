package org.vmy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class GraphBot extends Application {

    public GraphBot() {}

    private static FightReport report;

    public static void main(String[] args) throws Exception {

        report = FightReport.readReportFile();
        launch(new String[]{});
    }

    @Override
    public void start(Stage stage) throws Exception {

        try {
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
            //lineChart.setLegendVisible(false);
            //lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: DIMGRAY;");


            Comparator sortingByName = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Map.Entry<String,List<Integer>> p1 = (Map.Entry<String,List<Integer>>)o1;
                    Map.Entry<String,List<Integer>> p2 = (Map.Entry<String,List<Integer>>)o2;
                    Integer p1Last = p1.getValue().get(p1.getValue().size()-1);
                    Integer p2Last = p2.getValue().get(p2.getValue().size()-1);
                    return -p1Last.compareTo(p2Last);
                }
            };

            Object[] objects = report.getDmgMap().entrySet().toArray();
            //Arrays.stream(objects).sorted(sortingByName).limit(10).forEach((s)->System.out.println(s));
            objects = Arrays.stream(objects).sorted(sortingByName).limit(org.vmy.Parameters.getInstance().graphPlayerLimit).toArray();

            //populating the series with data
            //Object[] names = report.getDmgMap().keySet().toArray();
            for (int i = 0; i < objects.length; i++) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                Map.Entry<String,List<Integer>> m = (Map.Entry<String,List<Integer>>) objects[i];
                String name = (String) m.getKey();
                series.setName((String) m.getKey());
                Object[] ao = report.getDmgMap().get(name).toArray();
                int lastValue = 0;
                for (int j = 0; j < ao.length; j++) {
                    int nextValue = (Integer) ao[j];
                    series.getData().add(new XYChart.Data(j, nextValue - lastValue));
                    lastValue = nextValue;
                }
                lineChart.getData().add(series);
            }

            Scene scene = new Scene(lineChart, 800, 600);

            try {
                File f = new File(org.vmy.Parameters.getInstance().homeDir + "dark-theme.css");
                String ex = f.toURI().toURL().toExternalForm();
                scene.getStylesheets().add(ex);
            } catch (Exception e) {
                e.printStackTrace();
            }

            stage.setScene(scene);
            lineChart.setAnimated(false);

            saveAsPng(scene, org.vmy.Parameters.getInstance().homeDir + "fightreport.png");

            //terminate
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void saveAsPng(Scene scene, String path) {
        WritableImage image = scene.snapshot(null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}