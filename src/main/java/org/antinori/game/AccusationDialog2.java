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

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class AccusationDialog2 extends javax.swing.JDialog {

	int selected_suspect = -1;
	int selected_weapon = -1;
	int selected_room = -1;

	Notebook notebook;

	String suspect_label = "[SUSPECT]";
	String weapon_label = "[WEAPON]";
	String room_label = "[ROOM]";

	BufferedImage checked_icon = null;
	BufferedImage lock_icon = null;

	ArrayList<Card> accusation = new ArrayList<>();

	public AccusationDialog2(final Frame owner, final Notebook notebook) {
		super(owner, true);

		this.setUndecorated(true);
		ClueMain.setLocationInCenter(this, -400, -300);

		this.notebook = notebook;

		this.checked_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 129, 52, 83, 83), 30);
		this.lock_icon = ClueMain.resizeImage(ClueMain.loadIcon("clue-icons.png", 44, 54, 83, 83), 30);

		this.initComponents();
	}

	//return the data after clicking OK
	public Object showDialog() {
		this.setVisible(true);
		return this.accusation;
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

	private void setButtonInfo(final javax.swing.JButton button, final String text) {
		//button.setForeground(java.awt.SystemColor.textInactiveText);
		//button.setText(text);
		button.setToolTipText(text);
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		this.bg = new javax.swing.JPanel();
		this.fgPanel = new javax.swing.JPanel();
		this.scarlet = new javax.swing.JButton();
		this.white = new javax.swing.JButton();
		this.plum = new javax.swing.JButton();
		this.green = new javax.swing.JButton();
		this.mustard = new javax.swing.JButton();
		this.knife = new javax.swing.JButton();
		this.peacock = new javax.swing.JButton();
		this.pipe = new javax.swing.JButton();
		this.rope = new javax.swing.JButton();
		this.wrench = new javax.swing.JButton();
		this.candle = new javax.swing.JButton();
		this.gun = new javax.swing.JButton();

		final String text = String.format(ClueMain.accusationFormatter, this.notebook.getPlayer().toString(), this.suspect_label, this.weapon_label, this.room_label);
		this.suggestion_ta = new SuggestionTextArea(text, 5, 10);

		this.cancelButton = new javax.swing.JButton();

		this.okButton = new javax.swing.JButton();
		this.okButton.setEnabled(false);

		this.jLabel3 = new javax.swing.JLabel();
		this.jLabel4 = new javax.swing.JLabel();
		this.jLabel5 = new javax.swing.JLabel();
		this.bgLabel = new javax.swing.JLabel();

		this.bg.setMinimumSize(new java.awt.Dimension(1, 1));
		this.bg.setLayout(new java.awt.GridBagLayout());

		this.fgPanel.setOpaque(false);
		this.fgPanel.setPreferredSize(new java.awt.Dimension(600, 500));

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

		this.library = new javax.swing.JButton();
		this.library.setIcon(this.getIcon(ButtonIcon.LIBRARY.get(), TYPE_ROOM, ROOM_LIBRARY));
		this.study = new javax.swing.JButton();
		this.study.setIcon(this.getIcon(ButtonIcon.STUDY.get(), TYPE_ROOM, ROOM_STUDY));
		this.kitchen = new javax.swing.JButton();
		this.kitchen.setIcon(this.getIcon(ButtonIcon.KITCHEN.get(), TYPE_ROOM, ROOM_KITCHEN));
		this.hall = new javax.swing.JButton();
		this.hall.setIcon(this.getIcon(ButtonIcon.HALL.get(), TYPE_ROOM, ROOM_HALL));
		this.lounge = new javax.swing.JButton();
		this.lounge.setIcon(this.getIcon(ButtonIcon.LOUNGE.get(), TYPE_ROOM, ROOM_LOUNGE));
		this.billiard = new javax.swing.JButton();
		this.billiard.setIcon(this.getIcon(ButtonIcon.BILLIARD.get(), TYPE_ROOM, ROOM_BILLIARD));
		this.conservatory = new javax.swing.JButton();
		this.conservatory.setIcon(this.getIcon(ButtonIcon.CONSERVATORY.get(), TYPE_ROOM, ROOM_CONSERVATORY));
		this.ballroom = new javax.swing.JButton();
		this.ballroom.setIcon(this.getIcon(ButtonIcon.BALLROOM.get(), TYPE_ROOM, ROOM_BALLROOM));
		this.dining = new javax.swing.JButton();
		this.dining.setIcon(this.getIcon(ButtonIcon.DINING.get(), TYPE_ROOM, ROOM_DINING));

		this.setButtonInfo(this.conservatory, "Conservatory");
		this.setButtonInfo(this.library, "Library");
		this.setButtonInfo(this.hall, "Hall");
		this.setButtonInfo(this.study, "Study");
		this.setButtonInfo(this.lounge, "Lounge");
		this.setButtonInfo(this.kitchen, "Kitchen");
		this.setButtonInfo(this.billiard, "Billiard");
		this.setButtonInfo(this.dining, "Dining");
		this.setButtonInfo(this.ballroom, "Ballroom");

		this.scarlet.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.scarletActionPerformed(evt);
			}
		});

		this.white.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.whiteActionPerformed(evt);
			}
		});

		this.plum.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.plumActionPerformed(evt);
			}
		});

		this.green.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.greenActionPerformed(evt);
			}
		});

		this.mustard.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.mustardActionPerformed(evt);
			}
		});

		this.knife.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.knifeActionPerformed(evt);
			}
		});

		this.peacock.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.peacockActionPerformed(evt);
			}
		});

		this.pipe.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.pipeActionPerformed(evt);
			}
		});

		this.rope.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.ropeActionPerformed(evt);
			}
		});

		this.wrench.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.wrenchActionPerformed(evt);
			}
		});

		this.candle.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.candleActionPerformed(evt);
			}
		});

		this.gun.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.gunActionPerformed(evt);
			}
		});

		this.suggestion_ta.setColumns(20);
		this.suggestion_ta.setRows(5);

		this.okButton.setText("OK");
		this.okButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.okButtonActionPerformed(evt);
			}
		});

		this.conservatory.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.conservatoryActionPerformed(evt);
			}
		});

		this.library.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.libraryActionPerformed(evt);
			}
		});

		this.study.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.studyActionPerformed(evt);
			}
		});

		this.hall.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.hallActionPerformed(evt);
			}
		});

		this.billiard.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.billiardActionPerformed(evt);
			}
		});

		this.lounge.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.loungeActionPerformed(evt);
			}
		});

		this.kitchen.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.kitchenActionPerformed(evt);
			}
		});

		this.ballroom.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.ballroomActionPerformed(evt);
			}
		});

		this.dining.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.diningActionPerformed(evt);
			}
		});

		this.jLabel3.setText("Select a room:");

		this.jLabel4.setText("Select a weapon:");

		this.jLabel5.setText("Select a suspect:");

		this.cancelButton.setText("Cancel");
		this.cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AccusationDialog2.this.cancelButtonActionPerformed(evt);
			}
		});

		final org.jdesktop.layout.GroupLayout fgPanelLayout = new org.jdesktop.layout.GroupLayout(this.fgPanel);
		this.fgPanel.setLayout(fgPanelLayout);
		fgPanelLayout.setHorizontalGroup(
				fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(org.jdesktop.layout.GroupLayout.TRAILING, fgPanelLayout.createSequentialGroup()
						.addContainerGap(113, Short.MAX_VALUE)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(this.jLabel5)
								.add(this.jLabel4)
								.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
										.add(fgPanelLayout.createSequentialGroup()
												.add(this.knife, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.pipe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.rope, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.wrench, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.candle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.gun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(fgPanelLayout.createSequentialGroup()
												.add(this.scarlet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.mustard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.green, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.plum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.white, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(this.peacock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(this.jLabel3)
												.add(fgPanelLayout.createSequentialGroup()
														.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
																.add(this.conservatory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(fgPanelLayout.createSequentialGroup()
																		.add(76, 76, 76)
																		.add(this.library, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																.add(fgPanelLayout.createSequentialGroup()
																		.add(228, 228, 228)
																		.add(this.hall, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																.add(fgPanelLayout.createSequentialGroup()
																		.add(304, 304, 304)
																		.add(this.billiard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																.add(fgPanelLayout.createSequentialGroup()
																		.add(228, 228, 228)
																		.add(this.kitchen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																.add(fgPanelLayout.createSequentialGroup()
																		.add(304, 304, 304)
																		.add(this.ballroom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																.add(fgPanelLayout.createSequentialGroup()
																		.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
																				.add(org.jdesktop.layout.GroupLayout.LEADING, this.suggestion_ta)
																				.add(org.jdesktop.layout.GroupLayout.LEADING, fgPanelLayout.createSequentialGroup()
																						.add(152, 152, 152)
																						.add(this.study, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																		.add(this.okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
																.add(this.lounge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(this.dining, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(this.cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
						.add(37, 37, 37))
				);
		fgPanelLayout.setVerticalGroup(
				fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(org.jdesktop.layout.GroupLayout.TRAILING, fgPanelLayout.createSequentialGroup()
						.add(this.jLabel5)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(this.scarlet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.mustard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.green, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.plum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.white, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.peacock, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
						.add(5, 5, 5)
						.add(this.jLabel4)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(this.candle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.wrench, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.knife, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.pipe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.rope, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(this.gun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(this.jLabel3)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
								.add(fgPanelLayout.createSequentialGroup()
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(fgPanelLayout.createSequentialGroup()
														.add(this.billiard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.ballroom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
												.add(fgPanelLayout.createSequentialGroup()
														.add(this.hall, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.kitchen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
												.add(fgPanelLayout.createSequentialGroup()
														.add(this.lounge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
														.add(this.dining, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
												.add(this.cancelButton)
												.add(this.okButton)))
								.add(fgPanelLayout.createSequentialGroup()
										.add(fgPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(this.study, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.add(this.conservatory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.add(this.library, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
										.add(this.suggestion_ta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
						.add(109, 109, 109))
				);

		this.bg.add(this.fgPanel, new java.awt.GridBagConstraints());

		this.bgLabel.setIcon(ClueMain.getImageIcon("frame1.png")); // NOI18N

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

	private void conservatoryActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conservatoryActionPerformed
		this.buttonClicked(ROOM_CONSERVATORY, TYPE_ROOM);
	}//GEN-LAST:event_conservatoryActionPerformed

	private void libraryActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryActionPerformed
		this.buttonClicked(ROOM_LIBRARY, TYPE_ROOM);
	}//GEN-LAST:event_libraryActionPerformed

	private void studyActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studyActionPerformed
		this.buttonClicked(ROOM_STUDY, TYPE_ROOM);
	}//GEN-LAST:event_studyActionPerformed

	private void hallActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hallActionPerformed
		this.buttonClicked(ROOM_HALL, TYPE_ROOM);
	}//GEN-LAST:event_hallActionPerformed

	private void billiardActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_billiardActionPerformed
		this.buttonClicked(ROOM_BILLIARD, TYPE_ROOM);
	}//GEN-LAST:event_billiardActionPerformed

	private void loungeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loungeActionPerformed
		this.buttonClicked(ROOM_LOUNGE, TYPE_ROOM);
	}//GEN-LAST:event_loungeActionPerformed

	private void kitchenActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kitchenActionPerformed
		this.buttonClicked(ROOM_KITCHEN, TYPE_ROOM);
	}//GEN-LAST:event_kitchenActionPerformed

	private void ballroomActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ballroomActionPerformed
		this.buttonClicked(ROOM_BALLROOM, TYPE_ROOM);
	}//GEN-LAST:event_ballroomActionPerformed

	private void diningActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diningActionPerformed
		this.buttonClicked(ROOM_DINING, TYPE_ROOM);
	}//GEN-LAST:event_diningActionPerformed

	private void okButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		if (this.selected_suspect == -1 || this.selected_weapon == -1 || this.selected_room == -1) {
			return;
		}

		this.accusation.add(Card.getInstance(TYPE_ROOM, this.selected_room));
		this.accusation.add(Card.getInstance(TYPE_SUSPECT, this.selected_suspect));
		this.accusation.add(Card.getInstance(TYPE_WEAPON, this.selected_weapon));

		this.dispose();
	}//GEN-LAST:event_okButtonActionPerformed

	private void cancelButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		this.accusation = null;
		this.dispose();

	}//GEN-LAST:event_cancelButtonActionPerformed

	public void buttonClicked(final int index, final int type) {

		SoundEffect.CLICK.play();

		if (type == TYPE_SUSPECT) {
			this.selected_suspect = index;
			this.suspect_label = Card.getInstance(TYPE_SUSPECT, index).toString();

		} else if (type == TYPE_ROOM) {
			this.selected_room = index;
			this.room_label = Card.getInstance(TYPE_ROOM, index).toString();
		} else {
			this.selected_weapon = index;
			this.weapon_label = Card.getInstance(TYPE_WEAPON, index).toString();
		}

		if (this.selected_weapon != -1 && this.selected_suspect != -1 && this.selected_room != -1) {
			this.okButton.setEnabled(true);
		}

		final String text = String.format(ClueMain.accusationFormatter, this.notebook.getPlayer().toString(), this.suspect_label, this.weapon_label, this.room_label);
		this.suggestion_ta.setText(text);

	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton ballroom;
	private javax.swing.JPanel bg;
	private javax.swing.JLabel bgLabel;
	private javax.swing.JButton billiard;
	private javax.swing.JButton candle;
	private javax.swing.JButton conservatory;
	private javax.swing.JButton dining;
	private javax.swing.JPanel fgPanel;
	private javax.swing.JButton green;
	private javax.swing.JButton gun;
	private javax.swing.JButton hall;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JButton kitchen;
	private javax.swing.JButton knife;
	private javax.swing.JButton library;
	private javax.swing.JButton lounge;
	private javax.swing.JButton mustard;
	private javax.swing.JButton okButton;
	private javax.swing.JButton peacock;
	private javax.swing.JButton pipe;
	private javax.swing.JButton plum;
	private javax.swing.JButton rope;
	private javax.swing.JButton scarlet;
	private javax.swing.JButton study;
	private SuggestionTextArea suggestion_ta;
	private javax.swing.JButton white;
	private javax.swing.JButton wrench;
	private javax.swing.JButton cancelButton;

	// End of variables declaration//GEN-END:variables
	class SuggestionTextArea extends JTextArea {

		SuggestionTextArea(final String text, final int rows, final int cols) {
			super(text, rows, cols);
			this.setFont(ClueMain.FONT_14);
			//setForeground(Color.white);
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
		WRENCH("wrench-icon.png"),
		STUDY("Study-icon.png"),
		HALL("Hall-icon.png"),
		CONSERVATORY("Conservatory-icon.png"),
		BILLIARD("billiard-icon.png"),
		DINING("DiningRoom-icon.png"),
		BALLROOM("Ballroom-icon.png"),
		KITCHEN("Kitchen-icon.png"),
		LOUNGE("Lounge-icon.png"),
		LIBRARY("Library-icon.png");

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
