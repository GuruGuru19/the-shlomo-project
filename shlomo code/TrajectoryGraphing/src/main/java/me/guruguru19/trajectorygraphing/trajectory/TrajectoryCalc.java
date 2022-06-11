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
    private static double dt = 0.0005;

    private static CalcOperations calcOperation = CalcOperations.SET_TARGET_HIGHT;

    private static final double g = 9.81;

    private static String display_ballDiameter = "";
    private static String display_crossSectionalArea = "";
    private static String display_Cd = "";

    public static void setInitialState(double cameraAngle, double cameraHight, double dragCoefficient, double projectileMass, double dt, CalcOperations calcOperation, double targetHight, double targetDistance, double targetArea){
        TrajectoryCalc.cameraAngle = cameraAngle;
        TrajectoryCalc.cameraHight = cameraHight;
        TrajectoryCalc.dragCoefficient = dragCoefficient;
        System.out.println(dragCoefficient);
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

    public static LaunchPlan calc(double tx, double ty, double ta){
        System.out.println(calcOperation);
        System.out.println(targetDistance+", "+targetHight);
        correct(tx, ty, ta);
        if (dragCoefficient == 0){
            System.out.println("no drag");
            return calcNoDrag();
        }

        LaunchPlan p1 = calcWithDrag(10);
        LaunchPlan p2 = calcWithDrag(20);
        System.out.println("p1.getTime() = "+p1.getTime());
        System.out.println("p2.getTime() = "+p2.getTime());
        if (p1.getTime()<p2.getTime()){
            return p1;
        }
        return p2;
    }


    private static LaunchPlan calcWithDrag(double angleOffSet){
        double at = radToDeg(Math.atan((targetHight-cameraHight)/(targetDistance)));
        double a0 = at + angleOffSet;

        double t = 0;//time
        double a = 0;//heading angle
        double v0 = 0;//initial velocity

        double x = 0;//x
        double y = 0;//y
        double vx = 0;// x-axis velocity
        double vy = 0;// y-axis velocity
        double ax = 0;// x-axis acceleration
        double ay = 0;// y-axis acceleration



        double err = Double.POSITIVE_INFINITY;//error
        double bestErr = err;
        LaunchPlan bestTrajectory = new LaunchPlan(0,0,new ArrayList<>(), 0);
        double lastErr = err;

        ArrayList<Point> trajectory;


        while (v0 <= 10000 && t<=60){//setting the maximum time to 1 minute
            v0+=0.001;
            t=0;
            vx = v0 * Math.cos(degToRad(a0));
            vy = v0 * Math.sin(degToRad(a0));
            x=0;
            y=cameraHight;
            trajectory = new ArrayList<>();
            lastErr = err;
            while ((!(t!=0&&y<=0))&&x<targetDistance){
                trajectory.add(new Point(x,y));
                x+=vx*dt;
                y+=vy*dt;
                ax = -((vx*vx*dragCoefficient)/projectileMass);
                ay = -((vy*vy*dragCoefficient)/projectileMass)-g;
                vx += ax*dt;
                vy += ay*dt;
                t+=dt;

            }
            err = err(trajectory, targetDistance,targetHight);
            if (err<bestErr){
                bestErr =err;
                bestTrajectory = new LaunchPlan(v0, a0, trajectory, t);
            }
        }
        //System.out.println(bestTrajectory.hashCode());
        return bestTrajectory;
    }

    private static double pythagoreanTheorem(double v1, double v2){
        return Math.sqrt(v1*v1+v2*v2);
    }

    private static double err(ArrayList<Point> list, double x, double y){
        double min = Double.POSITIVE_INFINITY;
        for (Point p: list) {
            if (min > pythagoreanTheorem(p.x-x,p.y-y)){
                min = pythagoreanTheorem(p.x-x,p.y-y);
            }
        }
        return min;
    }

    private static LaunchPlan calcNoDrag(){

        double A = -((targetHight-cameraHight)/(targetDistance*targetDistance));
        double B = -2*A*targetDistance;
        double C = cameraHight;


        double a0 = radToDeg(Math.atan(B));
        double v0 = Math.sqrt((2*targetDistance*g)/(Math.sin(degToRad(2*a0))));

        ArrayList<Point> trajectory = new ArrayList<>();
        for (double x = 0; x<=targetDistance; x+=dt){
            trajectory.add(new Point(x, A*x*x+B*x+C));
        }
        if (A>=0){
            return new LaunchPlan(Double.NaN, a0, trajectory, -1);
        }
        return new LaunchPlan(v0, a0, trajectory, -1);
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
