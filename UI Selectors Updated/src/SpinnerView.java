import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author stewartpowell
 *
 */
public class SpinnerView extends UiView {
    /** The SWT widget for a spinner */
    private final Spinner spinner;
    /** The label of the spinner */
    private final Label spinLabel;
    /** The value in range model */
    private final ValueInRange model;
    /** The group containing the label and the spinner */
    private final Composite spinGroup;

    /**
     * The constructor for SpinnerView
     * 
     * @param model    the model associated with this view
     * @param minValue the min value of range
     * @param maxValue the max value of range
     * @param title    the title of the model
     * @param type     the type of view
     */
    public SpinnerView(ValueInRange model, int minValue, int maxValue, String title, Views type) {
        super(model, type);
        this.model = model;
        this.spinGroup = new Composite(model.getShell(), SWT.NONE);
        this.spinLabel = new Label(spinGroup, SWT.NONE);
        this.spinner = new Spinner(spinGroup, SWT.BORDER);
        spinLabel.setText(title);
        spinGroup.setLayout(new GridLayout(1, false));
        spinGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        spinner.setValues(0, minValue, maxValue, 0, 1, 10);

        spinner.addModifyListener(e -> {
            int prevSel = spinner.getSelection();
            String string = spinner.getText();
            String message = null;
            try {
                int value = Integer.parseInt(string);
                if (value > maxValue) {
                    message = "Current input is greater than maximum";
                } else if (value < minValue) {
                    message = "Current input is less than minimum";
                }
            } catch (Exception ex) {
                message = "Current input is not numeric";
            }
            if (message != null) {
                spinner.setForeground(model.getDisplay().getSystemColor(SWT.COLOR_RED));
                spinner.setSelection(prevSel);
            } else {
                spinner.setForeground(null);
                // model.updateViews();
            }
        });

        spinner.addSelectionListener(widgetSelectedAdapter(e -> {
            int selection = spinner.getSelection();
            model.setCurrentValue(selection);
            model.addCurrValue(selection);
            model.updateViews();
            System.out.println("Selection is " + selection);
        }));
    }

    @Override
    public void setGroupTitle(String title) {
        this.spinLabel.setText(title);
    }

    @Override
    public void updateView(List<Object> currValues) {
        updateView(model.getCurrentValue());
    }

    /**
     * Updates the current selection with the new value
     * 
     * @param newValue the new updated value
     */
    public void updateView(int newValue) {
        spinner.setSelection(newValue);
    }

    /**
     * Gets the Spinner
     * 
     * @return the spinner
     */
    public Spinner getSpinner() {
        return spinner;
    }

    /**
     * Gets the Spin Label
     * 
     * @return the spinLabel
     */
    public Label getSpinLabel() {
        return spinLabel;
    }

    /**
     * Gets the spinGroup
     * 
     * @return the spinGroup
     */
    public Composite getSpinGroup() {
        return spinGroup;
    }

    @Override
    public void addButton(String label) {
    }

    @Override
    public void removeButton(String label) {
    }

    @Override
    public void addManyButtons(List<Object> labels) {
    }

}
