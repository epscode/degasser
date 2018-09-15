package degasser;

import java.awt.Color;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class GradientButton extends JButton {
	public GradientButton(String label) {
		super(label);
		setContentAreaFilled(true);
		setFocusPainted(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(new GradientPaint(new Point(0, 0), 
				Color.WHITE, 
				new Point(0, getHeight() ), 
				Color.PINK.darker() ) );
		g2.fillRect(0, 0, getWidth(), getHeight() );
		g2.dispose();

		super.paintComponent(g);
	}
	
	public static GradientButton newInstance() {
        return new GradientButton("test");
    }
}