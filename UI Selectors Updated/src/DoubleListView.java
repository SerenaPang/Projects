import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 */

/**
 * @author stewartpowell
 *
 */
public class DoubleListView extends UiView {
    /** The Second view that shows the selections */
    private final FullListView choiceView;
    /** The group of buttons in the scrollable group */
    private final Composite buttonGroup;
    /** The name of the group of check boxes */
    private Label groupLabel;

    /**
     * Constructor for the DoubleList view presenter
     * 
     * @param model the model associated with the shell and display
     */
    public DoubleListView(SemanticControl model) {
        super(model, Views.DOUBLE);
        this.buttonGroup = new Composite(model.getShell(), SWT.NONE);
        buttonGroup.setLayout(new GridLayout(1, false));
        buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.choiceView = new FullListView(model, Views.DOUBLE);
        this.groupLabel = new Label(buttonGroup, SWT.NONE);
        initViews();
    }

    /**
     * Initializes the views of the controller
     */
    public void initViews() {
        for (Object value : getModel().getAllValues()) {
            if (value != null) {
                addButton((String) value);
            }
        }
    }

    /**
     * Creates a new button for the choiceView presenter
     * 
     * @param source the button to get the text of our new button from
     * @return the new button for our choiceView presenter
     */
    public Button getNewChoiceButton(Button source) {
        Button tempButton = new Button(choiceView.getButtonGroup(), SWT.CHECK);
        tempButton.setText(source.getText());
        tempButton.setVisible(true);
        tempButton.setSelection(true);
        tempButton.setGrayed(true);
        tempButton.setEnabled(false);
        return tempButton;
    }

    /**
     * Checks if a button already exists in the choiceView presenter
     * 
     * @param label the name of the button to check if it exists
     * @return the existing button or null if it doesn't exist
     */
    public Button choiceButtonExists(String label) {
        Button exists = null;
        for (Button button : choiceView.getButtons()) {
            if (button.getText().equals(label)) {
                button.setVisible(true);
                exists = button;
                break;
            }
        }
        return exists;
    }

    @Override
    public void addButton(String label) {
        Button newButton = buttonExists(label);
        if (newButton == null) {
            // System.out.println("New Visible: " + label);
            newButton = new Button(this.getButtonGroup(), SWT.CHECK);
            newButton.setText(label);
            newButton.setVisible(true);
            newButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button source = (Button) e.getSource();
                    if (source.getSelection()) {
                        getModel().getCurrValue().add(source.getText());
                        Button tempButton = choiceButtonExists(source.getText());

                        if (tempButton == null) {
                            tempButton = getNewChoiceButton(source);
                        } else {
                            tempButton.setVisible(true);
                            tempButton.setSelection(true);
                            tempButton.setGrayed(true);
                            tempButton.setEnabled(false);
                        }
                        choiceView.getButtons().add(tempButton);
                        for (Control child : choiceView.getButtonGroup().getChildren()) {
                            if (child instanceof Button && !child.isVisible()) {
                                child.moveBelow(tempButton);
                            }
                        }
                        choiceView.getButtonGroup().layout();
                        getModel().getShell().layout();
                        getModel().getShell().redraw();
                    } else {
                        getModel().getCurrValue().remove(source.getText());
                        choiceView.removeButton(source.getText());
                        for (Control child : choiceView.getButtonGroup().getChildren()) {
                            if (child instanceof Button && child.isVisible()) {
                                source.moveBelow(child);
                            }
                        }
                        choiceView.getButtonGroup().layout();
                        getModel().getShell().layout();
                        getModel().getShell().redraw();
                    }
                    getModel().updateViews();
                }

            });
            this.getButtons().add(newButton);
            for (Control child : getButtonGroup().getChildren()) {
                if (child instanceof Button && !child.isVisible()) {
                    child.moveBelow(newButton);
                }
            }
            this.setNumButtons(getNumButtons() + 1);
        } else {
            newButton.setVisible(true);
            // System.out.println("Now Visible: " + newButton.getText());
            for (Control child : getButtonGroup().getChildren()) {
                if (child instanceof Button && !child.isVisible()) {
                    child.moveBelow(newButton);
                }
            }
        }
        getButtonGroup().update();
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
     * Sets a name to the results view presenter
     * 
     * @param label the name of the results view
     */
    public void setChoiceViewName(String label) {
        choiceView.setGroupTitle(label);
    }

    /**
     * Gets the choice view of this presenter
     * 
     * @return the choiceView of this presenter
     */
    public FullListView getChoiceView() {
        return choiceView;
    }

    /**
     * Gets the ButtonGroup()
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

    @Override
    public void updateView(List<Object> currValues) {
        if (currValues.size() == 0) {
            for (Button button : getButtons()) {
                button.setSelection(false);
            }
            for (Button button : choiceView.getButtons()) {
                button.setVisible(false);
            }
        } else {
            for (Object label : currValues) {
                String text = (String) label;
                for (Button button : getButtons()) {
                    if (button.getText().equals(text) || currValues.contains(button.getText())) {
                        button.setSelection(true);
                        Button exists = choiceButtonExists(button.getText());
                        if (exists == null) {
                            Button tempButton = getNewChoiceButton(button);
                            choiceView.getButtons().add(tempButton);
                            for (Control child : choiceView.getButtonGroup().getChildren()) {
                                if (child instanceof Button && !child.isVisible()) {
                                    tempButton.moveAbove(child);
                                }
                            }
                            choiceView.getButtonGroup().layout();
                        }
                    } else {
                        button.setSelection(false);
                    }
                }
                for (Button button : choiceView.getButtons()) {
                    if (!currValues.contains(button.getText())) {
                        button.setVisible(false);
                        for (Control child : choiceView.getButtonGroup().getChildren()) {
                            if (child instanceof Button && child.isVisible()) {
                                button.moveBelow(child);
                            }
                        }
                        choiceView.getButtonGroup().layout();
                    }
                }
            }
        }
    }
}
