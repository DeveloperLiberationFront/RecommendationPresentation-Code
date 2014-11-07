package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;
import org.xml.sax.SAXException;

import ui.core.Recommendation;
import ui.core.TaskReader;
import ui.utils.Utils;
import data.Recorder;

public class ExperimentShell {
    private static ExperimentShell instance;

    private Display display;

    private Shell shlTasksrecommendations;

    private Composite leftPane;

    private Composite rightPaneTop;

    private Label lblTaskNumber;

    private Label lblTaskDetails;

    private Text textbox;

    private Button btnNext;

    private Label lblNewRecommendations;

    private RecommendationList newRecommendationList;

    private Label lblYouMayEnter;

    private Font font24;

    private Font font14;

    private ScrolledComposite taskDetailsWrapper;

    private ExperimentShell() {
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void open() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
        if (shlTasksrecommendations == null || shlTasksrecommendations.isDisposed()) {
            display = PlatformUI.getWorkbench().getDisplay();

            resetBeforeExperiment();

            shlTasksrecommendations = new Shell(display);
            shlTasksrecommendations.setText("Tasks and Recommendations");

            customizeShell();
            addPanesToShell();
            
            getUserId();
            getConsent();

            shlTasksrecommendations.open();
            
        } else {
            shlTasksrecommendations.forceActive();
        }
    }

    private void addPanesToShell() {
        addLeftPane();
        addRightPane();
    }

    private void addRightPane() {

        // right top pane starts here

        rightPaneTop = new BrushedMetalComposite(shlTasksrecommendations, SWT.NO_MERGE_PAINTS);

        GridData rightPaneTopLayoutData = new GridData();
        rightPaneTopLayoutData.grabExcessHorizontalSpace = true;
        rightPaneTopLayoutData.widthHint = 242;
        rightPaneTopLayoutData.grabExcessVerticalSpace = true;
        rightPaneTopLayoutData.horizontalSpan = 2;
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

        newRecommendationList.setRecommendationListener(new CommandLinkAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                CommandLink cLink = null;
                if (e.widget instanceof CommandLink) {
                    cLink = (CommandLink) e.widget;
                }
                else if (e.widget instanceof Label) {
                    Composite parent = ((Label) e.widget).getParent();

                    if (parent instanceof CommandLink) {
                        cLink = (CommandLink) parent;
                    }
                    else {
                        cLink = (CommandLink) (parent.getParent());
                    }
                }
                else if (e.widget instanceof StarRating) {
                    cLink = (CommandLink) ((StarRating) e.widget).getParent().getParent();

                }
                else if (e.widget instanceof Composite) {
                    cLink = (CommandLink) ((Composite) e.widget).getParent();
                }

                if (cLink != null) {
                    // record the click
                    Recorder recorder = Recorder.getInstance();
                    recorder.recordClick(Utils.currentTaskNumber, cLink.getRecommendation());

                    // open the details for the recommendation
                    RecoDetailsShell details = new RecoDetailsShell(cLink.getRecommendation(), null);
                    details.open();
                }

            }
        });

    }

    private void addLeftPane() {
        leftPane = new Composite(shlTasksrecommendations, SWT.NONE);
        leftPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        leftPane.setLayout(new GridLayout(2, true));

        lblTaskNumber = new Label(leftPane, SWT.WRAP);
        lblTaskNumber.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
        // http://stackoverflow.com/questions/1449968/change-just-the-font-size-in-swt
        FontData[] fd1 = lblTaskNumber.getFont().getFontData();
        fd1[0].setHeight(24);
        font24 = new Font(display, fd1);
        lblTaskNumber.setFont(font24);
        lblTaskNumber.setForeground(new Color(this.display, 35, 107, 178));
        setTaskNumber(1, Utils.taskList.size());

        // see http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/CreateaScrolledCompositewithwrappingcontent.htm
        taskDetailsWrapper = new ScrolledComposite(leftPane, SWT.V_SCROLL);
        taskDetailsWrapper.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
        
        lblTaskDetails = new Label(taskDetailsWrapper, SWT.WRAP);
        //lblTaskDetails.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
        FontData[] fd2 = lblTaskDetails.getFont().getFontData();
        fd2[0].setHeight(14);
        font14 = new Font(display, fd2);
        lblTaskDetails.setFont(font14);
        lblTaskDetails.setForeground(new Color(this.display, 35, 107, 178));
        lblTaskDetails.setText(Utils.taskList.get(Utils.currentTaskNumber).getTaskDetails());
        
        taskDetailsWrapper.setContent(lblTaskDetails);
        taskDetailsWrapper.setExpandHorizontal(true);
        taskDetailsWrapper.setExpandVertical(true);
        // the min size won't automatically get updated unless we add a listener
        taskDetailsWrapper.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
              fixTaskLabelSize();
            }
          });
        //detailsWrapper.setMinSize(SWT.DEFAULT, SWT.DEFAULT);

        lblYouMayEnter = new Label(leftPane, SWT.NONE);
        lblYouMayEnter.setFont(font14);
        lblYouMayEnter.setForeground(new Color(this.display, 35, 107, 178));
        lblYouMayEnter.setText("Enter your response here...");

        textbox = new Text(leftPane, SWT.BORDER | SWT.MULTI);
        textbox.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
        textbox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        textbox.setText("Enter your answer here...");
        textbox.setForeground(new Color(display, 128, 128, 128));

        textbox.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                if (textbox.getText().length() == 0) {
                    textbox.setText("Enter your answer here...");
                    textbox.setForeground(new Color(display, 128, 128, 128));
                }

            }

            @Override
            public void focusGained(FocusEvent e) {
                if ("Enter your answer here...".equals(textbox.getText())) {
                    textbox.setText("");
                    textbox.setForeground(new Color(display, 0, 0, 0));
                }
            }
        });

        btnNext = new Button(leftPane, SWT.NONE);
        btnNext.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 2, 1));
        btnNext.addSelectionListener(new NextButtonListener(this));
        btnNext.setText("Next");

    }

    private void customizeShell() {
        //shlTasksrecommendations.setMaximized(true);
        GridLayout gl_shlTasksrecommendations = new GridLayout();
        gl_shlTasksrecommendations.numColumns = 5;
        gl_shlTasksrecommendations.makeColumnsEqualWidth = true;

        shlTasksrecommendations.setLayout(gl_shlTasksrecommendations);

        // http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/Preventashellfromclosingprompttheuser.htm
        shlTasksrecommendations.addListener(SWT.Close, new Listener() {
            @Override
            public void handleEvent(Event event) {
                int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
                MessageBox messageBox = new MessageBox(shlTasksrecommendations, style);
                messageBox.setText("Are you sure you want to quit?");
                messageBox.setMessage("Are you sure you want to quit?  \n"
                        + "You have some tasks incomplete.");
                event.doit = (messageBox.open() == SWT.YES);
            }
        });

        shlTasksrecommendations.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                try {
                    if (font14 != null)
                    font14.dispose();
                    if (font24 != null)
                    font24.dispose();
                    wrapupExperiment();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                System.out.println("Experiment Completed");
            }

        });
    }

    public boolean isDisposed() {
        return shlTasksrecommendations.isDisposed();
    }

    public static ExperimentShell getInstance() {
        if (instance == null || instance.isDisposed()) {
            instance = new ExperimentShell();
        }
        return instance;
    } 

    private void resetBeforeExperiment() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
        Utils.resetGlobals();
        TaskReader tr = new TaskReader();
        Utils.taskList = tr.getTaskList();
        
        
    }

    private void getConsent() {
        int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
        MessageBox messageBox = new MessageBox(shlTasksrecommendations, style);
        messageBox.setText("Do you consent?");
        messageBox.setMessage("Click yes if you consent to have your data sent to a third "
                + "party for ");
        boolean didUserConsent = (messageBox.open() == SWT.YES);
        System.out.println("Consented: "+didUserConsent);
    }

    private void getUserId() {
        InputDialog inputBox = new InputDialog(shlTasksrecommendations, "Unity ID",
                "Please enter your Unity id.  Incorrectly typing this may result in your lab "
                        + "excercise not being graded.", "", null) {
            @Override
            protected void createButtonsForButtonBar(Composite parent) {
                createButton(parent, IDialogConstants.OK_ID,
                        IDialogConstants.OK_LABEL, true); // this should be saved to getOkayButton()
                                                          // , but that is not implemented
            }
        };

        inputBox.setBlockOnOpen(true);
        inputBox.open();
        
        String schoolID = inputBox.getValue();
        System.out.println("user id is " + schoolID);
        
        Utils.assignUserNumericId(schoolID);
        
        System.out.println("This is " + Utils.getParticipantID());
    }

    private void wrapupExperiment() throws Exception {
        Recorder.getInstance().dumpRecords();
        Utils.experimentRunning = false;

    }

    
    public void setTaskDescription(String str) {
        lblTaskDetails.setText(str);
        fixTaskLabelSize();
    }

    public Label getTaskNumberLabel() {
        return lblTaskNumber;
    }

    public RecommendationList getNewRecoList() {
        return newRecommendationList;
    }

    public void refreshRecommendationLists() throws URISyntaxException, IOException {
        HashSet<Recommendation> recos = newRecommendationList.getRecommendations();
        int size = recos.size();
        while (size < 10 && !Utils.recommendationQueue.isEmpty()) {
            Recommendation reco = Utils.recommendationQueue.pollFirst();
            newRecommendationList.addRecommendation(reco);
            Recorder.getInstance().recordRecommendation(Utils.currentTaskNumber, reco);
            size++;
        }
    }

    public Button getBtnNext() {
        return btnNext;
    }
    
    public void setAnswerBoxText(String str) {
        textbox.setText(str);
    }

    public void close() {
        shlTasksrecommendations.dispose();
        
    }

    public void clearRecommendations() {
        newRecommendationList.clear();
    }

    private void fixTaskLabelSize() {
        Rectangle r = taskDetailsWrapper.getClientArea();
          taskDetailsWrapper.setMinSize(lblTaskDetails.computeSize(r.width,
              SWT.DEFAULT));
    }

    public void setTaskNumber(int n, int total) {
        this.lblTaskNumber.setText("Task " + n + " of " + total);
    }

    public String getCurrentAnswer() {
        return this.textbox.getText();
    }
}
