
import java.util.LinkedList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
/*
 * Demonstrate how to create an on and off semantical control
 * */
public class OnAndOffDemo {
	
	public static void main(String[] args) {
		Display display = new Display();
	     Shell shell = new Shell(display);
	     shell.setLayout(new GridLayout(4, true));
	     
	     shell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
	     shell.setBackgroundMode(SWT.INHERIT_FORCE);

	     UiRunner ui = new UiRunner(display, shell);
	     
	     
		//First Step: Creating labels for the buttons	 
		List<Object> group = new LinkedList<>();
		group.add("My Friends");
		group.add("Follows");
		group.add("Followers");
		group.add("Strangers");
		group.add(null);
		
		//Second Step: Creating the On and Off class Object

		OnAndOff model = new OnAndOff(display, shell);
		model.addManyToAllValues(group);


		//Third Step: Instantiate the Views and connect the views to the on and off model that you just created
		CheckBoxView checkBoxView = new CheckBoxView(model);
		ToggleButtonView tButtonview = new ToggleButtonView(model);
		SwitchButtonView switchButtonview = new SwitchButtonView(model);
		
		model.addView(checkBoxView);
		model.addView(tButtonview);
		model.addView(switchButtonview);


		model.getViews().get(0).setGroupTitle("Select who CAN see the posts on my account:");
		model.getViews().get(1).setGroupTitle("Select Who CAN see my posts on my account:");
		model.getViews().get(2).setGroupTitle("Select Who CAN see my posts on my account:");
		
		
		//Now launch your On and Off semantical control!!
		//model.launchUi();
		ui.launchUi();
		System.out.println("The Following Group can see your posts: " + model.getCurrValue());
		
		
	}
	

}


