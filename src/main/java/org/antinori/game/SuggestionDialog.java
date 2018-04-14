package org.antinori.game;

import static org.antinori.game.Card.NUM_SUSPECTS;
import static org.antinori.game.Card.NUM_WEAPONS;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.antinori.astar.MapView;

public class SuggestionDialog extends JDialog {

	int selected_suspect = -1;
	int selected_weapon = -1;
	Card room_card = null;

	Notebook notebook;

	SuggestionTextArea suggestion_text;
	String suspect_label = "[SUSPECT]";
	String weapon_label = "[WEAPON]";
	SuggestionButton suggest_button;

	BufferedImage checked_icon = null;
	BufferedImage lock_icon = null;

	ArrayList<Card> suggestion = new ArrayList<>();

	BufferedImage green_background = ClueMain.loadIcon("green-pattern-cropped.jpg");

	public SuggestionDialog(final Frame owner, final Card room_card, final Notebook notebook) {
		super(owner, "Suggestion Dialog", true);

		this.setUndecorated(true);

		this.room_card = room_card;
		this.notebook = notebook;

		this.checked_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 129, 52, 83, 83), 30);
		this.lock_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 44, 54, 83, 83), 30);

		final JPanel suspects = new JPanel();
		suspects.setLayout(new FlowLayout());
		suspects.setOpaque(false);
		for (int i = 0; i < NUM_SUSPECTS; i++) {
			final ImagePanel image_panel = new ImagePanel(MapView.suspect_images.get(i), TYPE_SUSPECT, i, 50);
			suspects.add(image_panel);
		}

		final JPanel weapons = new JPanel();
		weapons.setLayout(new FlowLayout());
		weapons.setOpaque(false);
		for (int i = 0; i < NUM_WEAPONS; i++) {
			final ImagePanel image_panel = new ImagePanel(MapView.weapon_images.get(i), TYPE_WEAPON, i, 20);
			weapons.add(image_panel);
		}

		final JPanel suggestion_panel = new JPanel();
		suggestion_panel.setLayout(new FlowLayout());
		suggestion_panel.setOpaque(false);

		final String text = String.format(ClueMain.formatter, notebook.getPlayer().toString(), this.suspect_label, this.weapon_label, room_card.toString());

		this.suggestion_text = new SuggestionTextArea(text, 3, 10);
		this.suggestion_text.setPreferredSize(new Dimension(350, 100));
		suggestion_panel.add(this.suggestion_text);

		final JPanel button_panel = new JPanel();
		this.suggest_button = new SuggestionButton("Make Suggestion", this);
		this.suggest_button.setEnabled(false);
		button_panel.add(this.suggest_button);
		button_panel.setOpaque(false);

		final JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.add(suspects, BorderLayout.NORTH);
		top.add(weapons, BorderLayout.CENTER);
		top.add(suggestion_panel, BorderLayout.SOUTH);

		final BackgroundImagePanel main = new BackgroundImagePanel();
		main.setLayout(new BorderLayout());
		main.add(top, BorderLayout.CENTER);
		main.add(button_panel, BorderLayout.SOUTH);

		this.add(main, BorderLayout.CENTER);

		this.setSize(800, 450);
		this.setLocationRelativeTo(null);

	}

	//return the data after clicking OK
	public Object showDialog() {
		this.setVisible(true);
		return this.suggestion;
	}

	class BackgroundImagePanel extends JPanel {

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(SuggestionDialog.this.green_background, 0, 0, null);
		}
	}

	class SuggestionButton extends JButton implements ActionListener {

		JDialog dialog = null;

		SuggestionButton(final String text, final JDialog dialog) {
			super(text);
			this.dialog = dialog;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (SuggestionDialog.this.selected_suspect == -1 || SuggestionDialog.this.selected_weapon == -1) {
				return;
			}

			SuggestionDialog.this.suggestion.add(SuggestionDialog.this.room_card);
			SuggestionDialog.this.suggestion.add(Card.getInstance(TYPE_SUSPECT, SuggestionDialog.this.selected_suspect));
			SuggestionDialog.this.suggestion.add(Card.getInstance(TYPE_WEAPON, SuggestionDialog.this.selected_weapon));

			this.dialog.dispose();
		}
	}

	class SuggestionTextArea extends JTextArea {

		SuggestionTextArea(final String text, final int rows, final int cols) {
			super(text, rows, cols);
			this.setFont(ClueMain.FONT_18);
			this.setForeground(Color.white);
			this.setEditable(false); //uneditable
			this.setLineWrap(true);
			this.setHighlighter(null); //unselectable
			this.setOpaque(false);
		}
	}

	class ImagePanel extends JPanel implements MouseListener {

		private double m_zoomPercentage;
		private BufferedImage m_image;
		int index = 0;
		int type = 0;

		ImagePanel(final BufferedImage image, final int type, final int index, final double zoom) {
			this.index = index;
			this.type = type;
			this.m_image = ClueMain.resizeImage(image, zoom);
			this.addMouseListener(this);
		}

		@Override
		public void paintComponent(final Graphics grp) {
			final Graphics2D g2D = (Graphics2D) grp;
			g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

			g2D.setComposite(AlphaComposite.Src);
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			BufferedImage image = this.m_image;
			if (SuggestionDialog.this.notebook.isCardInHand(Card.getInstance(this.type, this.index))) {
				image = ClueMain.overlayImages(this.m_image, SuggestionDialog.this.lock_icon, 5, 5);
			} else if (SuggestionDialog.this.notebook.isCardToggled(Card.getInstance(this.type, this.index))) {
				image = ClueMain.overlayImages(this.m_image, SuggestionDialog.this.checked_icon, 5, 5);
			}

			g2D.drawImage(image, 0, 0, this);

		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(this.m_image.getWidth(), this.m_image.getHeight());
		}

		@Override
		public void mouseClicked(final MouseEvent e) {

			SoundEffect.CLICK.play();

			if (this.type == TYPE_SUSPECT) {
				SuggestionDialog.this.selected_suspect = this.index;
				SuggestionDialog.this.suspect_label = Card.getInstance(TYPE_SUSPECT, this.index).toString();
			} else {
				SuggestionDialog.this.selected_weapon = this.index;
				SuggestionDialog.this.weapon_label = Card.getInstance(TYPE_WEAPON, this.index).toString();
			}

			if (SuggestionDialog.this.selected_weapon != -1 && SuggestionDialog.this.selected_suspect != -1) {
				SuggestionDialog.this.suggest_button.setEnabled(true);
			}

			final String text = String.format(ClueMain.formatter, SuggestionDialog.this.notebook.getPlayer().toString(), SuggestionDialog.this.suspect_label, SuggestionDialog.this.weapon_label, SuggestionDialog.this.room_card.toString());
			if (SuggestionDialog.this.suggestion_text != null) {
				SuggestionDialog.this.suggestion_text.setText(text);
			}

		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			this.setBorder(new BevelBorder(BevelBorder.RAISED));

			Cursor cursor = Cursor.getDefaultCursor();
			cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cursor);

		}

		@Override
		public void mouseExited(final MouseEvent e) {
			this.setBorder(new BevelBorder(BevelBorder.LOWERED));

			Cursor cursor = Cursor.getDefaultCursor();
			cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
			this.setCursor(cursor);
		}
	}

}
