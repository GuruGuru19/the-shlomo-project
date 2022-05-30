package me.guruguru19.trajectorygraphing;

import javafx.scene.image.ImageView;
import me.guruguru19.trajectorygraphing.ui.App;
import me.guruguru19.trajectorygraphing.vision.TargetDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;

public class Main {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
        System.out.println("OpenCV Version: " + Core.VERSION);
        App app = new App();

        app.startApp(args);
    }

    public static void vision(ImageView cameraFeed){
        final JPanel cameraPanel = new JPanel();
        TargetDetection.createJFrame(cameraPanel);
        new Thread(() -> {
            final VideoCapture camera = new VideoCapture(0);
            final Mat frame = new Mat();
            Mat processedFrame;
            while (true){
                camera.read(frame);

                processedFrame = TargetDetection.processedFrame(frame);
                TargetDetection.markOuterContour(processedFrame,frame);
                System.out.println(TargetDetection.markMiddleOfContour(processedFrame,frame));
                TargetDetection.drawImage(frame, cameraFeed);
                TargetDetection.drawImage(processedFrame, cameraPanel);
            }
        }).start();

    }
}
