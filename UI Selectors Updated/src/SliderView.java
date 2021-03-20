import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * @author stewartpowell
 *
 */
public class SliderView extends UiView {
    /** The slider widget */
    private final Slider slider;
    /** The label for the group */
    private final Label groupLabel;
    /** The label to display the selections */
    private final Label valueLabel;
    /** The value in range model */
    private final ValueInRange model;
    /** The group containing the label and the spinner */
    private final Composite sliderGroup;
    /** The name of the type of selection */
    private String valueTitle;

    /**
     * @param model      the semantic control associated with this view
     * @param minValue   the minimum value of the slider
     * @param maxValue   the maximum value of the slider
     * @param title      the title of the slider group
     * @param valueTitle the Selection Title
     * @param type       the type of View this is
     */
    public SliderView(ValueInRange model, int minValue, int maxValue, String title, String valueTitle, Views type) {
        super(model, type);
        this.model = model;
        this.sliderGroup = new Composite(model.getShell(), SWT.NONE);
        this.groupLabel = new Label(sliderGroup, SWT.NONE);
        this.slider = new Slider(sliderGroup, SWT.HORIZONTAL);
        this.valueLabel = new Label(sliderGroup, SWT.NONE);
        this.valueTitle = valueTitle;
        sliderGroup.setLayout(new GridLayout(1, false));
        sliderGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        slider.setValues(0, minValue, maxValue + 1, 1, 1, 1);
        groupLabel.setText(title);
        valueLabel.setText(valueTitle + ": " + slider.getSelection() + "         ");

        slider.addListener(SWT.Selection, event -> {
            String string = "SWT.NONE";
            int selection = 0;
            switch (event.detail) {
                case SWT.DRAG:
                    string = "SWT.DRAG";
                    selection = slider.getSelection();
                    model.setCurrentValue(selection);
                    valueLabel.setText(valueTitle + ": " + selection);
                    break;
                case SWT.PAGE_DOWN:
                    string = "SWT.PAGE_DOWN";
                    selection = slider.getSelection();
                    model.setCurrentValue(selection);
                    valueLabel.setText(valueTitle + ": " + selection);
                    break;
                case SWT.PAGE_UP:
                    string = "SWT.PAGE_UP";
                    selection = slider.getSelection();
                    model.setCurrentValue(selection);
                    valueLabel.setText(valueTitle + ": " + selection);
                    break;
            }
            model.updateViews();
            System.out.println("Scroll detail -> " + string + "\n Selection -> " + selection);
        });

    }

    /**
     * Sets the value title
     * 
     * @param title the new value title
     */
    public void setValueTitle(String title) {
        valueTitle = title;
    }

    @Override
    public void setGroupTitle(String title) {
        groupLabel.setText(title);
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

    @Override
    public void updateView(List<Object> currValues) {
        updateView(model.getCurrentValue());

    }

    /**
     * Updates the selection of the slider
     * 
     * @param newValue the new updated value
     */
    public void updateView(int newValue) {
        slider.setSelection(newValue);
        valueLabel.setText(valueTitle + ": " + newValue);
    }

    /**
     * Gets the slider of the slider view
     * 
     * @return the slider of the slider view
     */
    public Slider getSlider() {
        return slider;
    }

}
