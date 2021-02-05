package quizBox;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class QuizCardPlayer implements Serializable {

	private JTextArea display;
	private ArrayList<QuizCard> cardList;
	private QuizCard currentCard;
	private int currentCardIndex;
	private JFrame frame;
	private JButton nextButton;
	private boolean isShowAnswer;
	
	public static void main(String[] args) {
		
		QuizCardPlayer reader=new QuizCardPlayer();
		reader.go();
		}
	
	public void go() {
		
		frame=new JFrame("Ответь на вопрос!");
		JPanel mainPanel=new JPanel();
		Font bigFont=new Font("sanserif",Font.BOLD, 24);
		
		display=new JTextArea(8,23);
		display.setFont(bigFont);
		
		display.setLineWrap(true);
		display.setEditable(false);
		
		JScrollPane qScroller=new JScrollPane(display);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		nextButton=new JButton("Показать вопрос");
		mainPanel.add(qScroller);
		mainPanel.add(nextButton);
		nextButton.addActionListener(new NextCardListener());
		
		JMenuBar menuBar=new JMenuBar();
		JMenu fileMenu=new JMenu("File");
		JMenuItem loadMenuItem=new JMenuItem("Загрузить набор карточек");
		loadMenuItem.addActionListener(new OpenMenuListener());
		fileMenu.add(loadMenuItem);
		menuBar.add(fileMenu);
		
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
		frame.setSize(640, 400);
		frame.setVisible(true);
		
	}
	
	public class NextCardListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ev) {
			
			if(isShowAnswer) {
				display.setText(currentCard.getAnswer());
				nextButton.setText("Следующая карточка");
				isShowAnswer=false;
			}else {
				if(currentCardIndex<cardList.size()) {
					showNextCard();
				}else {
					display.setText("Это была последняя карточка");
					nextButton.setEnabled(false);
				}
			}
			
		}
	
	}
	
	public class OpenMenuListener implements ActionListener {
		
		public void actionPerformed(ActionEvent ev) {
			
			JFileChooser fileOpen=new JFileChooser();
			fileOpen.showOpenDialog(frame);
			loadFile(fileOpen.getSelectedFile());
			
		}
	
	}
	
	private void loadFile(File file) {
		
		cardList=new ArrayList<QuizCard>();
		
		try {
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String line=null;
			 while((line=reader.readLine())!= null) { 
				makeCard(line);}
			reader.close();
		}catch(Exception ex) {
			System.out.println("Что-то пошло не так");
			ex.printStackTrace();}
		
		showNextCard();
	}
	
	private void makeCard(String lineToParse) {
		
		String[] result=lineToParse.split("/");
		QuizCard card=new QuizCard(result[0],result[1]);
		cardList.add(card);
	}
	
	private void showNextCard() {
		
		currentCard=cardList.get(currentCardIndex);
		currentCardIndex++;
		display.setText(currentCard.getQuestion());
		nextButton.setText("Показать ответ");
		isShowAnswer=true;
		
	}
	
	

}
