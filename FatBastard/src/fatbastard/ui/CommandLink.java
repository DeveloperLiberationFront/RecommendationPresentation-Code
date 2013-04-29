package fatbastard.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import fatbastard.ui.core.Recommendation;

/**
 * Instance of this class are composite that represents a choice like in Windows
 * Vista and Seven. It is composed of a green arrow, instruction and text
 */
public class CommandLink extends Composite {
	
	private Recommendation recommendation;
	
	private Image oldImage;

	private Label image;
	private Label label;
	private Label explanation;

	//private final List<SelectionListener> selectionListeners;

	private boolean selection;
	private boolean insideComposite;
	private boolean insideImage;
	private boolean insideText;
	private boolean insideInstruction;
	private boolean mouseDown;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * 
	 * @param parent a widget which will be the parent of the new instance
	 *            (cannot be null)
	 * @param style the style of widget to construct
	 * 
	 * @exception IllegalArgumentException <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                </ul>
	 * 
	 * @see Composite#Composite(Composite, int)
	 * @see SWT#NO_BACKGROUND
	 * @see SWT#NO_FOCUS
	 * @see SWT#NO_MERGE_PAINTS
	 * @see SWT#NO_REDRAW_RESIZE
	 * @see SWT#NO_RADIO_GROUP
	 * @see SWT#EMBEDDED
	 * @see SWT#DOUBLE_BUFFERED
	 * @see Widget#getStyle
	 */
	public CommandLink(final Composite parent, final int style) {
		super(parent, style);

		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		this.setLayout(new GridLayout(2, false));

		buildGreenArrow();
		buildLabel();
		buildExplanation();
		addMouseListeners();

//		this.selectionListeners = new ArrayList<SelectionListener>();
		this.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				drawComposite();
			}
		});
	}
	
	@Override
	public int hashCode() {
		return recommendation.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CommandLink)) return false;
		return this.recommendation.equals(((CommandLink)obj).recommendation);
	}

	/**
	 * Build the green arrow
	 */
	private void buildGreenArrow() {
		this.image = new Label(this, SWT.NONE);
//		this.image.setImage(SWTGraphicUtil.createImage("images/arrowGreenRight.png"));
		this.image.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.image.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 1, 2));
	}

	/**
	 * Build the instruction
	 */
	private void buildLabel() {
		final Color color = new Color(Display.getCurrent(), 35, 107, 178);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
//				SWTGraphicUtil.dispose(color);
			}
		});
		this.label = new Label(this, SWT.NONE);
		this.label.setForeground(color);
		FontData[] fd = this.label.getFont().getFontData();
		fd[0].setHeight(15);
		this.label.setFont(new Font(this.getDisplay(), fd[0]));
		this.label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
	}

	/**
	 * Build the panel
	 */
	private void buildExplanation() {
		this.explanation = new Label(this, SWT.NONE);
		this.explanation.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		this.explanation.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true));
	}

	/**
	 * Add mouse listeners
	 */
	private void addMouseListeners() {
		final Listener mouseEnterListener = new Listener() {

			@Override
			public void handleEvent(final Event event) {

				if (event.widget.equals(CommandLink.this)) {
					CommandLink.this.insideComposite = true;
				}

				if (event.widget.equals(CommandLink.this.image)) {
					CommandLink.this.insideImage = true;
				}
				if (event.widget.equals(CommandLink.this.explanation)) {
					CommandLink.this.insideText = true;
				}
				if (event.widget.equals(CommandLink.this.label)) {
					CommandLink.this.insideInstruction = true;
				}

				drawComposite();
			}
		};

		final Listener mouseExitListener = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.widget.equals(CommandLink.this)) {
					CommandLink.this.insideComposite = false;
				}

				if (event.widget.equals(CommandLink.this.image)) {
					CommandLink.this.insideImage = false;
				}
				if (event.widget.equals(CommandLink.this.explanation)) {
					CommandLink.this.insideText = false;
				}
				if (event.widget.equals(CommandLink.this.label)) {
					CommandLink.this.insideInstruction = false;
				}
				drawComposite();
			}
		};

		final MouseListener mouseListener = new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				mouseDown = false;
				drawComposite();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				mouseDown = true;
				drawComposite();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		};

		addListener(SWT.MouseEnter, mouseEnterListener);
		this.image.addListener(SWT.MouseEnter, mouseEnterListener);
		this.explanation.addListener(SWT.MouseEnter, mouseEnterListener);
		this.label.addListener(SWT.MouseEnter, mouseEnterListener);

		addListener(SWT.MouseExit, mouseExitListener);
		this.image.addListener(SWT.MouseExit, mouseExitListener);
		this.explanation.addListener(SWT.MouseExit, mouseExitListener);
		this.label.addListener(SWT.MouseExit, mouseExitListener);

		addMouseListener(mouseListener);
		this.image.addMouseListener(mouseListener);
		this.explanation.addMouseListener(mouseListener);
		this.label.addMouseListener(mouseListener);
		
	}
	
	public void addCommandLinkListener(MouseListener recommendationListener) {
		addMouseListener(recommendationListener);
		this.image.addMouseListener(recommendationListener);
		this.explanation.addMouseListener(recommendationListener);
		this.label.addMouseListener(recommendationListener);
		
	}

	/**
	 * Draw the composite
	 */
	private void drawComposite() {

		final Rectangle rect = this.getClientArea();
		final Image newImage = new Image(getDisplay(), Math.max(1, rect.width), Math.max(1, rect.height));

		final GC gc = new GC(newImage);

		final boolean inside = this.insideComposite || this.insideImage || this.insideInstruction || this.insideText;
		final boolean mouseDown = this.mouseDown;

		if (!inside && !this.selection) {
			gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.drawRectangle(rect.x, rect.y, rect.width, rect.height);
		} else {
			// The mouse is over OR the item is selected
			final Color gradientColor = mouseDown ? new Color(getDisplay(), 148, 173, 202) : (inside ? new Color(getDisplay(), 220, 231, 243) : new Color(getDisplay(), 241, 241, 241));
			final Color borderColor = inside ? new Color(getDisplay(), 35, 107, 178) : new Color(getDisplay(), 192, 192, 192);

			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(gradientColor);
			gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);

			gc.setForeground(borderColor);
			gc.drawRoundRectangle(rect.x, rect.y, rect.width - 1, rect.height - 1, 2, 2);

			gradientColor.dispose();
			borderColor.dispose();
		}
		gc.dispose();

		this.setBackgroundImage(newImage);
		if (this.oldImage != null) {
			this.oldImage.dispose();
		}
		this.oldImage = newImage;

	}

	public String getLabel() {
		return label.getText();
	}
	
	public String getExplanation() {
		return explanation.getText();
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
	}
	
	public void setExplanation(String explanation) {
		if (explanation != null)
			this.explanation.setText(explanation);
	}
	
	public void setText(String label, String explanation){
		this.setLabel(label);
		this.setExplanation(explanation);
		this.redraw();
	}

	public void setSelection(final boolean selection) {
		this.selection = selection;
	}

	public Recommendation getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Recommendation recommendation) {
		this.recommendation = recommendation;
		this.setText(recommendation.getLabel(), recommendation.getConditionString());
		
	}

}


