package proofs;

public class Test2 {

	public static String s = "registration+Gravy94+FirstApplet+https://hooks.slack.com/services/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";
	private String slug_Repo;
	private String incoming_link;

	public static void main(String args[]) throws Exception {
		Test2 t = new Test2();
		int i = 0;

		// before I've to check if string text contains http://
		// before if text string contains 3 parameters
		// the text string have format registration+S+R+link
		if (t.ceckParameters(t.s)) {
			t.createLinks(t.s);
		}

		System.out.println("1) " + t.slug_Repo);
		System.out.println("2) " + t.incoming_link);
		// t.createRepoLink(s);

	}

	/**
	 * Function checking 3 parameters
	 * @param text	string returned in slack json body response
	 * @return true if there are 3 parameters including incoming link
	 */
	private boolean ceckParameters(String text) {
		int i = 0, c = 0;
		while (i < text.length() & i != -1) {
			i = this.s.indexOf('+', i);
			if (i != -1) {
				//System.out.println(this.s.charAt(i) + " at " + i + " position");
				i++;
				c++;
			}
		}
		// check numbers of parameters
		if (c != 3) {
			System.err.println("Error: numbers of parameters not equals to 3!");
			return false;
		} else {
			// check the http://
			if (text.contains("https://hooks.slack.com/services/"))
				return true;
			else {
				System.err.println("Error: incoming link not correct!");
				return false;
			}
		}
	}

	/**
	 * Function creating repo_link and incoming_link
	 * @param text string returned in slack json body response
	 * @throws Exception
	 */
	private void createLinks(String text) {
		String temp_Repo_Slug = new String();
		String temp_incoming_link = new String();
		int c = 0;
		int index = 13;
		int length = text.length();
		char c_temp;
		/* EXTRACT repo_link from message body json text */
		while (index < length & c < 2) {
			c_temp = text.charAt(index);
			if (c_temp != '+')
				temp_Repo_Slug += text.charAt(index);
			else if (c_temp == '+' & c < 1) {
				temp_Repo_Slug += "%2F";
				c++;
			} else
				c++;
			index++;
		}
		//System.out.println("\nLink creato: " + temp_Repo_Slug);
		this.slug_Repo = temp_Repo_Slug;
		/* EXTRACT incoming_link from message body json text */
		while (index < text.length()) {
			temp_incoming_link += text.charAt(index);
			index++;
		}
		//System.out.println("\nLink creato: " + temp_incoming_link);
		this.incoming_link = temp_incoming_link;
	}
}
