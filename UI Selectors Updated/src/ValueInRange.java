import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author stewartpowell The Value in Range Semantic Control
 */
public class ValueInRange extends SemanticControl {

    /** The max value of range */
    private final int maxValue;
    /** the minValue of range */
    private final int minValue;
    /** the Selected value from range */
    private int currentValue;

    /**
     * Constructor for the ValueInRange Semantic Control
     * 
     * @param display  the display of the presenters
     * @param shell    the shell of the presenters
     * 
     * @param maxValue the max value of range
     * @param minValue the min value of range
     */
    public ValueInRange(Display display, Shell shell, int minValue, int maxValue) {
        super(display, shell);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.currentValue = 0;
    }

    @Override
    public void addView(Views view, boolean layout, String title) {
        switch (view) {
            case SPINNER:
                SpinnerView spinView = new SpinnerView(this, getMinValue(), getMaxValue(), title, Views.SPINNER);
                getViews().add(spinView);
                break;
            case SLIDER:
                SliderView slideView = new SliderView(this, getMinValue(), getMaxValue(), title, title, Views.SLIDER);
                getViews().add(slideView);
                break;
            default:
                System.out.println("Error: Incapatable view selected: " + view);
        }

    }

    /**
     * Adds a UiView object to the list views of this presenter
     * 
     * @param view the new view to add to the model
     */
    public void addView(UiView view) {
        if ((view.getType() == Views.SLIDER) || (view.getType() == Views.SPINNER)) {
            getViews().add(view);
        } else {
            System.out.println("Error: Non-valid view");
        }
    }

    /**
     * Gets the MaxValue in the range
     * 
     * @return the max value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Gets the minValue in the Range
     * 
     * @return the min value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Gets the Current Selection in range
     * 
     * @return the current value
     */
    public int getCurrentValue() {
        return currentValue;
    }

    /**
     * 
     * @param newValue the new current Value
     */
    public void setCurrentValue(int newValue) {
        currentValue = newValue;
    }

}
