package wiisics;

import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WiiRemoteAdapter;

import javax.swing.*;


public class Wiisics extends WiiRemoteAdapter {
    private final WiisicsHandler handler;
    public boolean running;
    private double[] calibratedData;
    private boolean calibrating;
    private Calibrator calibrator;
    private JDialog calibratingDialog;

    public Wiisics(WiisicsHandler handler) {
        this.handler = handler;
        running = false;
        calibratedData = new double[0];
        calibrating = false;
    }

    @Override
    public void accelerationInputReceived(WRAccelerationEvent evt) {
        if (calibrating) {
            double[] data = new double[]{evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration()};
            if (calibrator == null) {
                calibrator = new Calibrator(handler, evt.getPitch(), evt.getRoll(), data);
            } else {
                calibrator.addData(evt.getPitch(), evt.getRoll(), data);
            }
        } else if (running) {
            if (calibratedData.length == 5) {
                PhysicsProcessor physics = handler.getPhysicsProcessor();
                Debugger.println("Acceleration input received");

                physics.update(evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration(), calibratedData, evt.isStill());
                handler.getDisplay().update(physics.getTime(), physics.getDisplacement(), physics.getVelocity(), physics.getAcceleration());
            } else {
                running = false;
            }
        }
    }

    public void calibrate() {
        if (!calibrating) {
            calibrating = true;
            if (calibratingDialog == null) {
                calibratingDialog = new Dialog_Calibrating(handler.getDisplay(), false);
                calibratingDialog.setVisible(true);
            }
        }
    }

    public void calibrationResults(double[] results) {
        calibrator = null;
        calibrating = false;
        if (calibratingDialog != null) {
            calibratingDialog.dispose();
            calibratingDialog = null;
        }

        if (results.length == 5) {
            new Dialog_CalibratePass(handler.getDisplay(), false);
        } else {
            new Dialog_CalibrateFail(handler.getDisplay(), false);
        }

        calibratedData = results;
        handler.getDisplay().refresh();
    }

    public double[] getCalibratedData() {
        return calibratedData;
    }
}