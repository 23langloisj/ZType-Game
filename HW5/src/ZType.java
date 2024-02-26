import java.awt.Color;
import javalib.funworld.*;
import javalib.worldimages.*;
import tester.*;     
import java.util.Random;

// Represents any functions that don't belong in a specific class
class Utils {

  Random seed;

  Utils(Random seed) {
    this.seed = seed;
  }

  //randomly generates a string with 6 random letters
  String randomWord() {
    return randomWordAcc("", 0, seed);
  }

  //accumulates the current string
  String randomWordAcc(String currWord, int length, Random seed) {
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    int charNum = seed.nextInt(26);
    if (length < 6) {
      return randomWordAcc(currWord + alphabet.charAt(charNum), length + 1, seed);
    } else {
      return currWord;
    }
  }
}

// Represents the world that the ZType game exists in
class ZTypeWorld extends World {

  // the list of words falling from the ceiling of the background
  ILoWord words;

  // the ticks as the game pass
  int tickCount;


  // Constructor for a ZTypeWorld 
  ZTypeWorld(ILoWord words) {
    this.words = words;
  }

  // Constructor for a ZTypeWorld 
  ZTypeWorld(ILoWord words, int tickCount) {
    this.words = words;
    this.tickCount = tickCount;
  }

  /*
  Template: 
  Fields: 
  ... this.words ...     -- ILoWord
  ... this.tickCount ...  -- int 

  Methods: 
  ... this.makeScene ...      -- WorldScene
  ... this.onTick() ...       -- World
  ... this.lastScene() ...    -- WorldScene 
  ... this.onKeyEvent() ...    -- WorldScene 

  Methods of fields: 
  ... this.words.draw(WorldScene) ...    -- WorldScene
  ... this.words.addWord(IWord) ...      -- ILoWord 
  ... this.words.move() ...              -- ILoWord
  ... this.words.checkAndReduce(String s) ...       -- ILoWord
  ... this.words.makeActive(String s) ....    -- ILoWord
  ... this.words.activeCount() ...       -- int 
  ... this.words.filterOutEmpties() ...  -- ILoWord
  ... this.words.reachBottom() ... -- boolean

   */



  // returns a WorldScene representing this ZTypeWorld 
  public WorldScene makeScene() {
    return this.words.draw(new WorldScene(ILoWord.CANVAS_HEIGHT, ILoWord.CANVAS_LENGTH)
        .placeImageXY(new RectangleImage(
            ILoWord.CANVAS_HEIGHT, 
            ILoWord.CANVAS_LENGTH, 
            OutlineMode.SOLID, 
            Color.PINK), 250, 250)
        .placeImageXY(new TextImage("ඞ Score: " + Integer.toString(this.tickCount * 10) + " ඞ", 
            18, FontStyle.BOLD, Color.RED), 250, 480));
  }

  // Makes a new word fall from the ceiling and moves every word on the scene 
  // overrides the onTick() function in World class
  public World onTick() {

    this.tickCount++;

    System.out.println(this.tickCount);

    if (this.words.reachBottom()) {
      return this.endOfWorld("GAME OVER"); 
    }

    Utils utils = new Utils(new Random());
    ILoWord updatedWords = this.words;

    if (this.tickCount % 16 == 0) {
      updatedWords = this.words.addWord(new InactiveWord(utils.randomWord()));
    }
    return new ZTypeWorld(updatedWords.move(), this.tickCount);
  }

  // Makes a scene for when the game has ended
  // overrides the lastScene() function in World class
  public WorldScene lastScene(String msg) {
    return new WorldScene(ILoWord.CANVAS_HEIGHT, ILoWord.CANVAS_LENGTH)
        .placeImageXY(new TextImage(msg, 30, 
            FontStyle.BOLD, Color.RED), 250, 250);

  }

  // returns the ZTypeWorld after the player presses a key
  public ZTypeWorld onKeyEvent(String key) {
    return new ZTypeWorld(this.words
        .filterOutEmpties()
        .makeActive(key)
        .checkAndReduce(key), this.tickCount);
  }

}

// Represents a list of IWords
interface ILoWord {

  // Constants for the game: 
  int CANVAS_LENGTH = 500;

  int CANVAS_HEIGHT = 500;

  int WORD_CEILING = 60;

  // returns a WorldScene with the given WorldScene 
  WorldScene draw(WorldScene acc);

  // returns a new ILoWord with a new IWord added 
  ILoWord addWord(IWord word);

  // Moves all of the words in an ILoWord 
  ILoWord move(); 

  // Checks and reduces the first letter of an active word
  // if the first letter of the word matches the letter given 
  public ILoWord checkAndReduce(String s);

  // makes the first word in this list that begins 
  // with the given one letter string an active word
  public ILoWord makeActive(String s); 

  // Returns the number of active words
  public int activeCount(); 

  // Gets rid of all the empty words in this list
  ILoWord filterOutEmpties(); 

  // returns if a word in the current game has reached the bottom of the screen 
  boolean reachBottom();

}


// Represents an empty list of IWords
class MtLoWord implements ILoWord {

  MtLoWord() {}

  /* Template 

  Methods: 
  ... this.draw(WorldScene) ...     -- WorldScene
  ... this.addWord(IWord) ...       -- ILoWord
  ... this.move() ...               -- ILoWord
  ... this.checkAndReduce(String) ... -- ILoWord
  ... this.makeActive(String s) ...   -- ILoWord
  ... this.activeCount() ...       -- int 
  ... this.filterOutEmpties() ...  -- ILoWord
  ... this.reachBottom() ... -- boolean

   */

  // returns an empty world scene 
  public WorldScene draw(WorldScene acc) {
    return acc;
  }


  // returns a new list of words with a given word added to the list
  public ILoWord addWord(IWord word) {
    /* Template
    Fields and methods: 
    EVERTHING IN THE CONSLOWORDS TEMPLATE 

    Methods of parameter: 
    ... word.draw(WorldScene) ...     -- WorldScene 
    ... word.move() ...               -- IWord 
     */
    return new ConsLoWord(word, this);
  }

  // returns this same empty list of words
  public ILoWord move() {
    return this;
  }

  // returns a ILoWord where any active words in this ILoWord
  // are reduced by removing the first letter if the given
  // string matches the first letter. 
  // In this MtLoWord case, method just returns this MtLoWord
  public ILoWord checkAndReduce(String s) {
    return this;
  }

  // makes the first word in this list that begins 
  // with the given one letter string an active word
  public ILoWord makeActive(String s) {
    return this; 
  }

  // Returns the number of active words
  public int activeCount() {
    return 0; 
  }

  // Gets rid of all empty strings in this list
  public ILoWord filterOutEmpties() {
    return this; 
  }

  // returns if there is word in this MtLoWord has reached the bottom 
  // of the screen. Always false since no words in list. 
  public boolean reachBottom() {
    return false;
  }

}

// represents a non empty list of words 
class ConsLoWord implements ILoWord {

  IWord first;

  ILoWord rest;

  // Constructor for a ConsLoWord
  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  /* Template  
  Fields: 
  ... this.first ...      -- IWord
  ... this.rest ...       -- ILoWord

  Methods: 
  ... this.draw(WorldScene) ...     -- WorldScene
  ... this.addWord(IWord) ...       -- ILoWord
  ... this.move() ...               -- ILoWord
  ... this.checkAndReduce(String) ... -- ILoWord
  ... this.makeActive(String s) ...   -- ILoWord
  ... this.activeCount() ...       -- int 
  ... this.filterOutEmpties() ...  -- ILoWord
  ... this.reachBottom() ... -- boolean

  Methods of Fields: 
  ... this.first.draw(WorldScene) ...   -- WorldScene
  ... this.first.move() ...             -- IWord
  ... this.first.checkAndReduceHelper(String that) ... -- IWord
  ... this.first.checkAndMakeActiveHelper(String s) ...  -- IWord 
  ... this.first.makeActiveReduceHelper() ...  -- IWord
  ... this.first.activeCount() ...        -- int
  ... this.first.isEmpty() ...            -- boolean
  ... this.first.isBottom() ... `         -- boolean

  ... this.rest.draw(WorldScene) ...     -- WorldScene
  ... this.rest.addWord(IWord) ...       -- ILoWord
  ... this.rest.move() ...               -- ILoWord
  ... this.rest.checkAndReduce(String) ... -- ILoWord
  ... this.rest.makeActive(String s) ...   -- ILoWord
  ... this.rest.activeCount() ...       -- int 
  ... this.rest.filterOutEmpties() ...  -- ILoWord
  ... this.rest.reachBottom() ... -- boolean
   */

  // returns a WorldScene with the given WorldScene
  // representing the list of words drawn 
  public WorldScene draw(WorldScene acc) {
    return this.first.draw(this.rest.draw(acc)); 
  }

  // returns a ConsLoWord with the give IWord added at the end
  public ILoWord addWord(IWord word) {
    /* Template
    Fields and methods: 
    EVERTHING IN THE CONSLOWORDS TEMPLATE 

    Methods of parameter: 
    ... word.draw(WorldScene) ...     -- WorldScene
    ... word.addWord(IWord) ...       -- ILoWord
    ... word.move() ...               -- ILoWord
    ... word.checkAndReduce(String) ... -- ILoWord
    ... word.makeActive(String s) ...   -- ILoWord
    ... word.activeCount() ...       -- int 
    ... word.filterOutEmpties() ...  -- ILoWord
    ... word.reachBottom() ... -- boolean
     */
    return new ConsLoWord(this.first, 
        this.rest.addWord(word));
  }

  // moves every IWord in this ConsLoWord  
  public ILoWord move() {
    return new ConsLoWord(this.first.move(), this.rest.move());
  }

  // returns a ILoWord where any active words in this ILoWord
  // are reduced by removing the first letter if the given
  // string matches the first letter. 
  public ILoWord checkAndReduce(String s) {

    /*
     Fields and Methods: 
     Everything in the ConsLoWord class template 

     Methods for Parameter: 
     ... s.compareToIgnoreCase(String that) ...       -- int 
     ... other methods on String ... 

     */

    return new ConsLoWord(this.first.checkAndReduceHelper(s), 
        this.rest.checkAndReduce(s));
  }


  //makes the first word in this list that begins 
  // with the given one letter string an active word
  public ILoWord makeActive(String s) {
    // Check if there are already active words
    if (this.activeCount() < 1) {
      IWord newFirst = this.first.checkAndMakeActiveHelper(s);
      if (newFirst.activeCount() == 1) { 
        return new ConsLoWord(newFirst.makeActiveReduceHelper(), this.rest); 
      } 
      else {
        return new ConsLoWord(this.first, this.rest.makeActive(s));
      }
    } 
    else {
      return this;
    }
  }

  // Counts the number of active words in this list
  public int activeCount() {
    return this.first.activeCount() + this.rest.activeCount(); 
  }

  //returns ILoWord with any IWord that's an empty string filtered out 
  public ILoWord filterOutEmpties() {
    if (this.first.isEmpty()) {
      return this.rest.filterOutEmpties();
    }
    else {
      return new ConsLoWord(this.first, this.rest.filterOutEmpties()); 
    }
  }

  // returns true if there is a word in this list of words that's at the bottom
  // of the screen. 
  public boolean reachBottom() {
    return this.first.isBottom() || this.rest.reachBottom();
  }




}

// Represents either an active or inactive word in the game ZType 
interface IWord {

  // Draws the given world scene
  WorldScene draw(WorldScene ws);

  // Makes the words fall down the screen
  IWord move();

  // Helper method for checkAndReduce that returns a new updated word
  IWord checkAndReduceHelper(String that); 

  // Helper method for checkAndMakeActive that returns a new updated word
  IWord checkAndMakeActiveHelper(String s); 

  // helper method that will make an active word and get rid of first letter 
  IWord makeActiveReduceHelper();

  // Returns the number of active words
  int activeCount(); 

  // Determines if a word is empty
  boolean isEmpty(); 

  boolean isBottom();

}

// Represents an active word 
class ActiveWord implements IWord {

  String word;

  int x;

  int y;

  Random rand;

  // Constructor for an ActiveWord
  ActiveWord(String word, int x, int y) {
    this.rand = new Random();
    this.word = word;
    this.x = x;
    this.y = y;
  }


  // Another constructor for an ActiveWord that will appear on the top of screen
  ActiveWord(String word) {
    this.word = word;
    this.rand = new Random();
    this.x = rand.nextInt(ILoWord.CANVAS_HEIGHT);
    this.y = rand.nextInt(ILoWord.WORD_CEILING);

  }

  /* Template
  Fields: 
  ... this.word ....  -- String 
  ... this.x ...  -- int 
  ... this.y ...  -- int 
  ... this.rand ... -- Random

  Methods: 
  ... this.draw(WorldScene) ...     -- WorldScene
  ... this.addWord(IWord) ...       -- ILoWord
  ... this.move() ...               -- ILoWord
  ... this.checkAndReduce(String) ... -- ILoWord
  ... this.makeActive(String s) ...   -- ILoWord
  ... this.activeCount() ...       -- int 
  ... this.filterOutEmpties() ...  -- ILoWord
  ... this.reachBottom() ... -- boolean

   */

  // returns a world scene with this IWord drawn on it
  public WorldScene draw(WorldScene ws) {
    return ws.placeImageXY(
        new TextImage(this.word, 18, FontStyle.BOLD, Color.RED), 
        this.x, 
        this.y);
  }

  // Moves a word by changing its field y
  public IWord move() {
    return new ActiveWord(this.word, this.x, this.y + 2);
  }

  //returns a new active word with the first letter removed if the first letter of this word 
  // matches the given one letter String 
  public IWord checkAndReduceHelper(String s) {

    /* TEMPLATE 

   Fields and Methods: 
   Everything in the ActiveWord class template

   Methods for parameter: 
   ... s.equals() ... -- boolean 
   ... other methods of String ... 

     */

    if (s.equals(this.word.substring(0, 1))) {
      return new ActiveWord(this.word.substring(1), this.x, this.y); 
    }
    else {
      return this;
    }
  }

  //returns a new active word with the first letter removed 
  public IWord makeActiveReduceHelper() {
    return new ActiveWord(this.word.substring(1), this.x, this.y); 
  }

  // Helper method for checkAndMakeActive
  public IWord checkAndMakeActiveHelper(String s) {

    /* TEMPLATE 

   Fields and Methods: 
   Everything in the ActiveWord class template

   Methods for parameter: 
   ... s.equals() ... -- boolean 
   ... other methods of String ... 

     */

    return this; 
  }

  // Returns the number of currently active words
  public int activeCount() {
    return 1; 
  }

  // returns true if the word has an empty string 
  public boolean isEmpty() {
    return this.word.equals(""); 
  }

  //returns true if the word has reached the bottom of the screen 
  public boolean isBottom() {
    return this.y >= ILoWord.CANVAS_HEIGHT;
  }

}


// Represents an inactive word in the game 
class InactiveWord implements IWord {

  String word;

  int x;

  int y;

  Random rand;


  // Constructor for an inactive word 
  InactiveWord(String word, int x, int y) {
    this.rand = new Random();
    this.word = word;
    this.x = x;
    this.y = y;
  }

  // Another constructor for an inactive word that will fall from the top
  InactiveWord(String word) {
    this.word = word;
    this.rand = new Random();
    this.x = 50 + rand.nextInt(ILoWord.CANVAS_HEIGHT - 100);
    this.y = ILoWord.WORD_CEILING - 75;
  }


  /* Template
  Fields: 
  ... this.word ....  -- String 
  ... this.x ...  -- int 
  ... this.y ...  -- int 
  ... this.rand ... -- Random

  Methods: 
  ... this.draw(WorldScene) ...     -- WorldScene
  ... this.addWord(IWord) ...       -- ILoWord
  ... this.move() ...               -- ILoWord
  ... this.checkAndReduce(String) ... -- ILoWord
  ... this.makeActive(String s) ...   -- ILoWord
  ... this.activeCount() ...       -- int 
  ... this.filterOutEmpties() ...  -- ILoWord
  ... this.reachBottom() ... -- boolean

   */

  // Draws this word onto the given world scene
  public WorldScene draw(WorldScene ws) {
    return ws.placeImageXY(
        new TextImage(this.word, 18, FontStyle.BOLD, Color.WHITE), 
        this.x, 
        this.y);
  }

  // moves this IWord by changing its field y 
  public IWord move() {
    return new InactiveWord(this.word, this.x, this.y + 2);
  }

  //returns a IWord with the first letter removed if the first letter of this word 
  // matches the given one letter String and this word is active, 
  // in this case, we're working with inactive words so no change at all. 
  public IWord checkAndReduceHelper(String s) {

    /* TEMPLATE 

   Fields and Methods: 
   Everything in the InactiveWord class template

   Methods for parameter: 
   ... s.equals(String that) ...  -- boolean 
   ... other methods of the String class ... 

     */

    return this;
  }

  //returns a new active word with the first letter removed 
  public IWord makeActiveReduceHelper() {
    return this;
  }

  // Helper method for checkAndMakeActive
  public IWord checkAndMakeActiveHelper(String s) {

    /* TEMPLATE 

   Fields and Methods: 
   Everything in the ActiveWord class template

   Methods for parameter: 
   ... s.equals() ... -- boolean 
   ... other methods of String ... 

     */

    if (s.equals(this.word.substring(0, 1))) {
      return new ActiveWord(this.word, this.x, this.y); 
    }
    else {
      return this;
    }
  }

  // Returns the number of current active words
  public int activeCount() {
    return 0; 
  }

  // returns true if the word has an empty string 
  public boolean isEmpty() {
    return this.word.equals(""); 
  }

  // returns true if the word has reached the bottom of the screen
  public boolean isBottom() {
    return this.y >= ILoWord.CANVAS_HEIGHT;
  }

}

// Represents examples and tests for the ZType Game 
class ExamplesGame {

  IWord jake = new ActiveWord("Jake", 100, 100);
  IWord yulan = new ActiveWord("Yulan", 300, 300);
  IWord kelly = new ActiveWord("Kelly", 400, 400);
  IWord sean = new InactiveWord("Sean", 30, 30);
  IWord maxine = new InactiveWord("Maxine", 50, 50);
  IWord fernando = new InactiveWord("Fernando", 80, 80);
  IWord emptyWord = new ActiveWord("", 60, 60);
  IWord bottomWord = new InactiveWord("end", 40, 500); 


  ILoWord empty = new MtLoWord();
  ILoWord example = new ConsLoWord(jake, 
      new ConsLoWord(yulan, empty));

  ILoWord exampleWorld1 = new ConsLoWord(jake,
      new ConsLoWord(kelly,
          new ConsLoWord(yulan, empty)));

  ILoWord exampleWorld2 = new ConsLoWord(jake,
      new ConsLoWord(sean,
          new ConsLoWord(kelly,
              new ConsLoWord(maxine, empty))));

  ILoWord exampleWorld3 = new ConsLoWord(sean, 
      new ConsLoWord(maxine, 
          new ConsLoWord(fernando, empty)));

  ILoWord exampleWorld4 = new ConsLoWord(sean,
      new ConsLoWord(maxine,
          new ConsLoWord(emptyWord, empty)));

  ILoWord listWithEmpties = new ConsLoWord(jake,
      new ConsLoWord(kelly, 
          new ConsLoWord(emptyWord, 
              new ConsLoWord(yulan, empty))));

  ILoWord listWithBottom = new ConsLoWord(jake,
      new ConsLoWord(kelly, 
          new ConsLoWord(bottomWord, 
              new ConsLoWord(yulan, empty))));



  ZTypeWorld w2 = new ZTypeWorld(empty);
  ZTypeWorld w1 = new ZTypeWorld(exampleWorld1);
  ZTypeWorld w3 = new ZTypeWorld(exampleWorld2);
  ZTypeWorld w4 = new ZTypeWorld(exampleWorld4);

  Utils util = new Utils(new Random());

  // Test that the game works 
  boolean testBigBang(Tester t) {
    ZTypeWorld w = new ZTypeWorld(empty);
    return w.bigBang(ILoWord.CANVAS_HEIGHT, ILoWord.CANVAS_LENGTH, 0.075);
  }

  // Tests that scenes are properly constructed
  boolean testMakeScene(Tester t) {
    return 
        // tests on an ZTypeWorld with an empty list of words
        t.checkExpect(w2.makeScene(), new WorldScene(500, 500)
            .placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.PINK), 250, 250)
            .placeImageXY(new TextImage("ඞ Score: 0 ඞ", 18, FontStyle.BOLD, Color.RED), 250, 480))
        // tests on a ZTypeWorld with an non empty list of active words
        && t.checkExpect(w1.makeScene(), new WorldScene(500, 500)
            .placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.PINK), 250, 250)
            .placeImageXY(new TextImage("Jake", 18, FontStyle.BOLD, Color.RED), 100, 100)
            .placeImageXY(new TextImage("Kelly", 18, FontStyle.BOLD, Color.RED), 400, 400)
            .placeImageXY(new TextImage("Yulan", 18, FontStyle.BOLD, Color.RED), 300, 300)
            .placeImageXY(new TextImage("ඞ Score: 0 ඞ", 18, FontStyle.BOLD, Color.RED), 250, 480))
        // tests on a ZTypeWorld with an non empty list of both inactive and active words
        && t.checkExpect(w3.makeScene(), new WorldScene(500, 500)
            .placeImageXY(new RectangleImage(500, 500, OutlineMode.SOLID, Color.PINK), 250, 250)
            .placeImageXY(new TextImage("Jake", 18, FontStyle.BOLD, Color.RED), 100, 100)
            .placeImageXY(new TextImage("Sean", 18, FontStyle.BOLD, Color.WHITE), 30, 30)
            .placeImageXY(new TextImage("Kelly", 18, FontStyle.BOLD, Color.RED), 400, 400)
            .placeImageXY(new TextImage("Maxine", 18, FontStyle.BOLD, Color.WHITE), 50, 50)
            .placeImageXY(new TextImage("ඞ Score: 0 ඞ", 18, FontStyle.BOLD, Color.RED), 250, 480));
  }

  // tests the move() method 
  boolean testMove(Tester t) {
    return 
        // checks that it moves an active word
        t.checkExpect(jake.move(), new ActiveWord("Jake", 100, 102))
        // checks that it moves an active word
        && t.checkExpect(yulan.move(), new ActiveWord("Yulan", 300, 302))
        // checks that it moves an inactive word
        && t.checkExpect(sean.move(), new InactiveWord("Sean", 30, 32))
        // checks that it moves a non empty list of words
        && t.checkExpect(example.move(), new ConsLoWord(new ActiveWord("Jake", 100, 102), 
            new ConsLoWord(new ActiveWord("Yulan", 300, 302), empty)))
        // checks that it moves a non empty list of words with more than two words, 
        // containing both active and inactive words
        && t.checkExpect(exampleWorld2.move(), new ConsLoWord(new ActiveWord("Jake", 100, 102), 
            new ConsLoWord(new InactiveWord("Sean", 30, 32), 
                new ConsLoWord(new ActiveWord("Kelly", 400, 402), 
                    new ConsLoWord(new InactiveWord("Maxine", 50, 52), 
                        empty)))))
        // checks that it works on an empty list of words
        && t.checkExpect(empty.move(), empty); 
  }

  // tests the the random word method works - generates a random word  
  boolean testRandomWordTest(Tester t) {
    return t.checkExpect(new Utils(new Random(20)).randomWord(), "xsvphp")
        && t.checkExpect(new Utils(new Random(10)).randomWord(), "pglyes")
        && t.checkExpect(new Utils(new Random(5)).randomWord(), "paaked");
  }

  // tests the the random word accumulator method works 
  boolean testRandomWordTestAcc(Tester t) {
    return t.checkExpect(new Utils(new Random(20))
        .randomWordAcc("xsvp", 4, new Random(20)), "xsvpxs")
        && t.checkExpect(new Utils(new Random(10))
            .randomWordAcc("jake", 4, new Random(10)), "jakepg")
        && t.checkExpect(new Utils(new Random(10))
            .randomWordAcc("yulan", 5, new Random(5)), "yulanp");
  }

  // tests that the checkAndReduce method works
  boolean testCheckAndReduce(Tester t) {
    return 
        // tests it on a list of words where one word is reduced 
        t.checkExpect(exampleWorld1.checkAndReduce("K"), 
            new ConsLoWord(jake,
                new ConsLoWord(new ActiveWord("elly", 400, 400),
                    new ConsLoWord(yulan, empty))))
        // checks that it works when the givens string doesn't match the 
        // fist letter of any active word
        && t.checkExpect(exampleWorld2.checkAndReduce("m"), 
            new ConsLoWord(jake,
                new ConsLoWord(sean,
                    new ConsLoWord(kelly,
                        new ConsLoWord(maxine, empty)))))
        // work on an empty list
        && t.checkExpect(empty.checkAndReduce("a"), new MtLoWord());

  }

  // tests checkAndReduceHelper(String that) method 
  boolean testCheckAndReduceHelper(Tester t) {
    return 
        // tests that it works on an active word starting with the same given letter
        t.checkExpect(jake.checkAndReduceHelper("J"), new ActiveWord("ake", 100, 100))
        // tests that it works on an active word, but not with the same first letter 
        && t.checkExpect(yulan.checkAndReduceHelper("J"), yulan)
        // tests that it works on an inactive word, making no changes 
        && t.checkExpect(maxine.checkAndReduceHelper("M"), maxine);
  }

  // tests checkAndMakeActiveHelper(String) {
  boolean testCheckAndMakeActiveHelper(Tester t) {
    return 
        // returns an active word when the inactiveword's first letter matches given letter
        t.checkExpect(maxine.checkAndMakeActiveHelper("M"), new ActiveWord("Maxine", 50, 50))
        // returns an active word when this word is already active 
        && t.checkExpect(jake.checkAndMakeActiveHelper("J"), jake)
        // returns an inactive word when the inactive word's first letter doesn't
        && t.checkExpect(sean.checkAndMakeActiveHelper("J"), sean);
  }

  // tests the makeActiveReduceHelper() method 
  boolean testMakeActiveReduceHelper(Tester t) {
    return 
        // returns an active word with the first letter removed 
        t.checkExpect(sean.makeActiveReduceHelper(), sean)
        // returns an 
        && t.checkExpect(jake.makeActiveReduceHelper(), new ActiveWord("ake", 100, 100));
  }

  // tests the makeActive(String s) method 
  boolean testMakeActive(Tester t) {
    return 
        // test that it works when all the words in this list is inactive 
        t.checkExpect(exampleWorld3.makeActive("M"), 
            new ConsLoWord(sean, 
                new ConsLoWord(new ActiveWord("axine", 50, 50), 
                    new ConsLoWord(fernando, empty))))
        // checks that it doesn't do anything when there is already an active word in the list
        && t.checkExpect(exampleWorld2.makeActive("M"),
            new ConsLoWord(jake,
                new ConsLoWord(sean,
                    new ConsLoWord(kelly,
                        new ConsLoWord(maxine, empty)))))
        // checks that it works on an empty list
        && t.checkExpect(empty.makeActive("s"), new MtLoWord());

  }

  // tests the activeCount() method in ILoWord class
  boolean testActiveCount(Tester t) {
    return 
        // checks that it counts the active words in a list of words
        t.checkExpect(exampleWorld1.activeCount(), 3)
        // on a list of both active and non active words 
        && t.checkExpect(exampleWorld2.activeCount(), 2)
        // on a list of no active words
        && t.checkExpect(exampleWorld3.activeCount(), 0)
        // on an empty list
        && t.checkExpect(empty.activeCount(), 0);
  }

  // tests the activeCount() method in IWord class 
  boolean testActiveCountIWord(Tester t) {
    return 
        // checks that it returns 1 when word is active 
        t.checkExpect(jake.activeCount(), 1)
        // checks that it returns 0 when word is inactive
        && t.checkExpect(maxine.activeCount(), 0);
  }

  // tests the filterOutEmpties() method 
  boolean testFilterOutEmpties(Tester t) {
    return 
        // checks that it works with a list with an empty word
        t.checkExpect(listWithEmpties.filterOutEmpties(), 
            new ConsLoWord(jake,
                new ConsLoWord(kelly, 
                    new ConsLoWord(yulan, empty))))
        // checks that it works with a list with no empty words
        && t.checkExpect(exampleWorld1.filterOutEmpties(), 
            new ConsLoWord(jake,
                new ConsLoWord(kelly,
                    new ConsLoWord(yulan, empty))));
  }

  // tests the reachBottom() method
  boolean testReachBottom(Tester t) {
    return 
        // works when there is a word at the bottom of the screen 
        t.checkExpect(listWithBottom.reachBottom(), true)
        // works when there isn't any word at the bottom of the screen 
        && t.checkExpect(exampleWorld2.reachBottom(), false)
        // works on an empty list 
        && t.checkExpect(empty.reachBottom(), false);
  }


  // tests the isEmpty() method 
  boolean testIsEmpty(Tester t) {
    return 
        // works when there is a word with the empty string 
        t.checkExpect(emptyWord.isEmpty(), true)
        // when there is a word that's not empty
        && t.checkExpect(sean.isEmpty(), false);
  }

  // tests the draw() method
  boolean testDraw(Tester t) {
    // Tests that a empty list of words is drawn correctly
    return t.checkExpect(empty.draw(new WorldScene(500, 500)), new WorldScene(500, 500))
        // Tests that a list of full words is drawn correctly
        && t.checkExpect(exampleWorld1.draw(new WorldScene(500, 500)), new WorldScene(500, 500)
            .placeImageXY(new TextImage("Yulan", 18, FontStyle.BOLD, Color.RED), 300, 300)
            .placeImageXY(new TextImage("Kelly", 18, FontStyle.BOLD, Color.RED), 400, 400)
            .placeImageXY(new TextImage("Jake", 18, FontStyle.BOLD, Color.RED), 100, 100));
  }



  // tests the lastScene() function 
  boolean testLastScene(Tester t) {
    return t.checkExpect(w1.lastScene("OVER!"), 
        new WorldScene(ILoWord.CANVAS_HEIGHT, ILoWord.CANVAS_LENGTH)
        .placeImageXY(new TextImage("OVER!", 30, 
            FontStyle.BOLD, Color.RED), 250, 250));
  }

  // Tests the onKeyEvent() method
  boolean testOnKeyEvent(Tester t) {
    // Tests when the key pressed on an empty list
    return t.checkExpect(w2.onKeyEvent("z"), w2)
        // Tests when the key pressed doesn't effect active words
        && t.checkExpect(w1.onKeyEvent("k"), w1)
        // Tests when the key pressed on a word it should effect it does
        && t.checkExpect(w4.onKeyEvent("s"), new ZTypeWorld(new ConsLoWord(sean,
            new ConsLoWord(maxine, empty))));
  }
  
  //tests the onTick function 
  boolean testOnTick(Tester t) {
    // tests the world changes after a tick 
    return t.checkExpect(new ZTypeWorld(empty, 0).onTick(), 
            new ZTypeWorld(empty, 1))
        // tests the world changes after a tick with words 
        && t.checkExpect(new ZTypeWorld(new ConsLoWord(jake, new MtLoWord()), 0).onTick(), 
            new ZTypeWorld(new ConsLoWord(new ActiveWord("Jake", 100, 102), new MtLoWord()), 1));
  }
}