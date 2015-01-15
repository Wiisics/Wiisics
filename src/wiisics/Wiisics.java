package wiisics;

import com.intel.bluetooth.BlueCoveConfigProperties;
import java.awt.*;
import javax.swing.*;
import wiiremotej.*;
import wiiremotej.event.*;


public class Wiisics extends WiiRemoteAdapter {
    private WiisicsHandler handler;
    public boolean running;
    private double[] calibratedData;
    private boolean calibrating;
    private Calibrator calibrator;

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
                System.out.println("Adding calibration data");
                calibrator.addData(evt.getPitch(), evt.getRoll(), data);
            }
        }
        else if (running) {
            if (calibratedData.length == 5) {
                PhysicsProcessor physics = handler.getPhysicsProcessor();
                Debugger.println("Acceleration input received");

                physics.update(evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration(), calibratedData);
                handler.getDisplay().update(physics.getTime(), physics.getDisplacement(), physics.getVelocity(), physics.getAcceleration());
            } else {
                System.out.println("Missing calibration");
            }
        }
    }

    public void calibrate() {
        if (!calibrating) {
            calibrating = true;
            System.out.println("Start calibration");
        }
    }

    public void calibrationResults(double[] results) {
        calibrator = null;
        calibrating = false;

        calibratedData = results;
        handler.getDisplay().refresh();
    }

    public double[] getCalibratedData() {
        return calibratedData;
    }
}