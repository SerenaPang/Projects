

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
 
public class OnOffButtonDemo {
 
    public static void main(String[] args) {
 
        Display display = new Display();
        Shell shell = new Shell(display);
        
        shell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
        shell.setBackgroundMode(SWT.INHERIT_FORCE);
        
        shell.setText("selected groups can see your posts, you can also deselect the groups to hide your posts from that group");
 
        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.marginLeft = 10;
        rowLayout.marginTop = 10;
        rowLayout.spacing = 15;
        shell.setLayout(rowLayout);
 
        // Composite
        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new RowLayout(SWT.HORIZONTAL));
 
        Label label = new Label(composite, SWT.NONE);
        label.setText("select who CAN see my posts: ");
 
        // Followers
        Button toggleFollowers = new Button(composite, SWT.TOGGLE);
        toggleFollowers.setText("Followers");
 
        // My Friends
        Button toggleFriends = new Button(composite, SWT.TOGGLE);
        toggleFriends.setText("My Friends");
 
        // Follows
        Button toggleFollows = new Button(composite, SWT.TOGGLE);
        toggleFollows.setText("Follows");
        
        // Strangers
        Button toggleStrangers = new Button(composite, SWT.TOGGLE);
        toggleStrangers.setText("Strangers");
 
        Label labelAnswer = new Label(shell, SWT.NONE);
        labelAnswer.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
 
        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
 
                if (source.getSelection()) {
                    labelAnswer.setText(source.getText() + " CAN see your posts");
                    labelAnswer.pack();
                } else {
                    labelAnswer.setText(source.getText() + " CAN NOT see your posts");
                    labelAnswer.pack();
                }
 
            }
        };
 
        toggleFollowers.addSelectionListener(selectionListener); 
        toggleFriends.addSelectionListener(selectionListener);
        toggleFollows.addSelectionListener(selectionListener);
        toggleStrangers.addSelectionListener(selectionListener);
        
        shell.setSize(400, 250);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
 
}
