package me.guruguru19.targetvision.util;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TargetDetection {

    private static final int HUE_HIGH = 80;
    private static final int HUE_LOW = 44;

    private static final int SATURATION_HIGH = 255;
    private static final int SATURATION_LOW = 40;

    private static final int VALUE_HIGH = 255;
    private static final int VALUE_LOW = 52;

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
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        return erodedFrame;
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

                    final boolean isNotNoise = area > 1000;

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
                new Scalar(124, 252, 0), // Green color
                1
        );
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
}
