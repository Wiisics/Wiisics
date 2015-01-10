package wiisics;

import com.intel.bluetooth.BlueCoveConfigProperties;
import java.awt.*;
import javax.swing.*;
import wiiremotej.*;
import wiiremotej.event.*;


public class Wiisics extends WiiRemoteAdapter {
    private WiiRemote remote;
    private static PhysicsProcessor physics;
    private static Display display;

    public Wiisics(WiiRemote remote, Display display) {
        this.remote = remote;
        physics = new PhysicsProcessor();
        this.display = display;
    }

    @Override
    public void disconnected() {
        System.out.println("Remote disconnected... Goodbye!");
        System.exit(0);
    }

    @Override
    public void accelerationInputReceived(WRAccelerationEvent evt) {
        Debugger.println("Acceleration input received");
        physics.update(evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration(), evt.getPitch(), evt.getRoll());

        display.update(physics.getTime(), physics.getDisplacement(), physics.getVelocity(), physics.getAcceleration());
    }

}