package org.antinori.game;

import static org.antinori.game.Card.SUSPECT_GREEN;
import static org.antinori.game.Card.SUSPECT_MUSTARD;
import static org.antinori.game.Card.SUSPECT_PEACOCK;
import static org.antinori.game.Card.SUSPECT_PLUM;
import static org.antinori.game.Card.SUSPECT_SCARLET;
import static org.antinori.game.Card.SUSPECT_WHITE;
import static org.antinori.game.Player.COLOR_GREEN;
import static org.antinori.game.Player.COLOR_MUSTARD;
import static org.antinori.game.Player.COLOR_PEACOCK;
import static org.antinori.game.Player.COLOR_PLUM;
import static org.antinori.game.Player.COLOR_SCARLET;
import static org.antinori.game.Player.COLOR_WHITE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.antinori.astar.AStar;
import org.antinori.astar.ClueMap;
import org.antinori.astar.Location;
import org.antinori.astar.MapView;
import org.antinori.astar.PathFinder;
import org.antinori.astar.RoomIconPlacement;
import org.antinori.multiplayer.MultiplayerFrame;

import cis579.ai.AiPlayerManager;
import cis579.ai.Database;
import cis579.ai.ResultLogger;

public class ClueMain {

	public static Clue clue;
	public static ClueMap map;
	public static MapView mapView;
	public static ClueMain demo;
	public static NotebookPanel notebookpanel;
	public static ShowCardsRoutine showcards;
	public static MultiplayerFrame multiplayerFrame;
	public static Turn turn;

	public static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);

	public static JFrame frame;
	public static Dice dice = new Dice(1, 6);

	public static PathFinder<Location> pathfinder;

	public static Font FONT_14 = new Font("Franklin Gothic Heavy", Font.PLAIN, 14);
	public static Font FONT_18 = new Font("Franklin Gothic Heavy", Font.PLAIN, 18);
	public static Font FONT_24 = new Font("Franklin Gothic Heavy", Font.PLAIN, 24);

	public static ArrayList<Player> players = null;
	public static Player currentTurnPlayer = null;
	public static Player yourPlayer = null;

	public static boolean difficult_setting = false;

	public static final String formatter = "%s suggests\n%s\ncommitted the crime\nwith the %s\nin the %s.";
	public static final String accusationFormatter = "%s makes\nan accusation that\n%s\ncommitted the crime\nwith the %s\nin the %s.";

	public static final RoomIconPlacement playerIconPlacement = new RoomIconPlacement();

	public ClueMain() {
		ResultLogger.reset();

		try {

			map = new ClueMap(this.readMapTemplate());

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			SoundEffect.init();

		} catch (final Exception e1) {
		}
	}

	public static void main(final String[] args) {
		demo = new ClueMain();
		demo.initComponents();
	}

	public static void setLocationInCenter(final JDialog d, final int offset_x, final int offset_y) {

		d.setLocationRelativeTo(null);
		d.setLocation(d.getX() + offset_x, d.getY() + offset_y);

	}

	public void initComponents() {

		frame = new JFrame("Clue");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setIconImage(getImageIcon("clue-icon.png").getImage());

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent windowEvent) {
				super.windowClosing(windowEvent);

				Database.getInstance().close();
			}
		});

		//invisible for now
		showcards = new ShowCardsRoutine();
		multiplayerFrame = new MultiplayerFrame();
		notebookpanel = new NotebookPanel();
		mapView = new MapView(map);

		final JPanel main = new JPanel();
		final JPanel jPanel1 = multiplayerFrame;
		final JPanel jPanel2 = mapView;
		final JPanel jPanel3 = notebookpanel;

		final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		//jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 260, Short.MAX_VALUE)
				);
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 800, Short.MAX_VALUE)
				);

		final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 800, Short.MAX_VALUE)
				);
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
				);

		final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 200, Short.MAX_VALUE)
				);
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
				);

		final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(main);
		main.setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);

		frame.add(main);

		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);

	}

	public static void setCurrentPlayer(final Player player) {
		currentTurnPlayer = player;
	}

	public static void startSinglePlayerGame() {
		do {
			pathfinder = new AStar<>();
			clue = new Clue();
			turn = new Turn();

			AiPlayerManager.createAiPlayers();

			//SinglePlayerSelectionDialog2 settingsDialog = new SinglePlayerSelectionDialog2(frame, true);
			//settingsDialog.setVisible(true);

			try {
				clue.createDeck();

				final String msg = clue.dealShuffledDeck();

				//System.out.println(msg);

			} catch (final Exception e) {
				e.printStackTrace();
			}

			players = clue.getPlayers();

			final Location scarlet_location = map.getLocationAndSetColor(7, 24, COLOR_SCARLET);
			final Location mustard_location = map.getLocationAndSetColor(0, 17, COLOR_MUSTARD);
			final Location green_location = map.getLocationAndSetColor(13, 0, COLOR_GREEN);
			final Location plum_location = map.getLocationAndSetColor(23, 18, COLOR_PLUM);
			final Location white_location = map.getLocationAndSetColor(9, 0, COLOR_WHITE);
			final Location peacock_location = map.getLocationAndSetColor(23, 5, COLOR_PEACOCK);

			// set locations for the current players
			for (final Player player : players) {

				final Notebook book = new Notebook(player);
				player.setNotebook(book);

				if (!player.isComputerPlayer()) {
					setCurrentPlayer(player);
					yourPlayer = player;
					notebookpanel.setNotebook(book);
				}

				switch (player.getSuspectNumber()) {
				case SUSPECT_SCARLET:
					player.setLocation(scarlet_location);
					break;
				case SUSPECT_MUSTARD:
					player.setLocation(mustard_location);
					break;
				case SUSPECT_GREEN:
					player.setLocation(green_location);
					break;
				case SUSPECT_PLUM:
					player.setLocation(plum_location);
					break;
				case SUSPECT_WHITE:
					player.setLocation(white_location);
					break;
				case SUSPECT_PEACOCK:
					player.setLocation(peacock_location);
					break;
				}

				AiPlayerManager.addPlayer(player);
			}

			mapView.repaint();

			turn.startGame(players);

		} while(ResultLogger.runAgain());

		//ResultLogger.printResults();
	}

	public static void setUpMultiplayerGame() {

		players = clue.getPlayers();

		final Location scarlet_location = map.getLocationAndSetColor(7, 24, COLOR_SCARLET);
		final Location mustard_location = map.getLocationAndSetColor(0, 17, COLOR_MUSTARD);
		final Location green_location = map.getLocationAndSetColor(13, 0, COLOR_GREEN);
		final Location plum_location = map.getLocationAndSetColor(23, 18, COLOR_PLUM);
		final Location white_location = map.getLocationAndSetColor(9, 0, COLOR_WHITE);
		final Location peacock_location = map.getLocationAndSetColor(23, 5, COLOR_PEACOCK);

		// set locations for the current players
		for (final Player player : players) {
			switch (player.getSuspectNumber()) {
			case SUSPECT_SCARLET:
				player.setLocation(scarlet_location);
				break;
			case SUSPECT_MUSTARD:
				player.setLocation(mustard_location);
				break;
			case SUSPECT_GREEN:
				player.setLocation(green_location);
				break;
			case SUSPECT_PLUM:
				player.setLocation(plum_location);
				break;
			case SUSPECT_WHITE:
				player.setLocation(white_location);
				break;
			case SUSPECT_PEACOCK:
				player.setLocation(peacock_location);
				break;
			}
		}

		final Notebook book = new Notebook(currentTurnPlayer);
		currentTurnPlayer.setNotebook(book);
		yourPlayer = currentTurnPlayer;
		notebookpanel.setNotebook(book);

		mapView.repaint();

	}

	public String readMapTemplate() {
		String mapTemplate = "";

		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/map.template")));
			while (true) {
				final String line = reader.readLine();
				if (line == null) {
					break;
				}
				mapTemplate += line + "\n";
			}
			reader.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return mapTemplate;

	}

	public static Location getPlayerLocation(final int id) {
		Location location = null;
		if (players == null) {
			return null;
		}
		final Player player = demo.clue.getPlayer(id);
		if (player != null) {
			location = player.getLocation();
		}
		return location;
	}

	public static void setPlayerLocationFromMapClick(final Location location) {
		setPlayerLocationFromMapClick(currentTurnPlayer, currentTurnPlayer.getPlayerColor(), currentTurnPlayer.getLocation(), location);
	}

	public static void setPlayerLocationFromMapClick(final Player player, final Color color, final Location from_location, final Location to_location) {

		//playerIconPlacement.removePlayerIcon(player.getSuspectNumber());
		//playerIconPlacement.addPlayerIcon(to_location.getRoomId(), player.getSuspectNumber());

		//reset original color back to gray
		//map.setLocationColor(from_location, Color.gray);

		//set the players location
		player.setLocation(to_location);
		//set location color to the players color
		to_location.setColor(color);
	}

	public static BufferedImage loadIcon(final String file, final int x, final int y, final int width, final int height) {
		BufferedImage image = null;
		try {
			final BufferedImage sheet = ImageIO.read(ClueMain.class.getResourceAsStream("/" + file));
			image = sheet.getSubimage(x, y, width, height);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static BufferedImage loadIcon(final String file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(ClueMain.class.getResourceAsStream("/" + file));
		} catch (final Exception e) {
		}
		return image;
	}

	public static ImageIcon getImageIcon(final String file) {
		final ClassLoader cldr = ClueMain.class.getClassLoader();
		final java.net.URL imageURL = cldr.getResource(file);
		return new ImageIcon(imageURL);
	}

	public static BufferedImage overlayImages(final BufferedImage bgImage, final BufferedImage fgImage, final int overlay_x, final int overlay_y) {
		if (fgImage.getHeight() > bgImage.getHeight() || fgImage.getWidth() > fgImage.getWidth()) {
			System.err.println("Foreground Image Is Bigger In One or Both Dimensions"
					+ "\nCannot proceed with overlay.\nPlease use smaller Image for foreground");
			return null;
		}

		final Graphics2D g = bgImage.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//Create a rescale filter op that makes the image 50% opaque.
		//float[] scales = { 1f, 1f, 1f, 0.5f };
		//float[] offsets = new float[4];
		//RescaleOp rop = new RescaleOp(scales, offsets, null);
		g.drawImage(bgImage, 0, 0, null);

		//g.drawImage(fgImage, rop, overlay_x, overlay_y);
		g.drawImage(fgImage, overlay_x, overlay_y, null);

		g.dispose();
		return bgImage;
	}

	public static BufferedImage resizeImage(final BufferedImage originalImage, final double zoom) {

		final double zoomPercentage = zoom / 100;
		final int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		final double width = originalImage.getWidth() * zoomPercentage;
		final double height = originalImage.getHeight() * zoomPercentage;

		final BufferedImage resizedImage = new BufferedImage((int) width, (int) height, type);

		final Graphics2D g = resizedImage.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
		g.dispose();

		return resizedImage;
	}

	/**
	 * Convenience method that returns a scaled instance of the provided
	 * {@code BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance, in pixels
	 * @param targetHeight the desired height of the scaled instance, in pixels
	 * @param hint one of the rendering hints that corresponds to
	 * {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	 * {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 * {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	 * {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality if true, this method will use a multi-step scaling
	 * technique that provides higher quality than the usual one-step technique
	 * (only useful in down-scaling cases, where {@code targetWidth} or
	 * {@code targetHeight} is smaller than the original dimensions, and
	 * generally only when the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {
	 * @codey BufferedImage}
	 */
	public static BufferedImage getScaledInstance(final BufferedImage img, final int targetWidth, final int targetHeight, final Object hint, final boolean higherQuality) {
		final int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			final BufferedImage tmp = new BufferedImage(w, h, type);
			final Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	public static void drawSquareOnImage(final Graphics2D g2D, final BufferedImage baseImage) {

		final String[] coordsText = new String[]{"10,10", "10,20", "20,20", "20,10"};

		final Path2D.Float regionOfInterest = new Path2D.Float();
		// We must store the first X,Y coordinates so we can close the path, by creating a line
		// to the last point to the first one.
		boolean isFirst = true;
		double firstX = 0, firstY = 0;
		// For each of the X,Y coordinates, parse and store them on the Path2D.Float.
		for (final String s : coordsText) {
			final String[] xy = s.split(",");
			final double x = Double.parseDouble(xy[0]);
			final double y = Double.parseDouble(xy[1]);
			if (isFirst) {
				regionOfInterest.moveTo(x, y);
				firstX = x;
				firstY = y;
				isFirst = false;
			} else {
				regionOfInterest.lineTo(x, y);
			}
		}
		// Close the path.
		regionOfInterest.lineTo(firstX, firstY);
		// We have the path that define the region of interest. In order to dim the image regions
		// outside of this path we must create another path that contains everything but the
		// region of interest.
		// First we create a path for the whole image -- a rectangle with the image's coordinates.
		final Path2D.Float pathForWholeImage = new Path2D.Float();
		pathForWholeImage.moveTo(0, 0);
		pathForWholeImage.lineTo(baseImage.getWidth(), 0);
		pathForWholeImage.lineTo(baseImage.getWidth(), baseImage.getHeight());
		pathForWholeImage.lineTo(0, baseImage.getHeight());
		pathForWholeImage.lineTo(0, 0);
		// In order to use Constructive Area Geometry (CAG) operations we must use the Area class.
		// First we create an Area with the path for the whole image...
		final Area wholeImage = new Area(pathForWholeImage);
		// .. then we subtract the region of interest from this Area.
		wholeImage.subtract(new Area(regionOfInterest));
		// Now we have a Path2D.Float for the region of interest and an Area for the rest of the image.
		// To draw and paint them we need a graphic context, which we will get from the image itself.
		final Graphics2D g2d = (Graphics2D) baseImage.getGraphics();
		// We want antialiasing!
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Fill the rest of the image with a transparent (100/255) white.
		g2d.setColor(new Color(255, 255, 255, 100));
		g2d.fill(wholeImage);
		// Draw the region of interest with a thick, almost opaque red line.
		g2d.setStroke(new BasicStroke(5f));
		g2d.setColor(new Color(255, 0, 0, 200));
		g2d.draw(regionOfInterest);
	}

}
