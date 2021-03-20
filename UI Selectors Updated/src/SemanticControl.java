import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 */

/**
 * @author stewartpowell
 *
 */
public abstract class SemanticControl {
    /** The set of all possible values */
    private final List<Object> allValues;
    /** The Current list of value selected */
    private final List<Object> currValue;
    /** The array of Instantiated Views for this Model */
    private final List<UiView> views;
    /** The SWT Display used for the UI presenter */
    private final Display display;
    /** The SWT Shell used for the UI presenter */
    private final Shell shell;

    /**
     * Constructor for Semantic Control
     * 
     * @param display the display used to display the presenters
     * @param shell   the shell used to output the Ui
     */
    public SemanticControl(Display display, Shell shell) {
        this.allValues = new LinkedList<>();
        this.currValue = new LinkedList<>();
        this.views = new LinkedList<>();
        this.display = display;
        this.shell = shell;
        this.shell.setLayout(new GridLayout(2, false));
        this.shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    }

    /**
     * Gets a list of the possible values
     * 
     * @return the list of possible values
     */
    public List<Object> getAllValues() {
        return allValues;
    }

    /**
     * Adds a value to the current list of values
     * 
     * @param value a value to add to the current Values
     */
    @SuppressWarnings("unchecked")
    public void addToAllValue(Object value) {
        this.getAllValues().add(value);
    }

    /**
     * Adds a list of values to the current list of values
     * 
     * @param values a value to add to the current Values
     */
    @SuppressWarnings("unchecked")
    public void addManyToAllValues(List<Object> values) {
        this.getAllValues().addAll(values);
    }

    /**
     * Adds a view to the current list of views
     * 
     * @param view   a value to add to the current Values
     * @param layout the layout of the presenter buttons
     * @param title  the title of the view, null if no title
     */
    public abstract void addView(Views view, boolean layout, String title);

    /**
     * Gets a list of all the model's views
     * 
     * @return the list of views for this model
     */
    public List<UiView> getViews() {
        return views;
    }

    /**
     * Gets the current value
     * 
     * @return the currValue or values
     */
    public List<Object> getCurrValue() {
        return currValue;
    }

    /**
     * Updates the View when the CurrValues of Model are changed
     */
    public void updateViews() {

        for (UiView view : views) {
            System.out.println("Updating: " + view);
            view.updateView(currValue);
        }
    }

    /**
     * Adds a value to the current list of values
     * 
     * @param value a value to add to the current Values
     */
    @SuppressWarnings("unchecked")
    public void addCurrValue(Object value) {
        this.getCurrValue().add(value);
    }

    /**
     * Adds a list of values to the current list of values
     * 
     * @param values a value to add to the current Values
     */
    @SuppressWarnings("unchecked")
    public void addCurrValues(List<Object> values) {
        this.getCurrValue().addAll(values);
    }

    /**
     * Gets the Display of the UI presenter
     * 
     * @return the display of the UI presenter
     */
    public Display getDisplay() {
        return display;
    }

    /**
     * Disposes of the display
     */
    public void disposeDisplay() {
        this.getDisplay().dispose();
    }

    /**
     * Gets the shell of the UI presenter
     * 
     * @return the shell of the UI presenter
     */
    public Shell getShell() {
        return shell;
    }

    /**
     * Sets the size of the shell to the given width and height
     * 
     * @param width  the width of the shell
     * @param height the height of the shell
     */
    public void setShellSize(int width, int height) {
        this.getShell().setSize(width, height);
    }

    /**
     * Sets a name of the Shell
     * 
     * @param text the name of the shell
     */
    public void setShellText(String text) {
        this.getShell().setText(text);
    }

    /**
     * Opens the Shell
     */
    public void openShell() {
        this.getShell().open();
    }
}
