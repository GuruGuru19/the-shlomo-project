package me.guruguru19.trajectorygraphing.trajectory;


import org.opencv.core.Point;

import java.util.ArrayList;

public class TrajectoryCalc {
    public enum CalcOperations {
        SET_TARGET_HIGHT,
        SET_TARGET_AREA,
        SET_TARGET
    }

    private static double targetHight = 1;
    private static double targetDistance = 1;
    private static double targetXOffSet = 0;
    private static double targetArea = 0;

    private static double cameraAngle = 0;
    private static double cameraHight = 0;
    private static double dragCoefficient = 0;//not Cd!!!! but 1/2*Cd*p*A
    private static double projectileMass = 0;
    private static double dt = 0.005;

    private static CalcOperations calcOperation = CalcOperations.SET_TARGET_HIGHT;

    private static final double g = 9.81;

    public static void setInitialState(double cameraAngle, double cameraHight, double dragCoefficient, double projectileMass, double dt, CalcOperations calcOperation, double targetHight, double targetDistance, double targetArea){
        TrajectoryCalc.cameraAngle = cameraAngle;
        TrajectoryCalc.cameraHight = cameraHight;
        TrajectoryCalc.dragCoefficient = dragCoefficient;
        TrajectoryCalc.projectileMass = projectileMass;
        TrajectoryCalc.dt = dt;

        TrajectoryCalc.calcOperation = calcOperation;

        TrajectoryCalc.targetHight = targetHight;
        TrajectoryCalc.targetDistance = targetDistance;
        TrajectoryCalc.targetArea = targetArea;
    }

    public static double getDt() {
        return dt;
    }

    public static double dragCoefficientCalc(double cross_sectional_area, double drag_coefficient, double density_of_fluid){
        return 0.5*cross_sectional_area*density_of_fluid*drag_coefficient;
    }

    public static LunchPlan calc(double tx, double ty, double targetArea){
        if (calcOperation == CalcOperations.SET_TARGET_HIGHT){
            if (dragCoefficient == 0){
                return calcHNoDrag(tx,ty);
            }
            return null;
        }
        if (calcOperation == CalcOperations.SET_TARGET_AREA){
            return calcA(tx,ty,targetArea);
        }
        return null;
    }

    private static LunchPlan calcA(double tx, double ty, double targetArea){




        return null;
    }

    private static LunchPlan calcHNoDrag(double tx, double ty){
        tx = 45;
        targetDistance = (targetHight-cameraAngle)/Math.tan(degToRad(cameraAngle+tx));
        double A = -((targetHight-cameraHight)/(targetDistance*targetDistance));
        double B = -2*A*targetDistance;
        double C = cameraHight;
        ArrayList<Point> trajectory = new ArrayList<>();
        for (double x = 0; x<=targetDistance; x+=dt){
            trajectory.add(new Point(x, A*x*x+B*x+C));
        }
        double a0 = radToDeg(Math.atan(B));
        double v0 = Math.sqrt((2*targetDistance*g)/(Math.sin(degToRad(2*a0))));

        return new LunchPlan(v0, a0, trajectory);
    }

    private static double degToRad(double v){
        return v*Math.PI/180;
    }

    private static double radToDeg(double v){
        return v*180/Math.PI;
    }
}
