package me.guruguru19.trajectorygraphing.trajectory;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class LunchPlan {
    private double exitVelocity;
    private double lunchAngle;
    private double time;

    private List<Point> trajectory;


    public LunchPlan(double exitVelocity, double lunchAngle) {
        this.exitVelocity = exitVelocity;
        this.lunchAngle = lunchAngle;
        this.time = 0;
        this.trajectory = new ArrayList<>();
    }

    public LunchPlan(double exitVelocity, double lunchAngle, List<Point> trajectory) {
        this.exitVelocity = exitVelocity;
        this.lunchAngle = lunchAngle;
        this.time = -1;
        this.trajectory = trajectory;
    }


    public double getExitVelocity() {
        return exitVelocity;
    }

    public double getLunchAngle() {
        return lunchAngle;
    }

    public void addPoint(double x, double y){
        this.trajectory.add(new Point(x,y));
        this.time += TrajectoryCalc.getDt();
    }

    public List<Point> getTrajectory() {
        return trajectory;
    }
}
