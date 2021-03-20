import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
 * @author Will Array of bullets with labels Used
 *         for One-From-N Semantic Control
 *
 */
public class PopMenuView extends UiView {
	/** The group of buttons in the Radio button group */
    private final Composite buttonGroup;
    /** The name of the group of Radio button */
    private Label groupLabel;
    /** The menu of all labels */
    private CCombo combo;

	/**
	 * Constructor
	 * 
	 * @param model the model associated with the shell and display
	 */
	public PopMenuView(SemanticControl model) {
		super(model, Views.POPUP);
		model.getShell().setLayout(new GridLayout(2, false));
	    model.getShell().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
		combo = new CCombo(getButtonGroup(), SWT.READ_ONLY);
		addButton("Select one");
	}
	
	@Override
	public void addButton(String label) {
		combo.setText(label);
		
	    for (Object value : getModel().getAllValues()) {
	        if (value != null) {
	        	combo.add(value.toString());
	        }
	    }
		
		combo.addSelectionListener(new SelectionAdapter() {
			
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	        	getModel().getCurrValue().clear();;
                getModel().getCurrValue().add(combo.getItem(combo.getSelectionIndex()));
                getModel().updateViews();
	        }
	    });
	    getButtonGroup().update();
	    
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
	
	@Override
	public void updateView(List<Object> currValues) {
		int comboIndex = -1;
		
	    if (currValues.size() != 0) {
	    	combo.deselectAll();
	    	for (Object value : getModel().getAllValues()) {
	    		comboIndex++;
	            if (value.toString().equals(currValues.get(0))) {
	            	System.out.println("Updating view: " + value.toString());
	            	combo.select(comboIndex);
	            }
	        }
	    }
	}
	
	
	/**
	 * Adds a colored background to the group of buttons
	 * 
	 * @param color the color of the background from SWT class
	 */
	public void setGroupBackground(int color) {
	    getButtonGroup().setBackground(Display.getCurrent().getSystemColor(color));
	}

}
