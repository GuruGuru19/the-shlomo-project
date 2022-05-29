//made by GuruGuru19 (Itai) at 28.5.2022


package me.guruguru19.targetvision;

import me.guruguru19.targetvision.util.TargetDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;

public class Main {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {

        System.out.println("OpenCV Version: " + Core.VERSION);

        final JPanel cameraFeed = new JPanel();
        final JPanel processedFeed = new JPanel();
        TargetDetection.createJFrame(cameraFeed, processedFeed);

        final VideoCapture camera = new VideoCapture(0);

        Main.startTargetDetection(cameraFeed, processedFeed, camera).run();
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new run(args);
//            }
//        });
    }
    private static Runnable startTargetDetection(final JPanel cameraFeed,
                                                 final JPanel processedFeed,
                                                 final VideoCapture camera){
        return ()->{
            final Mat frame = new Mat();
            Mat processedFrame = new Mat();
            while (true){
                camera.read(frame);
                processedFrame = TargetDetection.processedFrame(frame);
                TargetDetection.drawImage(processedFrame, processedFeed);
                TargetDetection.markOuterContour(processedFrame,frame);
                System.out.println(TargetDetection.markMiddleOfContour(processedFrame,frame));
                TargetDetection.drawImage(frame, cameraFeed);
            }
        };
    }
}
