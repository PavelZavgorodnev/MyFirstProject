package quizBox;

public class QuizCard {
	
	String a;
	String q;
	
	public QuizCard(String qu,String an) {
		
		q=qu;
		a=an;
	}
	

	public String getQuestion(){
		return q;
	}
	
	public String getAnswer(){
		return a;
	}

}
