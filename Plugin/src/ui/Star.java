/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron@gmail.com) - initial API and implementation
 *******************************************************************************/
package ui;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Instances of this class represent a star displayed by the StarRating component
 */
class Star {
    boolean hover;

    boolean marked;

    boolean halfstar;

    Rectangle bounds;

    Image defaultImage;

    Image hoverImage;

    Image selectedImage;

    Image selectedHoverImage;

    private StarRating parent;

    private Image halfSelectedImage;

    void dispose() {
        defaultImage.dispose();
        hoverImage.dispose();
        selectedImage.dispose();
        selectedHoverImage.dispose();
    }

    void draw(final GC gc, final int x, final int y) {
        Image image;
        /*
         * if (!parent.isEnabled()) {
         * image = defaultImage;
         * } else {
         */
        if (marked) {
            /*
             * if (hover) {
             * image = selectedHoverImage;
             * } else {
             */
            if (halfstar)
                image = halfSelectedImage;
            else
                image = selectedImage;
            // }
        } else {
            /*
             * if (hover) {
             * image = hoverImage;
             * } else {
             */
            image = defaultImage;
            // }
        }
        // }

        gc.drawImage(image, x, y);
        bounds = new Rectangle(x, y, image.getBounds().width, image.getBounds().height);
    }

    static Star initBig(final StarRating parent) {
        final Star star = new Star();
        star.parent = parent;
        star.defaultImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("images/stars/32.png"));
        star.hoverImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("images/stars/focus32.png"));
        star.selectedImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("images/stars/mark32.png"));
        star.selectedHoverImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("images/stars/mark-focus32.png"));
        return star;
    }

    static Star initSmall(final StarRating parent) {
        final Star star = new Star();
        star.parent = parent;
        star.defaultImage = new Image(Display.getCurrent(), star.getClass().getClassLoader().getResourceAsStream("stars/16.png"));
        star.hoverImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("stars/focus16.png"));
        star.halfSelectedImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("stars/mark16half.png"));
        star.selectedImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("stars/mark16.png"));
        star.selectedHoverImage = new Image(Display.getCurrent(), star.getClass().getClassLoader()
                .getResourceAsStream("stars/mark-focus16.png"));
        return star;
    }
}
