package me.guruguru19.trajectorygraphing.vision;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import me.guruguru19.trajectorygraphing.trajectory.Target;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TargetDetection {

    private static int HUE_HIGH = 88;
    private static int HUE_LOW = 44;

    private static int SATURATION_HIGH = 255;
    private static int SATURATION_LOW = 40;

    private static int VALUE_HIGH = 255;
    private static int VALUE_LOW = 52;

    private static final int AREA_FILTER = 500;

    private static int CAMERA_FOV_X = 16;
    private static int CAMERA_FOV_Y = 0;

    public static void setColor(int hHigh, int hLow, int sHigh, int sLow, int vHigh, int vLow) {
        System.out.println("old color HIGH("+HUE_HIGH+", "+SATURATION_HIGH+", "+VALUE_HIGH+") -> LOW("+HUE_LOW+", "+SATURATION_LOW+", "+VALUE_LOW+")");

        HUE_HIGH = hHigh;
        HUE_LOW = hLow;

        SATURATION_HIGH = sHigh;
        SATURATION_LOW = sLow;

        VALUE_HIGH = vHigh;
        VALUE_LOW = vLow;

        System.out.println("new color HIGH("+hHigh+", "+sHigh+", "+vHigh+") -> LOW("+hLow+", "+sLow+", "+vLow+")");
    }

    public static void setCameraFovX(int cameraFovX) {
        CAMERA_FOV_X = cameraFovX;
    }

    public static void setCameraFovY(int cameraFovY) {
        CAMERA_FOV_Y = cameraFovY;
    }

    public static void createJFrame(final JPanel... panels){
        final JFrame window = new JFrame("Shape Detection");
        window.setSize(new Dimension(panels.length*640, 480));
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(new GridLayout(1, panels.length));

        for (final JPanel panel : panels){
            window.add(panel);
        }

        window.setVisible(true);
    }

    public static Mat processedFrame(final Mat frame) {

        final Mat hsvFrame = new Mat(frame.height(), frame.width(), frame.type());
        // Switch from RGB to GRAY
        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_RGB2HSV);

        final Mat thresholdFrame = new Mat(frame.height(), frame.width(), frame.type());
        Core.inRange(hsvFrame,
                new Scalar(HUE_LOW,SATURATION_LOW,VALUE_LOW),
                new Scalar(HUE_HIGH,SATURATION_HIGH,VALUE_HIGH),
                thresholdFrame);

        final Mat erodedFrame = new Mat(frame.height(), frame.width(), frame.type());
        Imgproc.erode(thresholdFrame, erodedFrame, new Mat(),new Point(-1,-1));
        final Mat dilatedFrame = new Mat(frame.height(), frame.width(), frame.type());
        Imgproc.dilate(erodedFrame, dilatedFrame, new Mat(), new Point(-1,-1));

        return dilatedFrame;
    }

    public static void markOuterContour(final Mat processedImage,
                                        final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(
                processedImage,
                allContours,
                new Mat(processedImage.size(), processedImage.type()),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
        );

        // Filter out noise and display contour area value
        final List<MatOfPoint> filteredContours = allContours.stream()
                .filter(contour -> {
                    final double area = Imgproc.contourArea(contour);
                    final Rect rect = Imgproc.boundingRect(contour);

                    final boolean isNotNoise = area > AREA_FILTER;

                    if (isNotNoise) {
                        Imgproc.putText (
                                originalImage,
                                "Area: " + (int) area,
                                new Point(rect.x + rect.width, rect.y + rect.height),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );

                        MatOfPoint2f dst = new MatOfPoint2f();
                        contour.convertTo(dst, CvType.CV_32F);
                        Imgproc.approxPolyDP(dst, dst, 0.02 * Imgproc.arcLength(dst, true), true);
                        Imgproc.putText (
                                originalImage,
                                "Points: " + dst.toArray().length,
                                new Point(rect.x + rect.width, rect.y + rect.height + 15),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );
                    }

                    return isNotNoise;
                }).collect(Collectors.toList());

        // Mark contours
        Imgproc.drawContours(
                originalImage,
                filteredContours,
                -1, // Negative value indicates that we want to draw all of contours
                new Scalar(176, 76, 255), // purple color
                1
        );
    }


    public static Target markMiddleOfContour(final Mat processedImage,
                                             final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(
                processedImage,
                allContours,
                new Mat(processedImage.size(), processedImage.type()),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
        );
        AtomicReference<MatOfPoint> maxContour = new AtomicReference<MatOfPoint>();
        AtomicInteger maxContourArea = new AtomicInteger();
        // Filter out noise and display contour area value
        final List<MatOfPoint> filteredContours = allContours.stream()
                .filter(contour -> {
                    final double area = Imgproc.contourArea(contour);
                    final Rect rect = Imgproc.boundingRect(contour);

                    final boolean isNotNoise = area > AREA_FILTER;

                    if (isNotNoise) {
                        if ((int) area > maxContourArea.get()){
                            maxContourArea.set((int) area);
                            maxContour.set(contour);
                        }

                        Point centerP = getCenterOfMass(contour);
                        Point center = convertToDeg(getCenterOfMass(contour),originalImage);
                        Imgproc.putText (
                                originalImage,
                                "mx: " + center.x,
                                new Point(rect.x - rect.width, rect.y),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );

                        MatOfPoint2f dst = new MatOfPoint2f();
                        contour.convertTo(dst, CvType.CV_32F);
                        Imgproc.approxPolyDP(dst, dst, 0.02 * Imgproc.arcLength(dst, true), true);
                        Imgproc.putText (
                                originalImage,
                                "my: " + center.y,
                                new Point(rect.x - rect.width, rect.y + 15),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );
                        Imgproc.drawMarker(originalImage, centerP,new Scalar(255,140,0),Imgproc.MARKER_STAR,(int)(Math.sqrt(area)/10),2);
                    }

                    return isNotNoise;
                }).collect(Collectors.toList());
        if (maxContour.get() != null){
            Point p = convertToDeg(getCenterOfMass(maxContour.get()),originalImage);
            return new Target(p.x, p.y, Imgproc.contourArea(maxContour.get()));
        }
        return new Target(-180,-180, 0);
    }

    private static Point getCenterOfMass(MatOfPoint contour) {
        Moments moments = Imgproc.moments(contour);
        return new Point(moments.m10 / moments.m00, moments.m01 / moments.m00);
    }

    private static Point convertToDeg(Point point, Mat frame){
        double xp = point.x-frame.width()/2.0;
        double yp = -point.y+frame.height()/2.0;
        return new Point((xp/(frame.width()/2.0))*(CAMERA_FOV_X/2.0), (yp/(frame.height()/2.0))*(CAMERA_FOV_Y/2.0));
        //return new Point(xp,yp);
    }

    public static void drawImage(final Mat mat, final ImageView cameraFrame) {
        // Get buffered image from mat frame

        WritableImage showFrame = SwingFXUtils.toFXImage(convertMatToBufferedImage(mat), null);
        // Draw image to panel
        cameraFrame.setImage(showFrame);
    }

    public static void drawImage(final Mat mat, final JPanel panel) {
        // Get buffered image from mat frame
        final BufferedImage image = TargetDetection.convertMatToBufferedImage(mat);

        // Draw image to panel
        final Graphics graphics = panel.getGraphics();
        graphics.drawImage(image, 0, 0, panel);
    }

    private static BufferedImage convertMatToBufferedImage(final Mat mat) {
        // Create buffered image
        final BufferedImage bufferedImage = new BufferedImage(
                mat.width(),
                mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR
        );

        // Write data to image
        final WritableRaster raster = bufferedImage.getRaster();
        final DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0, 0, dataBuffer.getData());

        return bufferedImage;
    }

    public static void setFrame(Mat mat){
        final BufferedImage bufferedImage = new BufferedImage(
                mat.width(),
                mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR
        );
        WritableImage showFrame = SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
