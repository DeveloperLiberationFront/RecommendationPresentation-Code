package ui;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ui.CommandLink;
import ui.core.Recommendation;

public class RecommendationList extends ScrolledComposite  {

	Composite parent;
	HashSet<CommandLink> elements;
	HashSet<Recommendation> recommendations;
	MouseListener recommendationListener;
	Composite holder;
	int preferredWidth;

	public RecommendationList(Composite parent, int style) {
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		this.parent = parent;
		elements = new HashSet<CommandLink>();
		recommendations = new HashSet<Recommendation>();

		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);

		holder = new Composite(this, SWT.NONE);
		this.setContent(holder);
		holder.setLayout(new GridLayout(1, true));
		holder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		preferredWidth = parent.getSize().x;
		this.setMinSize(holder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void setRecommendationListener(MouseListener recommendationListener) {
		this.recommendationListener = recommendationListener;
	}

	public HashSet<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void addRecommendation(Recommendation reco){

		recommendations.add(reco);
		CommandLink commandLink = new CommandLink(holder, SWT.NONE, reco);
		commandLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		if (recommendationListener != null){
			commandLink.addCommandLinkListener(recommendationListener);
		}

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		elements.add(commandLink);

		refresh();

	}

	public Recommendation removeRecommendation(Recommendation reco){
		if (reco == null) return null;
		recommendations.remove(reco);
		CommandLink cl = findCommandLinkFromRecommendation(reco);
		elements.remove(cl);
		GridData clData = (GridData)cl.getLayoutData();
		clData.exclude = true;
		cl.dispose();

		refresh();
		return reco;
	}

	private CommandLink findCommandLinkFromRecommendation(Recommendation reco) {
		for (CommandLink cl : elements){
			if (cl.getRecommendation().equals(reco)){
				return cl;
			}
		}
		return null;
	}

	private void refresh() {
		checkWidget();
		preferredWidth = parent.getSize().x;
		this.setMinSize(holder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.holder.layout();

	}

	public void clear() {
		recommendations.clear();
		for (CommandLink cl : elements) {
			GridData clData = (GridData) cl.getLayoutData();
			clData.exclude = true;
			cl.dispose();
		}
		elements.clear();
		
	}

}
