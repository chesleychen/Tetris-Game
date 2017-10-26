/**
 * The author of TetrisUI: Yu Wang  and Sheng-Fu Chuang
 * Student ID:             W1189106 and W1189934
 */

/**
 * Implement idea and design originally from Stanford University CS108 Course Assignment
 * please refer to http://web.stanford.edu/class/cs108/ 
 *                 http://web.stanford.edu/class/cs108/handouts152/15HW2Tetris.pdf
 */
package application;
	


import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class TetrisUI extends Application {
	/*
	 * Yu start
	 */
	private Pane gameBoxPane;
	private VBox setUpPane;
	private Button startButton, stopButton, quitButton;
	private int cellLength;
	private int width, height;
	private Rectangle[][] squares;
	private Timer timer;
	private long speedLong;
	private boolean bottomReached;
	private Block[] blocks;
	private Block curBlock;
	private GameBox gb;
	private Text scoreText, timeText, speedText;
	private int score, speed;
	private long startTime;
	private long pauseTime;
	private long totalPauseTime = 0;
	private int min, sec, hour;
	private Timeline timeShow; 
	private int curX, curY;
	private int[] scoreSheet;
	/*
	 * Yu end
	 */
	/*
	 * ShengFu start
	 */
	private Random random;//sam
    private ImageView stopImageView;//sam
    private Lighting lighting = new Lighting(new Light.Distant(245, 50, Color.WHITE));
	private Slider speedBar;
	private Pane nextBoxPane;//sam2
    private Rectangle[][] nextSquares;//sam2
    private int nextHeight = 4;//sam2
    private int nextWidth = 4;//sam2
    private Block[] nextBlocks;//sam2
    private int nextLength = 1;//sam2
    private final Color[] blockColor = new Color[7];//sam2
    private boolean timerDeleteRow;
    private AudioClip soundDropDown;//sam3
	private AudioClip soundLeftRight;//sam3
	private AudioClip soundRotate;//sam3
	private AudioClip soundDeleteRow;//sam5
	private MediaPlayer backGroundMediaPlayer;//sam3
	private CheckBox bgMusicCheckBox;//sam4
	private CheckBox pauseCheckBox;//sam5
	private boolean pauseStatus = false;//sam5
	private boolean gameStartStatus = false;//sam
	/*
	 * ShengFu end
	 */
	
	
	/**
	 * Designed by Yu. ShengFu contribute the last 3 line
	 */
	public TetrisUI() {
		
		timerDeleteRow = false;
		
		this.width = 10;
		this.height = 24;
		this.cellLength = 18;
		
		startButton = new Button("start");
		stopButton = new Button("stop");
		stopButton.setDisable(true);
		quitButton = new Button("quit");
		
		score = 0;
		scoreSheet = new int[]{1, 3, 6, 10};
		scoreText = new Text("Score: " + score);
		
		speed = 1;
		speedText = new Text("Speed: " + speed);
		speedBar = new Slider(1,5,1);
		

		sec = 0;
		min = 0;
		hour = 0;
		timeText = new Text(String.format("%02d", hour) + " : " + String.format("%02d", min) + " : " + String.format("%02d", sec));
		timeShow = new Timeline();
		speedLong = 800; // millisecond
		showTime();
		
		gb = new GameBox(height, width); // need to modify later
		bottomReached = false; // true if current block reaches the bottom
		
		blocks = Block.allBlocks();//shengfu
		random = new Random();//shengfu
		
		
		
	}
	
	/**
	 * Designed by Yu 
	 * create a timeline that display the digital time periodically
	 */
	public void showTime() {
		
		timeText.setFont(new Font("Arial", 15));
		timeText.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		timeShow.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {
				long totalSec = System.currentTimeMillis() - startTime - totalPauseTime;
				hour = (int)((totalSec / (1000 * 60 * 60)) % 60);
				min = (int)((totalSec / (1000 * 60)) % 60);
				sec = (int)((totalSec / 1000) % 60);
				timeText.setText(String.format("%02d", hour) + " : " + String.format("%02d", min) + " : " + String.format("%02d", sec));
			}
		}));
		timeShow.setCycleCount(Animation.INDEFINITE);
				
		
	}
	
	/**
	 * Designed by Yu. ShengFu contribute some modification
	 * Start the game when user click start button
	 * Start adding new block to the game box
	 */
	public synchronized void startTetris() {
		
		/*
		 *  ShengFu modify start 
		 */
		stopImageView.setOpacity(0);
		gameStartStatus = true;
		/*
		 * ShengFu modify end
		 */
		gb = new GameBox(height, width);
		placeNewBlock();
		startTime = System.currentTimeMillis();
		
		pauseCheckBox.setSelected(false);
		totalPauseTime = 0;
		score = 0;
		timeShow.play();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(pauseStatus){
            		return;
            	}
				if (!timerDeleteRow) {
					moveDown();}
				else {
					timerDeleteRow = false;
					soundDeleteRow.play();
					int rowDelete = gb.deleteFullRow();
					score += scoreSheet[rowDelete - 1];
					
				
					int i = 0;
					for (; i < gb.heights.length; i++) {
						if (gb.heights[i] >= height - 4 - 1) {
							break;
							
						}
					}
					
					if (i == gb.heights.length) {
						placeNewBlock();
					} else {
						stopTetris();
					}
				}
				
				
			}
		}, 1, speedLong);
		speedBar.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(false);
	}
	
	
	/**
	 * Designed by Yu. ShengFu contribute some modification
	 * Stop the game when user click stop button
	 */
	public synchronized void stopTetris() {
		/*
		 * ShengFu start
		 */
		
		gameStartStatus = false;
		curBlock = null;
		stopImageView.setOpacity(1);
		
		/*
		 * ShengFu end
		 */
		
		timer.cancel();
		timeShow.stop();
		
		speedBar.setDisable(false);
		startButton.setDisable(false);
		stopButton.setDisable(true);
	}
	
	/**
	 * Designed by Yu.
	 * Called by user keyboard stroke. Move current block to left by one column.
	 * No action if the reach the bound of the game box.
	 */
	public synchronized void moveLeft() {
		if (curBlock == null) return;
		gb.erase();
		int x = curX - 1;
		int temp = this.setPosition(curBlock, x, curY);	
		if (temp < 0) {
			gb.track(curBlock, curX, curY);
		} else {
			if (temp == 1) {
				// check if curBlock reaches the bottom and also reaches the top line
				if(curY >= height - 4 - curBlock.getHeight()) {
					stopTetris();
				} else {
					this.placeNewBlock();
				}
			}
		}
	}
	
	/**
	 * Designed by Yu.
	 * Called by user keyboard stroke. Move current block to right by one column.
	 * No action if the reach the bound of the game box.
	 */
	public synchronized void moveRight() {
		if (curBlock == null) return;
		gb.erase();
		int x = curX + 1;
		int temp = this.setPosition(curBlock, x, curY);
		if (temp < 0) {
			gb.track(curBlock, curX, curY);
		} else {
			if (temp == 1) {
				// check if curBlock reaches the bottom and also reaches the top line
				if(curY >= height - 4 - curBlock.getHeight()) {
					stopTetris();
				} else {
					this.placeNewBlock();
				}
			}
		}
	}
	
	/**
	 * Designed by Yu.
	 * Called by user keyboard stroke. Move current block down by one row.
	 * No action if the reach the bottom of the game box.
	 */
	public synchronized void moveDown() {
		if (curBlock == null) return;
		gb.erase();
		int y = curY - 1;
		int temp = this.setPosition(curBlock, curX, y);
		if (temp < 0) {
			if (bottomReached == false) {
				gb.track(curBlock, curX, curY);
				bottomReached = true;
			} else {
				bottomReached = false;
				gb.setMoveDownFail();
				gb.track(curBlock, curX, curY);
			}
		}
		
		
		
		// check if curBlock reaches the bottom
		if (temp == 1) {
			// check if curBlock reaches the bottom and also reaches the top line
			if(curY >= height - 4 - curBlock.getHeight()) {
				stopTetris();
			} else {
				this.placeNewBlock();
			}
		}
		
		// check if curBlock reaches the bottom and need to delete some rows
		if(temp == 2) {
			
			paintDeletedRow();
			timerDeleteRow = true;
			return;

		}
		
		
	}
	
	
	/**
	 * Designed by Yu.
	 * Rotate current block 90 degree counter clock-wise by user keyboard stroke. 
	 */
	public synchronized void rotate() {
		if (curBlock == null) return;
		gb.erase();
	
		Block afterRotate = curBlock.rotate();
		int temp = this.setPosition(afterRotate, curX - (curBlock.getWidth() - afterRotate.getWidth()) / 2, curY - (curBlock.getHeight() - afterRotate.getHeight()) / 2);
		if (temp < 0) {
			gb.track(curBlock, curX, curY);
		} else {
			if (temp == 1) {
				// check if curBlock reaches the bottom and also reaches the top line
				if(curY >= height - 4 - curBlock.getHeight()) {
					stopTetris();
				} else {
					this.placeNewBlock();
				}
			}
			
			// check if curBlock reaches the bottom and need to delete some rows
			if(temp == 2) {
				paintDeletedRow();			
				timerDeleteRow = true;
			}
		}
	}
	
	/**
	 * Designed by Yu.
	 * Drop current block to the bottom by user keyboard stroke. 
	 */
	public synchronized void drop() {
		if (curBlock == null) return;
		gb.erase();
		int y = gb.fastenDrop(curBlock, curX, curY);
		int temp = this.setPosition(curBlock, curX, y);
		// check if curBlock reaches the bottom
		if (temp == 1) {
			// check if curBlock reaches the bottom and also reaches the top line
			if(curY >= height - 4 - curBlock.getHeight()) {
				stopTetris();
			} else {
				this.placeNewBlock();
			}
		}

		// check if curBlock reaches the bottom and need to delete some rows
		if(temp == 2) {
			paintDeletedRow();
			timerDeleteRow = true;
		}
	}
	
	
	
	/**
	 * Designed by ShengFu.
	 * When start new game or current block reached the bottom, add a new block
	 * to the game box. 
	 */
	public void placeNewBlock() {
		Block newBlock;
        newBlock = nextBlocks[0];
        for(int i = 0; i < nextBlocks.length -1; i++){
        	nextBlocks[i] = nextBlocks[i + 1];
        }
        nextBlocks[nextBlocks.length-1] = generateBlock();
        //compute middle position, need to be refine
        int newX = width/2;
        int newY = height - 4;
        //call setPosition to place the new block
        setPosition(newBlock, newX , newY);
		
	}

	
	/**
	 * Designed by ShengFu.
	 * randomly generate a new block.
	 */
	public Block generateBlock() {
		return blocks[(int)(blocks.length * random.nextDouble())];
	}
	
	/**
	 * Designed by ShengFu
	 * Given a block and coordinates x and y, place the block the location x, y
	 * return the same result by calling place(block, x, y)
	 */
	public int setPosition(Block newBlock, int newX, int newY) {

		int res = gb.track(newBlock, newX, newY);

		//success
		if (res >=0 ){
			curBlock = newBlock;
			curX = newX;
			curY = newY;
			paintBox();
		}
		return res;
	}
	
	/*
	 * Designed by Yu.
	 * when rows is filled, the function paint the delete effect
	 */
	public synchronized void paintDeletedRow() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				ArrayList<Integer> rowToDelete = gb.rowToDelete();
				for (Integer i: rowToDelete) {
					for (int j = 0; j < width; j++) {
						squares[height - i- 1][j].setOpacity(1);
						squares[height - i- 1][j].setFill(Color.rgb(255, 255, 153));
						squares[height - i- 1][j].setArcHeight(7);
						squares[height - i- 1][j].setArcWidth(7);
					}
				}
			}
		});
	}
	
	/**
	 * Designed by ShengFu, Yu contribute the runLater functionality
	 */
	public synchronized void paintBox() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				scoreText.setText("Score: " + score);
				//scoreLabel;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						int tempColor = gb.gameBox[j][height - 1 - i]; 
						if( tempColor >= 0) {
							squares[i][j].setOpacity(1);
							squares[i][j].setEffect(lighting);
							squares[i][j].setFill(blockColor[tempColor]);
							squares[i][j].setArcHeight(7);
							squares[i][j].setArcWidth(7);
						}
						else {squares[i][j].setOpacity(0);}
					}
				}


		        //nextBox
				for (int i = 0; i < nextHeight; i++) {
					for (int j = 0; j < nextWidth; j++) {
						nextSquares[i][j].setOpacity(0);
		
					}
				}
				for (int index = 0; index < nextBlocks.length; index++){
					Point[] nowPoint= nextBlocks[index].getPoints();
					for (int i = 0; i < nowPoint.length; i++){
						nextSquares[3 - nowPoint[i].getY() + 4*index][nowPoint[i].getX()].setOpacity(1);
						nextSquares[3 - nowPoint[i].getY() + 4*index][nowPoint[i].getX()].setEffect(lighting);
						nextSquares[3 - nowPoint[i].getY() + 4*index][nowPoint[i].getX()].setFill(blockColor[nextBlocks[index].getColor()]);
						nextSquares[3 - nowPoint[i].getY() + 4*index][nowPoint[i].getX()].setArcHeight(7);
						nextSquares[3 - nowPoint[i].getY() + 4*index][nowPoint[i].getX()].setArcWidth(7);
					}
				}
			}
		});
	}
	
	/**
	 * Designed by Yu.
	 * Print board to debug, need to remove before submit
	 */
	public boolean getGrid(int x, int y) {
		if (x < width && x >=0){
			if (y < height && y >= 0){
				if (gb.gameBox[x][y] < 0) {
					return false;
				}
			}
		}
		return true; // YOUR CODE HERE
	}
	
	/**
	 * Designed by Yu.
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
	
	/** 
	 * Designed by Yu, ShengFu contribute some part
	 * Initiate the game interface, including gameBox, game control panel
	 * 
	 * @param primaryStage
	 */
	
	public void initiateGame(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root,cellLength * width * 2,cellLength * height + 50);
		
		Image bgImage = new Image(getClass().getResourceAsStream("/background1.gif"));
		ImageView bgImageView = new ImageView(bgImage);
		bgImageView.setOpacity(0.4);
		bgImageView.setFitHeight(scene.getHeight());
		bgImageView.setFitWidth(scene.getWidth());
		root.getChildren().add(bgImageView);
		
		HBox hbox = new HBox();
		gameBoxPane = new Pane();
		gameBoxPane.setPrefWidth(width * cellLength);
		gameBoxPane.setPrefHeight(height * cellLength);
		squares = new Rectangle[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				squares[i][j] = new Rectangle(cellLength * j, cellLength * i, cellLength, cellLength);
				squares[i][j].setStroke(Color.BLACK);
				squares[i][j].setStrokeWidth(3);
				squares[i][j].setFill(Color.WHITE);
				squares[i][j].setOpacity(0);
				gameBoxPane.getChildren().add(squares[i][j]);
			}
		}
		
		Line topLine = new Line(0, 4 * cellLength, width * cellLength, 4 * cellLength);
		topLine.setStrokeWidth(2);
		topLine.setStroke(Color.GREEN);
		
		Line upLine = new Line(0, 0 * cellLength, width * cellLength, 0 * cellLength);
		upLine.setStrokeWidth(2);
		upLine.setStroke(Color.GREEN);
		
		Line leftLine = new Line(0, 0 * cellLength, 0 * cellLength, height * cellLength);
		leftLine.setStrokeWidth(2);
		leftLine.setStroke(Color.GREEN);
		
		Line rightLine = new Line(width * cellLength, 0 * cellLength, width * cellLength, height * cellLength);
		rightLine.setStrokeWidth(2);
		rightLine.setStroke(Color.GREEN);
		
		Line downLine = new Line(0, height * cellLength, width * cellLength, height * cellLength);
		downLine.setStrokeWidth(2);
		downLine.setStroke(Color.GREEN);
		
		
		gameBoxPane.getChildren().add(topLine);
		gameBoxPane.getChildren().add(upLine);
		gameBoxPane.getChildren().add(leftLine);
		gameBoxPane.getChildren().add(rightLine);
		gameBoxPane.getChildren().add(downLine);
		
		
		root.getChildren().add(hbox);
		/*
		 *  ShengFu start
		 */
		Image stopImage = new Image(getClass().getResourceAsStream("/goodjob.gif"));
		stopImageView = new ImageView(stopImage);
		stopImageView.setOpacity(0);
		stopImageView.setFitHeight(scene.getHeight()/2 - cellLength * 3);
		stopImageView.setFitWidth(scene.getWidth()/2);
		stopImageView.setY(scene.getHeight()/4);
		root.getChildren().add(stopImageView);
		/*
		 * ShengFu end
		 */

		speedBar.setShowTickLabels(true);
		speedBar.setMajorTickUnit(1);
		speedBar.setBlockIncrement(1);
		speedBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            	speed = new_val.intValue();
        		speedText.setText("Speed: " + speed);
        		
        		speedLong = 800 - 180 * (speed - 1);
            }
        });
		

		/*
		 * ShengFu start
		 */
		
		bgMusicCheckBox = new CheckBox("Mute");
		bgMusicCheckBox.setPadding(new Insets(0,15,0,0));
		bgMusicCheckBox.setSelected(false);
		bgMusicCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	                if(new_val){
	                	backGroundMediaPlayer.stop();
	                }
	                else{
	                	backGroundMediaPlayer.play();
	                }
	        }
	    });
		
		pauseCheckBox = new CheckBox("Pause");
		pauseCheckBox.setSelected(false);
		pauseCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        	if(gameStartStatus == false) {return;}
	                if(new_val){
	                	timeShow.pause();
	                	pauseTime = System.currentTimeMillis();
	                	pauseStatus = true;
	                }
	                else{
	                	totalPauseTime += System.currentTimeMillis() - pauseTime;
	                	timeShow.play();
	                	pauseStatus = false;
	                }
	        }
	    });
		
		
		Text nextText = new Text("Next Block");
		nextBoxPane = new Pane();
		nextBoxPane.setMaxWidth(nextWidth * cellLength);
		nextBoxPane.setMaxHeight(nextHeight * cellLength);
		nextSquares = new Rectangle[nextHeight][nextWidth];
		for (int i = 0; i < nextHeight; i++) {
			for (int j = 0; j < nextWidth ; j++) {
				nextSquares[i][j] = new Rectangle(cellLength * j, cellLength * i, cellLength, cellLength);
				nextSquares[i][j].setStroke(Color.BLACK);
				nextSquares[i][j].setStrokeWidth(3);
				nextSquares[i][j].setFill(Color.WHITE);
				nextSquares[i][j].setOpacity(0);
				nextBoxPane.getChildren().add(nextSquares[i][j]);
			}
		}
		
		
		soundDropDown = new AudioClip(getClass().getResource("/cartoon035.mp3").toExternalForm());
		soundLeftRight = new AudioClip(getClass().getResource("/cartoon130.mp3").toExternalForm());
		soundRotate = new AudioClip(getClass().getResource("/cartoon136.mp3").toExternalForm());
		soundDeleteRow = new AudioClip(getClass().getResource("/cartoon017.mp3").toExternalForm());//sam5

		backGroundMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/tetris tone loop.mp3").toExternalForm()));
		backGroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		backGroundMediaPlayer.play();
		nextBlocks = new Block[nextLength];
		for(int i = 0; i < nextBlocks.length ; i++){
			nextBlocks[i] = generateBlock();
		}
		
		blockColor[0] = Color.rgb(255, 0, 0);
		blockColor[1] = Color.rgb(0, 255, 255);
		blockColor[2] = Color.rgb(255, 0, 255);
		blockColor[3] = Color.rgb(0, 255, 0);
		blockColor[4] = Color.rgb(153, 51, 255);
		blockColor[5] = Color.rgb(255, 153, 204);
		blockColor[6] = Color.rgb(255, 0, 127);

		/*
		 * ShengFu end
		 */
		setUpPane = new VBox(15);
		setUpPane.setPadding(new Insets(15,15,15,15));
		setUpPane.setAlignment(Pos.TOP_CENTER);
		setUpPane.getChildren().add(startButton);
		setUpPane.getChildren().add(stopButton);
		setUpPane.getChildren().add(quitButton);
		setUpPane.getChildren().add(timeText);
		setUpPane.getChildren().add(scoreText);
		setUpPane.getChildren().add(speedText);
		setUpPane.getChildren().add(speedBar);
		HBox prettyH = new HBox();
		prettyH.getChildren().addAll(bgMusicCheckBox,pauseCheckBox);
		setUpPane.getChildren().add(prettyH);
		setUpPane.getChildren().add(nextText);
		setUpPane.getChildren().add(nextBoxPane);
		
		hbox.getChildren().addAll(gameBoxPane, setUpPane);
	
		root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {

                if (keyEvent.getCode() == KeyCode.LEFT) {
                	/*
                	 * ShengFu start
                	 */
                	if(pauseStatus){
                		keyEvent.consume();
                		return;
                	}
                	/*
                	 * ShengFu end
                	 */
                    if (!timerDeleteRow) {
                    	soundLeftRight.play();
                    	moveLeft();
                    }
                     
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                	/*
                	 * ShengFu start
                	 */
                	if(pauseStatus){
                		keyEvent.consume();
                		return;
                	}
                	/*
                	 * ShengFu end
                	 */
                	if (!timerDeleteRow) {
                		soundLeftRight.play();
                		moveRight();
                	}
                                       
                    keyEvent.consume();
                }  else if (keyEvent.getCode() == KeyCode.UP) {
                	/*
                	 * ShengFu start
                	 */
                	if(pauseStatus){
                		keyEvent.consume();
                		return;
                	}
                	/*
                	 * ShengFu end
                	 */
                	if (!timerDeleteRow) {
                		soundRotate.play();
                		rotate();
                	}
                       
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                	/*
                	 * ShengFu start
                	 */
                	if(pauseStatus){
                		keyEvent.consume();
                		return;
                	}
                	/*
                	 * ShengFu end
                	 */
                	if (!timerDeleteRow) {
                		soundDropDown.play();
                		drop();
                	}
                       
                    keyEvent.consume();
                }
                
                
            }
        });
		                       
                   
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("The Tetris Game");
		primaryStage.setResizable(true);
		primaryStage.show();
		
		startButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                startTetris();
            }
        });
		
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                stopTetris();
            }
        });
		
		quitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Platform.exit();
            	System.exit(0);
            }
        });
		
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
            	Platform.exit();
            	System.exit(0);
            }

        });

		
	}
	
	/*
	 * Designed by Yu
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// Initiate gameBox and control Panel:
			initiateGame(primaryStage);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Designed by Yu
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
