import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.undo.*;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoManager;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
public class Minefield extends JFrame 
{
	int savedlevel;  
 
    JPanel panelmt = new JPanel();
    JTextField tf_mine, tf_time,savedT;
	//JTextArea savedT;
    JButton reset = new JButton("");
    JButton undo = new JButton(""); 
	 JPanel panelE = new JPanel();
    boolean check = true, starttime = false;
    Stopwatch sw;
   protected UndoManager undoManager = new UndoManager();
  
  //best timer
 SLL<Integer> myList = new SLL<Integer>();

  
  
int ROWS = 10;
int COLS = 15;
int BOMBS = 9;
	int cellsRevealed = 0;
	private ImageIcon	flag, bomb,winn,loss, un;
	// Data
	private MSCell field[][];
	// GUI components
	private JButton[][] grid = new JButton[ROWS+2][COLS+2];
	JPanel gridPanel;
	JLabel tempLabel;
	JLabel time;
    JPanel topPanel; 
	private final GridBagLayout layout;
	private final GridBagConstraints gbc;
	 MouseHandler mh = new MouseHandler();
	 private boolean selected= false;

	
	public Minefield()
	{

		
		// GUI components
		super("Minesweeper");
	    
		// create grid for bottons
		gridPanel = new JPanel();
		layout = new GridBagLayout();
		gridPanel.setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;	
		gridPanel.setBackground(Color.WHITE);			
		flag = new ImageIcon(this.getClass().getResource("flag.png"))	;
		bomb = new ImageIcon(this.getClass().getResource("bomb.png"))	;
		winn =  new ImageIcon(this.getClass().getResource("winning.png"));
		loss =  new ImageIcon(this.getClass().getResource("losing.png"));
		un =  new ImageIcon(this.getClass().getResource("undo.png"));
		sw = new Stopwatch();
		int row, col;
		for (row=0; row<ROWS+2; row++){
			for (col = 0; col<COLS+2; col++)
			{
				gbc.gridx = row;
				gbc.gridy = col;			
				grid[row][col] = new JButton(row +","+col);				
			    grid[row][col].setText(" ");
				gridPanel.add(grid[row][col],gbc);
				grid[row][col].addMouseListener(mh);
				
			}
		}
		setLayout(new BorderLayout());
		add(gridPanel,BorderLayout.CENTER);
			
		reset.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent ae) {
                try {
                    sw.stop();
                   setpanel();
                } catch (Exception ex) {
                    setpanel();
                }
				gridPanel.removeAll();
              reset();
	
 
            }
        });
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        show();

	field= new MSCell[ROWS+2][COLS+2];
		initialiseField();
		plantBombs();
		computeNumbers();
		displayField();
		setmanue();
		setpanel();
	}

	public void initialiseField()
	{
		for (int i=0; i<ROWS+2;i++)
		{
			for (int j=0; j<COLS+2; j++)
			{
				field[i][j]=new MSCell();
			}
		}
	}
	
	public void plantBombs()
	{
		Random randomNumbers = new Random();

		int bombsPlanted=0;
		int bombRow = 0;
		int bombCol = 0;
		while (bombsPlanted < BOMBS)
		{
			bombRow = randomNumbers.nextInt(ROWS)+1; //nextInt(n) produce random number from 0-(n-1) we want a number from 1-(ROWS)
			bombCol = randomNumbers.nextInt(COLS)+1;
			System.out.println( bombRow +" " + bombCol);
			if (!field[bombRow][bombCol].isBomb()) // if not yet bomb
			{
				 field[bombRow][bombCol].setBomb();
				// field[bombRow][bombCol].setIcon(bomb);
				 bombsPlanted++;
				 
				 
			}
			
		}
		
	}

	
	public void computeNumbers()
	{
		int countBombs= 0;
		for (int i=1; i<=ROWS;i++)
		{
			for (int j=1; j<=COLS; j++)
			{	
	            countBombs=0;
				if (!field[i][j].isBomb())
				{
					for (int a=-1; a<=1; a++)
					{
						for (int b=-1; b<=1; b++)
						{
							if (field[i+a][j+b].isBomb() )
							{
								countBombs++;
								
							}
						}
							
					}	
					field[i][j].setValue(countBombs);
					
				}
			}
			
		}
		
	}
		
	public void displayField()
	{
		for (int i=0; i<=ROWS+1;i++)
		{
			for (int j=0; j<=COLS+1; j++)
			{  
				grid[i][j].setText(field[i][j].toString());
			}
			repaint();
			revalidate();
		}
	}
	
	public void revealZero(int i,int j)
	{
		System.out.println( i +" " + j);
		for (int a=-1; a<=1; a++)
		{
			for (int b=-1; b<=1; b++)
			{
				if ((field[i+a][j+b].getValue() ==0) && (!field[i+a][j+b].isRevealed()))
				{
					field[i+a][j+b].setRevealed();
					if (field[i+a][j+b].getValue() !=-1) 
						cellsRevealed++;
					gridPanel.remove(grid[i+a][j+b]);
					gbc.gridx = i+a;
					gbc.gridy = j+b;
					tempLabel = new JLabel(field[i+a][j+b].toString());
					tempLabel.setForeground(Color.BLACK);
					tempLabel.setFont(new Font("Serif", Font.BOLD, 20));
			        tempLabel.setText("");

					
					//grid[i+a][j+b].setText(field[i+a][j+b].toString());
					System.out.println(tempLabel.getText());
					gridPanel.add(tempLabel,gbc);
			
					revalidate();
					repaint();
					
					revealZero(i+a,j+b); // clear more zeros!
				}
				else
				{
					if (!field[i+a][j+b].isRevealed())
					{
						field[i+a][j+b].setRevealed();
						if (field[i+a][j+b].getValue() !=-1) cellsRevealed++;
						gbc.gridx = i+a;
						gbc.gridy = j+b;
						tempLabel = new JLabel(field[i+a][j+b].toString());
						gridPanel.remove(grid[i+a][j+b]);
						gridPanel.add(tempLabel,gbc);
						grid[i+a][j+b].setText(field[i+a][j+b].toString());
					    tempLabel.setForeground(Color.BLUE);
						tempLabel.setFont(new Font("Serif", Font.BOLD, 20));
						revalidate();
						repaint();
					}
				}
			}
							
		}	
	}
	// GUI event handlers
	// inner class for MouseListener
	private class MouseHandler implements MouseListener
	{
		@Override 
		 
		 public void mouseClicked(MouseEvent me) 
		 {
	
			//left button
			if (me.getButton()== MouseEvent.BUTTON1)
			{
				Object o= me.getSource();
				tempLabel= new JLabel("");
				for (int r=1; r<=ROWS; r++)
					for (int c = 1; c<=COLS; c++)
					{
						if 	(grid[r][c] == (JButton) o)
						{
							field[r][c].setRevealed();
														
							if (field[r][c].isBomb())
							{
								//for (int i=1; i<=ROWS; i++){
									  
								//	for (int j =1; j<=COLS; j++)
									//{
									tempLabel.setText("  "+field[r][c].toString());
									field[r][c].setRevealed();   
								   // gbc.gridx = i;							
									gridPanel.remove(grid[r][c]);
									tempLabel.setText("  "+field[r][c].toString());
									gridPanel.add(tempLabel,gbc);								
								//	tempLabel.setIcon(bomb);
									
									//}
									
								//
								    tempLabel.setIcon(bomb);								
									reset.setIcon(loss);
									sw.stop();
								
			                playSound("lose.wav");
							int sortTime = Integer.parseInt(tf_time.getText());
							JOptionPane.showMessageDialog(gridPanel," You lose....\n Number of mines:"+tf_mine.getText()+"\nTime: "+sortTime+"seconds\nExit the game OR Reset");
								 myList.insertTime(sortTime);
								 System.out.println("\n\nTime Scored:  "+myList);
								 savedT.setText("\nTime Scored:  "+myList);				  
						         reset.setIcon(loss);
								//exit(0);
					
							}						
							if (!field[r][c].isBomb())// remove if later when exit is used
							{
								if(field[r][c].getValue()==0)// set revealed done already
							    {
								    revealZero(r,c);
									gbc.gridx = r;
									gbc.gridy = c;
									playSound("clickS.wav");
									gridPanel.remove(grid[r][c]);
									gridPanel.add(tempLabel,gbc);
								tempLabel.setForeground(Color.GREEN);
								tempLabel.setFont(new Font("Serif", Font.BOLD, 20));
									cellsRevealed++;
									
							    }
								else
								{
					              cellsRevealed++;
								}
							}
						
							tempLabel.setText("  "+field[r][c].toString());
							gbc.gridx = r;
							gbc.gridy = c;
							
							gridPanel.remove(grid[r][c]);
						    gridPanel.add(tempLabel,gbc);
							playSound("clickS.wav");
						
				           tempLabel.setForeground(Color.RED);
						   tempLabel.setFont(new Font("Serif", Font.BOLD, 20));
						   reset.setIcon(winn);	
							if (starttime == false) {
                                 sw.Start();
                                 starttime = true;
							}
							if (cellsRevealed==((ROWS*COLS)-BOMBS)){
								 reset.setIcon(winn);
								sw.stop();
								playSound("win.wav");
								int sortTime = Integer.parseInt(tf_time.getText());
							    JOptionPane.showMessageDialog(gridPanel," You win...\n Number of mines:"+tf_mine.getText()+"\nTime: "+sortTime+"seconds");
								 myList.insertTime(sortTime);
								 System.out.println("\n\nTime Scored:  "+myList);
								 savedT.setText("\nTime Scored:  "+myList);		
							}
							revalidate();
							repaint();
						}
					}
			}
			// right button
			if (me.getButton()== MouseEvent.BUTTON3)
			{
				Object o= me.getSource();
				tempLabel= new JLabel("@");
				for (int r=1; r<=ROWS; r++)
					for (int c = 1; c<=COLS; c++)
					{
						if 	(grid[r][c] == (JButton) o)
						{
							gbc.gridx = r;
							gbc.gridy = c;
							if (field[r][c].isFlagged())
							  field[r][c].setFlagged(false);
						    else
							field[r][c].setFlagged(true);
					        //BOMBS =	BOMBS-1;
						    //tf_time.getText();
							grid[r][c].setIcon(flag);
							--BOMBS;
							tf_mine.setText(""+BOMBS);
							playSound("clickS.wav");
							revalidate();
							repaint();
						}
					
					}
			}	 
		 }
		 public void mousePressed(MouseEvent me) {}
		 public void mouseReleased(MouseEvent me) {}
		 public void mouseEntered(MouseEvent me) {}
		 public void mouseExited(MouseEvent me) {}
	}

 public void playSound(String soundName)
 {
   try 
   {
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
    Clip clip = AudioSystem.getClip();
    clip.open(audioInputStream);
    clip.start();
   }
   catch(Exception ex)
   {
     System.out.println("Error with playing sound.");
     ex.printStackTrace( );
   }
 }    




public class Stopwatch extends JFrame implements Runnable {
 
        long startTime;
  
        Thread updater;
        boolean isRunning = false;
        long a = 0;
        Runnable displayUpdater = new Runnable() {
 
            public void run() {
                displayElapsedTime(a);
                a++;
            }
        };
 
        public void stop() {
            long elapsed = a;
            isRunning = false;
            try {
                updater.join();
            } catch (InterruptedException ie) {
            }
            displayElapsedTime(elapsed);
            a = 0;
        }
 
        private void displayElapsedTime(long elapsedTime) {
 
            if (elapsedTime >= 0 && elapsedTime < 9) {
                tf_time.setText("00" + elapsedTime);
            } else if (elapsedTime > 9 && elapsedTime < 99) {
                tf_time.setText("0" + elapsedTime);
            } else if (elapsedTime > 99 && elapsedTime < 999) {
                tf_time.setText("" + elapsedTime);
            }
        }
 
        public void run() {
            try {
                while (isRunning) {
                    SwingUtilities.invokeAndWait(displayUpdater);
                    Thread.sleep(1000);
                }
            } catch (java.lang.reflect.InvocationTargetException ite) {
                ite.printStackTrace(System.err);
            } catch (InterruptedException ie) {
            }
        }
 
        public void Start() {
            startTime = System.currentTimeMillis();
            isRunning = true;
            updater = new Thread(this);
            updater.start();
        }
    }
	


    public void reset() {
      // check = true;
        starttime = false;
		
         int row, col;
		for (row=0; row<ROWS+2; row++)
			for (col = 0; col<COLS+2; col++)
			{
				gbc.gridx = row;
				gbc.gridy = col;
				grid[row][col] = new JButton();
				grid[row][col].setText(field[row][col].toString());
			    grid[row][col].setText(" ");			
				gridPanel.add(grid[row][col],gbc);
				grid[row][col].addMouseListener (mh);			
			}     
    }
 
    public void setpanel( ) {

    
        tf_mine = new JTextField("0"+BOMBS, 3);
        tf_mine.setEditable(false);
        tf_mine.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));
        tf_mine.setBackground(Color.WHITE);
		savedT = new JTextField(" " , 8);
		savedT.setFont(new Font("DigtalFont.TTF", Font.BOLD, 16));
		savedT.setBackground(Color.WHITE);
		savedT.setForeground(Color.RED);
		savedT.setBorder(BorderFactory.createLoweredBevelBorder());
        tf_mine.setForeground(Color.RED);
        tf_mine.setBorder(BorderFactory.createLoweredBevelBorder());
        tf_time = new JTextField("000", 3);
        tf_time.setEditable(false);
        tf_time.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));
        tf_time.setBackground(Color.WHITE);
        tf_time.setForeground(Color.RED);
        tf_time.setBorder(BorderFactory.createLoweredBevelBorder());
		undo.setBorder(BorderFactory.createLoweredBevelBorder());
		reset.setIcon(winn);
        undo.setIcon(un);	
        undo.setBackground(Color.WHITE);
        reset.setBackground(Color.WHITE);		
      
	   String timeT = tf_mine.getText();
	   int sortTime = Integer.parseInt(timeT);
	        gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        reset.setBorder(BorderFactory.createLoweredBevelBorder());
 
        panelmt.removeAll();
        panelmt.setLayout(new BorderLayout());
		
        panelmt.add(tf_mine, BorderLayout.WEST);
        panelmt.add(reset, BorderLayout.CENTER);
        panelmt.add(tf_time, BorderLayout.EAST);
		panelmt.add(undo, BorderLayout.NORTH);
        panelmt.setBorder(BorderFactory.createLoweredBevelBorder());
		
		panelE.removeAll();
		panelE.setLayout(new BorderLayout());
		panelE.add(savedT,BorderLayout.WEST);
		panelE.setBorder(BorderFactory.createLoweredBevelBorder());
        reset();
 
       gridPanel.revalidate();
       gridPanel.repaint();	   
        getContentPane().setLayout(new BorderLayout());     
        getContentPane().repaint();
        getContentPane().add(gridPanel, BorderLayout.CENTER);
        getContentPane().add(panelmt, BorderLayout.NORTH);
		getContentPane().add(panelE, BorderLayout.WEST);
        setVisible(true);
    }
 
    public void setmanue() {
				
        JMenuBar bar = new JMenuBar();
 
        JMenu game = new JMenu("GAME");
 
        JMenuItem menuitem = new JMenuItem("Levels");
        final JCheckBoxMenuItem beginner = new JCheckBoxMenuItem("Beginner");
        final JCheckBoxMenuItem intermediate = new JCheckBoxMenuItem("Intermediate");
      
        final JMenuItem exit = new JMenuItem("Exit");
        final JMenu help = new JMenu("Help");
        final JMenuItem helpitem = new JMenuItem("Help");
 
        ButtonGroup status = new ButtonGroup();
 	
        menuitem.addActionListener(
                new ActionListener() {
 
                    public void actionPerformed(ActionEvent e) {
                                      
                      setpanel();
                      gridPanel.revalidate();
                      gridPanel.repaint();
                    }
                });

      beginner.addActionListener(
                new ActionListener() {
 
                    public void actionPerformed(ActionEvent e) {
						 ROWS=7;
						 COLS = 8;
						 BOMBS = 5;
					gridPanel.removeAll();
                       reset();
                       setpanel();
                       gridPanel.revalidate();
                       gridPanel.repaint();
                       beginner.setSelected(true);                  
	                   savedlevel = 1;  
					  
			 }
                });
	 
        intermediate.addActionListener(
                new ActionListener() {
 
                    public void actionPerformed(ActionEvent e) {
						  ROWS= 9;
						  COLS =10;
						  BOMBS =7;
                      gridPanel.removeAll();
                      reset();
                      setpanel();
                      gridPanel.revalidate();
                      gridPanel.repaint();
                      intermediate.setSelected(true);
	                  savedlevel = 2;
			           
                    }
                });
  


        exit.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
 
        helpitem.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Instructions: \nClick a square, you get a number. \nThat number is the number of how many mines are surrounding it. \nIf you find the mine, you can open (unopened) squares around it, opening more areas.");
 
            }
        });

        setJMenuBar(bar);
        status.add(beginner);
        status.add(intermediate);
       
        game.add(menuitem);
        game.addSeparator();
        game.add(beginner);
        game.add(intermediate);
      
        game.addSeparator();
        game.add(exit);
        help.add(helpitem); 
        bar.add(game);
        bar.add(help);
 
    }
			
 }
		
