package wiisics;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.intel.bluetooth.BlueCoveConfigProperties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRIREvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by funstein on 08/01/15.
 */
public class WiisicsHandler {
    public Wiisics getWiisics() {
        return wiisics;
    }

    public Display getDisplay() {
        return display;
    }

    public PhysicsProcessor getPhysicsProcessor() {
        return physics;
    }

    public WiiRemote getWiiremote() {
        return wiiremote;
    }

    public void disconnect() {
        if (wiiremote != null)
            wiiremote.disconnect();

        wiiremote = null;
        physics = null;
        display.refresh();
    }

    private Wiisics wiisics;
    private Display display;
    private PhysicsProcessor physics;
    private WiiRemote wiiremote;
    public Thread finderThread;
    private final WiisicsHandler handler;

    public WiisicsHandler() throws Exception {
        handler = this;
        WiiRemoteJ.setConsoleLoggingAll();
        System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true"); //Fix for weird bug in BlueCove

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean IS_MAC = lcOSName.startsWith("mac os x");
        if (IS_MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wiisics");
            Application.getApplication().setDockIconImage(ImageIO.read(Class.forName("wiisics.Wiisics").getResourceAsStream("Wiisics.png")));
            Application.getApplication().setAboutHandler(new AboutHandler() {
                @Override
                public void handleAbout(AppEvent.AboutEvent aboutEvent) {
                    Dialog_About dialog = new Dialog_About(handler.getDisplay(), true);
                }
            });
        }

        this.display = new Display(this);
        display.setVisible(true);
    }

    public void startConnect() {
        if (wiiremote == null) {
            System.out.println("test1");
            String address = "";

            while (address.length() != 12) {
                Dialog_Initial dialog = new Dialog_Initial(display, true);
                address = dialog.getMACAddress();

                if (address == null)
                    return;
            }

            Dialog_Connecting connecting = new Dialog_Connecting(this.getDisplay(), false, this);
            int errors = 0;
            WiiRemote wr = null;
            while (wr == null) {
                if (errors >= 50) {
                    System.out.println("Could not connect to the device.");
                    connecting.dispose();
                    findFailed();
                    return;
                }
                try {
                    wr = WiiRemoteJ.connectToRemote(address); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                    connecting.dispose();
                    connectResults(wr);
                    return;
                } catch (Exception e) {
                    wr = null;
                    //e.printStackTrace();
                    System.out.println("Failed to connect remote, trying again: Attempt " + (errors + 1) + ".");
                    errors++;
                }
            }
            /*if (finderThread == null)
                finderThread = new Thread(new ConnectProcessor(this, address, connecting));
            if (!finderThread.isAlive())
                finderThread.run();*/
        }
    }

    public void findFailed() {
        try {
            finderThread.join();
        } catch (Exception e) {}
        handler.connectResults(null);
    }

    public void connectResults(WiiRemote remote) {
        if (remote == null) {
            Dialog_ConnectFail failed = new Dialog_ConnectFail(display, true);
            startConnect();
        } else {
            /*try {
                finderThread.join();
            } catch (Exception ignore) {}
            Debugger.println("Connected!");*/
            System.out.println("test2");
            this.wiiremote = remote;
            this.physics = new PhysicsProcessor();
            this.wiisics = new Wiisics(this);
            display.refresh();

            try {
                wiiremote.addWiiRemoteListener(wiisics);
                wiiremote.setAccelerometerEnabled(true);
                wiiremote.setSpeakerEnabled(true);
                wiiremote.setIRSensorEnabled(false, WRIREvent.BASIC);
                wiiremote.setLEDIlluminated(0, true);
            } catch (Exception e) {}

            final WiisicsHandler handler = this;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.disconnect();
                }
            }));
        }
    }

    public void start() {
        if (wiisics != null)
            wiisics.running = true;
    }

    public void stop() {
        if (wiisics != null) {
            wiisics.running = false;
            display.getGraphList().add(new GraphDot(System.currentTimeMillis()));
        }
    }

    public void reset() {
        if (wiisics != null) {
            display.reset();
            physics = new PhysicsProcessor();
        }
    }

    public void save() {
        if (wiisics != null) {
            JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new CSVFilter());
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);
            int returnVal = fc.showSaveDialog(display);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (FilenameUtils.isExtension(file.getAbsolutePath(), "png")) {
                    if (!wiisics.running) {
                        System.out.println("Exporting image");

                        // Figure out the image height and width
                        int height = 400;
                        double millisecondWidth = 400.0 / (GraphPanel.GRAPH_TIMEFRAME * 1000);
                        int width = (int) (Math.abs(display.getGraphList().getFirst().getTime() - display.getGraphList().getLast().getTime()) * millisecondWidth + 0.5);

                        // Draw the individual panels
                        GraphPanel[] panels = display.getPanels();
                        BufferedImage[] images = new BufferedImage[panels.length];
                        for (int i = 0; i < panels.length; i++) {
                            GraphPanel component = panels[i];
                            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                            Graphics g = image.createGraphics();
                            component.paintGraphPanel(g, width, height);
                            images[i] = image;
                        }

                        // Join them now
                        int bigHeight = height * panels.length;
                        BufferedImage newImage = new BufferedImage(width,bigHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = newImage.createGraphics();
                        Color oldColor = g2.getColor();
                        //fill background
                        g2.setPaint(Color.WHITE);
                        g2.fillRect(0, 0, width, bigHeight);
                        //draw image
                        g2.setColor(oldColor);
                        for (int i = 0; i < images.length; i++) {
                            BufferedImage img = images[i];
                            g2.drawImage(img, null, 0, i * height);
                            g2.setColor(Color.GREEN);
                            g2.drawLine(0, i*height + 1, width, i*height + 1);
                            g2.setColor(oldColor);
                        }
                        g2.dispose();

                        try {
                            ImageIO.write(newImage, "png", file);
                        } catch (IOException ex) {
                            System.out.println("Could not save image.");
                        }
                    }

                } else {
                    System.out.println("Exporting CSV");

                    if (!FilenameUtils.isExtension(file.getAbsolutePath(), "csv")) {
                        file = new File(file.getAbsolutePath()+".csv");
                    }

                    FileWriter fileWriter = null;
                    CSVPrinter csvFilePrinter = null;
                    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("timestamp", "aX", "aY", "aZ", "aMag", "vX", "vY", "vZ", "vMag", "sX", "sY", "sZ", "sMag").withRecordSeparator("\n");

                    try {
                        fileWriter = new FileWriter(file);
                        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

                        for (GraphDot dot : display.getGraphList()) {
                            if (!dot.isPause()) {
                                List list = new ArrayList();
                                list.add(dot.getTime());
                                double[][] arr = dot.getValue();
                                for (int i = arr.length - 1; i >= 0; i--) {
                                    for (int j = 0; j < arr[i].length; j++) {
                                        list.add(arr[i][j]);
                                    }
                                }
                                csvFilePrinter.printRecord(list);
                            }
                        }
                        System.out.println("CSV file was created successfully !!!");

                    } catch (Exception e) {
                        System.out.println("Error in CsvFileWriter !!!");
                    } finally {
                        try {
                            fileWriter.flush();
                            fileWriter.close();
                            csvFilePrinter.close();
                        } catch (IOException e) {
                            System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                        }
                    }
                }
            }
        }
    }
}
