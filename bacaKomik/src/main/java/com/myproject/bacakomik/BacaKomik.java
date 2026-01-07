package com.myproject.bacakomik;

import com.myproject.view.MainView;
import javax.swing.SwingUtilities;

public class BacaKomik {
    public static void main(String[] args) {
  
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
 
            new MainView(); 
        });
    }
}