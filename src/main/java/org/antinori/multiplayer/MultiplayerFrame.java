package org.antinori.multiplayer;

import static org.antinori.game.Card.SUSPECT_GREEN;
import static org.antinori.game.Card.SUSPECT_MUSTARD;
import static org.antinori.game.Card.SUSPECT_PEACOCK;
import static org.antinori.game.Card.SUSPECT_PLUM;
import static org.antinori.game.Card.SUSPECT_SCARLET;
import static org.antinori.game.Card.SUSPECT_WHITE;
import static org.antinori.game.Card.TYPE_ROOM;
import static org.antinori.game.Card.TYPE_SUSPECT;
import static org.antinori.game.Card.TYPE_WEAPON;
import static org.antinori.game.Card.green;
import static org.antinori.game.Card.mustard;
import static org.antinori.game.Card.peacock;
import static org.antinori.game.Card.plum;
import static org.antinori.game.Card.scarlet;
import static org.antinori.game.Card.white;
import static org.antinori.game.Player.COLOR_GREEN;
import static org.antinori.game.Player.COLOR_MUSTARD;
import static org.antinori.game.Player.COLOR_PEACOCK;
import static org.antinori.game.Player.COLOR_PLUM;
import static org.antinori.game.Player.COLOR_SCARLET;
import static org.antinori.game.Player.COLOR_WHITE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.antinori.astar.Location;
import org.antinori.game.AccusationDialog2;
import org.antinori.game.Card;
import org.antinori.game.Clue;
import org.antinori.game.ClueMain;
import org.antinori.game.PickCardsToShowDialog2;
import org.antinori.game.Player;
import org.antinori.game.SoundEffect;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.User;
import sfs2x.client.entities.match.BoolMatch;
import sfs2x.client.entities.match.MatchExpression;
import sfs2x.client.entities.match.RoomProperties;
import sfs2x.client.entities.variables.SFSUserVariable;
import sfs2x.client.entities.variables.UserVariable;
import sfs2x.client.requests.CreateRoomRequest;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.FindRoomsRequest;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LeaveRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.LogoutRequest;
import sfs2x.client.requests.PrivateMessageRequest;
import sfs2x.client.requests.PublicMessageRequest;
import sfs2x.client.requests.RoomSettings;
import sfs2x.client.requests.SetUserVariablesRequest;

public class MultiplayerFrame extends javax.swing.JPanel implements IEventListener {

	private SmartFox sfs;
	private RoomListModel roomListModel;
	private UserListModel userListModel;
	private StringBuilder chatHistory;

	String mySelectedCharacter = scarlet.toString();

	public MultiplayerFrame() {

		this.initComponents();

		// Creates new instance of SmatFoxClient and adds the event handlers
		this.sfs = new SmartFox();
		this.sfs.addEventListener(SFSEvent.CONFIG_LOAD_SUCCESS, this);
		this.sfs.addEventListener(SFSEvent.CONFIG_LOAD_FAILURE, this);
		this.sfs.addEventListener(SFSEvent.CONNECTION, this);
		this.sfs.addEventListener(SFSEvent.CONNECTION_LOST, this);
		this.sfs.addEventListener(SFSEvent.HANDSHAKE, this);

		this.sfs.addEventListener(SFSEvent.CONNECTION_RETRY, this);
		this.sfs.addEventListener(SFSEvent.CONNECTION_RESUME, this);

		this.sfs.addEventListener(SFSEvent.LOGIN, this);
		this.sfs.addEventListener(SFSEvent.LOGOUT, this);

		this.sfs.addEventListener(SFSEvent.ROOM_FIND_RESULT, this);
		this.sfs.addEventListener(SFSEvent.ROOM_ADD, this);
		this.sfs.addEventListener(SFSEvent.ROOM_CREATION_ERROR, this);
		this.sfs.addEventListener(SFSEvent.ROOM_REMOVE, this);
		this.sfs.addEventListener(SFSEvent.USER_COUNT_CHANGE, this);

		this.sfs.addEventListener(SFSEvent.ROOM_JOIN, this);
		this.sfs.addEventListener(SFSEvent.ROOM_JOIN_ERROR, this);

		this.sfs.addEventListener(SFSEvent.USER_ENTER_ROOM, this);
		this.sfs.addEventListener(SFSEvent.USER_EXIT_ROOM, this);

		this.sfs.addEventListener(SFSEvent.PUBLIC_MESSAGE, this);
		this.sfs.addEventListener(SFSEvent.PRIVATE_MESSAGE, this);

		this.sfs.addEventListener(SFSEvent.OBJECT_MESSAGE, this);
		this.sfs.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);

		// Sets the Room and User list models to the user and room lists
		this.roomListModel = (RoomListModel) this.listRooms.getModel();
		this.userListModel = (UserListModel) this.listUsers.getModel();

	}

	public boolean isConnected() {
		return this.sfs.isConnected();
	}

	@Override
	public void dispatch(final BaseEvent event) throws SFSException {

		if (event.getType().equals(SFSEvent.CONFIG_LOAD_SUCCESS)) {

		} else if (event.getType().equals(SFSEvent.CONFIG_LOAD_FAILURE)) {

		} else if (event.getType().equals(SFSEvent.CONNECTION)) {

			System.out.println(event.getArguments());

			if (event.getArguments().get("success") != null) {

				final MatchExpression exp = new MatchExpression(RoomProperties.HAS_FREE_PLAYER_SLOTS, BoolMatch.EQUALS, true);
				this.sfs.send(new FindRoomsRequest(exp));

			} else {

				JOptionPane.showMessageDialog(ClueMain.frame, "Failed connecting to " + this.sfs.getConfig().getHost() + ":" + this.sfs.getConfig().getPort(), "", JOptionPane.ERROR_MESSAGE);

			}

		} else if (event.getType().equals(SFSEvent.CONNECTION_LOST)) {

			System.out.println(this.textUserName.getText() + " CONNECTION_LOST: " + event.getArguments());

			this.buttonJoin.setEnabled(false);
			this.buttonSendPrivate.setEnabled(false);
			this.buttonLogout.setEnabled(false);
			this.textChatMessage.setEnabled(false);
			this.buttonSend.setEnabled(false);

			this.LoginButton.setEnabled(true);

			this.toggleRadioButtons(false);

			this.roomListModel.removeRooms();
			this.userListModel.removeUsers();

		} else if (event.getType().equals(SFSEvent.CONNECTION_RETRY)) {
			System.out.println(this.textUserName.getText() + " CONNECTION_RETRY: " + event.getArguments());

		} else if (event.getType().equals(SFSEvent.CONNECTION_RESUME)) {
			System.out.println(this.textUserName.getText() + " CONNECTION_RESUME: " + event.getArguments());

		} else if (event.getType().equals(SFSEvent.LOGIN)) {
			System.out.println(this.textUserName.getText() + " LOGIN: " + event.getArguments());

		} else if (event.getType().equals(SFSEvent.HANDSHAKE)) {

			System.out.println(this.textUserName.getText() + " HANDSHAKE: " + event.getArguments());

			//login the user
			this.sfs.send(new LoginRequest(this.textUserName.getText(), "", "Clue"));
			this.LoginButton.setEnabled(false);

			this.chatHistory = new StringBuilder();
			this.textChatHistory.setText("");

			this.setMyCharacterUserVar();
			this.toggleRadioButtons(true);

			//get the list of rooms
			final MatchExpression exp = new MatchExpression(RoomProperties.HAS_FREE_PLAYER_SLOTS, BoolMatch.EQUALS, true);
			this.sfs.send(new FindRoomsRequest(exp));

		} else if (event.getType().equals(SFSEvent.LOGOUT)) {

			System.out.println(this.textUserName.getText() + " LOGOUT: " + event.getArguments().toString());

			this.roomListModel.removeRooms();
			this.userListModel.removeUsers();

			this.LeaveRoomButton.setEnabled(false);

			this.sfs.disconnect();

		} else if (event.getType().equals(SFSEvent.OBJECT_MESSAGE)) {

			System.out.println(this.textUserName.getText() + " OBJECT_MESSAGE: " + event.getArguments().toString());

		} else if (event.getType().equals(SFSEvent.ROOM_FIND_RESULT)) {

			System.out.println("Rooms found: " + event.getArguments().get("rooms"));
			final ArrayList<Room> rooms = (ArrayList) event.getArguments().get("rooms");
			for (final Room room : rooms) {
				this.roomListModel.addRoom(room);
			}

			// When new room is created it's added to the room list.
		} else if (event.getType().equals(SFSEvent.ROOM_ADD)) {

			System.out.println(this.textUserName.getText() + " ROOM_ADD: " + event.getArguments());

			final Room room = (Room) event.getArguments().get("room");
			this.roomListModel.addRoom(room);

		} // If room creation failed an error message is shown
		else if (event.getType().equals(SFSEvent.ROOM_CREATION_ERROR)) {

			final String error = "Room creation error: " + event.getArguments().get("errorMessage").toString();
			JOptionPane.showMessageDialog(ClueMain.frame, error, "", JOptionPane.ERROR_MESSAGE);

		} // When a room is deleted it's removed from the room list.
		else if (event.getType().equals(SFSEvent.ROOM_REMOVE)) {

			System.out.println(this.textUserName.getText() + " ROOM_REMOVE: " + event.getArguments());

			final Room room = (Room) event.getArguments().get("room");
			this.roomListModel.removeRoom(room);
		} // When the user count change the room list is refreshed
		else if (event.getType().equals(SFSEvent.USER_COUNT_CHANGE)) {

			System.out.println(this.textUserName.getText() + " USER_COUNT_CHANGE: " + event.getArguments());

			final Room room = (Room) event.getArguments().get("room");
			this.roomListModel.updateRoom(room);

		} else if (event.getType().equals(SFSEvent.ROOM_JOIN)) {
			//fired when your user joins a room

			System.out.println(this.textUserName.getText() + " ROOM_JOIN: " + event.getArguments());

			final Room room = (Room) event.getArguments().get("room");

			this.roomListModel.updateRoom(room);

			this.listRooms.setSelectedValue(room, true);
			this.buttonJoin.setEnabled(false);
			this.textChatMessage.setEnabled(true);
			this.buttonSend.setEnabled(true);
			this.LeaveRoomButton.setEnabled(true);

			this.userListModel.setUserList(room.getUserList());

			this.chatHistory.append("<font color='#cc0000'>{ Room <b>");
			this.chatHistory.append(room.getName());
			this.chatHistory.append("</b> joined }</font><br>");
			this.textChatHistory.setText(this.chatHistory.toString());
			this.textChatMessage.requestFocus();

			this.scarletRadio.setEnabled(false);
			this.mustardRadio.setEnabled(false);
			this.greenRadio.setEnabled(false);
			this.whiteRadio.setEnabled(false);
			this.peacockRadio.setEnabled(false);
			this.plumRadio.setEnabled(false);

			//set the room name in the user so we can set the game over flag when they disconnect
			final List<UserVariable> userVars = new ArrayList<>();
			userVars.add(new SFSUserVariable("room", room.getName()));
			this.sfs.send(new SetUserVariablesRequest(userVars));

		} else if (event.getType().equals(SFSEvent.EXTENSION_RESPONSE)) {
			System.out.println(this.textUserName.getText() + " EXTENSION_RESPONSE: " + event.getArguments());

			if (event.getArguments().get("cmd").equals("validateJoinRoom")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");
				System.out.println(obj.getUtfString("message"));

				if (!obj.getBool("validated")) {
					JOptionPane.showMessageDialog(ClueMain.frame, obj.getUtfString("message"), "", JOptionPane.ERROR_MESSAGE);
				}

			} else if (event.getArguments().get("cmd").equals("gameOver")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");
				System.out.println(obj.getUtfString("message"));

				this.setChatText("GAME", obj.getUtfString("message"), false);

				//JOptionPane.showMessageDialog(ClueMain.frame,obj.getUtfString("message"), "", JOptionPane.WARNING_MESSAGE);
			} else if (event.getArguments().get("cmd").equals("setPlayers")) {

				ClueMain.clue = new Clue();

				this.setYourMultiplayer();

				final SFSObject obj = (SFSObject) event.getArguments().get("params");
				final ISFSArray ids = obj.getSFSArray("ids");
				final ISFSArray names = obj.getSFSArray("names");
				for (int i = 0; i < ids.size(); i++) {
					this.setOtherMultiplayer(ids.getInt(i), names.getUtfString(i));
				}

			} else if (event.getArguments().get("cmd").equals("dealtCard")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");
				final Card card = (Card) obj.getClass("card");
				ClueMain.yourPlayer.addCard(card);
				System.out.println("Got card: " + card);

				this.dealButton.setEnabled(false);

			} else if (event.getArguments().get("cmd").equals("getSet")) {

				ClueMain.setUpMultiplayerGame();

				this.setChatText("GAME", "Your notebook has been set.", false);
				this.setChatText("GAME", "Player locations have been set.", false);

			} else if (event.getArguments().get("cmd").equals("startTurn")) {

				this.setChatText("GAME", "It's your turn now.", false);

				ClueMain.threadPoolExecutor.execute(new Runnable() {
					@Override
					public void run() {
						ClueMain.setCurrentPlayer(ClueMain.yourPlayer);
						ClueMain.turn.startTurnMultiplayerTurn(ClueMain.yourPlayer);
					}
				});

			} else if (event.getArguments().get("cmd").equals("diceRoll")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final int roll1 = obj.getInt("roll1");
				final int roll2 = obj.getInt("roll2");

				SoundEffect.DICE.play();

				ClueMain.mapView.rolledDiceImageLeft = ClueMain.mapView.dice_faces.get(roll1 - 1);
				ClueMain.mapView.rolledDiceImageRight = ClueMain.mapView.dice_faces.get(roll2 - 1);

			} else if (event.getArguments().get("cmd").equals("move")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final String name = obj.getUtfString("player");
				final int id = obj.getInt("character");
				final int fx = obj.getInt("from-x");
				final int fy = obj.getInt("from-y");
				final int tx = obj.getInt("to-x");
				final int ty = obj.getInt("to-y");
				final Color color = new Color(obj.getInt("playerColor"));

				final boolean secret = obj.getBool("secretPassage");
				if (secret) {
					SoundEffect.CREAK.play();
				}

				ClueMain.setCurrentPlayer(ClueMain.clue.getPlayer(id));

				final Location from = ClueMain.map.getLocation(fx, fy);
				final Location to = ClueMain.map.getLocation(tx, ty);

				ClueMain.setPlayerLocationFromMapClick(ClueMain.currentTurnPlayer, color, from, to);
				ClueMain.mapView.repaint();

				//you have been called over in a suggestion
				if (ClueMain.clue.getPlayer(id) == ClueMain.yourPlayer) {
					final int roomid = ClueMain.mapView.getRoomRoomNameAtLocation(tx, ty);
					this.showTimedDialogAlert("You have has been called to the " + (roomid != -1 ? Card.getInstance(TYPE_ROOM, roomid).toString() : ""));
				}

				this.setChatText(name, " has moved.", false);

			} else if (event.getArguments().get("cmd").equals("suggestion")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final String name = obj.getUtfString("suggesting_player_name");
				final Card suggesting_suspect = Card.getInstance(TYPE_SUSPECT, obj.getInt("suggesting_player_suspectId"));
				final Card suspect = Card.getInstance(TYPE_SUSPECT, obj.getInt("suspect"));
				final Card weapon = Card.getInstance(TYPE_WEAPON, obj.getInt("weapon"));
				final Card room = Card.getInstance(TYPE_ROOM, obj.getInt("room"));

				final String suggestion_text = String.format(ClueMain.formatter, suggesting_suspect.toString(), suspect.toString(), weapon.toString(), room.toString());
				this.setChatText(name, suggestion_text, false);

			} else if (event.getArguments().get("cmd").equals("showCardRequest")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final String name = obj.getUtfString("suggesting_player_name");
				final Card suggesting_suspect = Card.getInstance(TYPE_SUSPECT, obj.getInt("suggesting_player_suspectId"));
				final Card suspect = Card.getInstance(TYPE_SUSPECT, obj.getInt("suspect"));
				final Card weapon = Card.getInstance(TYPE_WEAPON, obj.getInt("weapon"));
				final Card room = Card.getInstance(TYPE_ROOM, obj.getInt("room"));

				final String suggestion_text = String.format(ClueMain.formatter, suggesting_suspect.toString(), suspect.toString(), weapon.toString(), room.toString());

				final ArrayList<Card> cards = new ArrayList<>();
				cards.add(suspect);
				cards.add(weapon);
				cards.add(room);

				final PickCardsToShowDialog2 dialog = new PickCardsToShowDialog2(cards, suggestion_text, ClueMain.yourPlayer);
				final Card card_to_show = dialog.showDialog();

				this.sendShowCardResponse(card_to_show, ClueMain.yourPlayer, name);

			} else if (event.getArguments().get("cmd").equals("showCardResponse")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final int ct = obj.getInt("card_type");

				if (ct == -100) {
					ClueMain.turn.multiplayerGotShowCardResponse = true;
				} else {
					final String showing_player_name = obj.getUtfString("showing_player_name");
					final String player_to_show = obj.getUtfString("player_to_show");
					final int cv = obj.getInt("card_value");
					String text = showing_player_name;

					if (ct == -1) {
						text += " does not have a card to show.";
					} else if (ct == -99) {
						text += " showed a card to " + player_to_show;
					} else {
						text += " is showing " + Card.getInstance(ct, cv).toString();
					}

					this.setChatText(showing_player_name, text, false);
					this.showTimedDialogAlert(text);

					if (ct >= 0) {
						ClueMain.turn.multiplayerGotShowCardResponse = true;
					}

				}

			} else if (event.getArguments().get("cmd").equals("accusation")) {

				final SFSObject obj = (SFSObject) event.getArguments().get("params");

				final String name = obj.getUtfString("accusingPlayer");
				final int s = obj.getInt("suspect");
				final int w = obj.getInt("weapon");
				final int r = obj.getInt("room");
				final boolean valid = obj.getBool("valid");

				final Card suspect = Card.getInstance(TYPE_SUSPECT, s);
				final Card weapon = Card.getInstance(TYPE_WEAPON, w);
				final Card room = Card.getInstance(TYPE_ROOM, r);

				final String text = String.format(ClueMain.accusationFormatter, name, suspect.toString(), weapon.toString(), room.toString());

				this.setChatText("GAME", text, false);
				this.setChatText("GAME", "The accusation is " + valid + ".", false);

				if (valid) {
					SoundEffect.GASP.play();
				} else {
					final int rand = new Random().nextInt(10);
					if (rand > 5) {
						SoundEffect.LAUGH.play();
					} else {
						SoundEffect.GIGGLE.play();
					}
					final Player p = ClueMain.clue.getPlayer(name);
					if (p != null) {
						p.setHasMadeFalseAccusation();
					}
				}

				ClueMain.threadPoolExecutor.execute(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(ClueMain.frame, text + "\n\nThe accusation is " + valid + ".", "Accusation", JOptionPane.PLAIN_MESSAGE);
					}
				});

			}

		} else if (event.getType().equals(SFSEvent.ROOM_JOIN_ERROR)) {
			System.out.println(this.textUserName.getText() + " ROOM_JOIN_ERROR: " + event.getArguments());

			JOptionPane.showMessageDialog(ClueMain.frame, "" + event.getArguments().get("errorMessage"), "", JOptionPane.ERROR_MESSAGE);

		} else if (event.getType().equals(SFSEvent.USER_ENTER_ROOM)) {
			//fired when another user joins this room

			System.out.println(this.textUserName.getText() + " USER_ENTER_ROOM: " + event.getArguments());

			final User user = (User) event.getArguments().get("user");
			this.userListModel.addUser(user);

			final Room room = (Room) event.getArguments().get("room");

			if (room != null && room.isJoined() && room.getUserCount() > 2) {
				this.dealButton.setEnabled(true);
				this.setChatText("GAME", "You may start the game and deal the cards.", false);
			} else {
				this.dealButton.setEnabled(false);
				this.setChatText("GAME", "Waiting for more players to join.", false);
			}

		} // When a user leave the room the user list is updated
		else if (event.getType().equals(SFSEvent.USER_EXIT_ROOM)) {
			System.out.println(this.textUserName.getText() + " USER_EXIT_ROOM: " + event.getArguments());

			final User user = (User) event.getArguments().get("user");
			this.userListModel.removeUser(user.getId());

		} else if (event.getType().equals(SFSEvent.USER_VARIABLES_UPDATE)) {
			System.out.println(this.textUserName.getText() + " USER_VARIABLES_UPDATE: " + event.getArguments());

		} else if (event.getType().equals(SFSEvent.ROOM_VARIABLES_UPDATE)) {
			System.out.println(this.textUserName.getText() + " ROOM_VARIABLES_UPDATE: " + event.getArguments());

		} else if (event.getType().equals(SFSEvent.PUBLIC_MESSAGE)) {

			final User sender = (User) event.getArguments().get("sender");
			final String msg = event.getArguments().get("message").toString();
			this.setChatText(sender.getName(), msg, false);

		} else if (event.getType().equals(SFSEvent.PRIVATE_MESSAGE)) {

			final User sender = (User) event.getArguments().get("sender");
			final String msg = event.getArguments().get("message").toString();
			this.setChatText(sender.getName(), msg, true);
		}
	}

	public void setChatText(final String senderName, String msg, final boolean pm) {
		msg = MessageProcessor.parseSmiles(msg);

		if (!pm) {
			this.chatHistory.append("<b>[");
		} else {
			this.chatHistory.append("<b><font color='#550000'>[PM - ");
		}

		this.chatHistory.append(senderName);
		this.chatHistory.append("]:</b> ");
		this.chatHistory.append(msg);
		this.chatHistory.append("<br>");
		this.textChatHistory.setText(this.chatHistory.toString());
		try {
			final Document document = this.textChatHistory.getDocument();
			this.textChatHistory.setCaretPosition(document.getLength());
		} catch (final Exception e) {
		}
	}

	public void showTimedDialogAlert(final String text) {
		ClueMain.threadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				MultiplayerFrame.this.alertTextArea.setText(text);
				MultiplayerFrame.this.dialogAlert.start();
				MultiplayerFrame.this.dialogAlert.setVisible(true);
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(260, 800);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		this.dialogNewRoom = new javax.swing.JDialog();
		this.labelRoomName = new javax.swing.JLabel();
		this.textNewRoomName = new javax.swing.JTextField();
		this.labelMaxUsers = new javax.swing.JLabel();
		this.labelPassword = new javax.swing.JLabel();
		this.textNewRoomPassword = new javax.swing.JPasswordField();
		this.buttonCreateRoomOk = new javax.swing.JButton();
		this.buttonCreateRoomCancel = new javax.swing.JButton();
		this.sliderNewRoomMaxUsers = new javax.swing.JSlider();
		this.dialogPrivate = new javax.swing.JDialog();
		this.labelPrivateMessage = new javax.swing.JLabel();
		this.textPrivateMessage = new javax.swing.JTextField();
		this.buttonSendPrivateOk = new javax.swing.JButton();
		this.buttonSendPrivateCancel = new javax.swing.JButton();
		this.dialogJoinPrivateRoom = new javax.swing.JDialog();
		this.labelPrivateRoomPassword = new javax.swing.JLabel();
		this.textJoinPrivatePassword = new javax.swing.JPasswordField();
		this.buttonJoinPrivateCancel = new javax.swing.JButton();
		this.buttonJoinPrivateOk = new javax.swing.JButton();
		this.dialogLogin = new javax.swing.JDialog();
		this.labelUserName = new javax.swing.JLabel();
		this.textUserName = new javax.swing.JTextField();
		this.serverIPTextField = new javax.swing.JTextField();
		this.ipLabel = new javax.swing.JLabel();

		this.buttonLogin = new javax.swing.JButton();
		this.dialogAlert = new TimedDialogAlert();
		this.alertTextArea = new javax.swing.JTextArea();
		this.buttonAlertOk = new javax.swing.JButton();
		this.labelChatHistory = new javax.swing.JLabel();
		this.scrollPaneChatHistory = new javax.swing.JScrollPane();
		this.textChatHistory = new javax.swing.JTextPane();
		this.textChatMessage = new javax.swing.JTextField();
		this.buttonSend = new javax.swing.JButton();
		this.scrollPaneRoomList = new javax.swing.JScrollPane();
		this.listRooms = new javax.swing.JList();
		this.labelRoomList = new javax.swing.JLabel();
		this.buttonJoin = new javax.swing.JButton();
		this.buttonNewRoom = new javax.swing.JButton();
		this.scrollPaneUserList = new javax.swing.JScrollPane();
		this.listUsers = new javax.swing.JList();
		this.buttonSendPrivate = new javax.swing.JButton();
		this.labelUserList = new javax.swing.JLabel();
		this.buttonLogout = new javax.swing.JButton();
		this.LeaveRoomButton = new javax.swing.JButton();
		this.LeaveRoomButton.setEnabled(false);

		this.PlayerIconLabel = new javax.swing.JLabel();
		this.PlayerDescriptionArea = new javax.swing.JTextArea();

		this.playerSelectGroup = new javax.swing.ButtonGroup();

		this.mustardRadio = new javax.swing.JRadioButton();
		this.plumRadio = new javax.swing.JRadioButton();
		this.peacockRadio = new javax.swing.JRadioButton();
		this.whiteRadio = new javax.swing.JRadioButton();
		this.greenRadio = new javax.swing.JRadioButton();
		this.scarletRadio = new javax.swing.JRadioButton();

		this.PlayerIconLabel.setIcon(PlayerIcon.SCARLET.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_SCARLET]);
		this.PlayerDescriptionArea.setColumns(20);
		this.PlayerDescriptionArea.setRows(5);
		this.PlayerDescriptionArea.setEditable(false);
		this.PlayerDescriptionArea.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
		this.PlayerDescriptionArea.setLineWrap(true);

		this.jLabel1 = new javax.swing.JLabel();

		this.dealButton = new javax.swing.JButton();
		this.singlePlayerButton = new javax.swing.JButton();
		this.LoginButton = new javax.swing.JButton();
		endTurnButton = new javax.swing.JButton();
		endTurnButton.setEnabled(false);
		accuseButton = new javax.swing.JButton();
		accuseButton.setEnabled(false);
		this.exitGameButton = new javax.swing.JButton();

		this.dialogNewRoom.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.dialogNewRoom.setTitle("New Game Room");
		this.dialogNewRoom.setModal(true);
		this.dialogNewRoom.setName(null);
		this.dialogNewRoom.setResizable(false);

		this.labelRoomName.setText("Room name:");

		this.labelMaxUsers.setText("Max. users:");

		this.labelPassword.setText("Password (optional):");

		this.buttonCreateRoomOk.setText("Create");
		this.buttonCreateRoomOk.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonCreateRoomOkActionPerformed(evt);
			}
		});

		this.buttonCreateRoomCancel.setText("Cancel");
		this.buttonCreateRoomCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonCreateRoomCancelActionPerformed(evt);
			}
		});

		this.dealButton.setEnabled(false);
		this.dealButton.setText("Start Game");
		this.dealButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.dealButtonActionPerformed(evt);
			}
		});

		this.sliderNewRoomMaxUsers.setMajorTickSpacing(10);
		this.sliderNewRoomMaxUsers.setMaximum(50);
		this.sliderNewRoomMaxUsers.setMinimum(10);
		this.sliderNewRoomMaxUsers.setMinorTickSpacing(10);
		this.sliderNewRoomMaxUsers.setPaintLabels(true);
		this.sliderNewRoomMaxUsers.setPaintTicks(true);
		this.sliderNewRoomMaxUsers.setSnapToTicks(true);

		final javax.swing.GroupLayout dialogNewRoomLayout = new javax.swing.GroupLayout(this.dialogNewRoom.getContentPane());
		this.dialogNewRoom.getContentPane().setLayout(dialogNewRoomLayout);
		dialogNewRoomLayout.setHorizontalGroup(dialogNewRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogNewRoomLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						dialogNewRoomLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								dialogNewRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(this.textNewRoomName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
								.addComponent(this.labelRoomName).addComponent(this.labelMaxUsers).addComponent(this.sliderNewRoomMaxUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.labelPassword).addComponent(this.textNewRoomPassword))
						.addGroup(dialogNewRoomLayout.createSequentialGroup().addComponent(this.buttonCreateRoomOk).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonCreateRoomCancel)))
				.addContainerGap(26, Short.MAX_VALUE)));
		dialogNewRoomLayout.setVerticalGroup(dialogNewRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogNewRoomLayout.createSequentialGroup().addContainerGap().addComponent(this.labelRoomName).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.textNewRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(this.labelMaxUsers)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.sliderNewRoomMaxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18).addComponent(this.labelPassword).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.textNewRoomPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
				.addGroup(dialogNewRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(this.buttonCreateRoomOk).addComponent(this.buttonCreateRoomCancel))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		this.dialogPrivate.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.dialogPrivate.setTitle("Send Private Message");
		this.dialogPrivate.setModal(true);
		this.dialogPrivate.setName(null);
		this.dialogPrivate.setResizable(false);

		this.labelPrivateMessage.setText("Message:");

		this.buttonSendPrivateOk.setText("Send");
		this.buttonSendPrivateOk.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonSendPrivateOkActionPerformed(evt);
			}
		});

		this.buttonSendPrivateCancel.setText("Cancel");
		this.buttonSendPrivateCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonSendPrivateCancelActionPerformed(evt);
			}
		});

		final javax.swing.GroupLayout dialogPrivateLayout = new javax.swing.GroupLayout(this.dialogPrivate.getContentPane());
		this.dialogPrivate.getContentPane().setLayout(dialogPrivateLayout);
		dialogPrivateLayout.setHorizontalGroup(dialogPrivateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogPrivateLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						dialogPrivateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(this.textPrivateMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE).addComponent(this.labelPrivateMessage)
						.addGroup(dialogPrivateLayout.createSequentialGroup().addComponent(this.buttonSendPrivateOk).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonSendPrivateCancel)))
				.addContainerGap()));
		dialogPrivateLayout.setVerticalGroup(dialogPrivateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogPrivateLayout.createSequentialGroup().addContainerGap().addComponent(this.labelPrivateMessage).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.textPrivateMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
				.addGroup(dialogPrivateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(this.buttonSendPrivateOk).addComponent(this.buttonSendPrivateCancel))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		this.dialogJoinPrivateRoom.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.dialogJoinPrivateRoom.setTitle("Join Private Room");
		this.dialogJoinPrivateRoom.setModal(true);
		this.dialogJoinPrivateRoom.setName(null);
		this.dialogJoinPrivateRoom.setResizable(false);

		this.labelPrivateRoomPassword.setText("Password:");

		this.buttonJoinPrivateCancel.setText("Cancel");
		this.buttonJoinPrivateCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonJoinPrivateCancelActionPerformed(evt);
			}
		});

		this.buttonJoinPrivateOk.setText("Join Game Room");
		this.buttonJoinPrivateOk.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonJoinPrivateOkActionPerformed(evt);
			}
		});

		final javax.swing.GroupLayout dialogJoinPrivateRoomLayout = new javax.swing.GroupLayout(this.dialogJoinPrivateRoom.getContentPane());
		this.dialogJoinPrivateRoom.getContentPane().setLayout(dialogJoinPrivateRoomLayout);
		dialogJoinPrivateRoomLayout.setHorizontalGroup(dialogJoinPrivateRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogJoinPrivateRoomLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
						dialogJoinPrivateRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(this.textJoinPrivatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelPrivateRoomPassword)
						.addGroup(dialogJoinPrivateRoomLayout.createSequentialGroup().addComponent(this.buttonJoinPrivateOk).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(this.buttonJoinPrivateCancel)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		dialogJoinPrivateRoomLayout.setVerticalGroup(dialogJoinPrivateRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				dialogJoinPrivateRoomLayout.createSequentialGroup().addContainerGap().addComponent(this.labelPrivateRoomPassword).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(this.textJoinPrivatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
				.addGroup(dialogJoinPrivateRoomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(this.buttonJoinPrivateOk).addComponent(this.buttonJoinPrivateCancel))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		this.dialogLogin.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.dialogLogin.setTitle("Login");
		this.dialogLogin.setModal(true);
		this.dialogLogin.setName(null);
		this.dialogLogin.setResizable(false);
		this.dialogLogin.setLocationRelativeTo(null);

		this.labelUserName.setText("Type your player name:");

		this.buttonLogin.setText("Login");
		this.buttonLogin.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonLoginActionPerformed(evt);
			}
		});

		this.serverIPTextField.setText("127.0.0.1");
		this.ipLabel.setText("Enter the game server IP or hostname:");

		this.textUserName.addKeyListener(new java.awt.event.KeyListener() {
			@Override
			public void keyTyped(final java.awt.event.KeyEvent evt) {
			}

			@Override
			public void keyPressed(final java.awt.event.KeyEvent evt) {
				if (KeyEvent.VK_ENTER == evt.getKeyCode()) {
					MultiplayerFrame.this.buttonLogin.doClick();
				}
			}

			@Override
			public void keyReleased(final java.awt.event.KeyEvent evt) {
			}
		});

		final javax.swing.GroupLayout dialogLoginLayout = new javax.swing.GroupLayout(this.dialogLogin.getContentPane());
		this.dialogLogin.getContentPane().setLayout(dialogLoginLayout);
		dialogLoginLayout.setHorizontalGroup(
				dialogLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(dialogLoginLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(dialogLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(dialogLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(this.textUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(this.labelUserName)
										.addComponent(this.buttonLogin)
										.addComponent(this.serverIPTextField))
								.addComponent(this.ipLabel))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		dialogLoginLayout.setVerticalGroup(
				dialogLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(dialogLoginLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.labelUserName)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.textUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.ipLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.serverIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(this.buttonLogin)
						.addGap(30, 30, 30))
				);
		this.dialogLogin.pack();

		this.dialogAlert.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.dialogAlert.setModal(true);
		this.dialogAlert.setResizable(false);
		ClueMain.setLocationInCenter(this.dialogAlert, -200, 250);
		this.dialogAlert.setUndecorated(true);

		this.alertTextArea.setColumns(20);
		this.alertTextArea.setEditable(false);
		this.alertTextArea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		this.alertTextArea.setLineWrap(true);
		this.alertTextArea.setRows(5);

		this.buttonAlertOk.setText("Ok");
		this.buttonAlertOk.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonAlertOkActionPerformed(evt);
			}
		});

		final javax.swing.GroupLayout dialogAlertLayout = new javax.swing.GroupLayout(this.dialogAlert.getContentPane());
		this.dialogAlert.getContentPane().setLayout(dialogAlertLayout);
		dialogAlertLayout.setHorizontalGroup(
				dialogAlertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(dialogAlertLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.alertTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.buttonAlertOk, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
						.addContainerGap())
				);
		dialogAlertLayout.setVerticalGroup(
				dialogAlertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(dialogAlertLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(dialogAlertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(dialogAlertLayout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(this.buttonAlertOk))
								.addGroup(dialogAlertLayout.createSequentialGroup()
										.addComponent(this.alertTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 14, Short.MAX_VALUE)))
						.addContainerGap())
				);
		this.dialogAlert.pack();

		this.setMinimumSize(new java.awt.Dimension(250, 650));
		this.setName("frmMain"); // NOI18N

		this.labelChatHistory.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		this.labelChatHistory.setText("Chat History");
		this.labelChatHistory.setName("labelChatHistory"); // NOI18N

		this.textChatHistory.setContentType("text/html");
		this.textChatHistory.setEditable(false); //uneditable
		this.textChatHistory.setHighlighter(null); //unselectable
		this.textChatHistory.setName("textChatHistory"); // NOI18N
		this.textChatHistory.setEditorKit(this.configureHtmlEditorKit(this.textChatHistory));
		this.scrollPaneChatHistory.setViewportView(this.textChatHistory);

		this.textChatMessage.setEnabled(false);
		this.textChatMessage.setName("textChatMessage"); // NOI18N
		this.textChatMessage.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(final java.awt.event.KeyEvent evt) {
				MultiplayerFrame.this.textChatMessageKeyPressed(evt);
			}
		});

		this.buttonSend.setText("Send");
		this.buttonSend.setEnabled(false);
		this.buttonSend.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonSendActionPerformed(evt);
			}
		});

		this.listRooms.setModel(new RoomListModel());
		this.listRooms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		this.listRooms.setCellRenderer(new RoomListCellRenderer());
		this.listRooms.setName("listRooms"); // NOI18N
		this.listRooms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			@Override
			public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
				MultiplayerFrame.this.listRoomsValueChanged(evt);
			}
		});
		this.scrollPaneRoomList.setViewportView(this.listRooms);

		this.labelRoomList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		this.labelRoomList.setText("Game Room List");

		this.buttonJoin.setText("Join");
		this.buttonJoin.setEnabled(false);
		this.buttonJoin.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonJoinActionPerformed(evt);
			}
		});

		this.buttonNewRoom.setText("New Game Room");
		this.buttonNewRoom.setEnabled(false);
		this.buttonNewRoom.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonNewRoomActionPerformed(evt);
			}
		});

		this.listUsers.setModel(new UserListModel());
		this.listUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		this.listUsers.setCellRenderer(new UserListCellRenderer());
		this.listUsers.setVisibleRowCount(20);
		this.listUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			@Override
			public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
				MultiplayerFrame.this.listUsersValueChanged(evt);
			}
		});
		this.scrollPaneUserList.setViewportView(this.listUsers);

		this.buttonSendPrivate.setText("Send Private Message");
		this.buttonSendPrivate.setEnabled(false);
		this.buttonSendPrivate.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonSendPrivateActionPerformed(evt);
			}
		});

		this.labelUserList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		this.labelUserList.setText("Player List");

		this.buttonLogout.setText("Logout");
		this.buttonLogout.setEnabled(false);
		this.buttonLogout.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.buttonLogoutActionPerformed(evt);
			}
		});

		this.LeaveRoomButton.setText("Exit Game Room");
		this.LeaveRoomButton.setActionCommand("leave");
		this.LeaveRoomButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.LeaveRoomButtonActionPerformed(evt);
			}
		});

		this.playerSelectGroup.add(this.mustardRadio);
		this.mustardRadio.setText("Colonel Mustard");
		this.mustardRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.mustardRadioItemStateChanged(evt);
			}
		});

		this.playerSelectGroup.add(this.plumRadio);
		this.plumRadio.setText("Professor Plum");
		this.plumRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.plumRadioItemStateChanged(evt);
			}
		});

		this.playerSelectGroup.add(this.peacockRadio);
		this.peacockRadio.setText("Mrs. Peacock");
		this.peacockRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.peacockRadioItemStateChanged(evt);
			}
		});

		this.playerSelectGroup.add(this.whiteRadio);
		this.whiteRadio.setText("Mrs. White");
		this.whiteRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.whiteRadioItemStateChanged(evt);
			}
		});

		this.playerSelectGroup.add(this.greenRadio);
		this.greenRadio.setText("Mr. Green");
		this.greenRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.greenRadioItemStateChanged(evt);
			}
		});

		this.playerSelectGroup.add(this.scarletRadio);
		this.scarletRadio.setSelected(true);
		this.scarletRadio.setText("Miss Scarlet");
		this.scarletRadio.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				MultiplayerFrame.this.scarletRadioItemStateChanged(evt);
			}
		});

		this.jLabel1.setText("Select character before joining the game room");

		this.singlePlayerButton.setText("Start Single Player");
		this.singlePlayerButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.singlePlayerButtonActionPerformed(evt);
			}
		});

		this.LoginButton.setText("Login Multiplayer");
		this.LoginButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.LoginButtonActionPerformed(evt);
			}
		});

		endTurnButton.setText("End Turn");
		endTurnButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.endTurnButtonActionPerformed(evt);
			}
		});

		accuseButton.setText("Make Accusation");
		accuseButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.accuseButtonActionPerformed(evt);
			}
		});

		this.exitGameButton.setText("Quit Game");
		this.exitGameButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				MultiplayerFrame.this.exitGameButtonActionPerformed(evt);
			}
		});

		final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(this.LoginButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(this.singlePlayerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(accuseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(endTurnButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.labelChatHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(20, 20, 20)
										.addComponent(this.LeaveRoomButton))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(this.scarletRadio)
												.addGroup(layout.createSequentialGroup()
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
																.addComponent(this.whiteRadio)
																.addComponent(this.greenRadio, javax.swing.GroupLayout.Alignment.LEADING))
														.addGap(4, 4, 4)))
										.addGap(18, 18, 18)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(this.mustardRadio)
												.addComponent(this.plumRadio)
												.addComponent(this.peacockRadio)))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(this.labelUserList)
												.addComponent(this.scrollPaneUserList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
												.addComponent(this.dealButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(10, 10, 10)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(this.labelRoomList)
												.addComponent(this.buttonJoin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(this.scrollPaneRoomList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
												.addComponent(this.buttonNewRoom)))
								.addComponent(this.jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addComponent(this.buttonLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(this.exitGameButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(this.scrollPaneChatHistory, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(this.textChatMessage, javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addComponent(this.buttonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(this.buttonSendPrivate)))
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.PlayerIconLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(this.PlayerDescriptionArea, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.singlePlayerButton)
								.addComponent(endTurnButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.LoginButton)
								.addComponent(accuseButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(this.PlayerIconLabel)
								.addComponent(this.PlayerDescriptionArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.scarletRadio)
								.addComponent(this.mustardRadio))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.plumRadio)
								.addComponent(this.greenRadio))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.peacockRadio)
								.addComponent(this.whiteRadio))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.labelUserList)
								.addComponent(this.labelRoomList))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(this.scrollPaneRoomList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addComponent(this.scrollPaneUserList, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.buttonJoin)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.buttonNewRoom))
								.addComponent(this.dealButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.LeaveRoomButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.labelChatHistory)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(this.scrollPaneChatHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.textChatMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.buttonSend)
								.addComponent(this.buttonSendPrivate))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(this.buttonLogout)
								.addComponent(this.exitGameButton))
						.addGap(48, 48, 48))
				);

	}// </editor-fold>//GEN-END:initComponents

	/**
	 * When the user selects new room enables "Join Room" button if the room is
	 * not the current, otherwise disables it.
	 */
	private void listRoomsValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_listRoomsValueChanged
		final Room room = (Room) this.listRooms.getSelectedValue();
		if (room != null) {
			this.buttonJoin.setEnabled(true);
		}

	}// GEN-LAST:event_listRoomsValueChanged

	/**
	 * Joins the user to the selected room. If the room is private a dilog box
	 * that asks for the password is shown.
	 */
	private void buttonJoinActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonJoinActionPerformed

		final Room room = (Room) this.listRooms.getSelectedValue();

		//send validate if the room can be joined with the selected character here
		if (room != null) {
			final ISFSObject obj = new SFSObject();
			obj.putUtfString("character", this.mySelectedCharacter);
			obj.putUtfString("room", room.getName());
			this.sfs.send(new ExtensionRequest("validateJoinRoom", obj, null));
		}

	}// GEN-LAST:event_buttonJoinActionPerformed

	/**
	 * Sends public message to the server.
	 */
	private void buttonSendActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonSendActionPerformed
		if (this.textChatMessage.getText().length() > 0) {
			this.sfs.send(new PublicMessageRequest(this.textChatMessage.getText()));
			this.textChatMessage.setText("");
			this.textChatMessage.requestFocus();
		}
	}// GEN-LAST:event_buttonSendActionPerformed

	/**
	 * When the user slects new user enables "Send Private Message" button if
	 * the user is not the current user, otherwise disables it.
	 */
	private void listUsersValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_listUsersValueChanged
		final User user = (User) this.listUsers.getSelectedValue();
		if (user != null && user != this.sfs.getMySelf()) {
			this.buttonSendPrivate.setEnabled(true);
		} else {
			this.buttonSendPrivate.setEnabled(false);
		}
	}// GEN-LAST:event_listUsersValueChanged

	/**
	 * Displays a dialog that allows new room creation.
	 */
	private void buttonNewRoomActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonNewRoomActionPerformed
		this.dialogNewRoom.pack();
		this.dialogNewRoom.setLocationRelativeTo(null);
		this.textNewRoomName.setText("");
		this.textNewRoomPassword.setText("");
		this.sliderNewRoomMaxUsers.setValue(50);
		this.dialogNewRoom.setVisible(true);
	}// GEN-LAST:event_buttonNewRoomActionPerformed

	/**
	 * Shows dialog that asks for the private message to be send.
	 */
	private void buttonSendPrivateActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonSendPrivateActionPerformed
		this.dialogPrivate.pack();
		this.dialogPrivate.setLocationRelativeTo(null);
		this.textChatMessage.setText("");
		this.dialogPrivate.setVisible(true);
	}// GEN-LAST:event_buttonSendPrivateActionPerformed

	/**
	 * Sends private message to the server.
	 */
	private void buttonSendPrivateOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonSendPrivateOkActionPerformed
		final String msg = this.textPrivateMessage.getText();
		if (msg.length() > 0) {
			this.dialogPrivate.dispose();
			final User recipient = (User) this.listUsers.getSelectedValue();
			if (recipient != null) {
				this.sfs.send(new PrivateMessageRequest(msg, recipient.getId()));

			}
		}
	}// GEN-LAST:event_buttonSendPrivateOkActionPerformed

	/**
	 * Closes the private message dialog box.
	 */
	private void buttonSendPrivateCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonSendPrivateCancelActionPerformed
		this.dialogPrivate.dispose();
	}// GEN-LAST:event_buttonSendPrivateCancelActionPerformed

	/**
	 * Closes the new room dialog box.
	 */
	private void buttonCreateRoomCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonCreateRoomCancelActionPerformed
		this.dialogNewRoom.dispose();
	}// GEN-LAST:event_buttonCreateRoomCancelActionPerformed

	/**
	 * Creates new room
	 */
	private void buttonCreateRoomOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonCreateRoomOkActionPerformed
		final String roomName = this.textNewRoomName.getText();
		if (roomName.length() > 0) {
			this.dialogNewRoom.dispose();
			// Gets the new room properties for the user input
			// and sends them to the server.
			final int maxUsers = this.sliderNewRoomMaxUsers.getValue();
			final String password = new String(this.textNewRoomPassword.getPassword());

			// Create a new chat Room Room
			final RoomSettings settings = new RoomSettings(roomName);
			settings.setMaxUsers(maxUsers);
			settings.setGroupId("chats");
			settings.setPassword(password);
			settings.setGame(true);

			this.sfs.send(new CreateRoomRequest(settings));
		}
	}// GEN-LAST:event_buttonCreateRoomOkActionPerformed

	/**
	 * Closes the dialog box that allows the user to join to private room
	 */
	private void buttonJoinPrivateCancelActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonJoinPrivateCancelActionPerformed
		this.dialogJoinPrivateRoom.dispose();
	}// GEN-LAST:event_buttonJoinPrivateCancelActionPerformed

	/**
	 * Joins the user to private room
	 */
	private void buttonJoinPrivateOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonJoinPrivateOkActionPerformed
		if (this.textJoinPrivatePassword.getPassword().length > 0) {
			this.dialogJoinPrivateRoom.dispose();

			// Gets the room password
			final String password = new String(this.textJoinPrivatePassword.getPassword());
			// Joins the user to the currently selected room
			final Room room = (Room) this.listRooms.getSelectedValue();

			this.sfs.send(new JoinRoomRequest(room.getId(), password));

		}
	}// GEN-LAST:event_buttonJoinPrivateOkActionPerformed

	/**
	 * Logs the user in.
	 */
	private void buttonLoginActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonLoginActionPerformed
		if (this.textUserName.getText().length() > 0) {
			this.dialogLogin.dispose();
		}
	}// GEN-LAST:event_buttonLoginActionPerformed

	/**
	 * Closes the dialog box with the error messages.
	 */
	private void buttonAlertOkActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonAlertOkActionPerformed
		this.dialogAlert.dispose();
	}// GEN-LAST:event_buttonAlertOkActionPerformed

	/**
	 * Sends a public message to the server.
	 */
	private void textChatMessageKeyPressed(final java.awt.event.KeyEvent evt) {// GEN-FIRST:event_textChatMessageKeyPressed
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			this.buttonSendActionPerformed(null);
		}
	}// GEN-LAST:event_textChatMessageKeyPressed

	private void plumRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_plumRadioItemStateChanged
		this.mySelectedCharacter = plum.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.PLUM.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_PLUM]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_plumRadioItemStateChanged

	private void peacockRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_peacockRadioItemStateChanged
		this.mySelectedCharacter = peacock.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.PEACOCK.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_PEACOCK]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_peacockRadioItemStateChanged

	private void whiteRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_whiteRadioItemStateChanged
		this.mySelectedCharacter = white.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.WHITE.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_WHITE]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_whiteRadioItemStateChanged

	private void greenRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_greenRadioItemStateChanged
		this.mySelectedCharacter = green.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.GREEN.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_GREEN]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_greenRadioItemStateChanged

	private void scarletRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_scarletRadioItemStateChanged
		this.mySelectedCharacter = scarlet.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.SCARLET.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_SCARLET]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_scarletRadioItemStateChanged

	private void mustardRadioItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_mustardRadioItemStateChanged
		this.mySelectedCharacter = mustard.toString();
		this.PlayerIconLabel.setIcon(PlayerIcon.MUSTARD.get());
		this.PlayerDescriptionArea.setText(Card.descriptions[SUSPECT_MUSTARD]);
		this.setMyCharacterUserVar();

	}// GEN-LAST:event_mustardRadioItemStateChanged

	private void dealButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dealButtonActionPerformed

		final Room room = (Room) this.listRooms.getSelectedValue();
		final ISFSObject obj = new SFSObject();
		obj.putUtfString("room", room.getName());
		this.sfs.send(new ExtensionRequest("deal", obj, null));

	}//GEN-LAST:event_dealButtonActionPerformed

	private void singlePlayerButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_singlePlayerButtonActionPerformed
		this.LoginButton.setEnabled(false);
		this.singlePlayerButton.setEnabled(false);

		ClueMain.threadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				ClueMain.startSinglePlayerGame();
			}
		});
	}// GEN-LAST:event_singlePlayerButtonActionPerformed

	private void endTurnButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endTurnButtonActionPerformed
		endTurnButton.setEnabled(false);
		accuseButton.setEnabled(false);
		endTurnButton.setBackground(Color.white);

		final ISFSObject obj = new SFSObject();
		if (this.sfs.isConnected()) {
			this.sfs.send(new ExtensionRequest("endTurn", obj, null));
		}
	}//GEN-LAST:event_endTurnButtonActionPerformed

	private void accuseButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accuseButtonActionPerformed

		final AccusationDialog2 accusationDialog = new AccusationDialog2(ClueMain.frame, ClueMain.yourPlayer.getNotebook());
		final ArrayList<Card> accusation = (ArrayList<Card>) accusationDialog.showDialog();

		accuseButton.setEnabled(false);

		if (accusation == null) {
			return;
		}

		if (!this.isConnected()) {
			final boolean validAccusation = ClueMain.clue.matchesVictimSet(accusation);
			if (validAccusation) {
				SoundEffect.GASP.play();
				ClueMain.turn.gameOver = true;
			} else {
				final int rand = new Random().nextInt(10);
				if (rand > 5) {
					SoundEffect.LAUGH.play();
				} else {
					SoundEffect.GIGGLE.play();
				}
				ClueMain.yourPlayer.setHasMadeFalseAccusation();
			}
			JOptionPane.showMessageDialog(ClueMain.frame, "Your accusation is " + validAccusation + ".", "Accusation Validation", JOptionPane.PLAIN_MESSAGE);
		} else {
			Card suspect = null;
			Card weapon = null;
			Card room = null;

			for (final Card card : accusation) {
				if (card.getType() == TYPE_SUSPECT) {
					suspect = card;
				}
				if (card.getType() == TYPE_WEAPON) {
					weapon = card;
				}
				if (card.getType() == TYPE_ROOM) {
					room = card;
				}
			}

			//send the accusation the server for validation
			final ISFSObject obj = new SFSObject();
			obj.putInt("suspect", suspect.getValue());
			obj.putInt("room", room.getValue());
			obj.putInt("weapon", weapon.getValue());

			this.sfs.send(new ExtensionRequest("accusation", obj, null));
		}

	}//GEN-LAST:event_accuseButtonActionPerformed

	public void sendMoveEvent(final Player player, final int fx, final int fy, final int tx, final int ty, final Color color, final boolean secretPassage) {
		if (!this.isConnected()) {
			return;
		}

		final ISFSObject obj = new SFSObject();
		obj.putUtfString("player", player.getPlayerName());
		obj.putInt("character", player.getSuspectNumber());
		obj.putInt("from-x", fx);
		obj.putInt("from-y", fy);
		obj.putInt("to-x", tx);
		obj.putInt("to-y", ty);
		obj.putInt("playerColor", color.getRGB());
		obj.putBool("secretPassage", secretPassage);

		this.sfs.send(new ExtensionRequest("move", obj, null));

	}

	public void sendDiceRollEvent(final int roll1, final int roll2) {
		if (!this.isConnected()) {
			return;
		}

		final ISFSObject obj = new SFSObject();
		obj.putInt("roll1", roll1);
		obj.putInt("roll2", roll2);

		this.sfs.send(new ExtensionRequest("diceRoll", obj, null));

	}

	public void sendSetSuggestionEvent(final Card suspect, final Card weapon, final Card room, final Player suggesting_player) {

		if (!this.isConnected()) {
			return;
		}

		final ISFSObject obj = new SFSObject();
		obj.putUtfString("suggesting_player_name", suggesting_player.getPlayerName());
		obj.putInt("suggesting_player_suspectId", suggesting_player.getSuspectNumber());
		obj.putInt("suspect", suspect.getValue());
		obj.putInt("room", room.getValue());
		obj.putInt("weapon", weapon.getValue());

		this.sfs.send(new ExtensionRequest("suggestion", obj, null));

	}

	public void sendShowCardResponse(final Card card_to_show, final Player showing_player, final String player_to_show) {
		if (!this.isConnected()) {
			return;
		}

		int card_value = -1;
		int card_type = -1;
		if (card_to_show != null) {
			card_type = card_to_show.getType();
			card_value = card_to_show.getValue();
		}

		final ISFSObject obj = new SFSObject();
		obj.putUtfString("player_to_show", player_to_show);
		obj.putUtfString("showing_player_name", showing_player.getPlayerName());
		obj.putInt("showing_player_suspectId", showing_player.getSuspectNumber());
		obj.putInt("card_value", card_value);
		obj.putInt("card_type", card_type);

		this.sfs.send(new ExtensionRequest("showCardResponse", obj, null));
	}

	public void setYourMultiplayer() {
		Player player = null;

		if (this.scarletRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(scarlet, this.textUserName.getText(), COLOR_SCARLET, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("MsScarlett.png").getImage());
		}
		if (this.greenRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(green, this.textUserName.getText(), COLOR_GREEN, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("MrGreen.png").getImage());
		}
		if (this.mustardRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(mustard, this.textUserName.getText(), COLOR_MUSTARD, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("ColMustard.png").getImage());
		}
		if (this.plumRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(plum, this.textUserName.getText(), COLOR_PLUM, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("ProfPlum.png").getImage());
		}
		if (this.peacockRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(peacock, this.textUserName.getText(), COLOR_PEACOCK, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("MrsPeacock.png").getImage());
		}
		if (this.whiteRadio.isSelected()) {
			player = ClueMain.clue.addPlayer(white, this.textUserName.getText(), COLOR_WHITE, false);
			ClueMain.frame.setIconImage(ClueMain.getImageIcon("MrsWhite.png").getImage());
		}

		ClueMain.frame.setTitle("Clue - " + this.textUserName.getText());

		ClueMain.setCurrentPlayer(player);
		ClueMain.yourPlayer = ClueMain.currentTurnPlayer;

	}

	public void setOtherMultiplayer(final int id, final String name) {

		if (ClueMain.clue.getPlayer(id) != null) {
			return;
		}

		switch (id) {
		case SUSPECT_SCARLET:
			ClueMain.clue.addPlayer(scarlet, name, COLOR_SCARLET, false);
			break;
		case SUSPECT_MUSTARD:
			ClueMain.clue.addPlayer(mustard, name, COLOR_MUSTARD, false);
			break;
		case SUSPECT_GREEN:
			ClueMain.clue.addPlayer(green, name, COLOR_GREEN, false);
			break;
		case SUSPECT_WHITE:
			ClueMain.clue.addPlayer(white, name, COLOR_WHITE, false);
			break;
		case SUSPECT_PEACOCK:
			ClueMain.clue.addPlayer(peacock, name, COLOR_PEACOCK, false);
			break;
		case SUSPECT_PLUM:
			ClueMain.clue.addPlayer(plum, name, COLOR_PLUM, false);
			break;
		}

	}

	public void toggleRadioButtons(final boolean flag) {

		this.scarletRadio.setEnabled(flag);
		this.mustardRadio.setEnabled(flag);
		this.greenRadio.setEnabled(flag);
		this.whiteRadio.setEnabled(flag);
		this.peacockRadio.setEnabled(flag);
		this.plumRadio.setEnabled(flag);

	}

	public void setMyCharacterUserVar() {
		//set the character in my user
		final List<UserVariable> userVars = new ArrayList<>();
		userVars.add(new SFSUserVariable("character", this.mySelectedCharacter));
		this.sfs.send(new SetUserVariablesRequest(userVars));
	}

	public class TimedDialogAlert extends javax.swing.JDialog {

		public void start() {
			ClueMain.threadPoolExecutor.execute(new Runnable() {
				@Override
				public void run() {
					SoundEffect.BUTTON.play();
					try {
						Thread.sleep(5000);
					} catch (final Exception e) {
					}
					TimedDialogAlert.this.dispose();
				}
			});
		}

	}

	private HTMLEditorKit configureHtmlEditorKit(final javax.swing.JTextPane textPane) {
		final HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();
		final StyleSheet css = new StyleSheet();
		css.addRule("body { font-family:tahoma; font-size: 12 }");
		kit.setStyleSheet(css);
		return kit;
	}

	private void LoginButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed

		this.textUserName.setText("");
		this.dialogLogin.setVisible(true);
		this.buttonLogout.setEnabled(true);
		this.singlePlayerButton.setEnabled(false);

		this.sfs.connect(this.serverIPTextField.getText(), 9933);

	}//GEN-LAST:event_LoginButtonActionPerformed

	private void buttonLogoutActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonLogoutActionPerformed

		this.sfs.send(new LogoutRequest());

	}// GEN-LAST:event_buttonLogoutActionPerformed

	private void LeaveRoomButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_LeaveRoomButtonActionPerformed

		this.sfs.send(new LeaveRoomRequest(this.sfs.getLastJoinedRoom()));

		this.buttonJoin.setEnabled(true);
		this.LeaveRoomButton.setEnabled(false);

		this.scarletRadio.setEnabled(true);
		this.mustardRadio.setEnabled(true);
		this.greenRadio.setEnabled(true);
		this.whiteRadio.setEnabled(true);
		this.peacockRadio.setEnabled(true);
		this.plumRadio.setEnabled(true);

	}// GEN-LAST:event_LeaveRoomButtonActionPerformed

	private void exitGameButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitGameButtonActionPerformed
		if (this.isConnected()) {
			this.buttonLogoutActionPerformed(null);
		}
		System.exit(0);
	}//GEN-LAST:event_exitGameButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton buttonAlertOk;
	private javax.swing.JButton buttonCreateRoomCancel;
	private javax.swing.JButton buttonCreateRoomOk;
	private javax.swing.JButton buttonJoin;
	private javax.swing.JButton buttonJoinPrivateCancel;
	private javax.swing.JButton buttonJoinPrivateOk;
	private javax.swing.JButton buttonLogin;
	private javax.swing.JButton buttonLogout;
	private javax.swing.JButton buttonNewRoom;
	private javax.swing.JButton buttonSend;
	private javax.swing.JButton buttonSendPrivate;
	private javax.swing.JButton buttonSendPrivateCancel;
	private javax.swing.JButton buttonSendPrivateOk;
	private TimedDialogAlert dialogAlert;
	private javax.swing.JDialog dialogJoinPrivateRoom;
	private javax.swing.JDialog dialogLogin;
	private javax.swing.JTextField serverIPTextField;
	private javax.swing.JLabel ipLabel;

	private javax.swing.JDialog dialogNewRoom;
	private javax.swing.JDialog dialogPrivate;
	private javax.swing.JTextArea alertTextArea;
	private javax.swing.JLabel labelChatHistory;
	private javax.swing.JLabel labelMaxUsers;
	private javax.swing.JLabel labelPassword;
	private javax.swing.JLabel labelPrivateMessage;
	private javax.swing.JLabel labelPrivateRoomPassword;
	private javax.swing.JLabel labelRoomList;
	private javax.swing.JLabel labelRoomName;
	private javax.swing.JLabel labelUserList;
	private javax.swing.JLabel labelUserName;
	private javax.swing.JList listRooms;
	private javax.swing.JList listUsers;
	private javax.swing.JScrollPane scrollPaneChatHistory;
	private javax.swing.JScrollPane scrollPaneRoomList;
	private javax.swing.JScrollPane scrollPaneUserList;
	private javax.swing.JSlider sliderNewRoomMaxUsers;
	private javax.swing.JTextPane textChatHistory;
	private javax.swing.JTextField textChatMessage;
	private javax.swing.JPasswordField textJoinPrivatePassword;
	private javax.swing.JTextField textNewRoomName;
	private javax.swing.JPasswordField textNewRoomPassword;
	private javax.swing.JTextField textPrivateMessage;
	private javax.swing.JTextField textUserName;

	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton LeaveRoomButton;
	private javax.swing.JRadioButton whiteRadio;
	private javax.swing.JRadioButton greenRadio;
	private javax.swing.JRadioButton mustardRadio;
	private javax.swing.JRadioButton peacockRadio;
	private javax.swing.ButtonGroup playerSelectGroup;
	private javax.swing.JRadioButton plumRadio;
	private javax.swing.JRadioButton scarletRadio;
	private javax.swing.JButton dealButton;

	private javax.swing.JButton exitGameButton;
	private javax.swing.JButton LoginButton;
	public static javax.swing.JButton accuseButton;
	private javax.swing.JButton singlePlayerButton;
	public static javax.swing.JButton endTurnButton;

	private javax.swing.JTextArea PlayerDescriptionArea;
	private javax.swing.JLabel PlayerIconLabel;

	enum PlayerIcon {

		SCARLET("MsScarlett1.png"),
		MUSTARD("ColMustard1.png"),
		GREEN("MrGreen1.png"),
		WHITE("MrsWhite1.png"),
		PLUM("ProfPlum1.png"),
		PEACOCK("MrsPeacock1.png");

		private ImageIcon image;

		PlayerIcon(final String filename) {
			try {
				final URL url = this.getClass().getClassLoader().getResource(filename);
				this.image = new ImageIcon(ImageIO.read(url));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		public ImageIcon get() {
			return this.image;
		}

		public static void init() {
			values(); // calls the constructor for all the elements
		}
	}
}
