package org.antinori.game;

import static org.antinori.game.Card.ROOM_BALLROOM;
import static org.antinori.game.Card.ROOM_BILLIARD;
import static org.antinori.game.Card.ROOM_CONSERVATORY;
import static org.antinori.game.Card.ROOM_DINING;
import static org.antinori.game.Card.ROOM_HALL;
import static org.antinori.game.Card.ROOM_KITCHEN;
import static org.antinori.game.Card.ROOM_LIBRARY;
import static org.antinori.game.Card.ROOM_LOUNGE;
import static org.antinori.game.Card.ROOM_STUDY;
import static org.antinori.game.Card.SUSPECT_GREEN;
import static org.antinori.game.Card.SUSPECT_MUSTARD;
import static org.antinori.game.Card.SUSPECT_PEACOCK;
import static org.antinori.game.Card.SUSPECT_PLUM;
import static org.antinori.game.Card.SUSPECT_SCARLET;
import static org.antinori.game.Card.SUSPECT_WHITE;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;
import static org.antinori.game.Card.WEAPON_CANDLE;
import static org.antinori.game.Card.WEAPON_KNIFE;
import static org.antinori.game.Card.WEAPON_PIPE;
import static org.antinori.game.Card.WEAPON_REVOLVER;
import static org.antinori.game.Card.WEAPON_ROPE;
import static org.antinori.game.Card.WEAPON_WRENCH;

import java.util.ArrayList;

import javax.swing.JDialog;

public class PickCardsToShowDialog extends JDialog {

	public PickCardsToShowDialog(final ArrayList<Card> suggestion, final String suggestion_text, final Player player) {

		this.suggestion = suggestion;
		this.player = player;

		this.setModal(true);
		this.setUndecorated(true);
		ClueMain.setLocationInCenter(this, -200, -200);

		this.initComponents();

		this.suggestion_ta.setText(player.getPlayerName() + ", pick a card to show\naccording to the suggestion.\n\n" + suggestion_text);

		final ArrayList<Card> cards_in_hand = player.getCardsInHand();

		boolean has_a_card = false;

		for (final Card card : cards_in_hand) {

			if (!suggestion.contains(card)) {
				continue;
			}

			has_a_card = true;

			final int type = card.getType();
			final int value = card.getValue();

			if (type == TYPE_SUSPECT) {
				if (value == SUSPECT_SCARLET) {
					this.scarlet_cb.setEnabled(true);
				}
				if (value == SUSPECT_MUSTARD) {
					this.mustard_cb.setEnabled(true);
				}
				if (value == SUSPECT_GREEN) {
					this.green_cb.setEnabled(true);
				}
				if (value == SUSPECT_PLUM) {
					this.plum_cb.setEnabled(true);
				}
				if (value == SUSPECT_WHITE) {
					this.white_cb.setEnabled(true);
				}
				if (value == SUSPECT_PEACOCK) {
					this.peacock_cb.setEnabled(true);
				}
			} else if (type == TYPE_WEAPON) {
				if (value == WEAPON_REVOLVER) {
					this.revolver_cb.setEnabled(true);
				}
				if (value == WEAPON_PIPE) {
					this.pipe_cb.setEnabled(true);
				}
				if (value == WEAPON_ROPE) {
					this.rope_cb.setEnabled(true);
				}
				if (value == WEAPON_CANDLE) {
					this.candlestick_cb.setEnabled(true);
				}
				if (value == WEAPON_WRENCH) {
					this.wrench_cb.setEnabled(true);
				}
				if (value == WEAPON_KNIFE) {
					this.knife_cb.setEnabled(true);
				}
			} else {
				if (value == ROOM_KITCHEN) {
					this.kitchen_cb.setEnabled(true);
				}
				if (value == ROOM_BALLROOM) {
					this.ballroom_cb.setEnabled(true);
				}
				if (value == ROOM_CONSERVATORY) {
					this.conservatory_cb.setEnabled(true);
				}
				if (value == ROOM_BILLIARD) {
					this.billiard_cb.setEnabled(true);
				}
				if (value == ROOM_LIBRARY) {
					this.library_cb.setEnabled(true);
				}
				if (value == ROOM_STUDY) {
					this.study_cb.setEnabled(true);
				}
				if (value == ROOM_HALL) {
					this.hall_cb.setEnabled(true);
				}
				if (value == ROOM_LOUNGE) {
					this.lounge_cb.setEnabled(true);
				}
				if (value == ROOM_DINING) {
					this.dining_cb.setEnabled(true);
				}
			}

		}

		//let them click OK if they have no cards to show
		if (!has_a_card) {
			this.okButton.setEnabled(true);
		}

	}

	//return the data after clicking OK
	public Card showDialog() {
		this.setVisible(true);
		return this.picked_card;
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		this.bg = new javax.swing.JPanel();
		this.fgPanel = new javax.swing.JPanel();
		this.scarlet_cb = new javax.swing.JRadioButton();
		this.mustard_cb = new javax.swing.JRadioButton();
		this.green_cb = new javax.swing.JRadioButton();
		this.plum_cb = new javax.swing.JRadioButton();
		this.white_cb = new javax.swing.JRadioButton();
		this.peacock_cb = new javax.swing.JRadioButton();
		this.revolver_cb = new javax.swing.JRadioButton();
		this.candlestick_cb = new javax.swing.JRadioButton();
		this.rope_cb = new javax.swing.JRadioButton();
		this.pipe_cb = new javax.swing.JRadioButton();
		this.wrench_cb = new javax.swing.JRadioButton();
		this.knife_cb = new javax.swing.JRadioButton();
		this.kitchen_cb = new javax.swing.JRadioButton();
		this.ballroom_cb = new javax.swing.JRadioButton();
		this.conservatory_cb = new javax.swing.JRadioButton();
		this.billiard_cb = new javax.swing.JRadioButton();
		this.library_cb = new javax.swing.JRadioButton();
		this.study_cb = new javax.swing.JRadioButton();
		this.hall_cb = new javax.swing.JRadioButton();
		this.lounge_cb = new javax.swing.JRadioButton();
		this.dining_cb = new javax.swing.JRadioButton();
		this.okButton = new javax.swing.JButton();
		this.suggestion_ta = new javax.swing.JTextArea();
		this.bgLabel = new javax.swing.JLabel();
		this.buttonSelectGroup = new javax.swing.ButtonGroup();

		this.buttonSelectGroup.add(this.scarlet_cb);
		this.buttonSelectGroup.add(this.mustard_cb);
		this.buttonSelectGroup.add(this.green_cb);
		this.buttonSelectGroup.add(this.plum_cb);
		this.buttonSelectGroup.add(this.white_cb);
		this.buttonSelectGroup.add(this.peacock_cb);

		this.buttonSelectGroup.add(this.revolver_cb);
		this.buttonSelectGroup.add(this.candlestick_cb);
		this.buttonSelectGroup.add(this.rope_cb);
		this.buttonSelectGroup.add(this.pipe_cb);
		this.buttonSelectGroup.add(this.wrench_cb);
		this.buttonSelectGroup.add(this.knife_cb);

		this.buttonSelectGroup.add(this.kitchen_cb);
		this.buttonSelectGroup.add(this.ballroom_cb);
		this.buttonSelectGroup.add(this.conservatory_cb);
		this.buttonSelectGroup.add(this.billiard_cb);
		this.buttonSelectGroup.add(this.library_cb);
		this.buttonSelectGroup.add(this.study_cb);
		this.buttonSelectGroup.add(this.hall_cb);
		this.buttonSelectGroup.add(this.lounge_cb);
		this.buttonSelectGroup.add(this.dining_cb);

		this.scarlet_cb.addItemListener(new PickItemListener());
		this.mustard_cb.addItemListener(new PickItemListener());
		this.green_cb.addItemListener(new PickItemListener());
		this.plum_cb.addItemListener(new PickItemListener());
		this.white_cb.addItemListener(new PickItemListener());
		this.peacock_cb.addItemListener(new PickItemListener());

		this.revolver_cb.addItemListener(new PickItemListener());
		this.candlestick_cb.addItemListener(new PickItemListener());
		this.rope_cb.addItemListener(new PickItemListener());
		this.pipe_cb.addItemListener(new PickItemListener());
		this.wrench_cb.addItemListener(new PickItemListener());
		this.knife_cb.addItemListener(new PickItemListener());

		this.kitchen_cb.addItemListener(new PickItemListener());
		this.ballroom_cb.addItemListener(new PickItemListener());
		this.conservatory_cb.addItemListener(new PickItemListener());
		this.billiard_cb.addItemListener(new PickItemListener());
		this.library_cb.addItemListener(new PickItemListener());
		this.study_cb.addItemListener(new PickItemListener());
		this.hall_cb.addItemListener(new PickItemListener());
		this.lounge_cb.addItemListener(new PickItemListener());
		this.dining_cb.addItemListener(new PickItemListener());

		this.scarlet_cb.setEnabled(false);
		this.mustard_cb.setEnabled(false);
		this.green_cb.setEnabled(false);
		this.plum_cb.setEnabled(false);
		this.white_cb.setEnabled(false);
		this.peacock_cb.setEnabled(false);

		this.revolver_cb.setEnabled(false);
		this.candlestick_cb.setEnabled(false);
		this.rope_cb.setEnabled(false);
		this.pipe_cb.setEnabled(false);
		this.wrench_cb.setEnabled(false);
		this.knife_cb.setEnabled(false);

		this.kitchen_cb.setEnabled(false);
		this.ballroom_cb.setEnabled(false);
		this.conservatory_cb.setEnabled(false);
		this.billiard_cb.setEnabled(false);
		this.library_cb.setEnabled(false);
		this.study_cb.setEnabled(false);
		this.hall_cb.setEnabled(false);
		this.lounge_cb.setEnabled(false);
		this.dining_cb.setEnabled(false);

		this.okButton.setEnabled(false);

		this.bg.setMinimumSize(new java.awt.Dimension(1, 1));
		this.bg.setLayout(new java.awt.GridBagLayout());

		this.fgPanel.setOpaque(false);

		this.scarlet_cb.setText("Miss Scarlet");
		this.scarlet_cb.setOpaque(false);

		this.mustard_cb.setText("Colonel Mustard");
		this.mustard_cb.setOpaque(false);

		this.green_cb.setText("Mr. Green");
		this.green_cb.setOpaque(false);

		this.plum_cb.setText("Professor Plum");
		this.plum_cb.setOpaque(false);

		this.white_cb.setText("Mrs. White");
		this.white_cb.setOpaque(false);

		this.peacock_cb.setText("Mrs. Peacock");
		this.peacock_cb.setOpaque(false);

		this.revolver_cb.setText("Revolver");
		this.revolver_cb.setOpaque(false);

		this.candlestick_cb.setText("Candlelabra");
		this.candlestick_cb.setOpaque(false);

		this.rope_cb.setText("Rope");
		this.rope_cb.setOpaque(false);

		this.pipe_cb.setText("Lead Pipe");
		this.pipe_cb.setOpaque(false);

		this.wrench_cb.setText("Wrench");
		this.wrench_cb.setOpaque(false);

		this.knife_cb.setText("Knife");
		this.knife_cb.setOpaque(false);

		this.kitchen_cb.setText("Kitchen");
		this.kitchen_cb.setOpaque(false);

		this.ballroom_cb.setText("Ballroom");
		this.ballroom_cb.setOpaque(false);

		this.conservatory_cb.setText("Conservatory");
		this.conservatory_cb.setOpaque(false);

		this.billiard_cb.setText("Billiard Room");
		this.billiard_cb.setOpaque(false);

		this.library_cb.setText("Library");
		this.library_cb.setOpaque(false);

		this.study_cb.setText("Study");
		this.study_cb.setOpaque(false);

		this.hall_cb.setText("Hall");
		this.hall_cb.setOpaque(false);

		this.lounge_cb.setText("Lounge");
		this.lounge_cb.setOpaque(false);

		this.dining_cb.setText("Dining Room");
		this.dining_cb.setOpaque(false);

		this.okButton.setText("OK");
		this.okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				PickCardsToShowDialog.this.okButtonActionPerformed(evt);
			}
		});

		this.suggestion_ta.setColumns(20);
		this.suggestion_ta.setFont(ClueMain.FONT_14); // NOI18N
		this.suggestion_ta.setForeground(new java.awt.Color(0, 0, 255));
		this.suggestion_ta.setRows(5);
		this.suggestion_ta.setBorder(null);
		this.suggestion_ta.setOpaque(false);
		this.suggestion_ta.setEditable(false); //uneditable
		this.suggestion_ta.setLineWrap(true);
		this.suggestion_ta.setHighlighter(null); //unselectable

		final org.jdesktop.layout.GroupLayout fgPanelLayout = new org.jdesktop.layout.GroupLayout(this.fgPanel);
		this.fgPanel.setLayout(fgPanelLayout);
		fgPanelLayout.setHorizontalGroup(
				fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(fgPanelLayout.createSequentialGroup()
						.add(18, 18, 18)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(fgPanelLayout.createSequentialGroup()
										.add(this.scarlet_cb)
										.add(55, 55, 55)
										.add(this.revolver_cb))
								.add(fgPanelLayout.createSequentialGroup()
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(this.mustard_cb)
												.add(this.green_cb)
												.add(this.white_cb)
												.add(this.peacock_cb)
												.add(this.plum_cb))
										.add(33, 33, 33)
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(this.candlestick_cb)
												.add(this.rope_cb)
												.add(this.pipe_cb)
												.add(this.wrench_cb)
												.add(this.knife_cb)))
								.add(this.suggestion_ta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
						.add(31, 31, 31)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(org.jdesktop.layout.GroupLayout.TRAILING, fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
										.add(this.kitchen_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.study_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.library_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.billiard_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.ballroom_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.conservatory_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.hall_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.lounge_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(this.dining_cb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.add(org.jdesktop.layout.GroupLayout.TRAILING, this.okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(53, Short.MAX_VALUE))
				);
		fgPanelLayout.setVerticalGroup(
				fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(fgPanelLayout.createSequentialGroup()
						.add(16, 16, 16)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(fgPanelLayout.createSequentialGroup()
										.add(this.kitchen_cb)
										.add(0, 0, 0)
										.add(this.ballroom_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.conservatory_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.billiard_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.library_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.study_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.hall_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.lounge_cb)
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
										.add(this.dining_cb)
										.add(39, 39, 39)
										.add(this.okButton))
								.add(fgPanelLayout.createSequentialGroup()
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												.add(this.scarlet_cb)
												.add(this.revolver_cb))
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(fgPanelLayout.createSequentialGroup()
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.mustard_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.green_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.plum_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.white_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.peacock_cb))
												.add(fgPanelLayout.createSequentialGroup()
														.add(this.candlestick_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.rope_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.pipe_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.wrench_cb)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.knife_cb)))
										.add(18, 18, 18)
										.add(this.suggestion_ta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(34, Short.MAX_VALUE))
				);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		this.bg.add(this.fgPanel, gridBagConstraints);

		this.bgLabel.setIcon(ClueMain.getImageIcon("orange-gradient.jpg")); // NOI18N
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		this.bg.add(this.bgLabel, gridBagConstraints);

		final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(this.bg, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(this.bg, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
				);

		this.pack();
	}// </editor-fold>//GEN-END:initComponents

	class PickItemListener implements java.awt.event.ItemListener {

		@Override
		public void itemStateChanged(final java.awt.event.ItemEvent evt) {
			PickCardsToShowDialog.this.okButton.setEnabled(true);
		}
	}

	public void okButtonActionPerformed(final java.awt.event.ActionEvent evt) {

		if (this.scarlet_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_SCARLET);
		}
		if (this.mustard_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_MUSTARD);
		}
		if (this.green_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_GREEN);
		}
		if (this.plum_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_PLUM);
		}
		if (this.white_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_WHITE);
		}
		if (this.peacock_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_SUSPECT, SUSPECT_PEACOCK);
		}

		if (this.revolver_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_REVOLVER);
		}
		if (this.candlestick_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_CANDLE);
		}
		if (this.rope_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_ROPE);
		}
		if (this.pipe_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_PIPE);
		}
		if (this.wrench_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_WRENCH);
		}
		if (this.knife_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_WEAPON, WEAPON_KNIFE);
		}

		if (this.kitchen_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_KITCHEN);
		}
		if (this.ballroom_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_BALLROOM);
		}
		if (this.conservatory_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_CONSERVATORY);
		}
		if (this.billiard_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_BILLIARD);
		}
		if (this.library_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_LIBRARY);
		}
		if (this.study_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_STUDY);
		}
		if (this.hall_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_HALL);
		}
		if (this.lounge_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_LOUNGE);
		}
		if (this.dining_cb.isSelected()) {
			this.picked_card = Card.getInstance(TYPE_ROOM, ROOM_DINING);
		}

		this.dispose();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private ArrayList<Card> suggestion;
	private Player player;
	private Card picked_card = null;

	private javax.swing.JRadioButton ballroom_cb;
	private javax.swing.JPanel bg;
	private javax.swing.JLabel bgLabel;
	private javax.swing.JRadioButton billiard_cb;
	private javax.swing.JRadioButton candlestick_cb;
	private javax.swing.JRadioButton conservatory_cb;
	private javax.swing.JRadioButton dining_cb;
	private javax.swing.JPanel fgPanel;
	private javax.swing.JRadioButton green_cb;
	private javax.swing.JRadioButton hall_cb;
	private javax.swing.JRadioButton kitchen_cb;
	private javax.swing.JRadioButton knife_cb;
	private javax.swing.JRadioButton library_cb;
	private javax.swing.JRadioButton lounge_cb;
	private javax.swing.JRadioButton mustard_cb;
	private javax.swing.JButton okButton;
	private javax.swing.JRadioButton peacock_cb;
	private javax.swing.JRadioButton pipe_cb;
	private javax.swing.JRadioButton plum_cb;
	private javax.swing.JRadioButton revolver_cb;
	private javax.swing.JRadioButton rope_cb;
	private javax.swing.JRadioButton scarlet_cb;
	private javax.swing.JRadioButton study_cb;
	private javax.swing.JRadioButton white_cb;
	private javax.swing.JRadioButton wrench_cb;
	private javax.swing.JTextArea suggestion_ta;
	private javax.swing.ButtonGroup buttonSelectGroup;

	// End of variables declaration//GEN-END:variables
}
