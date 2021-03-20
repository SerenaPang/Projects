
  
 
 import org.eclipse.swt.SWT;
 import org.eclipse.swt.layout.*;
 import org.eclipse.swt.widgets.*;

 public class checkBoxDemo {
   public static void main(String[] args) {
     Display display = new Display();
     Shell shell = new Shell(display);
     shell.setLayout(new GridLayout(4, true));
     
     shell.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
     shell.setBackgroundMode(SWT.INHERIT_FORCE);

     
     shell.setText("selected groups can see your posts, you can also deselect the groups to hide your posts from that group");
  

     // Create check boxes
     new Button(shell, SWT.CHECK).setText("Follows");
     new Button(shell, SWT.CHECK).setText("Followers");
     new Button(shell, SWT.CHECK).setText("My Friends");
     new Button(shell, SWT.CHECK).setText("Strangers");

     shell.pack();
     shell.open();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     display.dispose();
   }
 }