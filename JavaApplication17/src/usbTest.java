
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.usbFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import controlador.Controlador;
import java.util.ArrayList;
import java.util.Arrays;
import vista.usbFrame;
/**
 *
 * @author Developer
 */
public class usbTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
            // TODO code application logic here
//        usbFrame vistaNew = null;
//        try {
//            vistaNew = new usbFrame();
//        } catch (IOException ex) {
//            Logger.getLogger(usbTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        vistaNew.setVisible(true);
        usbFrame vistaUsb = new usbFrame();
        ArrayList usbs = new ArrayList();
        String OS = System.getProperty("os.name").toLowerCase();
        Controlador controlUsb = new Controlador(vistaUsb);
        if(!controlUsb.isWindows(OS)){
            usbs = controlUsb.getDriveRunningOnMacLin();
        }else{
            usbs = controlUsb.getDriveRunningOnWin();
        }
        System.out.println("testeo: "+ usbs);
        for (int i = 0; i<usbs.size();i++){
            vistaUsb.comboBoxUSBList.addItem((String) usbs.get(i));
        }
        
        vistaUsb.formatearBtn.setVisible(false);
        vistaUsb.subirBtn.setVisible(false);
        

        vistaUsb.setVisible(true);
        
    }
    
}
