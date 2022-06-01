package me.guruguru19.trajectorygraphing.trajectory;

public class Target {
    private double mx;
    private double my;
    private double ma;

    public Target(double mx, double my, double ma) {
        this.mx = mx;
        this.my = my;
        this.ma = ma;
    }

    public double getMx() {
        return mx;
    }

    public double getMy() {
        return my;
    }

    public double getMa() {
        return ma;
    }

    public void setMx(double mx) {
        this.mx = mx;
    }

    public void setMy(double my) {
        this.my = my;
    }

    public void setMa(double ma) {
        this.ma = ma;
    }

    @Override
    public String toString() {
        return "Target{" +
                "mx=" + mx +
                ", my=" + my +
                ", ma=" + ma +
                '}';
    }
}
