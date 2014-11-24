/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

/**
 *
 * @author funstein
 */
public class Debugger {
    public static final boolean DEBUG_MODE = false;
    
    public static void println(Object o) {
        if (DEBUG_MODE)
            System.out.println(o);
    }
}
