import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is to test all presenters
 */
public class Presentation {

	/**
	 * Main
	 * @param args arguments
	 */
	 public static void main(String[] args) {
		 
		 Display display = new Display();
		 Shell shell = new Shell(display);
		 UiRunner runner = new UiRunner(display, shell);
		 
		 List<Object> gender = new LinkedList<>();
		 gender.add("Male");
		 gender.add("Female");
		 gender.add("I don't want to answer");

		 OneFromN model = new OneFromN(display, shell);
		 model.addManyToAllValues(gender);

		 RadioButtonView view = new RadioButtonView(model);
		 view.setGroupBackground(SWT.COLOR_DARK_GRAY);
		 model.addView(view);
		 model.setViewName(1, "Gender");

		 BoxButtonView view2 = new BoxButtonView(model);
		 view2.setGroupBackground(SWT.COLOR_DARK_CYAN);
		 model.addView(view2);
		 model.setViewName(2, "Gender");

		 PopMenuView view3 = new PopMenuView(model);
		 view3.setGroupBackground(SWT.COLOR_DARK_MAGENTA);
		 model.addView(view3);
		 model.setViewName(3, "Gender");

		 System.out.println("Before " + model.getCurrValue());
		 runner.launchUi();
		 System.out.println("After " + model.getCurrValue());
	 } 
}
