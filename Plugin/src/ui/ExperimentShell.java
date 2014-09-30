package ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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

import ui.core.Recommendation;
import ui.core.Recorder;
import ui.core.TaskReader;
import ui.utils.Utils;

public class ExperimentShell {
    private static ExperimentShell instance;

    private Display display;

    private Shell shlTasksrecommendations;

    private Composite leftPane;

    private Composite rightPaneTop;

    private Label lblTaskNumber;

    private Label lblThisIsA;

    private Text textbox;

    private Button btnNext;

    private Label lblNewRecommendations;

    private Button btnHint;

    private RecommendationList newRecommendationList;

    private Label lblYouMayEnter;

    private Font font24;

    private Font font16;

    private Font font14;

    private ExperimentShell() {
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
        lblTaskNumber.setText("Task 1 of 16   ");


        lblThisIsA = new Label(leftPane, SWT.WRAP);
        lblThisIsA.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1));
        FontData[] fd = lblThisIsA.getFont().getFontData();
        fd[0].setHeight(16);
        font16 = new Font(display, fd1);
        lblThisIsA.setFont(font16);
        lblThisIsA.setForeground(new Color(this.display, 35, 107, 178));
        lblThisIsA.setText(Utils.taskList.get(Utils.currentTaskNumber).getTaskDetails());

        lblYouMayEnter = new Label(leftPane, SWT.NONE);
        FontData[] fd2 = lblYouMayEnter.getFont().getFontData();
        fd2[0].setHeight(14);
        font14 = new Font(display, fd1);
       // lblYouMayEnter.setFont(SWTResourceManager.getFont("Lucida Grande", 14, SWT.NORMAL));
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                System.out.println("Conditions: " + Utils.conditions[0] + " " + Utils.conditions[1] +
                        " " + Utils.conditions[2] + " " + Utils.conditions[3]);
                System.out.println("Experiment Completed");
            }

        });
    }

    public boolean isDisposed() {
        return shlTasksrecommendations.isDisposed();
    }

    public static ExperimentShell getInstance() {
        if (instance == null || instance.isDisposed())
            instance = new ExperimentShell();

        return instance;
    }

    private void initiateExperiment() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
        Utils.resetGlobals();
        TaskReader tr = new TaskReader();
        Utils.taskList = tr.getTaskList();
    }

    private void wrapupExperiment() throws Exception {
        Recorder.getInstance().dumpRecords();
        Utils.experimentRunning = false;

    }

    public Label getTaskLabel() {
        return lblThisIsA;
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

    public Text getTextBox() {
        return textbox;
    }

    public void close() {
        shlTasksrecommendations.dispose();
    }

    public void clearRecommendations() {
        newRecommendationList.clear();
    }
}
