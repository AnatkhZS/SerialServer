package zht.title;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/** 
 * Title PopupMenu 
 * 
 * @author zhangtao 
 * 
 * Msn & Mail: zht_dream@hotmail.com 
 */
public class ZHTPopupMenu extends JPopupMenu { 
  
    public static final int POSITION_TOP = 1; 
    public static final int POSITION_BOTTOM = 2; 
    public static final int POSITION_LEFT = 3; 
    public static final int POSITION_RIGHT = 4; 
  
    private int titleSize = 30; 
    private Color titleColor = Color.BLACK; 
    private Color titleFillColor = Color.WHITE; 
    private boolean titleGradient = true; 
    private Color titleGradientColor = Color.BLACK; 
    private Font titleFont = new Font("Dialog", Font.BOLD, 12); 
    private int titlePosition = POSITION_LEFT; 
    private int titleGap = 5; 
    private boolean titleInCenter = true; 
  
    public ZHTPopupMenu() { 
        super(); 
    } 
  
    public ZHTPopupMenu(String label) { 
        super(label); 
    } 
  
    public Insets getInsets() { 
        Insets insets = (Insets) super.getInsets().clone(); 
        switch (titlePosition) { 
        case POSITION_TOP: 
            insets.top += titleSize; 
            break; 
        case POSITION_BOTTOM: 
            insets.bottom += titleSize; 
            break; 
        case POSITION_LEFT: 
            insets.left += titleSize; 
            break; 
        case POSITION_RIGHT: 
            insets.right += titleSize; 
            break; 
        } 
        return insets; 
    } 
  
    protected void paintComponent(Graphics g) { 
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D) g.create(); 
        Dimension size = this.getSize(); 
        GradientPaint paint; 
        g2d.setColor(titleFillColor); 
        String text = this.getLabel(); 
        int len = 0;//text.length(); 
        if (text != null && text.trim().length() > 0) { 
            len = text.length(); 
        } 
        boolean hasEN = false; 
        for (int i = 0; i < len; i++) { 
            if (isEN(text.charAt(i))) { 
                hasEN = true; 
                break; 
            } 
        } 
        JLabel label = new JLabel(text); 
        label.setFont(titleFont); 
        label.setForeground(titleColor); 
        Dimension labelSize = label.getPreferredSize(); 
        switch (titlePosition) { 
        case POSITION_TOP: { 
            if (titleGradient) { 
                paint = new GradientPaint(0, 0, titleFillColor, size.width, titleSize, titleGradientColor); 
                g2d.setPaint(paint); 
            } 
            g2d.fillRect(0, 0, size.width, titleSize); 
            if (len != 0) { 
                if (titleInCenter) { 
                    SwingUtilities.paintComponent(g2d, label, this, size.width / 2 - labelSize.width / 2, titleSize / 2 - labelSize.height / 2, labelSize.width, labelSize.height); 
                } else { 
                    SwingUtilities.paintComponent(g2d, label, this, titleGap, titleGap, labelSize.width, labelSize.height); 
                } 
            } 
        } 
            break; 
        case POSITION_BOTTOM: { 
            if (titleGradient) { 
                paint = new GradientPaint(0, size.height - titleSize, titleFillColor, size.width, size.height, titleGradientColor); 
                g2d.setPaint(paint); 
            } 
            g2d.fillRect(0, size.height - titleSize, size.width, titleSize); 
            if (len != 0) { 
                if (titleInCenter) { 
                    SwingUtilities.paintComponent(g2d, label, this, size.width / 2 - labelSize.width / 2, size.height - titleSize / 2 - labelSize.height / 2, labelSize.width, 
                            labelSize.height); 
  
                } else { 
                    SwingUtilities.paintComponent(g2d, label, this, titleGap, size.height - titleGap - labelSize.height, labelSize.width, labelSize.height); 
                } 
            } 
        } 
            break; 
        case POSITION_LEFT: { 
            if (titleGradient) { 
                paint = new GradientPaint(0, 0, titleFillColor, titleSize, size.height, titleGradientColor); 
                g2d.setPaint(paint); 
            } 
            g2d.fillRect(0, 0, titleSize, size.height); 
            if (len != 0) { 
                if (hasEN) { 
                    if (titleInCenter) { 
                        g2d.rotate(90 * Math.PI / 180, titleSize / 2 - labelSize.height / 2, size.height / 2 - labelSize.width / 2); 
                        SwingUtilities.paintComponent(g2d, label, this, titleSize / 2 - labelSize.height / 2, size.height / 2 - labelSize.width / 2 - labelSize.height, 
                                labelSize.width, labelSize.height); 
                    } else { 
                        g2d.rotate(90 * Math.PI / 180, titleGap, titleGap); 
                        SwingUtilities.paintComponent(g2d, label, this, titleGap, titleGap - labelSize.height, labelSize.width, labelSize.height); 
                    } 
                } else { 
                    int sum = 0; 
                    if (titleInCenter) { 
                        for (int i = 0; i < len; i++) { 
                            label.setText(text.charAt(i) + ""); 
                            labelSize = label.getPreferredSize(); 
                            sum += labelSize.height; 
                        } 
                    } 
                    sum = size.height / 2 - sum / 2; 
                    for (int i = 0; i < len; i++) { 
                        label.setText(text.charAt(i) + ""); 
                        labelSize = label.getPreferredSize(); 
                        SwingUtilities.paintComponent(g2d, label, this, titleSize / 2 - labelSize.width / 2, sum, labelSize.width, labelSize.height); 
                        sum += labelSize.height; 
                    } 
                } 
            } 
        } 
            break; 
        case POSITION_RIGHT: { 
            if (titleGradient) { 
                paint = new GradientPaint(size.width - titleSize, 0, titleFillColor, size.width, size.height, titleGradientColor); 
                g2d.setPaint(paint); 
            } 
            g2d.fillRect(size.width - titleSize, 0, titleSize, size.height); 
            if (len != 0) { 
                if (hasEN) { 
                    if (titleInCenter) { 
                        g2d.rotate(90 * Math.PI / 180, size.width - titleSize / 2 - labelSize.height / 2, size.height / 2 - labelSize.width / 2); 
                        SwingUtilities.paintComponent(g2d, label, this, size.width - titleSize / 2 - labelSize.height / 2, 
                                size.height / 2 - labelSize.width / 2 - labelSize.height, labelSize.width, labelSize.height); 
                    } else { 
                        g2d.rotate(90 * Math.PI / 180, size.width - titleSize + titleGap, titleGap); 
                        SwingUtilities.paintComponent(g2d, label, this, size.width - titleSize + titleGap, titleGap - labelSize.height, labelSize.width, labelSize.height); 
                    } 
                } else { 
                    int sum = 0; 
                    if (titleInCenter) { 
                        for (int i = 0; i < len; i++) { 
                            label.setText(text.charAt(i) + ""); 
                            labelSize = label.getPreferredSize(); 
                            sum += labelSize.height; 
                        } 
                    } 
                    sum = size.height / 2 - sum / 2; 
                    for (int i = 0; i < len; i++) { 
                        label.setText(text.charAt(i) + ""); 
                        labelSize = label.getPreferredSize(); 
                        SwingUtilities.paintComponent(g2d, label, this, size.width - titleSize / 2 - labelSize.width / 2, sum, labelSize.width, labelSize.height); 
                        sum += labelSize.height; 
                    } 
                } 
            } 
        } 
            break; 
        } 
    } 
  
    public static boolean isEN(char cn) { 
        byte[] bytes = (String.valueOf(cn)).getBytes(); 
        if (bytes == null || bytes.length > 2 || bytes.length <= 0) { 
            return false; 
        } 
        if (bytes.length == 1) { // en 
            return true; 
        } 
        if (bytes.length == 2) { // cn 
            return false; 
        } 
        return false; 
    } 
  
    public int getTitleSize() { 
        return titleSize; 
    } 
  
    public void setTitleSize(int titleSize) { 
        this.titleSize = titleSize; 
        this.repaint(); 
    } 
  
    public Color getTitleColor() { 
        return titleColor; 
    } 
  
    public void setTitleColor(Color titleColor) { 
        this.titleColor = titleColor; 
        this.repaint(); 
    } 
  
    public Color getTitleFillColor() { 
        return titleFillColor; 
    } 
  
    public void setTitleFillColor(Color titleFillColor) { 
        this.titleFillColor = titleFillColor; 
        this.repaint(); 
    } 
  
    public boolean isTitleGradient() { 
        return titleGradient; 
    } 
  
    public void setTitleGradient(boolean titleGradient) { 
        this.titleGradient = titleGradient; 
        this.repaint(); 
    } 
  
    public Color getTitleGradientColor() { 
        return titleGradientColor; 
    } 
  
    public void setTitleGradientColor(Color titleGradientColor) { 
        this.titleGradientColor = titleGradientColor; 
        this.repaint(); 
    } 
  
    public Font getTitleFont() { 
        return titleFont; 
    } 
  
    public void setTitleFont(Font titleFont) { 
        this.titleFont = titleFont; 
        this.repaint(); 
    } 
  
    public int getTitlePosition() { 
        return titlePosition; 
    } 
  
    public void setTitlePosition(int titlePosition) { 
        this.titlePosition = titlePosition; 
        this.repaint(); 
    } 
  
    public int getTitleGap() { 
        return titleGap; 
    } 
  
    public void setTitleGap(int titleGap) { 
        this.titleGap = titleGap; 
        this.repaint(); 
    } 
  
    public boolean isTitleInCenter() { 
        return titleInCenter; 
    } 
  
    public void setTitleInCenter(boolean titleCenter) { 
        this.titleInCenter = titleCenter; 
        this.repaint(); 
    } 
}