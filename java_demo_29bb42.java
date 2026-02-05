// This tutorial teaches how to procedurally generate fractal patterns in Java using recursion and Java2D.
// We will focus on the concept of recursion and how it can be used to create self-similar geometric shapes.
// The goal is to understand how a simple recursive function can lead to complex and beautiful visual patterns.

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Our main class that sets up the JFrame and the drawing panel.
public class FractalGenerator extends JFrame {

    // Constructor for our fractal window.
    public FractalGenerator() {
        // Set the title of the window.
        setTitle("Recursive Fractal Generator");
        // Set the default close operation to exit the application when the window is closed.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add our custom JPanel which will do the actual drawing.
        add(new FractalPanel());
        // Set the size of the window.
        setSize(800, 600);
        // Center the window on the screen.
        setLocationRelativeTo(null);
    }

    // The main method where our application starts.
    public static void main(String[] args) {
        // Swing applications should be run on the Event Dispatch Thread (EDT).
        // SwingUtilities.invokeLater ensures our GUI creation happens on the EDT.
        SwingUtilities.invokeLater(() -> {
            // Create an instance of our FractalGenerator window.
            FractalGenerator frame = new FractalGenerator();
            // Make the window visible.
            frame.setVisible(true);
        });
    }
}

// This JPanel is responsible for drawing the fractal.
class FractalPanel extends JPanel {

    // The main drawing method that Java calls when the panel needs to be painted.
    @Override
    protected void paintComponent(Graphics g) {
        // Call the superclass's paintComponent to ensure proper Swing painting behavior.
        super.paintComponent(g);

        // Cast the Graphics object to Graphics2D to access more advanced drawing features.
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother lines and curves.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the background color of the panel.
        g2d.setColor(Color.BLACK);
        // Fill the entire panel with the background color.
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Set the drawing color for our fractal.
        g2d.setColor(Color.WHITE);
        // Set the stroke for drawing lines. We're using a thin line here.
        g2d.setStroke(new BasicStroke(1.0f));

        // Define the starting point and length for our initial fractal element.
        // We'll start drawing from the center of the panel, downwards, with a specific length.
        int startX = getWidth() / 2;
        int startY = getHeight() - 50; // A bit from the bottom
        int initialLength = 150;

        // Start the recursive drawing process.
        // This is where the magic of recursion happens!
        // We pass the Graphics2D object, the starting point (x, y), the initial direction (angle in radians - 0 is right, PI/2 is up), and the initial length.
        drawFractal(g2d, startX, startY, Math.toRadians(90), initialLength); // 90 degrees is straight up
    }

    // The recursive function to draw the fractal.
    // This function calls itself to create smaller, self-similar versions of the pattern.
    private void drawFractal(Graphics2D g2d, int x1, int y1, double angle, int length) {
        // Base Case: This is the condition that stops the recursion.
        // If the length is too small, we stop drawing to prevent infinite recursion and tiny, invisible lines.
        if (length < 5) {
            return; // Stop recursion
        }

        // Calculate the end point of the current line segment based on the starting point, angle, and length.
        // 'cos' and 'sin' are used to determine the change in x and y coordinates based on the angle.
        // Note: In Java2D, the y-axis increases downwards.
        int x2 = (int) (x1 + length * Math.cos(angle));
        int y2 = (int) (y1 - length * Math.sin(angle)); // Subtract for upward movement

        // Draw the current line segment.
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));

        // Recursive Step: Here, we call the drawFractal function again to create new branches.
        // We're creating two new branches, each shorter and at a different angle.

        // Branch 1:
        // The new starting point is the end point of the current line (x2, y2).
        // The new angle is the current angle plus a deviation (e.g., 30 degrees or PI/6 radians).
        // The new length is a fraction of the current length (e.g., 2/3 or 0.66).
        double angle1 = angle + Math.toRadians(30); // Turn right by 30 degrees
        int length1 = (int) (length * 0.66); // Reduce length by 1/3
        drawFractal(g2d, x2, y2, angle1, length1);

        // Branch 2:
        // Similar to Branch 1, but with a different angle deviation.
        double angle2 = angle - Math.toRadians(30); // Turn left by 30 degrees
        int length2 = (int) (length * 0.66); // Reduce length by 1/3
        drawFractal(g2d, x2, y2, angle2, length2);
    }
}