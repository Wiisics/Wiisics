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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by funstein on 08/01/15.
 */
class WiisicsHandler {
    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("locales.Displays", new Locale("tr")); //NON-NLS
    private final Display display;
    private Wiisics wiisics;
    private PhysicsProcessor physics;
    private WiiRemote wiiremote;

    private boolean harmonicMode;
    private boolean periodicalMode;
    private boolean demoMode;

    public WiisicsHandler() throws Exception {
        WiiRemoteJ.setConsoleLoggingAll();
        System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true"); //Fix for weird bug in BlueCove

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean IS_MAC = lcOSName.startsWith("mac os x"); //NON-NLS
        if (IS_MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Wiisics");
            Application.getApplication().setDockIconImage(ImageIO.read(Class.forName("wiisics.Wiisics").getResourceAsStream("Wiisics.png"))); //NON-NLS
        }

        this.display = new Display(this);
        display.setVisible(true);

        if (IS_MAC) {
            Application.getApplication().setAboutHandler(new AboutHandler() {
                @Override
                public void handleAbout(AppEvent.AboutEvent aboutEvent) {
                    new Dialog_About(display, true);
                }
            });
        }
    }

    public boolean setHarmonicMode(boolean b) {
        if (getWiisics() == null) {
            harmonicMode = b;
        }

        return harmonicMode;
    }

    public boolean getHarmonicMode() {
        return harmonicMode;
    }

    public boolean setPeriodicalMode(boolean b) {
        periodicalMode = false;
        /*if (getWiisics() == null) {
            periodicalMode = b;
        }*/

        return periodicalMode;
    }

    public boolean getPeriodicalMode() {
        return periodicalMode;
    }

    public boolean setDemoMode(boolean b) {
        demoMode = false;
        /*if (getWiisics() == null) {
            demoMode = b;
        }*/

        return demoMode;
    }

    public boolean getDemoMode() {
        return demoMode;
    }

    public Wiisics getWiisics() {
        return wiisics;
    }

    public Display getDisplay() {
        return display;
    }

    public PhysicsProcessor getPhysicsProcessor() {
        return physics;
    }

    public void disconnect() {
        if (wiiremote != null)
            wiiremote.disconnect();

        wiiremote = null;
        physics = null;
        display.refresh();
    }

    public void startConnect() {
        if (wiiremote == null) {
            String address = "";

            while (address.length() != 12) {
                Dialog_Initial dialog = new Dialog_Initial(display, true);
                address = dialog.getMACAddress();

                if (address == null)
                    return;
            }

            Dialog_Connecting connecting = new Dialog_Connecting(this.getDisplay(), false);
            connecting.setVisible(true);
            (new ConnectProcessor(this, address, connecting)).execute();
        }
    }

    public void connectResults(WiiRemote remote) {
        if (remote == null) {
            new Dialog_ConnectFail(display, true);
            startConnect();
        } else {
            new Dialog_ConnectPass(display, false);
            this.wiiremote = remote;
            if (harmonicMode) {
                this.physics = new HarmonicPhysicsProcessor();
            } else {
                this.physics = new PhysicsProcessor();
            }
            this.wiisics = new Wiisics(this);
            display.refresh();

            try {
                wiiremote.addWiiRemoteListener(wiisics);
                wiiremote.setAccelerometerEnabled(true);
                wiiremote.setSpeakerEnabled(true);
                wiiremote.setIRSensorEnabled(false, WRIREvent.BASIC);
                wiiremote.setLEDIlluminated(0, true);
            } catch (Exception e) {
            }

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
        if (wiisics != null && wiisics.running)
            return;

        JFileChooser fc = new JFileChooser();
        CSVFilter f1 = new CSVFilter();
        ImageFilter f2 = new ImageFilter();
        fc.addChoosableFileFilter(f1);
        fc.addChoosableFileFilter(f2);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(f1);
        int returnVal = fc.showSaveDialog(display);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (fc.getFileFilter() == f2) {
                if (!FilenameUtils.isExtension(file.getAbsolutePath(), "png")) { //NON-NLS
                    file = new File(file.getAbsolutePath() + ".png");
                }
                if (!wiisics.running) {
                    // Figure out the image height and width
                    int height = 400;
                    double millisecondWidth = height / (GraphPanel.GRAPH_TIMEFRAME * 1000.0);
                    int width = (int) (Math.abs(display.getGraphList().get(0).getTime() - display.getGraphList().get(display.getGraphList().size() - 1).getTime()) * millisecondWidth + 0.5);

                    // Draw the individual panels
                    GraphPanel[] panels = display.getPanels();
                    BufferedImage[] images = new BufferedImage[panels.length];
                    for (int i = 0; i < panels.length; i++) {
                        GraphPanel component = panels[i];
                        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = image.createGraphics();
                        Color oldColor = g.getColor();
                        Color superLightGray = new Color(200, 200, 200);
                        if (i % 2 == 1) {
                            g.setColor(superLightGray);
                            g.drawRect(0, 0, width, height);
                            g.fillRect(0, 0, width, height);
                            g.setColor(oldColor);
                        }
                        component.paintGraphPanel(g, width, height);
                        images[i] = image;
                        g.dispose();
                    }

                    // Join them now
                    int bigHeight = height * panels.length;
                    BufferedImage newImage = new BufferedImage(width, bigHeight, BufferedImage.TYPE_INT_ARGB);
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
                    }
                    g2.dispose();

                    try {
                        ImageIO.write(newImage, "png", file); //NON-NLS
                    } catch (IOException ex) {
                    }
                }

            } else {
                if (!FilenameUtils.isExtension(file.getAbsolutePath(), "csv")) { //NON-NLS
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                FileWriter fileWriter = null;
                CSVPrinter csvFilePrinter = null;
                CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader("timestamp", "aX", "aY", "aZ", "aMag", "vX", "vY", "vZ", "vMag", "sX", "sY", "sZ", "sMag").withRecordSeparator("\n"); //NON-NLS

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
                } catch (Exception e) {
                } finally {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                        csvFilePrinter.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


}
