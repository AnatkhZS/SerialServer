package ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class LimitedDocument extends PlainDocument{
	private JTextComponent textComponent;
    private int lineMax = 10;
    public   LimitedDocument(JTextComponent tc,int lineMax){     
        textComponent = tc;
        this.lineMax = lineMax;
    }
    public   LimitedDocument(JTextComponent tc){     
        textComponent = tc;
    }
    public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
        System.out.println("Enter insert");
        String value =   textComponent.getText();   
        int overrun = 0;
        if(value!=null && value.split("\n").length>=lineMax){
        	System.out.println("Enter remove");
            overrun = value.indexOf(' ')+1;
            super.remove(0, overrun);
        }
        super.insertString(offset-overrun,   s,   attributeSet);     
    }
}
