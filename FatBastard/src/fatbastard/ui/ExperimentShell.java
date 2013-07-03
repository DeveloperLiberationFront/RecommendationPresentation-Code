package fatbastard.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;
import org.xml.sax.SAXException;

import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Recorder;
import fatbastard.ui.core.TaskReader;
import fatbastard.ui.utils.Utils;


public class ExperimentShell  {
	private static ExperimentShell instance;

	private Display display;
	private Shell shlTasksrecommendations;

	private Composite leftPane;

	private Composite rightPaneTop;
	private Label lblTaskNumber;
	private Label lblThisIsA;
	private Text text;
	private Button btnNext;
	private Label lblNewRecommendations;
	private Label lblOldRecommendations;
	private Button btnHint;

	private SashForm sashForm;

	private RecommendationList newRecommendationList;
	private RecommendationList oldRecommendationList;

	private BrushedMetalComposite rightPaneBottom;
	private Label lblYouMayEnter;


	private ExperimentShell(){
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() throws ParserConfigurationException, SAXException,
			IOException, URISyntaxException {
		display = PlatformUI.getWorkbench().getDisplay();

		initiateExperiment();

		shlTasksrecommendations = new Shell(display);
		shlTasksrecommendations.setText("Tasks and Recommendations");

		customizeShell();
		addPanesToShell();

		shlTasksrecommendations.open();
	}

	private void addPanesToShell() {
		addLeftPane();
		addRightPane();
	}

	private void addRightPane() {
		sashForm = new SashForm(shlTasksrecommendations, SWT.BORDER | SWT.VERTICAL);
		GridData sashFormLayoutData = new GridData();
		sashFormLayoutData.grabExcessHorizontalSpace = true;
		sashFormLayoutData.grabExcessVerticalSpace = true;
		sashFormLayoutData.horizontalSpan = 2;
		sashFormLayoutData.verticalAlignment = GridData.FILL;
		sashFormLayoutData.horizontalAlignment = SWT.FILL;
		sashForm.setLayoutData(sashFormLayoutData);

		//right top pane starts here

		rightPaneTop = new BrushedMetalComposite(sashForm, SWT.NO_MERGE_PAINTS);

		GridData rightPaneTopLayoutData = new GridData();
		rightPaneTopLayoutData.grabExcessHorizontalSpace = true;
		rightPaneTopLayoutData.widthHint = 242;
		rightPaneTopLayoutData.grabExcessVerticalSpace = true;
		rightPaneTopLayoutData.horizontalSpan = 1;
		rightPaneTopLayoutData.verticalAlignment = GridData.FILL;
		rightPaneTopLayoutData.horizontalAlignment = SWT.FILL;

		rightPaneTop.setLayoutData(rightPaneTopLayoutData);
		rightPaneTop.setLayout(new GridLayout(1, true));
		
		lblNewRecommendations = new Label(rightPaneTop, SWT.NONE);
		lblNewRecommendations.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		lblNewRecommendations.setText("New Eclipse Command Recommendations");

		Composite holderTop = new Composite(rightPaneTop, SWT.NONE);
		holderTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		holderTop.setLayout(new FillLayout());

		newRecommendationList = new RecommendationList(holderTop, SWT.NONE);
		newRecommendationList.setToolTipText("New commands will be recommended here as you complete the tasks.");
		
		//right bottom pane starts here

		rightPaneBottom = new BrushedMetalComposite(sashForm, SWT.NO_MERGE_PAINTS);
		rightPaneBottom.setLayout(new GridLayout(1, false));

		GridData rightPaneBottomLayoutData = new GridData();
		rightPaneBottomLayoutData.grabExcessHorizontalSpace = true;
		rightPaneBottomLayoutData.widthHint = 242;
		rightPaneBottomLayoutData.grabExcessVerticalSpace = true;
		rightPaneBottomLayoutData.horizontalSpan = 1;
		rightPaneBottomLayoutData.verticalAlignment = GridData.FILL;
		rightPaneBottomLayoutData.horizontalAlignment = SWT.FILL;

		rightPaneBottom.setLayoutData(rightPaneBottomLayoutData);
		
		lblOldRecommendations = new Label(rightPaneBottom, SWT.NONE);
		lblOldRecommendations.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		lblOldRecommendations.setText("Already Seen Recommendations");

		Composite holderBottom = new Composite(rightPaneBottom, SWT.NONE);
		holderBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		holderBottom.setLayout(new FillLayout());

		oldRecommendationList = new RecommendationList(holderBottom, SWT.NONE);
		oldRecommendationList.setToolTipText("Recommendations that you have seen will go to this list.");
		
		newRecommendationList.setRecommendationListener(new CommandLinkListener(){
			@Override
			public void mouseDown(MouseEvent e) {
				CommandLink cLink = null;
				if (e.widget instanceof CommandLink){
					cLink = (CommandLink) e.widget;
				}
				else if (e.widget instanceof Label){
					Composite parent = ((Label)e.widget).getParent();
					
					if (parent instanceof CommandLink){
						cLink = (CommandLink)parent;
					}
					else {
						cLink = (CommandLink) (parent.getParent());
					}
				}
				else if (e.widget instanceof StarRating){
					cLink = (CommandLink) ((StarRating)e.widget).getParent().getParent();
						
				}
				else if (e.widget instanceof Composite){
					cLink = (CommandLink) ((Composite)e.widget).getParent();
				}
				
				
				//open the details for the recommendation
				RecoDetailsShell details = new RecoDetailsShell(cLink.getRecommendation(), null);
				boolean alreadyKnown = details.open();
				
				if (!alreadyKnown){
					//record the click
					Recorder recorder = Recorder.getInstance();
					recorder.recordClick(cLink.getRecommendation());
				}
				
				//remove recommendation from this list and put it in "seen list"
				if (newList != null && oldList != null){
					newList.removeRecommendation(cLink.getRecommendation());
					oldList.addRecommendation(cLink.getRecommendation());
				}
				//add more recommendations to the new recommendations list, if there are any
				try {
					shell.refreshRecommendationLists();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		oldRecommendationList.setRecommendationListener(new CommandLinkListener(){

			@Override
			public void mouseDown(MouseEvent e) {
				CommandLink cLink = null;
				if (e.widget instanceof CommandLink){
					cLink = (CommandLink) e.widget;
				}
				else if (e.widget instanceof Label){
					Composite parent = ((Label)e.widget).getParent();
					
					if (parent instanceof CommandLink){
						cLink = (CommandLink)parent;
					}
					else {
						cLink = (CommandLink) (parent.getParent());
					}
				}
				else if (e.widget instanceof StarRating){
					cLink = (CommandLink) ((StarRating)e.widget).getParent().getParent();
						
				}
				else if (e.widget instanceof Composite){
					cLink = (CommandLink) ((Composite)e.widget).getParent();
				}
				
				//open the details for the recommendation
				RecoDetailsShell details = new RecoDetailsShell(cLink.getRecommendation(), null);
				boolean alreadyKnown = details.open();
				
				if (!alreadyKnown){
					//record the click
					Recorder recorder = Recorder.getInstance();
					recorder.recordClick(cLink.getRecommendation());
				}
			}});
		
	}

	private void addLeftPane() {
		leftPane = new Composite(shlTasksrecommendations, SWT.NONE);
		leftPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		leftPane.setLayout(new GridLayout(2, true));

		lblTaskNumber = new Label(leftPane, SWT.WRAP);
		lblTaskNumber.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		FontData[] fd1 = lblTaskNumber.getFont().getFontData();
		fd1[0].setHeight(24);
		lblTaskNumber.setFont(SWTResourceManager.getFont("Lucida Grande", 18, SWT.NORMAL));
		lblTaskNumber.setForeground(new Color(this.display, 35, 107, 178));
		lblTaskNumber.setText("Task 1 of 8");
		
		btnHint = new Button(leftPane, SWT.NONE);
		btnHint.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1));
		btnHint.setText("Show Recommendations");
		btnHint.setToolTipText("By default, recommendations for this task will be shown once you finish the task. This will use your usage data to generate recommendations");
		btnHint.addSelectionListener(new HintButtonListener(this));
		
		lblThisIsA = new Label(leftPane, SWT.WRAP);
		lblThisIsA.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
		FontData[] fd = lblThisIsA.getFont().getFontData();
		fd[0].setHeight(24);
		lblThisIsA.setFont(SWTResourceManager.getFont("Lucida Grande", 18, SWT.NORMAL));
		lblThisIsA.setForeground(new Color(this.display, 35, 107, 178));
		lblThisIsA.setText(Utils.taskList.get(Utils.currentTaskNumber).getTaskDetails());
		
		lblYouMayEnter = new Label(leftPane, SWT.NONE);
		lblYouMayEnter.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
		lblYouMayEnter.setForeground(new Color(this.display, 35, 107, 178));
		lblYouMayEnter.setText("You may enter your response here...");
		
		text = new Text(leftPane, SWT.BORDER | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		text.setText("Enter your answer here...");
		text.setForeground(new Color(display, 128, 128, 128));
		
		text.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (text.getText().length() == 0){
					text.setText("Enter your answer here...");
					text.setForeground(new Color(display, 128, 128, 128));
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (text.getText().equals("Enter your answer here...")){
					text.setText("");
					text.setForeground(new Color(display, 0, 0, 0));
				}
			}
		});

		btnNext = new Button(leftPane, SWT.NONE);
		btnNext.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 2, 1));
		btnNext.addSelectionListener(new NextButtonListener(this));
		btnNext.setText("Next");
		
		
	}

	private void customizeShell() {
		shlTasksrecommendations.setMaximized(true);
		GridLayout gl_shlTasksrecommendations = new GridLayout();
		gl_shlTasksrecommendations.numColumns = 5;
		gl_shlTasksrecommendations.makeColumnsEqualWidth = true;

		shlTasksrecommendations.setLayout(gl_shlTasksrecommendations);

		shlTasksrecommendations.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					wrapupExperiment();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				System.out.println("Conditions: " + Utils.conditions[0] + " " + Utils.conditions[1] +
						" " + Utils.conditions[2] + " " + Utils.conditions[3]);
				System.out.println("Experiment Completed");
			}


		});
	}

	public boolean isDisposed(){
		return shlTasksrecommendations.isDisposed();
	}

	public static ExperimentShell getInstance() {
		if (instance == null || instance.isDisposed())
			instance = new ExperimentShell();

		return instance;
	}

	private void initiateExperiment() throws ParserConfigurationException, SAXException, IOException, URISyntaxException{
		Utils.resetGlobals();
		TaskReader tr = new TaskReader();
		Utils.taskList = tr.getTaskList();
	}

	private void wrapupExperiment() throws FileNotFoundException {
		Recorder.getInstance().dumpRecords();
		Utils.experimentRunning = false;
		

	}

	public Label getTaskLabel() {
		return lblThisIsA;
	}
	
	public Label getTaskNumberLabel(){
		return lblTaskNumber;
	}
	
	public RecommendationList getNewRecoList(){
		return newRecommendationList;
	}
	
	public RecommendationList getOldRecoList(){
		return oldRecommendationList;
	}

	public void refreshRecommendationLists() throws URISyntaxException, IOException {
		HashSet<Recommendation> recos = newRecommendationList.getRecommendations();
		int size = recos.size();
		while (size < 10 && !Utils.recommendationQueue.isEmpty()){
			Recommendation reco = Utils.recommendationQueue.pollFirst();
			reco.addCondition();
			newRecommendationList.addRecommendation(reco);
			Recorder.getInstance().recordRecommendation(reco);
			size++;
		}
	}
	
	public Button getBtnNext() {
		return btnNext;
	}
	
	public Text getText() {
		return text;
	}

	public void close() {
		shlTasksrecommendations.dispose();
	}
}
