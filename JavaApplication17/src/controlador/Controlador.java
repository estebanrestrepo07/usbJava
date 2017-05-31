/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import vista.usbFrame;

/**
 *
 * @author Developer
 */
public class Controlador implements ActionListener{
    usbFrame vistaUsb = new usbFrame();
    private Object javax;
    int mt[][];
    String volumenToFormat = null;
    String selected = "";
    ArrayList<String[]> matrix = new ArrayList<String[]>();
    String OS = System.getProperty("os.name").toLowerCase();
    public Controlador(usbFrame vistaUsb) {
        
        this.vistaUsb = vistaUsb;
        this.vistaUsb.formatearBtn.addActionListener(this);
        this.vistaUsb.subirBtn.addActionListener(this);
        this.vistaUsb.comboBoxUSBList.addActionListener(this);
        this.vistaUsb.actualizarBtn.addActionListener(this);
        this.vistaUsb.examinarBtn.addActionListener(this);
        
    }

    public Controlador() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public boolean isWindows(String OS){
        boolean isWindows = false;
        if(OS.indexOf("win") >= 0 ) {
            isWindows = true;
        }
        return isWindows;
    }
    public boolean isMac(String OS){
        boolean isMac = false;
        if(OS.indexOf("mac") >= 0 ) {
            isMac = true;
        }
        return isMac;
    }
    public boolean isLinux(String OS){
        boolean isLi = false;
        if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) {
            isLi = true;
        }
        return isLi;
    }
    public  ArrayList getDriveRunningOnWin(){
        String s = null;
        ArrayList root = new ArrayList();
        try {
            Process p = Runtime.getRuntime().exec("wmic logicaldisk where drivetype=2 get deviceid, volumename, filesystem");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            
            while ((s = stdInput.readLine()) != null) {
                String[] spliteado = s.trim().replaceAll(" +", " ").split(" ");
                System.out.println("split length: "+spliteado.length);
                if(spliteado.length>1){
                    
                    if (spliteado.length==2) {
                        spliteado = new String[]{spliteado[0],spliteado[1], " "};
                    }
                    matrix.add(spliteado);          
                }
            }
            
            for (int i = 1; i < matrix.size(); i++) {
                    root.add(matrix.get(i)[0]+" "+matrix.get(i)[2]);        
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return root;
    }
    public ArrayList getDriveRunningOnMacLin(){
        // Get the root of all the drives attached to the computer
        
        File[] roots = null;
//        if(isWindows(OS)){
//            roots = File.listRoots();
//        }
        if(isMac(OS)){
            File volumes = new File("/Volumes");  
            roots = volumes.listFiles();
        }
        if(isLinux(OS)){
            String username = System.getProperty("user.name");
            File media = new File("/media/"+username);  
            roots = media.listFiles();
        }
       
        // Get the actual location of the application
        
        // Loop through the roots and check if any match the start of the application's running path
        ArrayList root = new ArrayList();
        for(int i = 0; i < roots.length; i++){
            String tmp = "";
            if(roots[i].toString().contains(" ")){
                String[] ahora = roots[i].toString().split(" ");
                tmp = mountGrep(0, ahora[0]+"\\ "+ahora[1]);
            }else{
                tmp = mountGrep(0, roots[i].toString());
            }
            System.out.println("file: "+tmp);
            System.out.println(roots[i].toString());
            if("0".equals(tmp)){
                root.add(roots[i].toString());
                System.out.println(root.get(root.size()-1)+ " con el volumen "+roots[i].toString());  
            }
            
            //System.out.println(root);
//            if(getAppPath().startsWith(roots[i].toString())){
//                //String root = roots[i].toString();
//                return root;
//            }
        }
        
        // If the above loop doesn't find a match, just treat the folder the application is running in as the drive
        return root; 
    }
    
    @SuppressWarnings("empty-statement")
    public String mountGrep(int value, String VolumeName){
        String s = null;
        String error = null;
        String tmp = null;
        System.out.println("el volumeName es: "+VolumeName);
        if(VolumeName.contains(" ")){
            String[] volume = VolumeName.split(" ");
            System.out.println("volume: "+volume[0] + volume[0].charAt(volume[0].length()-1));
            if(!"\\".equals(volume[0].charAt(volume[0].length()-1)) && value == 0){
                VolumeName = volume[0]+" "+volume[1];
            }else{
                VolumeName = volume[0]+"\\ "+volume[1];                
            }
            
            System.out.println("veame aca por 2 : "+VolumeName);
        }
        String[] cmdResult = {"/bin/sh", "-c","mount | grep "+VolumeName};
        try {    
            Process p = Runtime.getRuntime().exec(cmdResult);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            
            
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            vistaUsb.infoText.setText("");
            while ((s = stdInput.readLine()) != null) {
                
                System.out.println(s);
                tmp = s;
            }
            
            if(value == 0){            
           
                // read any errors from the attempted command
                //System.out.println("Here is the standard error of the command (if any):\n");
                while ((error = stdError.readLine()) != null) {
                    System.out.println(error);
                }

                try {
                    p.waitFor();
                    System.out.println("el valor es: "+p.exitValue());
                    error = Integer.toString(p.exitValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(value == 0){
            return error;
        }else{
            return tmp;
        }
    }

    public int formatear(int value, String dev){
        String s = "";
        String[] cmdResult = new String[]{};
        Process p = null;
        try{
            if (value == 0) {
                String[] unidad; 
                unidad = dev.split(" ");
                String unidadaux = unidad[0];
                unidad = unidad[0].split("/");
                String unidadFin = unidadaux.substring(0,unidadaux.length()-2 );
                System.out.println(unidadFin);

                cmdResult = new String[]{"/bin/sh", "-c","diskutil partitionDisk "+ unidadFin +" 1 MBRFormat \"MS-DOS FAT32\" ACTYLUS 100%"};
            }else{
                cmdResult = new String[]{"CMD","/C", "format", "/FS:FAT32", dev, "/q", "/y"};
            }
            
            p = Runtime.getRuntime().exec(cmdResult);
            BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
            
            
            BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));
           vistaUsb.infoText.setText("");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                vistaUsb.infoText.setText(vistaUsb.infoText.getText()+"\n"+s);
                
            }
        
            
            // read any errors from the attempted command
            //System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
           try {
                p.waitFor();
                System.out.println("el valor es: "+p.exitValue());
                 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
               Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  p.exitValue();
    }
    public String whatFormatIs(int val,String volumen){
        
        String format = null;
        if (val == 0) {
            String[] unidad = volumen.split(" ");
            for(int i=0;i<unidad.length;i++){
                if(unidad[i].contains("(") && unidad[i].contains(",")){
                    format = unidad[i].replace("(", "").replace(",", "");
                }
            }
            System.out.println("format: "+format);
        }else{
            int i = 1;
            while(!volumen.equals(matrix.get(i)[0])){
                i++;
            }
            format= matrix.get(i)[1];
            System.out.println("formato "+ format);
        }
        
            
        return format;
    }
    
    @SuppressWarnings("empty-statement")
    public int copiararchivo(String sourceFile,String destinationFile) throws IOException{
        
        System.out.println("Desde: " + sourceFile);
        if(isWindows(OS))
            destinationFile = destinationFile.split(" ")[0];
        
        if(destinationFile.contains(" ")){
            
            String[] na = destinationFile.split(" ");
            destinationFile = "";
            for(int i=0;i<na.length;i++){
                destinationFile = destinationFile + na[i]+"\\ ";
            }
            destinationFile = destinationFile.substring(0, destinationFile.length()-2);
        }
        System.out.println("Hacia: " + destinationFile);
        String s = null;
        String error = null;
        String tmp = null;
        String[] cmdResult=new String[]{};
        if(isWindows(OS)){
            cmdResult = new String[]{"CMD","/C", "COPY", "/Y", sourceFile, destinationFile};
        }
        if(isMac(OS) || isLinux(OS)){
            cmdResult = new String[]{"/bin/sh", "-c","cp "+sourceFile+" "+destinationFile};
           System.out.println("por aqui pas�");
        }
        Process p = Runtime.getRuntime().exec(cmdResult);
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
            
            
            BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));
            
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            vistaUsb.infoText.setText("");
            while ((s = stdInput.readLine()) != null) {
                
                System.out.println(s);
                tmp = s;
               
                vistaUsb.infoText.setText(vistaUsb.infoText.getText()+"\n"+s);
            }
            
            
            // read any errors from the attempted command
            //System.out.println("Here is the standard error of the command (if any):\n");
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
                vistaUsb.infoText.setText(vistaUsb.infoText.getText()+"\n"+s);
            }
            
            try {
                p.waitFor();
                System.out.println("el valor es: "+p.exitValue());
                 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return p.exitValue();
    }
    public void changeName(String dev){
        String s = null,tmp=null,error=null;
        
        String[] cmdResult = new String[]{"CMD","/C", "label", dev+"ACTYLUS"}; 
        try {
            Process p = Runtime.getRuntime().exec(cmdResult);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            
            while ((s = stdInput.readLine()) != null) {
                System.out.println(""+s);
                vistaUsb.infoText.setText(vistaUsb.infoText.getText()+"\n"+s);
            }
            try {
                p.waitFor();
                System.out.println("el valor es: "+p.exitValue());
                if (tmp == null){
                    error = Integer.toString(p.exitValue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void refresh(){
            volumenToFormat = null;
           selected = "";
           vistaUsb.infoText.setText("");
           vistaUsb.subirBtn.setVisible(false);
           vistaUsb.jLabel1.setText("");
           vistaUsb.formatearBtn.setVisible(false);
           vistaUsb.comboBoxUSBList.removeAllItems();  
           vistaUsb.comboBoxUSBList.addItem("Select Option...");
           matrix.clear();
           ArrayList usb = new ArrayList();
            if(!isWindows(OS)){
                usb = getDriveRunningOnMacLin();
            }else{
                usb = getDriveRunningOnWin();
            }
             
            System.out.println("testeo: "+ usb);
            for (int i = 0; i<usb.size();i++){
                vistaUsb.comboBoxUSBList.addItem((String) usb.get(i));
            }
    }
   
    public void message(boolean d, String a){
        vistaUsb.actualizarBtn.setEnabled(d);
        vistaUsb.examinarBtn.setEnabled(d);
        vistaUsb.formatearBtn.setEnabled(d);
        vistaUsb.comboBoxUSBList.setEnabled(d);
        vistaUsb.infoLabel.setVisible(!d); 
        vistaUsb.infoLabel.setText(a);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       
       if(e.getSource() == vistaUsb.comboBoxUSBList){
           vistaUsb.infoText.setText("");
           vistaUsb.formatearBtn.setVisible(false);
           vistaUsb.subirBtn.setVisible(false);
           if(vistaUsb.comboBoxUSBList.getSelectedIndex() != 0){
               
               if (vistaUsb.comboBoxUSBList.getSelectedItem() != null) {
                   System.out.println(vistaUsb.comboBoxUSBList.getSelectedItem().toString());
                   selected = vistaUsb.comboBoxUSBList.getSelectedItem().toString();
                    
               }
               
                if(!"".equals(selected)){
                    if(!isWindows(OS)){
                        volumenToFormat = mountGrep(1, selected);
                        System.out.println("volumen a formatear"+volumenToFormat);
                        if (volumenToFormat == null) {
                             JOptionPane.showMessageDialog(null, "Select other option ");
                             vistaUsb.examinarBtn.setEnabled(false);
                             
                        } else {
                            vistaUsb.examinarBtn.setEnabled(true);
                             String format = whatFormatIs(0,volumenToFormat);
                            vistaUsb.infoText.setText("");
                            if("msdos".equals(format)){
                                vistaUsb.infoText.setVisible(true);
                                vistaUsb.infoText.setText("Choose a file, then upload the file in the selected USB driver\n Use the 'Upload File' button");
                                vistaUsb.formatearBtn.setVisible(false);
                                vistaUsb.subirBtn.setVisible(true);
                            }else{
                                vistaUsb.infoText.setVisible(true);
                                vistaUsb.infoText.setText("The selected USB driver is with a wrong format* \n Please format the selected USB driver \n You could use the 'Formar Driver' button \n\n *The Format must be MSDOS FAT32");
                                vistaUsb.formatearBtn.setVisible(true);
                                vistaUsb.subirBtn.setVisible(false);

                            }
                        }
                    }else{
                        volumenToFormat = selected.split(" ")[0];
                        String format = whatFormatIs(1,volumenToFormat);
                        
                        vistaUsb.infoText.setText("");
                        if("FAT32".equals(format)){
                            vistaUsb.infoText.setVisible(true);
                            vistaUsb.infoText.setText("Choose a file, then upload the file in the selected USB driver \n Use the 'Upload File' button");
                            vistaUsb.formatearBtn.setVisible(false);
                            vistaUsb.subirBtn.setVisible(true);
                            
                        }else{
                            vistaUsb.infoText.setVisible(true);
                            vistaUsb.infoText.setText("The selected USB driver is with a wrong format* \n Please format the selected USB driver \n You could use the 'Formar Driver' button \n\n *The Format must be FAT32");
                            vistaUsb.formatearBtn.setVisible(true);
                            vistaUsb.subirBtn.setVisible(false);
                        } 
                    }       
                }
            }   
        }
       if(e.getSource() == vistaUsb.formatearBtn){
           String text = "";
           int value = isWindows(OS) ? 1 : 0;
           int b = formatear(value, volumenToFormat); 
           if(isWindows(OS)){
               changeName(volumenToFormat);
           }
               
           if(b>1){
              text = "There was a problem, try again";
           }else{
               text = "Success!";
           }
           JOptionPane.showMessageDialog(null, text); vistaUsb.jLabel1.setText("");
              refresh();
       }
       if(e.getSource() == vistaUsb.subirBtn){
           try {
               if(vistaUsb.jLabel1.getText().length()>0){
                    int b = copiararchivo(vistaUsb.jLabel1.getText(), vistaUsb.comboBoxUSBList.getSelectedItem().toString());

                    String text = "";
                    if(b>0){
                        text = "There was a problem, try again";
                    }else{
                      text = "Success!";
                    }

                    JOptionPane.showMessageDialog(null, text); vistaUsb.jLabel1.setText("");
         
                }else{
                   JOptionPane.showMessageDialog(null, "Select a file");
               }
           } catch (IOException ex) {
               Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
               
           } 
       }
       if(e.getSource() == vistaUsb.actualizarBtn){
           refresh();
           
       }
       if(e.getSource() == vistaUsb.examinarBtn){
        JFileChooser fc = new JFileChooser();
        File file;
        
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION){
            file = fc.getSelectedFile();
            vistaUsb.jLabel1.setText(String.valueOf(file));

        }
       }
    }
}
