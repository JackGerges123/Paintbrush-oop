package paint;

import java.awt.*;
import javax.swing.*;


public class Paint {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Paint");

            Draw drawPanel = new Draw();

            JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
            toolbar.setFloatable(false);                 
            toolbar.setBackground(new Color(255, 105, 180)); 
            toolbar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        
            toolbar.add(drawPanel.rect);
            toolbar.add(drawPanel.oval);
            toolbar.add(drawPanel.line);
            toolbar.add(drawPanel.freehand);
           

            toolbar.addSeparator();   

        
            toolbar.add(drawPanel.red);
            toolbar.add(drawPanel.green);
            toolbar.add(drawPanel.blue);
             toolbar.add(drawPanel.filledCheckbox);

            toolbar.addSeparator();

          
            toolbar.add(drawPanel.eraser);
            toolbar.add(drawPanel.undo);
            toolbar.add(drawPanel.clearAll);

            toolbar.addSeparator();

        
            toolbar.add(drawPanel.openButton);
            toolbar.add(drawPanel.saveButton);

           
            Font btnFont = new Font("Arial", Font.BOLD, 10);
            for (Component c : toolbar.getComponents()) {
                c.setFont(btnFont);
                if (c instanceof JButton) {
                    ((JButton) c).setPreferredSize(new Dimension(95, 30));
                } else if (c instanceof JCheckBox) {
                    ((JCheckBox) c).setPreferredSize(new Dimension(95, 30));
                }
            }

      
            frame.setLayout(new BorderLayout());
            frame.add(toolbar, BorderLayout.NORTH);
            frame.add(drawPanel, BorderLayout.CENTER);

            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
