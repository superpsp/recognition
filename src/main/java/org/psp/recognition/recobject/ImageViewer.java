package org.psp.recognition.recobject;

import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.image.DataBufferByte;

public class ImageViewer {
    final static Logger LOG = LoggerFactory.getLogger(ImageViewer.class.getName());
    private JLabel jLabel;

    public void show(Mat mat, String windowName){
        setSystemLookAndFeel();
        JFrame frame = createJFrame(windowName);
        Image loadedImage = toBufferedImage(mat);
        jLabel.setIcon(new ImageIcon(loadedImage));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createJFrame(String windowName) {
        JFrame frame = new JFrame(windowName);
        jLabel = new JLabel();
        final JScrollPane imageScrollPane = new JScrollPane(jLabel);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        frame.add(imageScrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    public Image toBufferedImage(Mat matrix){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( matrix.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
        byte [] buffer = new byte[bufferSize];
        matrix.get(0,0,buffer); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(),matrix.
                rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().
                getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}
