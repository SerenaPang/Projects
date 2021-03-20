import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 */

/**
 * @author stewartpowell
 *
 */
public class MfromN extends SemanticControl {

    /**
     * Constructor for MfromN Model
     * 
     * @param display the display for the presenters
     * @param shell   the shell for the presenters
     */
    public MfromN(Display display, Shell shell) {
        super(display, shell);
    }

    /**
     * Adds a view to the current list of views
     * 
     * @param view   a value to add to the current Values
     * @param layout the layout of the presenter buttons
     */
    @Override
    public void addView(Views view, boolean layout, String title) {
        switch (view) {
            case FULL:
                FullListView fullView = new FullListView(this, Views.FULL);
                if (!title.equals(null)) {
                    fullView.setGroupTitle(title);
                }
                getViews().add(fullView);
                break;
            case SCROLL:
                ScrollListView scrollView = new ScrollListView(this, Views.SCROLL);
                if (!title.equals(null)) {
                    scrollView.setGroupTitle(title);
                }
                getViews().add(scrollView);
                break;
            case DOUBLE:
                DoubleListView doubleView = new DoubleListView(this);
                if (!title.equals(null)) {
                    doubleView.setGroupTitle(title);
                }
                getViews().add(doubleView);
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
        if ((view.getType() == Views.FULL) || (view.getType() == Views.SCROLL) || (view.getType() == Views.DOUBLE)) {
            getViews().add(view);
        } else {
            System.out.println("Error: Non-valid view");
        }
    }

}
