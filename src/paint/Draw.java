package paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Draw extends JPanel {

    private String currentShape = "";
    private boolean isFreeHand = false;
    private boolean isEraser = false;
    private boolean isFilled = false;

    Color color = Color.BLACK;

    int count = 0;
    Shape[] shapes = new Shape[1000];

    int startX, startY, endX, endY;
    
    private Shape tempShape = null;
    private boolean isDragging = false;

    // Our buttons 
    JButton rect = new JButton("RECT");
    JButton oval = new JButton("OVAL");
    JButton line = new JButton("LINE");
    JButton red = new JButton("RED");
    JButton green = new JButton("GREEN");
    JButton blue = new JButton("BLUE");
    JButton freehand = new JButton("FREE HAND");
    JButton eraser = new JButton("ERASER");
    JButton clearAll = new JButton("CLEAR ALL");
    JButton undo = new JButton("UNDO");
    JButton saveButton = new JButton("SAVE");
    JButton openButton = new JButton("OPEN");

    JCheckBox filledCheckbox = new JCheckBox("Filled");

    private BufferedImage loadedImage = null;

    public Draw() {

        MyListener m = new MyListener();
        this.addMouseListener(m);
        this.addMouseMotionListener(m);

        add(rect);
        add(oval);
        add(line);
        add(red);
        add(green);
        add(blue);
        add(freehand);
        add(eraser);
        add(clearAll);
        add(undo);
        add(saveButton);
        add(openButton);
        add(filledCheckbox);

        rect.addActionListener(e -> {
            currentShape = "RECT";
            isFreeHand = false;
            isEraser = false;
        });

        oval.addActionListener(e -> {
            currentShape = "OVAL";
            isFreeHand = false;
            isEraser = false;
        });

        line.addActionListener(e -> {
            currentShape = "LINE";
            isFreeHand = false;
            isEraser = false;
        });

        red.addActionListener(e -> color = Color.RED);
        green.addActionListener(e -> color = Color.GREEN);
        blue.addActionListener(e -> color = Color.BLUE);

        freehand.addActionListener(e -> {
            currentShape = "FREEHAND";
            isFreeHand = true;
            isEraser = false;
        });

        eraser.addActionListener(e -> {
            currentShape = "ERASER";
            isFreeHand = true;
            isEraser = true;
        });

        clearAll.addActionListener(e -> {
            count = 0;
            loadedImage = null;
            repaint();
        });

        undo.addActionListener(e -> {
            if (count > 0) {
                count--;
                repaint();
            }
        });

        filledCheckbox.addActionListener(e -> {
            isFilled = filledCheckbox.isSelected();
        });

        saveButton.addActionListener(e -> saveDrawing());

        openButton.addActionListener(e -> openImage());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);

        if (loadedImage != null) {
            g.drawImage(loadedImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        for (int i = 0; i < count; i++) {
            shapes[i].draw(g);
        }

        if (isDragging && tempShape != null) { 
            tempShape.draw(g); 
        }
    }

    class MyListener implements MouseListener, MouseMotionListener {

        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();

            if (isFreeHand) {
                shapes[count++] = new Line(startX, startY, startX, startY, isEraser ? Color.WHITE : color);
                repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {
            endX = e.getX();
            endY = e.getY();

            if (!isFreeHand && tempShape != null) { // done
                shapes[count++] = tempShape; // done
                tempShape = null; // done
                isDragging = false; // done
                repaint(); // done
            }
        }

        public void mouseDragged(MouseEvent e) {
            endX = e.getX();
            endY = e.getY();

            if (isFreeHand) {
                if (isEraser) {
                    shapes[count++] = new ThickLine(startX, startY, endX, endY, Color.WHITE);
                } else {
                    shapes[count++] = new Line(startX, startY, endX, endY, color);
                }
                startX = endX;
                startY = endY;
                repaint();
            } else {
                switch (currentShape) { 
                    case "RECT": 
                        tempShape = new Rect(startX, startY, endX, endY, color, isFilled); 
                        break; 
                    case "OVAL": 
                        tempShape = new Oval(startX, startY, endX, endY, color, isFilled); 
                        break; 
                    case "LINE": 
                        tempShape = new Line(startX, startY, endX, endY, color); 
                        break; 
                } 
                isDragging = true; 
                repaint(); 
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    interface Shape {
        void draw(Graphics g);
    }

    class Rect implements Shape {
        int x1, y1, x2, y2;
        Color c;
        boolean filled;

        public Rect(int x1, int y1, int x2, int y2, Color c, boolean filled) {
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.abs(x2 - x1);
            this.y2 = Math.abs(y2 - y1);
            this.c = c;
            this.filled = filled;
        }

        public void draw(Graphics g) {
            g.setColor(c);
            if (filled) {
                g.fillRect(x1, y1, x2, y2);
            } else {
                g.drawRect(x1, y1, x2, y2);
            }
        }
    }

    class Oval implements Shape {
        int x1, y1, x2, y2;
        Color c;
        boolean filled;

        public Oval(int x1, int y1, int x2, int y2, Color c, boolean filled) {
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.abs(x2 - x1);
            this.y2 = Math.abs(y2 - y1);
            this.c = c;
            this.filled = filled;
        }

        public void draw(Graphics g) {
            g.setColor(c);
            if (filled) {
                g.fillOval(x1, y1, x2, y2);
            } else {
                g.drawOval(x1, y1, x2, y2);
            }
        }
    }

    class Line implements Shape {
        int x1, y1, x2, y2;
        Color c;

        public Line(int x1, int y1, int x2, int y2, Color c) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.c = c;
        }

        public void draw(Graphics g) {
            g.setColor(c);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    class ThickLine implements Shape {
        int x1, y1, x2, y2;
        Color c;

        public ThickLine(int x1, int y1, int x2, int y2, Color c) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.c = c;
        }

        public void draw(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(c);
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(10));
            g2.drawLine(x1, y1, x2, y2);
            g2.setStroke(oldStroke);
        }
    }

    private void saveDrawing() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        this.paint(g2);
        g2.dispose();

        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Images", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg");
        FileNameExtensionFilter gifFilter = new FileNameExtensionFilter("GIF Images", "gif");

        chooser.addChoosableFileFilter(pngFilter);
        chooser.addChoosableFileFilter(jpgFilter);
        chooser.addChoosableFileFilter(gifFilter);
        chooser.setFileFilter(pngFilter);

        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            String ext = "";
            FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) chooser.getFileFilter();

            if (selectedFilter == pngFilter) {
                ext = "png";
            } else if (selectedFilter == jpgFilter) {
                ext = "jpg";
            } else if (selectedFilter == gifFilter) {
                ext = "gif";
            } else {
                if (path.toLowerCase().endsWith(".png")) {
                    ext = "png";
                } else if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg")) {
                    ext = "jpg";
                } else if (path.toLowerCase().endsWith(".gif")) {
                    ext = "gif";
                } else {
                    ext = "png";
                }
            }

            if (!path.toLowerCase().endsWith("." + ext)) {
                file = new File(path + "." + ext);
            }

            try {
                if (ext.equals("jpg") || ext.equals("gif")) {
                    BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = rgbImage.createGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
                    g.drawImage(image, 0, 0, null);
                    g.dispose();

                    ImageIO.write(rgbImage, ext, file);
                } else {
                    ImageIO.write(image, ext, file);
                }

                JOptionPane.showMessageDialog(this, "Image saved successfully as " + ext.toUpperCase() + "!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage());
            }
        }
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files (JPG, PNG, GIF,)", "jpg", "jpeg", "png", "gif");
        chooser.setFileFilter(filter);

        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                loadedImage = ImageIO.read(file);
                count = 0;
                repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        }
    }
}
