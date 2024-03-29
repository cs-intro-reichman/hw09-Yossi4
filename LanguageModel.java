import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String window = "";
        char c;

        In in = new In(fileName);

        // Reads just enough characters to form the first window
        for (int i = 0; i < windowLength; i++) {
            window += in.readChar();
        }

        while (!in.isEmpty()) {
            // Gets the next character
            c = in.readChar();

            
            List probs = CharDataMap.get(window);

            if (probs == null) {

                probs = new List();
                CharDataMap.put(window, probs);
            }


            probs.update(c);


            window += c;
            window = window.substring(1);
        }

        
        for (List probs : CharDataMap.values()){
            calculateProbabilities(probs);
        }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
		int totalChars = 0;
		for (int i = 0; i < probs.getSize() ;i++ ) 
		{
			totalChars += probs.get(i).count;	
		}
		// now we know how many chracters are in the list
		CharData first = probs.get(0);
		first.p = first.count /(double) totalChars;
		first.cp = first.count / (double) totalChars;

		CharData current;
		CharData prev = first;
		for (int i = 1 ;i < probs.getSize() ;i++ ) 
		{
			current = probs.get(i);
			current.p = current.count / (double) totalChars;
			current.cp = prev.cp + (current.count / (double) totalChars);
			prev = current;
		} 

	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Your code goes here
		double r = randomGenerator.nextDouble();
        int listSize = probs.getSize();

        for (int i = 0; i < listSize; i++) {
            CharData currentCharData = probs.get(i);
            if (currentCharData.cp > r) {
                return currentCharData.chr;
            }
        }

        return probs.get(listSize - 1).chr;
		
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
		if (initialText.length() < windowLength) 
		{
            return initialText;
           }

         String window = initialText.substring(initialText.length() - windowLength);
        String generatedText = window;
        for (int i = 0; i < textLength; i++) {
            List probs = CharDataMap.get(window);
            if (probs != null) {
                char newChar = getRandomChar(probs);
                generatedText += newChar;
                window = generatedText.substring(generatedText.length() - windowLength);
            } else {
                return generatedText;
            }
        }

        return generatedText;
		
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
