import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Testing Features of SWT
 * 
 * @author stewartpowell
 */
public class SwtTest {
    @SuppressWarnings("javadoc")
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        final ScrolledComposite c1 = new ScrolledComposite(shell, SWT.V_SCROLL);
        c1.setLayout(new GridLayout(1, false));

        // c1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
        final Composite composite = new Composite(c1, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
        Label label = new Label(composite, SWT.NONE);
        label.setText("Test Scroll");
        // label.pack();
        /*
         * for (int i = 0; i < 20; i++) { Button button = new Button(composite,
         * SWT.PUSH); button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
         * false, false)); button.setText("button" + i); }
         */

        // table.setLinesVisible(true);
        // table.setHeaderVisible(true);
        for (int i = 0; i < 20; i++) {
            Button button = new Button(composite, SWT.CHECK);
            button.setLayoutData(new GridData(SWT.BEGINNING, 0, false, false));
            button.setText("button" + i);
        }
        /*
         * for (int i = 0; i < table.getColumnCount(); i++) { table.getColumn(i).pack();
         * }
         */
        // composite.pack();
        c1.setExpandHorizontal(true);
        c1.setExpandVertical(true);
        c1.setContent(composite);
        c1.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
