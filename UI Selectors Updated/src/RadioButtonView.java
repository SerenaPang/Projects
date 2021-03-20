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
 * @author Will Array of bullets with labels Used
 *         for M-From-N Semantic Control
 *
 */
public class RadioButtonView extends UiView {
	/** The group of buttons in the Radio button group */
    private final Composite buttonGroup;
    /** The name of the group of Radio button */
    private Label groupLabel;

	/**
	 * Constructor
	 * 
	 * @param model the model associated with the shell and display
	 */
	public RadioButtonView(SemanticControl model) {
		super(model, Views.RADIOB);
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
	    for (Object value : getModel().getAllValues()) {
	        if (value != null) {
	            addButton((String) value);
	        }
	    }
	}
	
	@Override
	public void addButton(String label) {
	    Button newButton = new Button(getButtonGroup(), SWT.RADIO);
	    newButton.setText(label);
	    newButton.setVisible(true);
	    newButton.addSelectionListener(new SelectionAdapter() {
	
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            Button source = (Button) e.getSource();
	            if (source.getSelection()) {
	                getModel().getCurrValue().add(source.getText());
	                getModel().updateViews();
	            } else {
	                getModel().getCurrValue().remove(source.getText());
	            }
	            
	        }
	    });
	    this.getButtons().add(newButton);
	    this.setNumButtons(getNumButtons() + 1);
	    
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
	
	/**
	 * Adds a colored background to the group of buttons
	 * 
	 * @param color the color of the background from SWT class
	 */
	public void setGroupBackground(int color) {
	    getButtonGroup().setBackground(Display.getCurrent().getSystemColor(color));
	}
}
