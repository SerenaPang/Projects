import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 */

/**
 * @author stewartpowell Class used to launch and run the Ui components of the
 *         UiToolkit
 */
public class UiRunner {
    /** The display used with the UI components */
    private final Display display;
    /** The Shell used with the Ui Components */
    private final Shell shell;

    /**
     * Initializes the UiRunner
     * 
     * @param display the display used with all the Ui Components
     * @param shell   the Shell used with all the Ui components
     */
    public UiRunner(Display display, Shell shell) {
        this.display = display;
        this.shell = shell;
    }

    /**
     * Opens the Shell and displays the Presenters until exited
     */
    public void launchUi() {
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
