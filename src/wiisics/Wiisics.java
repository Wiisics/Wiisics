package wiisics;

import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WiiRemoteAdapter;

import javax.swing.*;
import java.util.TimerTask;


public class Wiisics extends WiiRemoteAdapter {
    private final WiisicsHandler handler;
    public boolean running;
    private double[] calibratedData;
    private boolean calibrating;
    private Calibrator calibrator;
    private JDialog calibratingDialog;
    //public Timer timer;

    public Wiisics(WiisicsHandler handler) {
        this.handler = handler;
        running = false;
        calibratedData = new double[0];
        calibrating = false;
    }

    @Override
    public void accelerationInputReceived(WRAccelerationEvent evt) {
        performMath(evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration(), evt.getRoll(), evt.getPitch());
    }

    public void performMath(double aX, double aY, double aZ, double roll, double pitch) {
        if (calibrating) {
            double[] data = new double[]{aX, aY, aZ};
            if (calibrator == null) {
                calibrator = new Calibrator(handler, pitch, roll, data);
            } else {
                calibrator.addData(pitch, roll, data);
            }
        } else if (running) {
            if (calibratedData.length == 5) {
                PhysicsProcessor physics = handler.getPhysicsProcessor();

                physics.update(aX, aY, aZ, calibratedData);
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