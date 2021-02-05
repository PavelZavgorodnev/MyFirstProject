package beatBox;

import java.awt.*;
import javax.swing.*;

import quizBox.QuizCard;
import quizBox.QuizCardBuilder.NewMenuListener;
import quizBox.QuizCardBuilder.SaveMenuListener;

import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BeatBox {
	
	JPanel mainPanel;
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
	
	String[] instrumentNames= {"Buss Drum","Closed Hi-Hat","Open Hi-Hat","Acoustic Snare","Crash Cymbal","Hand Clap","High Tom",
			"Hi Bongo","Maracas","Wistle","Low Conga","Cow Bell","Vibraslap","Low-mid Tom","High Agogo","Open Hi Conga"};
	
	int[]instruments= {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

	public static void main(String[] args) {
		
		new BeatBox().buildGui();
}
	
	public void buildGui() {
		
		theFrame=new JFrame("Cyber BeatBox");
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout=new BorderLayout();
		JPanel background=new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		checkboxList=new ArrayList<JCheckBox>();
		
		Box buttonBox=new Box(BoxLayout.Y_AXIS);
		
		JButton start= new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		JButton stop= new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		JButton upTempo= new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		JButton downTempo= new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		
		
		JMenuBar menuBar=new JMenuBar();
		  JMenu fileMenu=new JMenu("File");
		  JMenuItem restoreMenuItem=new JMenuItem("Restore");
		  JMenuItem saveMenuItem=new JMenuItem("Save");
		  
		  restoreMenuItem.addActionListener(new MyRestoreMenuListener());
		  saveMenuItem.addActionListener(new MySaveListener());
		  
		  fileMenu.add(restoreMenuItem);
		  fileMenu.add(saveMenuItem);
		  menuBar.add(fileMenu);
		
		Box nameBox=new Box(BoxLayout.Y_AXIS);
		
		for(int i=0;i<16;i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}
		
		background.add(BorderLayout.EAST,buttonBox);
		
		background.add(BorderLayout.WEST,nameBox);
		
		theFrame.getContentPane().add(background);
		
		GridLayout grid=new GridLayout(16,16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel=new JPanel(grid);
		
		background.add(BorderLayout.CENTER,mainPanel);
		
		for(int i=0;i<256;i++) {
			JCheckBox c=new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			mainPanel.add(c);	
		}
		
		setUpMidi();
		
		theFrame.setJMenuBar(menuBar);
		theFrame.setBounds(50, 50, 500, 500);
		theFrame.pack();
		theFrame.setVisible(true);
		
		}
	
	public void setUpMidi() {
		try {
			sequencer=MidiSystem.getSequencer();
			sequencer.open();
			sequence=new Sequence(Sequence.PPQ, 4);
			track=sequence.createTrack();
			sequencer.setTempoInBPM(120);
		} catch (Exception e) { e.printStackTrace();}
	}
	
	public void buildTrackAndStart() {
		
		int[] trackList=null;
		
		sequence.deleteTrack(track);
		track=sequence.createTrack();
		
		for(int i=0;i<16;i++) {
			trackList=new int[16];
			
			int key=instruments[i];
			
			for(int j=0;j<16;j++) {
				
				JCheckBox jc=(JCheckBox)checkboxList.get(j+(16*i));
				if(jc.isSelected()) {
					trackList[j]=key;
				}else {
					trackList[j]=0;
		}
			}
			makeTracks(trackList);
			track.add(makeEvent(176,1,127,0,16));
			  }
		track.add(makeEvent(192,9,1,0,15));
		try {
			
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		} catch(Exception ex) {ex.printStackTrace();}
		 }
	
	public class MyStartListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			buildTrackAndStart();
		}
	}
	
public class MyStopListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			sequencer.stop();
		}
	}

public class MyUpTempoListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
		float tempoFactor=sequencer.getTempoFactor();
		sequencer.setTempoFactor((float)(tempoFactor*1.03));
	}
}

public class MyDownTempoListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
		float tempoFactor=sequencer.getTempoFactor();
		sequencer.setTempoFactor((float)(tempoFactor*0.97));
	}
}

public class MySaveListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileSave=new JFileChooser();
		fileSave.showSaveDialog(theFrame);
		saveFile(fileSave.getSelectedFile());
	}
}

public class MyRestoreMenuListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileOpen=new JFileChooser();
		fileOpen.showOpenDialog(theFrame);
		loadFile(fileOpen.getSelectedFile());
		}
	}




  public void makeTracks(int[] list) {
	  
	  for(int i=0;i<16;i++) {
		  int key=list[i];
		  
		  if(key !=0) {
			  track.add(makeEvent(144,9,key,100,i));
			  track.add(makeEvent(128,9,key,100,i+1));
		  }
	  }
  }
  
  public MidiEvent makeEvent(int comd,int chan,  int one, int two, int tick) {
	  
	  MidiEvent event=null;
	  
	  try {
		  ShortMessage a=new ShortMessage();
		  a.setMessage(comd, chan, one, two);
		  event=new MidiEvent(a,tick);
	  }catch (Exception e) {e.printStackTrace();}
	  return event;
  }
  
  public void saveFile(File file) {
		
		try {
			boolean[]checkBoxState=new boolean[256];
			for(int i=0;i<256;i++) {
				JCheckBox check=(JCheckBox)checkboxList.get(i);
				if(check.isSelected()) {
					checkBoxState[i]=true;
				}
			}
			try {
				FileOutputStream fileStream =new FileOutputStream(file);
				ObjectOutputStream os=new ObjectOutputStream(fileStream);
				os.writeObject(checkBoxState);
				os.close();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			
			
			
		}catch(Exception ex) {
			System.out.println("Неудалось записать файл");
			ex.printStackTrace();}
		
	}
  
  private void loadFile(File file) {
		
	  boolean[] checkBoxState=null;
		
		try {
			FileInputStream fileIn=new FileInputStream(file);
			ObjectInputStream is=new ObjectInputStream(fileIn);
			checkBoxState=(boolean[])is.readObject();
			is.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		for(int i=0;i<256;i++) {
			JCheckBox check=(JCheckBox)checkboxList.get(i);
			if(checkBoxState[i]) {
				check.setSelected(true);
						}else {
							check.setSelected(false);
						}
							
			}
		sequencer.stop();
		buildTrackAndStart();
		}
	}


	
	
	
	
	
	

	


