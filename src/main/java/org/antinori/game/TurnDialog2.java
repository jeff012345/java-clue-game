package org.antinori.game;

import static org.antinori.game.Card.TYPE_ROOM;

import java.util.ArrayList;

public class TurnDialog2 extends javax.swing.JDialog {

	public static final int ACTION_VALID_ACCUSATION = 200;
	public static final int ACTION_INVALID_ACCUSATION = 500;
	public static final int ACTION_MADE_SUGGESTION = 300;
	public static final int ACTION_TOOK_PASSAGE = 310;
	public static final int ACTION_ROLLED_DICE = 320;

	Player player;

	boolean showDiceButton = true;
	boolean showSuggestionButton = true;
	boolean showSecretPassageButton = false;

	int action = 0;

	public TurnDialog2(final Player player, final boolean showDiceButton, final boolean showSecretPassageButton, final boolean showSuggestionButton) {
		super(ClueMain.frame, true);

		this.player = player;
		this.showDiceButton = showDiceButton;
		this.showSuggestionButton = showSuggestionButton;
		this.showSecretPassageButton = showSecretPassageButton;

		this.initComponents();
	}

	// return the data after disposing
			public int showDialog() {
				this.setVisible(true);
				return this.action;
			}

			private void initComponents() {

				this.setUndecorated(true);

				ClueMain.setLocationInCenter(this, -200, -200);

				java.awt.GridBagConstraints gridBagConstraints;

				this.bg = new javax.swing.JPanel();
				this.fgPanel = new DropShadowPanel();
				this.rollDiceButton = new javax.swing.JButton();
				this.takeSecretPassageButton = new javax.swing.JButton();
				this.makeSuggestionButton = new javax.swing.JButton();
				this.bgLabel = new javax.swing.JLabel();

				if (!this.showDiceButton) {
					this.rollDiceButton.setEnabled(false);
				}
				if (!this.showSuggestionButton) {
					this.makeSuggestionButton.setEnabled(false);
				}
				if (!this.showSecretPassageButton) {
					this.takeSecretPassageButton.setEnabled(false);
				}

				this.bg.setMinimumSize(new java.awt.Dimension(1, 1));
				this.bg.setLayout(new java.awt.GridBagLayout());

				this.fgPanel.setBackground(java.awt.SystemColor.info);
				this.fgPanel.setPreferredSize(new java.awt.Dimension(320, 235));

				this.rollDiceButton.setText("Roll the Dice");
				this.rollDiceButton.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(final java.awt.event.ActionEvent evt) {
						TurnDialog2.this.rollDiceButtonActionPerformed(evt);
					}
				});

				this.takeSecretPassageButton.setText("Take Secret Passage");
				this.takeSecretPassageButton.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(final java.awt.event.ActionEvent evt) {
						TurnDialog2.this.takeSecretPassageButtonActionPerformed(evt);
					}
				});

				this.makeSuggestionButton.setText("Make a Suggestion");
				this.makeSuggestionButton.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(final java.awt.event.ActionEvent evt) {
						TurnDialog2.this.makeSuggestionButtonActionPerformed(evt);
					}
				});

				final org.jdesktop.layout.GroupLayout fgPanelLayout = new org.jdesktop.layout.GroupLayout(this.fgPanel);
				this.fgPanel.setLayout(fgPanelLayout);
				fgPanelLayout.setHorizontalGroup(
						fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(fgPanelLayout.createSequentialGroup()
								.add(91, 91, 91)
								.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
										.add(this.makeSuggestionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(this.takeSecretPassageButton)
										.add(this.rollDiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(96, Short.MAX_VALUE))
						);
				fgPanelLayout.setVerticalGroup(
						fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(fgPanelLayout.createSequentialGroup()
								.add(64, 64, 64)
								.add(this.rollDiceButton)
								.add(18, 18, 18)
								.add(this.takeSecretPassageButton)
								.add(18, 18, 18)
								.add(this.makeSuggestionButton)
								.addContainerGap(66, Short.MAX_VALUE))
						);

				this.bg.add(this.fgPanel, new java.awt.GridBagConstraints());

				this.bgLabel.setIcon(ClueMain.getImageIcon("TurnFrame.png")); // NOI18N

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
						.add(this.bg, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						);
				layout.setVerticalGroup(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(this.bg, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						);

				this.pack();
			}// </editor-fold>//GEN-END:initComponents

			private void rollDiceButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollDiceButtonActionPerformed
				ClueMain.map.resetHighlights();

				final int roll = ClueMain.mapView.rollDice();

				ClueMain.map.highlightReachablePaths(this.player.getLocation(), ClueMain.pathfinder, roll);
				ClueMain.mapView.repaint();

				this.action = ACTION_ROLLED_DICE;

				this.dispose();
			}//GEN-LAST:event_rollDiceButtonActionPerformed

			private void takeSecretPassageButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_takeSecretPassageButtonActionPerformed
				this.action = ACTION_TOOK_PASSAGE;
				SoundEffect.CREAK.play();
				this.dispose();
			}//GEN-LAST:event_takeSecretPassageButtonActionPerformed

			private void makeSuggestionButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeSuggestionButtonActionPerformed
				final int room_id = this.player.getLocation().getRoomId();
				if (room_id == -1) {
					return;
				}

				final SuggestionDialog2 suggestionDialog = new SuggestionDialog2(ClueMain.frame, Card.getInstance(TYPE_ROOM, room_id), this.player.getNotebook());
				final ArrayList<Card> suggestion = (ArrayList<Card>) suggestionDialog.showDialog();

				//send the suggestion for multi players
				ClueMain.showcards.setSuggestion(suggestion, this.player, ClueMain.yourPlayer, ClueMain.clue.getPlayers());

				//for single player, continue with the showing of the cards
				if (!ClueMain.multiplayerFrame.isConnected()) {
					ClueMain.showcards.showCards();
				}

				this.action = ACTION_MADE_SUGGESTION;

				this.dispose();
			}//GEN-LAST:event_makeSuggestionButtonActionPerformed

			// Variables declaration - do not modify//GEN-BEGIN:variables
			private javax.swing.JPanel bg;
			private javax.swing.JLabel bgLabel;
			private DropShadowPanel fgPanel;
			private javax.swing.JButton makeSuggestionButton;
			private javax.swing.JButton rollDiceButton;
			private javax.swing.JButton takeSecretPassageButton;
			// End of variables declaration//GEN-END:variables

}
