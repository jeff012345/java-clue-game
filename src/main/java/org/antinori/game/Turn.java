package org.antinori.game;

import static org.antinori.game.Card.ROOM_CONSERVATORY;
import static org.antinori.game.Card.ROOM_KITCHEN;
import static org.antinori.game.Card.ROOM_LOUNGE;
import static org.antinori.game.Card.ROOM_STUDY;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.antinori.astar.Location;
import org.antinori.multiplayer.MultiplayerFrame;

import cis579.ai.AiPlayerManager;
import cis579.ai.ResultLogger;
import cis579.ai.Solution;

public class Turn {

    public BufferedImage green_background = ClueMain.loadIcon("green-pattern-cropped.jpg");

    public static final int ACTION_VALID_ACCUSATION = 200;
    public static final int ACTION_INVALID_ACCUSATION = 500;
    public static final int ACTION_MADE_SUGGESTION = 300;
    public static final int ACTION_TOOK_PASSAGE = 310;
    public static final int ACTION_ROLLED_DICE = 320;

    public boolean gameOver = false;
    public boolean multiplayerGotShowCardResponse = false;
    
    public Turn() {

    }

    public void startGame(ArrayList<Player> players) {
    	
        ClueMain.mapView.setEnabled(false); // disable clicking on the map until
        // they roll the dice

        while (!gameOver) {

            for (Player player : players) {
            	
            	if(player.hasMadeFalseAccusation())
            		continue; 
            	
                ClueMain.setCurrentPlayer(player);

                MultiplayerFrame.endTurnButton.setEnabled(false);
                MultiplayerFrame.accuseButton.setEnabled(false);

                //if (player.isComputerPlayer()) {
                	
            	ArrayList<Card> accusation = canMakeAccusationComputerPlayer(player);
            	if (accusation != null) {
            		if(ClueMain.clue.matchesVictimSet(accusation)) {
            			gameOver = true;
                        ResultLogger.logResult(player, accusation);
                        break;
            		} else {
        				ResultLogger.wrongAccusation(player);
        				player.setHasMadeFalseAccusation();
        				continue;
            		}
                }
            	
                clickOnMapComputerPlayer(player);
                // let them make a suggestion
                makeSuggestionComputerPlayer(player, players);

                /*
                } else {
					// dialog for showing roll dice button, take secret passage
                    // button or make suggestion button or make accusation

                    if (player.hasMadeFalseAccusation()) {
                        ClueMain.notebookpanel.setBystanderIndicator(true);
                        JOptionPane.showMessageDialog(ClueMain.frame, "You made a false accusation and are bystanding to show cards.", "Accusation", JOptionPane.PLAIN_MESSAGE);
                        ClueMain.multiplayerFrame.endTurnButton.doClick();
                        continue;
                    }

                    Location location = player.getLocation();
                    boolean isInRoom = location.getRoomId() != -1;
                    boolean showSecret = (location.getRoomId() == ROOM_LOUNGE || location.getRoomId() == ROOM_STUDY || location.getRoomId() == ROOM_CONSERVATORY || location.getRoomId() == ROOM_KITCHEN);

                    TurnDialog2 dialog1 = new TurnDialog2(player, true, showSecret, isInRoom);
                    int action = dialog1.showDialog();

                    if (action == ACTION_ROLLED_DICE) {

                        // wait here until they click on the new location
                        ClueMain.mapView.setEnabled(true);// let them click on
                        // the map
                        Location new_location = null;
                        do {
                            try {
                                Thread.currentThread().sleep(1000);
                                new_location = player.getLocation();
                                // System.out.println("Waiting to click on map..");
                            } catch (Exception e) {
                            }
                        } while (new_location == location);
                        ClueMain.mapView.setEnabled(false);// disable map clicks
                        // again

						// see if they made it to a room and let them make a
                        // suggestion
                        isInRoom = new_location.getRoomId() != -1;
                        if (isInRoom) {
                            TurnDialog2 dialog2 = new TurnDialog2(player, false, false, true);
                            dialog2.showDialog();
                        }

                    } else if (action == ACTION_TOOK_PASSAGE) {

                        int current_room = location.getRoomId();
                        switch (current_room) {
                            case ROOM_LOUNGE:
                                ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_CONSERVATORY));
                                break;
                            case ROOM_STUDY:
                                ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_KITCHEN));
                                break;
                            case ROOM_CONSERVATORY:
                                ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_LOUNGE));
                                break;
                            case ROOM_KITCHEN:
                                ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_STUDY));
                                break;
                        }

                        ClueMain.mapView.repaint();

                        TurnDialog2 dialog2 = new TurnDialog2(player, false, false, true);
                        dialog2.showDialog();

                    } else if (action == ACTION_MADE_SUGGESTION) {
                        // next player
                    }

                    MultiplayerFrame.accuseButton.setEnabled(true);

                }
                */

                //waitEndTurnButton();

                if (gameOver) {
                    break;
                }

            } // for loop on players
            
            ResultLogger.nextTurn();

        } // while not game over
        
    }

    public void waitEndTurnButton() {
        MultiplayerFrame.endTurnButton.setEnabled(true);
        // wait until end turn is pressed
        do {
            try {
                Thread.currentThread().sleep(1000);
                MultiplayerFrame.endTurnButton.setBackground(Color.red);
                Thread.currentThread().sleep(1000);
                MultiplayerFrame.endTurnButton.setBackground(Color.white);
            } catch (Exception e) {
            }
        } while (MultiplayerFrame.endTurnButton.isEnabled() && !gameOver);
    }

    public void waitShowCardResponse() {
        multiplayerGotShowCardResponse = false;
        do {
            try {
                System.out.println("waitShowCardResponse " + multiplayerGotShowCardResponse);
                Thread.currentThread().sleep(1000);
            } catch (Exception e) {
            }
        } while (!multiplayerGotShowCardResponse);
        multiplayerGotShowCardResponse = false;
    }

    public Location clickOnMapComputerPlayer(Player player) {
    	
    	Location new_location = null;
    	int roll = -1;
    	
    	if(AiPlayerManager.shouldRoll(player)) {
            roll = ClueMain.mapView.rollDice();

    		// try move the player to the room which is not in their cards or toggled 
            Location location = player.getLocation();
            
            ArrayList<Location> choices = ClueMain.map.highlightReachablePaths(location, ClueMain.pathfinder, roll);
            //ClueMain.mapView.repaint();
            
            new_location = AiPlayerManager.decideLocation(player, choices);
    	}
    	
    	 if (new_location == null) {
             new_location = player.getLocation();// just keep them in the same room then
             //JOptionPane.showMessageDialog(ClueMain.frame, player.toString() + " is staying in the same room.", "", JOptionPane.PLAIN_MESSAGE);

         } else {

             ClueMain.setPlayerLocationFromMapClick(new_location);
             //JOptionPane.showMessageDialog(ClueMain.frame, player.toString() + " rolled a " 
             //		+ roll 
             //		+ (new_location == player.getLocation() ? " has stayed in the room." : " and has moved."), "", JOptionPane.PLAIN_MESSAGE);
             ClueMain.mapView.repaint();
             ClueMain.map.resetHighlights();
         }

         return new_location;
    }

    /**
     * computer player turn for making a suggestion
     * 
     * @param player
     * @param players
     */
    public void makeSuggestionComputerPlayer(Player player, ArrayList<Player> players) {

        Location location = player.getLocation();
        if (location.getRoomId() == -1) {
        	// can't make a suggestion if you're not in a room
            return;
        }
        
        Solution aiSuggestion = AiPlayerManager.getSuggestion(player);
        ArrayList<Card> suggestion = new ArrayList<Card>();
        suggestion.add(aiSuggestion.suspect);
        suggestion.add(aiSuggestion.room);
        suggestion.add(aiSuggestion.weapon);
        
        ClueMain.showcards.setSuggestion(suggestion, player, ClueMain.yourPlayer, players);
        ClueMain.showcards.showCards();
    }

    public ArrayList<Card> canMakeAccusationComputerPlayer(Player player) {

        ArrayList<Card> accusation = player.getNotebook().canMakeAccusation();
        if (accusation == null) {
            return null;
        }

        String text = String.format(ClueMain.accusationFormatter, player.toString(), accusation.get(0).toString(), accusation.get(1).toString(), accusation.get(2).toString());

        //SoundEffect.GASP.play();

        //JOptionPane.showMessageDialog(ClueMain.frame, text + "\n\nThe accusation is true.  Game over.", "Accusation", JOptionPane.PLAIN_MESSAGE);

        return accusation;
    }

    public void startTurnMultiplayerTurn(Player player) {

        ClueMain.mapView.setEnabled(false); // disable clicking on the map until
        // they roll the dice

        MultiplayerFrame.endTurnButton.setEnabled(false);
        MultiplayerFrame.accuseButton.setEnabled(false);

        if (player.hasMadeFalseAccusation()) {
            ClueMain.notebookpanel.setBystanderIndicator(true);
            ClueMain.multiplayerFrame.showTimedDialogAlert("You made a false accusation\nand are bystanding to show cards.");
            ClueMain.multiplayerFrame.endTurnButton.doClick();
            return;
        }

        Location location = player.getLocation();
        boolean isInRoom = location.getRoomId() != -1;
        boolean showSecret = (location.getRoomId() == ROOM_LOUNGE || location.getRoomId() == ROOM_STUDY || location.getRoomId() == ROOM_CONSERVATORY || location.getRoomId() == ROOM_KITCHEN);

		//JOptionPane.showMessageDialog(ClueMain.frame,"showSecret " + showSecret,"",JOptionPane.PLAIN_MESSAGE);
        TurnDialog2 dialog1 = new TurnDialog2(player, true, showSecret, isInRoom);
        int action = dialog1.showDialog();

        if (action == ACTION_ROLLED_DICE) {

            // wait here until they click on the new location
            ClueMain.mapView.setEnabled(true);// let them click on the map
            Location new_location = null;
            do {
                try {
                	Thread.currentThread().sleep(1000);
                    new_location = player.getLocation();
                } catch (Exception e) {
                }
            } while (new_location == location);
            ClueMain.mapView.setEnabled(false);// disable map clicks again

            //send the location to the server
            ClueMain.multiplayerFrame.sendMoveEvent(player, location.getX(), location.getY(), new_location.getX(), new_location.getY(), player.getPlayerColor(), false);

            // see if they made it to a room and let them make a suggestion
            isInRoom = new_location.getRoomId() != -1;
            if (isInRoom) {
                TurnDialog2 dialog2 = new TurnDialog2(player, false, false, true);
                dialog2.showDialog();
                waitShowCardResponse();
            }

        } else if (action == ACTION_TOOK_PASSAGE) {

            int current_room = location.getRoomId();
            switch (current_room) {
                case ROOM_LOUNGE:
                    ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_CONSERVATORY));
                    break;
                case ROOM_STUDY:
                    ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_KITCHEN));
                    break;
                case ROOM_CONSERVATORY:
                    ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_LOUNGE));
                    break;
                case ROOM_KITCHEN:
                    ClueMain.setPlayerLocationFromMapClick(ClueMain.map.getRoomLocation(ROOM_STUDY));
                    break;
            }

            ClueMain.mapView.repaint();

            ClueMain.multiplayerFrame.sendMoveEvent(player, location.getX(), location.getY(), player.getLocation().getX(), player.getLocation().getY(), player.getPlayerColor(), true);

            TurnDialog2 dialog2 = new TurnDialog2(player, false, false, true);
            dialog2.showDialog();
            waitShowCardResponse();

        } else if (action == ACTION_MADE_SUGGESTION) {

            waitShowCardResponse();

        }

        MultiplayerFrame.accuseButton.setEnabled(true);

        waitEndTurnButton();

    }

}
