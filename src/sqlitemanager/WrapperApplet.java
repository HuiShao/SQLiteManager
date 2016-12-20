/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlitemanager;

import java.applet.Applet;

/**
 *
 * @author Shawn
 */
public class WrapperApplet extends Applet {

    public void start() {
       new Thread("application main Thread") {
          public void run() { runApplication(); }
       }.start();
    }

    private void runApplication() {
       mainFrame.main(new String[0]);
    }

}
