package me.guruguru19.trajectorygraphing.trajectory;


import org.opencv.core.Point;

import java.util.ArrayList;

public class TrajectoryCalc {
    public enum CalcOperations {
        SET_TARGET_HIGHT,
        SET_TARGET_AREA,
        SET_TARGET
    }

    private static double targetHight = 0;
    private static double targetDistance = -1;
    private static double targetXOffSet = 0;
    private static double targetArea = 0;

    private static double cameraAngle = 0;
    private static double cameraHight = 0;
    private static double dragCoefficient = 0;//not Cd!!!! but 1/2*Cd*p*A
    private static double projectileMass = 0;
    private static double dt = 0.005;

    private static CalcOperations calcOperation = CalcOperations.SET_TARGET_HIGHT;

    private static final double g = 9.81;

    private static String display_ballDiameter = "";
    private static String display_crossSectionalArea = "";
    private static String display_Cd = "";

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

    public static LunchPlan calc(double tx, double ty, double ta){
        System.out.println(calcOperation);
        System.out.println(targetDistance+", "+targetHight);
        if (dragCoefficient == 0){
            System.out.println("no drag");
            return calcNoDrag(tx, ty, ta);
        }
        return calcWithDrag(tx, ty, ta);
    }

    private static LunchPlan calcWithDrag(double tx, double ty, double ta){




        return null;
    }

    private static LunchPlan calcNoDrag(double tx, double ty, double ta){
        correct(tx, ty, ta);
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

    public static void correct(double tx, double ty, double ta){
        if (calcOperation == CalcOperations.SET_TARGET_HIGHT){
            targetDistance = (targetHight-cameraAngle)/Math.tan(degToRad(cameraAngle+tx));
        }
        if (calcOperation == CalcOperations.SET_TARGET_AREA){
            //TODO: calc
        }
    }

    private static double degToRad(double v){
        return v*Math.PI/180;
    }

    private static double radToDeg(double v){
        return v*180/Math.PI;
    }

    public static double getTargetHight() {
        return targetHight;
    }

    public static double getTargetDistance() {
        return targetDistance;
    }

    public static double getTargetXOffSet() {
        return targetXOffSet;
    }

    public static double getTargetArea() {
        return targetArea;
    }

    public static double getCameraAngle() {
        return cameraAngle;
    }

    public static double getCameraHight() {
        return cameraHight;
    }

    public static double getDragCoefficient() {
        return dragCoefficient;
    }

    public static double getProjectileMass() {
        return projectileMass;
    }

    public static CalcOperations getCalcOperation() {
        return calcOperation;
    }


    public static void setTargetHight(double targetHight) {
        TrajectoryCalc.targetHight = targetHight;
    }

    public static void setDisplay_ballDiameter(String m_ballDiameter) {
        TrajectoryCalc.display_ballDiameter = m_ballDiameter;
    }

    public static void setDisplay_crossSectionalArea(String m_crossSectionalArea) {
        TrajectoryCalc.display_crossSectionalArea = m_crossSectionalArea;
    }

    public static void setDisplay_Cd(String m_Cd) {
        TrajectoryCalc.display_Cd = m_Cd;
    }

    public static String getDisplay_ballDiameter() {
        return display_ballDiameter;
    }

    public static String getDisplay_crossSectionalArea() {
        return display_crossSectionalArea;
    }

    public static String getDisplay_Cd() {
        return display_Cd;
    }

}
