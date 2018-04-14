package org.antinori.game;

import static org.antinori.game.Card.SUSPECT_GREEN;
import static org.antinori.game.Card.SUSPECT_MUSTARD;
import static org.antinori.game.Card.SUSPECT_PEACOCK;
import static org.antinori.game.Card.SUSPECT_PLUM;
import static org.antinori.game.Card.SUSPECT_SCARLET;
import static org.antinori.game.Card.SUSPECT_WHITE;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;
import static org.antinori.game.Card.WEAPON_CANDLE;
import static org.antinori.game.Card.WEAPON_KNIFE;
import static org.antinori.game.Card.WEAPON_PIPE;
import static org.antinori.game.Card.WEAPON_REVOLVER;
import static org.antinori.game.Card.WEAPON_ROPE;
import static org.antinori.game.Card.WEAPON_WRENCH;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SuggestionDialog2 extends javax.swing.JDialog {

	int selected_suspect = -1;
	int selected_weapon = -1;
	Card room_card = null;

	Notebook notebook;

	String suspect_label = "[SUSPECT]";
	String weapon_label = "[WEAPON]";

	BufferedImage checked_icon = null;
	BufferedImage lock_icon = null;

	ArrayList<Card> suggestion = new ArrayList<>();

	BufferedImage green_background = ClueMain.loadIcon("green-pattern-cropped.jpg");

	private BackgroundImagePanel bgPanel;
	private javax.swing.JButton candle;
	private javax.swing.JButton green;
	private javax.swing.JButton gun;
	private javax.swing.JButton knife;
	private javax.swing.JButton mustard;
	private SuggestionButton okButton;
	private javax.swing.JButton peacock;
	private javax.swing.JButton pipe;
	private javax.swing.JButton plum;
	private javax.swing.JButton rope;
	private javax.swing.JButton scarlet;
	private SuggestionTextArea suggestion_ta;
	private javax.swing.JButton white;
	private javax.swing.JButton wrench;

	public SuggestionDialog2(final Frame owner, final Card room_card, final Notebook notebook) {
		super(owner, true);

		ClueMain.setLocationInCenter(this, -200, -200);

		this.setUndecorated(true);

		this.room_card = room_card;
		this.notebook = notebook;

		this.checked_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 129, 52, 83, 83), 30);
		this.lock_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 44, 54, 83, 83), 30);

		this.initComponents();
	}

	public ImageIcon getIcon(final BufferedImage image, final int type, final int value) {
		ImageIcon icon = null;
		if (this.notebook.isCardInHand(Card.getInstance(type, value))) {
			icon = new ImageIcon(ClueMain.overlayImages(image, this.lock_icon, 2, 2));
		} else if (this.notebook.isCardToggled(Card.getInstance(type, value))) {
			icon = new ImageIcon(ClueMain.overlayImages(image, this.checked_icon, 2, 2));
		} else {
			icon = new ImageIcon(image);
		}
		return icon;
	}

	private void initComponents() {

		this.bgPanel = new BackgroundImagePanel();
		this.scarlet = new javax.swing.JButton();
		this.scarlet.setIcon(this.getIcon(ButtonIcon.SCARLET.get(), TYPE_SUSPECT, SUSPECT_SCARLET));
		this.white = new javax.swing.JButton();
		this.white.setIcon(this.getIcon(ButtonIcon.WHITE.get(), TYPE_SUSPECT, SUSPECT_WHITE));
		this.plum = new javax.swing.JButton();
		this.plum.setIcon(this.getIcon(ButtonIcon.PLUM.get(), TYPE_SUSPECT, SUSPECT_PLUM));
		this.green = new javax.swing.JButton();
		this.green.setIcon(this.getIcon(ButtonIcon.GREEN.get(), TYPE_SUSPECT, SUSPECT_GREEN));
		this.mustard = new javax.swing.JButton();
		this.mustard.setIcon(this.getIcon(ButtonIcon.MUSTARD.get(), TYPE_SUSPECT, SUSPECT_MUSTARD));
		this.peacock = new javax.swing.JButton();
		this.peacock.setIcon(this.getIcon(ButtonIcon.PEACOCK.get(), TYPE_SUSPECT, SUSPECT_PEACOCK));

		this.knife = new javax.swing.JButton();
		this.knife.setIcon(this.getIcon(ButtonIcon.KNIFE.get(), TYPE_WEAPON, WEAPON_KNIFE));
		this.pipe = new javax.swing.JButton();
		this.pipe.setIcon(this.getIcon(ButtonIcon.PIPE.get(), TYPE_WEAPON, WEAPON_PIPE));
		this.rope = new javax.swing.JButton();
		this.rope.setIcon(this.getIcon(ButtonIcon.ROPE.get(), TYPE_WEAPON, WEAPON_ROPE));
		this.wrench = new javax.swing.JButton();
		this.wrench.setIcon(this.getIcon(ButtonIcon.WRENCH.get(), TYPE_WEAPON, WEAPON_WRENCH));
		this.candle = new javax.swing.JButton();
		this.candle.setIcon(this.getIcon(ButtonIcon.CANDLE.get(), TYPE_WEAPON, WEAPON_CANDLE));
		this.gun = new javax.swing.JButton();
		this.gun.setIcon(this.getIcon(ButtonIcon.GUN.get(), TYPE_WEAPON, WEAPON_REVOLVER));

		final String text = String.format(ClueMain.formatter, this.notebook.getPlayer().toString(), this.suspect_label, this.weapon_label, this.room_card.toString());
		this.suggestion_ta = new SuggestionTextArea(text, 5, 10);

		this.okButton = new SuggestionButton("OK");
		this.okButton.setEnabled(false);

		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		this.scarlet.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.scarletActionPerformed(evt);
			}
		});

		this.white.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.whiteActionPerformed(evt);
			}
		});

		this.plum.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.plumActionPerformed(evt);
			}
		});

		this.green.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.greenActionPerformed(evt);
			}
		});

		this.mustard.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.mustardActionPerformed(evt);
			}
		});

		this.knife.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.knifeActionPerformed(evt);
			}
		});

		this.peacock.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.peacockActionPerformed(evt);
			}
		});

		this.pipe.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.pipeActionPerformed(evt);
			}
		});

		this.rope.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.ropeActionPerformed(evt);
			}
		});

		this.wrench.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.wrenchActionPerformed(evt);
			}
		});

		this.candle.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.candleActionPerformed(evt);
			}
		});

		this.gun.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				SuggestionDialog2.this.gunActionPerformed(evt);
			}
		});

		final javax.swing.GroupLayout bgPanelLayout = new javax.swing.GroupLayout(this.bgPanel);
		this.bgPanel.setLayout(bgPanelLayout);
		bgPanelLayout.setHorizontalGroup(
				bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(bgPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(bgPanelLayout.createSequentialGroup()
										.addComponent(this.scarlet, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.mustard, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.green, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.plum, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.white, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.peacock, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(bgPanelLayout.createSequentialGroup()
										.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addGroup(bgPanelLayout.createSequentialGroup()
														.addComponent(this.knife, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.pipe, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.rope, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(this.wrench, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(this.suggestion_ta, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.candle, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(this.gun, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(this.okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		bgPanelLayout.setVerticalGroup(
				bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(bgPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(this.scarlet, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.mustard, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.green, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.plum, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.white, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.peacock, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(this.candle, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.wrench, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.knife, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.pipe, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.rope, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.gun, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(bgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(this.suggestion_ta, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(this.okButton))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);

		final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(this.bgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(this.bgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);

		this.pack();
	}// </editor-fold>//GEN-END:initComponents

	private void scarletActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scarletActionPerformed
		this.buttonClicked(SUSPECT_SCARLET, TYPE_SUSPECT);
	}//GEN-LAST:event_scarletActionPerformed

	private void whiteActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_whiteActionPerformed
		this.buttonClicked(SUSPECT_WHITE, TYPE_SUSPECT);
	}//GEN-LAST:event_whiteActionPerformed

	private void plumActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plumActionPerformed
		this.buttonClicked(SUSPECT_PLUM, TYPE_SUSPECT);
	}//GEN-LAST:event_plumActionPerformed

	private void greenActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenActionPerformed
		this.buttonClicked(SUSPECT_GREEN, TYPE_SUSPECT);
	}//GEN-LAST:event_greenActionPerformed

	private void mustardActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mustardActionPerformed
		this.buttonClicked(SUSPECT_MUSTARD, TYPE_SUSPECT);
	}//GEN-LAST:event_mustardActionPerformed

	private void knifeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_knifeActionPerformed
		this.buttonClicked(WEAPON_KNIFE, TYPE_WEAPON);
	}//GEN-LAST:event_knifeActionPerformed

	private void peacockActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peacockActionPerformed
		this.buttonClicked(SUSPECT_PEACOCK, TYPE_SUSPECT);
	}//GEN-LAST:event_peacockActionPerformed

	private void pipeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pipeActionPerformed
		this.buttonClicked(WEAPON_PIPE, TYPE_WEAPON);
	}//GEN-LAST:event_pipeActionPerformed

	private void ropeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ropeActionPerformed
		this.buttonClicked(WEAPON_ROPE, TYPE_WEAPON);
	}//GEN-LAST:event_ropeActionPerformed

	private void wrenchActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrenchActionPerformed
		this.buttonClicked(WEAPON_WRENCH, TYPE_WEAPON);
	}//GEN-LAST:event_wrenchActionPerformed

	private void candleActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_candleActionPerformed
		this.buttonClicked(WEAPON_CANDLE, TYPE_WEAPON);
	}//GEN-LAST:event_candleActionPerformed

	private void gunActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gunActionPerformed
		this.buttonClicked(WEAPON_REVOLVER, TYPE_WEAPON);
	}//GEN-LAST:event_gunActionPerformed

	public void buttonClicked(final int index, final int type) {

		SoundEffect.CLICK.play();

		if (type == TYPE_SUSPECT) {
			this.selected_suspect = index;
			this.suspect_label = Card.getInstance(TYPE_SUSPECT, index).toString();
		} else {
			this.selected_weapon = index;
			this.weapon_label = Card.getInstance(TYPE_WEAPON, index).toString();
		}

		if (this.selected_weapon != -1 && this.selected_suspect != -1) {
			this.okButton.setEnabled(true);
		}

		final String text = String.format(ClueMain.formatter, this.notebook.getPlayer().toString(), this.suspect_label, this.weapon_label, this.room_card.toString());
		this.suggestion_ta.setText(text);

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
			g2.drawImage(SuggestionDialog2.this.green_background, 0, 0, null);
		}
	}

	class SuggestionButton extends JButton implements ActionListener {

		SuggestionButton(final String text) {
			super(text);
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {

			if (SuggestionDialog2.this.selected_suspect == -1 || SuggestionDialog2.this.selected_weapon == -1) {
				return;
			}

			SuggestionDialog2.this.suggestion.add(SuggestionDialog2.this.room_card);
			SuggestionDialog2.this.suggestion.add(Card.getInstance(TYPE_SUSPECT, SuggestionDialog2.this.selected_suspect));
			SuggestionDialog2.this.suggestion.add(Card.getInstance(TYPE_WEAPON, SuggestionDialog2.this.selected_weapon));

			SuggestionDialog2.this.dispose();
		}
	}

	class SuggestionTextArea extends JTextArea {

		SuggestionTextArea(final String text, final int rows, final int cols) {
			super(text, rows, cols);
			this.setFont(ClueMain.FONT_14);
			this.setForeground(Color.white);
			this.setEditable(false); //uneditable
			this.setLineWrap(true);
			this.setHighlighter(null); //unselectable
			this.setOpaque(false);
		}
	}

	enum ButtonIcon {

		SCARLET("MsScarlett1.png"),
		MUSTARD("ColMustard1.png"),
		GREEN("MrGreen1.png"),
		WHITE("MrsWhite1.png"),
		PLUM("ProfPlum1.png"),
		PEACOCK("MrsPeacock1.png"),
		KNIFE("knife-icon.png"),
		ROPE("rope-icon.png"),
		GUN("gun-icon.png"),
		PIPE("pipe-icon.png"),
		CANDLE("candle-icon.png"),
		WRENCH("wrench-icon.png");

		private BufferedImage image;

		ButtonIcon(final String filename) {
			try {
				final URL url = this.getClass().getClassLoader().getResource(filename);
				this.image = ImageIO.read(url);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		public BufferedImage get() {
			return this.image;
		}

		public static void init() {
			values(); // calls the constructor for all the elements
		}
	}
}
