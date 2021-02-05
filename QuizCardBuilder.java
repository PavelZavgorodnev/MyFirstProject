package quizBox;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class QuizCardBuilder {
	
	private JTextArea question;
	private JTextArea answer;
	private ArrayList<QuizCard> cardList;
	private JFrame frame;

	public static void main(String[] args) {
		
		QuizCardBuilder builder=new QuizCardBuilder();
		builder.go();
	}
	
	public void go() { //��������� GUI
		
	  frame=new JFrame("������ ���� ��������");
	  JPanel mainPanel=new JPanel();
	  Font bigFont=new Font("sanserif",Font.BOLD,24);
	  question=new JTextArea(6,20);
	  question.setLineWrap(true);
	  question.setWrapStyleWord(true);
	  question.setFont(bigFont);
	  
	  JScrollPane qScroller=new JScrollPane(question);
	  qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	  qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	  
	  answer=new JTextArea(6,20);
	  answer.setLineWrap(true);
	  answer.setWrapStyleWord(true);
	  answer.setFont(bigFont);
	  
	  JScrollPane aScroller=new JScrollPane(answer);
	  aScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	  aScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	  
	  JButton nextButton=new JButton("��������� ��������");
	  
	  cardList=new ArrayList<QuizCard>();
	  
	  JLabel qLabel=new JLabel("������: ");
	  JLabel aLabel=new JLabel("�����: ");
	  
	  mainPanel.add(qLabel);
	  mainPanel.add(qScroller);
	  mainPanel.add(aLabel);
	  mainPanel.add(aScroller);
	  mainPanel.add(nextButton);
	  
	  nextButton.addActionListener(new NextCardListner());
	  
	  JMenuBar menuBar=new JMenuBar();
	  JMenu fileMenu=new JMenu("File");
	  JMenuItem newMenuItem=new JMenuItem("New");
	  JMenuItem saveMenuItem=new JMenuItem("Save");
	  
	  newMenuItem.addActionListener(new NewMenuListener());
	  saveMenuItem.addActionListener(new SaveMenuListener());
	  
	  fileMenu.add(newMenuItem);
	  fileMenu.add(saveMenuItem);
	  menuBar.add(fileMenu);
	  frame.setJMenuBar(menuBar);
	  frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
	  frame.setSize(500, 600);
	  frame.setVisible(true);
	 
	}
public class NextCardListner implements ActionListener{
	
	public void actionPerformed(ActionEvent e) {
		
		QuizCard card=new QuizCard(question.getText(),answer.getText());
		cardList.add(card);
		clearCard();
		}
}

public class NewMenuListener implements ActionListener{
	
public void actionPerformed(ActionEvent e) {
	
	cardList.clear();
	clearCard();
		
	}
}

public class SaveMenuListener implements ActionListener{
	
public void actionPerformed(ActionEvent e) {
	
	JFileChooser fileSave=new JFileChooser();
	fileSave.showSaveDialog(frame);
	saveFile(fileSave.getSelectedFile());
		}
}

public void clearCard() {
	
	question.setText("");
	answer.setText("");
	question.requestFocus();	
}

public void saveFile(File file) {
	
	try {
		
		BufferedWriter writer=new BufferedWriter(new FileWriter(file));
		
		for(QuizCard card:cardList) {
			writer.write(card.getQuestion()+"/");
			writer.write(card.getAnswer()+"\n");
			
		}
		writer.close();
	}catch(IOException ex) {
		System.out.println("��������� �� ������ �������� ��������");
		ex.printStackTrace();}
	
}

}
