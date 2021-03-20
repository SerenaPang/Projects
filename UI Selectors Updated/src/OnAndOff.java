import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Serena
 *
 */
public class OnAndOff extends SemanticControl {


	
	    public OnAndOff(Display display, Shell shell) {
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
	            	 CheckBoxView checkBoxView = new CheckBoxView(this);
	                 if (!title.equals(null)) {
	                	 checkBoxView.setGroupTitle(title);
	                 }
	            	
	                getViews().add(new CheckBoxView(this));
	                break;
	        
	            case TOGGLE:
	            	
	                getViews().add(new ToggleButtonView(this));
	                break;
	            case SWITCH:
	            	
	                getViews().add(new SwitchButtonView(this));
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
	        if ((view.getType() == Views.FULL) || (view.getType() == Views.TOGGLE)|| (view.getType() == Views.SWITCH)) {
	            getViews().add(view);
	        } else {
	            System.out.println("Error: Non-valid view");
	        }
	    }

	}


