package me.guruguru19.trajectorygraphing.trajectory;

import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import me.guruguru19.trajectorygraphing.vision.TargetDetection;
import org.opencv.core.Mat;
import org.opencv.core.Point;

public class GraphDrawer {
    private static Mat frame;//clear frame

    private static Mat snap;
    private static Target target;

    public static void updateFrame(Mat frame){//vision thread
        GraphDrawer.frame = frame;
    }

    public static void snap(ImageView snapFeed){//gui thread
        snap = frame;
        Mat processedFrame = TargetDetection.processedFrame(snap);
        TargetDetection.markOuterContour(processedFrame,snap);
        GraphDrawer.target = TargetDetection.markMiddleOfContour(processedFrame,snap);
        TargetDetection.drawImage(snap, snapFeed);
    }


    public static void draw(LaunchPlan lunchPlan, XYChart.Series series){
        series.setName("lunch velocity: "+lunchPlan.getExitVelocity()+" (m/2), lunch angle; "+lunchPlan.getLunchAngle()+"(deg)");
        if (Double.isNaN(lunchPlan.getExitVelocity())){
            series.setName("invalid Trajectory");
        }
        for (Point p: lunchPlan.getTrajectory()) {
            series.getData().add(new XYChart.Data(p.x, p.y));
        }
        //App.getAppStage().show();
    }

    public static Target getTarget() {
        return target;
    }
}
