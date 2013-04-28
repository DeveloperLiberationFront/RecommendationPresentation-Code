package fatbastard.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import fatbastard.ui.core.Recommendation;

public class RecoDetailsShell {
	
	Display display;
	Shell shell;
	private Recommendation recommendation;
	
	public RecoDetailsShell(Recommendation recommendation, Display display) {
		if (display == null)
			display = PlatformUI.getWorkbench().getDisplay();
		this.recommendation = recommendation;
		
	}
	
	public boolean open(){
		shell = new Shell(display);
		shell.setMaximized(true);
		shell.setText("Recommendation: " + recommendation.getLabel());
		shell.setLayout(new GridLayout(2, false));
		
		Label lblNameOfThe = new Label(shell, SWT.NONE);
		lblNameOfThe.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		lblNameOfThe.setText(recommendation.getLabel());
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Button btnIAlreadyKnow = new Button(composite, SWT.CHECK);
		btnIAlreadyKnow.setBounds(0, 0, 93, 18);
		btnIAlreadyKnow.setText("I already know this command");
		
		Button btnClose = new Button(composite, SWT.NONE);
		btnClose.setBounds(0, 0, 94, 28);
		btnClose.setText("Close");
		
		btnClose.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Browser browser = new Browser(shell, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		browser.setUrl(recommendation.getHtmlFile());
		
		shell.open();
		
		return btnIAlreadyKnow.getSelection();
	}
}
