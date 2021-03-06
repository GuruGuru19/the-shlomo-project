package me.guruguru19.trajectorygraphing;

import javafx.scene.image.ImageView;
import me.guruguru19.trajectorygraphing.gui.App;
import me.guruguru19.trajectorygraphing.trajectory.GraphDrawer;
import me.guruguru19.trajectorygraphing.vision.TargetDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.osgi.OpenCVNativeLoader;
import org.opencv.videoio.VideoCapture;

public class Main {


    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        System.out.println("OpenCV Version: " + Core.VERSION);
        App app = new App();

        OpenCVNativeLoader openCVNativeLoader = new OpenCVNativeLoader();
        openCVNativeLoader.init();// tested on opencv-4.5.1-2
        app.startApp(args);
    }
    //

    /**
     * starting the camera vision thread
     * @param cameraFeed
     * @param thresholdFeed
     */
    public static void vision(ImageView cameraFeed, ImageView thresholdFeed){
//        final JPanel cameraPanel = new JPanel();
//        TargetDetection.createJFrame(cameraPanel);
        new Thread(() -> {
            final VideoCapture camera = new VideoCapture(0);
            final Mat frame = new Mat();
            final Mat frame2 = new Mat();
            Mat processedFrame;
            while (true){
                camera.read(frame);
                camera.read(frame2);
                GraphDrawer.updateFrame(frame2);
                processedFrame = TargetDetection.processedFrame(frame);
                TargetDetection.markOuterContour(processedFrame,frame);
                TargetDetection.markMiddleOfContour(processedFrame,frame);
                TargetDetection.drawImage(frame, cameraFeed);
                TargetDetection.drawImage(processedFrame, thresholdFeed);
            }
        }).start();

    }
}
