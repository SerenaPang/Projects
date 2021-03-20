
 
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

 
public class TDemo {
 
    public static void main(String[] args) {
 
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("SWT Toggle Button");
 
        shell.setLayout(new GridLayout(4, true));
  
        Label label = new Label(shell, SWT.NONE);
        label.setText("Friends: ");
 
        Image iconSelect = MyImageUtils.getImage(display, "/button_image/t_on.jpg");
        Image iconDeselect = MyImageUtils.getImage(display, "/button_image/t_off.jpg");
 
        // Friends
        Button newButton = new Button(shell, SWT.TOGGLE);
       // appleButton.setText("Friends");
 
      //  appleButton.setImage(iconDeselect);
 
        newButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
 
                if (source.getSelection()) {
                    newButton.setImage(iconSelect);
                } else {
                    newButton.setImage(iconDeselect);
                }
 
            }
        });
 
        shell.setSize(400, 250);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
 
}