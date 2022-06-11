package me.guruguru19.trajectorygraphing.trajectory;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class LaunchPlan {
    private double exitVelocity;
    private double lunchAngle;
    private double time;

    private List<Point> trajectory;

    public LaunchPlan(double exitVelocity, double lunchAngle, List<Point> trajectory, double time) {
        this.exitVelocity = exitVelocity;
        this.lunchAngle = lunchAngle;
        this.time = time;
        this.trajectory = trajectory;
    }

    public double getExitVelocity() {
        return exitVelocity;
    }
    public double getTime() {
        return time;
    }

    public double getLunchAngle() {
        return lunchAngle;
    }

    public List<Point> getTrajectory() {
        return trajectory;
    }
}
