package sample;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import fatbastard.ui.CommandLink;
import fatbastard.ui.RecommendationList;
import fatbastard.ui.core.Recommendation;

public class SampleClass {

	public static void main(String[] args) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));

		RecommendationList rl = new RecommendationList(composite, SWT.NONE);
		Recommendation r = new Recommendation("hello");
		r.setLabel("Hello");
		r.setCondition(3);
		rl.addRecommendation(r);
		
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();



	}
}
