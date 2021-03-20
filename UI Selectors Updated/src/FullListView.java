
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * 
 */

/**
 * @author Serena Pang
 */
public class FullListView extends UiView {
    /** The group of buttons in the scrollable group */
    private final Composite buttonGroup;
    /** The name of the group of check boxes */
    private Label groupLabel;

    /**
     * Constructor for the Full List View class
     * 
     * @param model the model associated with the shell and display
     * @param type  the type of view
     */
    public FullListView(SemanticControl model, Views type) {
        super(model, type);
        // model.getShell().setLayout(new GridLayout(2, false));
        // model.getShell().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.buttonGroup = new Composite(model.getShell(), SWT.NONE);
        buttonGroup.setLayout(new GridLayout(1, false));
        buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.groupLabel = new Label(buttonGroup, SWT.NONE);
        initViews();
    }

    /**
     * Initializes the views of the controller
     */
    public void initViews() {
        if ((getType() == Views.DOUBLE) && this instanceof FullListView) {
            // System.out.println("Not for this view.");
        } else {
            for (Object value : getModel().getAllValues()) {
                if (value != null) {
                    addButton((String) value);
                }
            }
        }
    }

    @Override
    public void addButton(String label) {
        Button newButton = buttonExists(label);
        if (newButton == null) {
            // System.out.println("New Visible: " + label);
            newButton = new Button(getButtonGroup(), SWT.CHECK);
            newButton.setText(label);
            newButton.setVisible(true);
            newButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button source = (Button) e.getSource();
                    if (source.getSelection()) {
                        getModel().getCurrValue().add(source.getText());
                    } else {
                        getModel().getCurrValue().remove(source.getText());
                    }
                    getModel().updateViews();
                }
            });
            this.getButtons().add(newButton);
            this.setNumButtons(getNumButtons() + 1);
        } else {
            newButton.setVisible(true);
            System.out.println("Now Visible: " + newButton.getText());
            for (Control child : getButtonGroup().getChildren()) {
                if (child instanceof Button && !child.isVisible()) {
                    child.moveBelow(newButton);
                }
            }

        }
        getButtonGroup().update();
    }

    /**
     * Adds a new button to the presenter
     * 
     * @param button the new button to add
     */
    public void addButton(Button button) {
        // System.out.println("Adding button");
        if ((button.getStyle() & SWT.CHECK) != SWT.CHECK) {
            System.out.println("Invalid Button type: " + button.getStyle());
        } else {
            Button newButton = buttonExists(button.getText());
            if (newButton == null) {
                getButtons().add(button);
                // System.out.println("Adding new new button");
                for (Control child : getButtonGroup().getChildren()) {
                    if (child instanceof Button && child.isVisible()) {
                        child.moveBelow(button);
                    }
                }
                this.setNumButtons(getNumButtons() + 1);
            } else {
                // System.out.println("Adding new button");
                newButton.setVisible(button.isVisible());
                newButton.setSelection(button.getSelection());
                newButton.setGrayed(button.getGrayed());
                newButton.setEnabled(button.getEnabled());
                for (Control child : getButtonGroup().getChildren()) {
                    if (child instanceof Button && !child.isVisible()) {
                        child.moveBelow(newButton);
                    }
                }
            }
            getButtonGroup().update();
        }
        // this.getModel().updateViews();
    }

    @Override
    public void addManyButtons(List<Object> labels) {
        for (int i = 0; i < labels.size(); i++) {
            addButton((String) labels.get(i));
        }
    }

    @Override
    public void removeButton(String label) {
        for (Button button : getButtons()) {
            if (button.getText().equals(label) && button != null) {
                button.setVisible(false);

                for (Control child : getButtonGroup().getChildren()) {
                    if (child instanceof Button && child.isVisible()) {
                        child.moveAbove(button);
                    }
                }
                getButtonGroup().update();

            }
        }
        this.getModel().updateViews();
    }

    /**
     * Gets the buttonGroup
     * 
     * @return the button group
     */
    public Composite getButtonGroup() {
        return buttonGroup;
    }

    /**
     * Gets the name of the group of check boxes
     * 
     * @return the name of the group of check boxes
     */
    public Label getGroupLabel() {
        return groupLabel;
    }

    /**
     * Sets the groupLabel
     * 
     * @param label the new label
     */
    public void setGroupLabel(Label label) {
        this.groupLabel = label;
    }

    /**
     * Sets the title of the group of buttons
     * 
     * @param title the title of the group of check boxes
     */
    @Override
    public void setGroupTitle(String title) {
        this.groupLabel.setText(title);
    }

    /**
     * Adds a colored background to the group of buttons
     * 
     * @param color the color of the background from SWT class
     */
    public void setGroupBackground(int color) {
        getButtonGroup().setBackground(Display.getCurrent().getSystemColor(color));
    }

    @Override
    public void updateView(List<Object> currValues) {
        if (currValues.size() == 0) {
            for (Button button : getButtons()) {
                button.setSelection(false);
            }
        } else {
            for (Object label : currValues) {
                String text = (String) label;
                System.out.println("Updating view: " + text);
                for (Button button : getButtons()) {
                    if (button.getText().equals(text) || currValues.contains(button.getText())) {
                        button.setSelection(true);

                    } else {
                        button.setSelection(false);
                    }
                }
            }
        }

    }
}
