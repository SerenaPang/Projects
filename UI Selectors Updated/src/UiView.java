import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Button;

/**
 * All UiViews must contain a controller
 * 
 * @author stewartpowell
 *
 */
public abstract class UiView {

    /** This list of one or many buttons */
    private final List<Button> buttons;
    /** The model that contains the shell and display */
    private final SemanticControl model;
    /** The current number of buttons of the presenter */
    private int numButtons;
    /** The type of view */
    private final Views type;

    /**
     * Constructor of the UiView
     * 
     * @param model the model containing the shell of the view
     * @param type  the type of view
     */
    public UiView(SemanticControl model, Views type) {
        this.buttons = new LinkedList<>();
        this.model = model;
        // model.getShell().setLayout(new GridLayout(2, false));
        // model.getShell().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.type = type;
        this.numButtons = 0;
    }

    /**
     * Gets the list of buttons
     * 
     * @return the list of buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * Gets the model associated with this view
     * 
     * @return the model associated with this view
     */
    public SemanticControl getModel() {
        return model;
    }

    /**
     * Sets the title of the group of buttons
     * 
     * @param title the title of the group of check boxes
     */
    public abstract void setGroupTitle(String title);

    /**
     * Helper function to see if a button already exists
     * 
     * @param label the label attach to the button
     * @return the button if it exists, or null otherwise
     */
    public Button buttonExists(String label) {
        Button exists = null;
        for (Button button : getButtons()) {
            if (button.getText().equals(label)) {
                button.setVisible(true);
                exists = button;
                break;
            }
        }
        return exists;
    }

    /**
     * Adds a button to the presenter
     * 
     * @param label the label attached to the button
     */
    public abstract void addButton(String label);

    /**
     * Removes a button from the presenter. (Instead of disposing, the button
     * visibility is set to false, with the ability to reuse the button later)
     * 
     * @param label the label attached to the button to be removed
     */
    public abstract void removeButton(String label);

    /**
     * Adds many button to the presenter
     * 
     * @param labels the list of labels to attach to the buttons
     */
    public abstract void addManyButtons(List<Object> labels);
    
    /**
     * Updates the View when the CurrValues of Model are changed
     * 
     * @param currValues the list of current values of the model
     */
    public abstract void updateView(List<Object> currValues);

    /**
     * Gets the type of view
     * 
     * @return the type of view
     */
    public Views getType() {
        return type;
    }

    /**
     * Gets the number of buttons
     * 
     * @return the current number of buttons
     */
    public int getNumButtons() {
        return numButtons;
    }

    /**
     * Sets the number of buttons
     * 
     * @param i the new number of boxes
     */
    public void setNumButtons(int i) {
        numButtons = i;
    }

}
