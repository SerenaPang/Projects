import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Will
 *
 */
public class OneFromN extends SemanticControl {

    /**
     * Constructor
     * @param display the display used with all the Ui Components
     * @param shell   the Shell used with all the Ui components
     */
    public OneFromN(Display display, Shell shell) {
        super(display, shell);
    }

    /**
     * This function is to add a view to the current list of views
     * 
     * @param view the value to add to the current Values
     * @param layout the layout of the presenter buttons
     */
    @Override
    public void addView(Views view, boolean layout, String title) {
        switch (view) {
            case RADIOB:
            	RadioButtonView radioButtonView = new RadioButtonView(this);
            	
            	if (!title.equals(null)) {
                      radioButtonView.setGroupTitle(title);
                }
            	
                getViews().add(radioButtonView);
                break;
            case BOXB:
            	BoxButtonView boxButtonView = new BoxButtonView(this);
            	
            	if (!title.equals(null)) {
                    boxButtonView.setGroupTitle(title);
                }
            	
                getViews().add(boxButtonView);
                break;
            case POPUP:
            	PopMenuView popMenuView = new PopMenuView(this);
            	
            	if (!title.equals(null)) {
                    popMenuView.setGroupTitle(title);
                }
            	
                getViews().add(popMenuView);
                break;
            default:
                System.out.println("Error: Incapatable view selected: " + view);
        }
    }

    /**
     * This function is to add views
     * 
     * @param view The view to be added
     */
    public void addView(UiView view) {
        if ((view.getType() == Views.RADIOB) || (view.getType() == Views.BOXB) || (view.getType() == Views.POPUP) 
        		|| (view.getType() == Views.CIRCLEB) || (view.getType() == Views.TEXTFIELD)) {
            getViews().add(view);
        } else {
            System.out.println("Error: Non-valid view");
        }
    }
    
    /**
     * This function is to set the view name
     * 
     * @param number The order of the view
     * @param name The name of the view
     */
    public void setViewName(int number, String name) {
    	this.getViews().get(number - 1).setGroupTitle(name);
    }

}
