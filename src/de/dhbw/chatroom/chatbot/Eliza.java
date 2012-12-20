package de.dhbw.chatroom.chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse implementiert die "Intelligenz" des Chatbots. Es handelt sich
 * dabei um eine ziemlich direkte Übersetzung der Pythonversion von Eliza, die
 * Sie hier finden können: http://jezuk.co.uk/cgi-bin/view/eliza
 */
public class Eliza {
    private String[] firstMessages = new String[] {
        "Hi. I'm %%. Wanna talk with me?",
        "Good news, everyone!",
        "Hello all ...",
        "Hey Kids!",
        "G'day",
        "Hey folks.",
        "Hi friends",
        "It's me, %%",
        "It's %%",
        "Hello, %% speaking!",
    };

    private String[] idleMessages = new String[] {
        "It's rather quiet over here ...",
        "Oh comm'on. Nobody wants to talk?",
        "Maybe I should leave for the restroom ...",
        "Isn't this supposed to be a CHAT room?!",
        "Please, anybody say something.",
        "Sorry, but I'm bored.",
    };

    private Map<String, String> replacements = new HashMap<String, String>();
    private Map<String, String[]> patterns = new HashMap<String, String[]>();

    /**
     * Konstruktor. Hier werden die internen Strukturen mit dem Skript von
     * Eliza befüllt. Die erste Tabelle namens "replacements" definiert
     * Ersetzungsregeln, die auf den Eingaben des Anwenders durchgeführt
     * werden, so dass Wörten wie "I am" zu "You are" umgewandelt werden. Die
     * zweite Tabelle definiert die Schlüsselwörter, die Eliza erkennt. Dabei
     * wird zu jedem Schlüsselwort ein Array mit möglichen Antworten definiert.
     * Die Schlüsselwörter sind reguläre Ausdrücke, deren gematchter Inhalt mit
     * %% in den Antwortsatz übernommen wird.
     */
    public Eliza() {
        // Substitutionsregeln füllen
        this.replacements.put("\\bam\\b", "are");
        this.replacements.put("\\bwas\\b", "were");
        this.replacements.put("\\bi\\b", "you");
        this.replacements.put("\\bi'd\\b", "you would");
        this.replacements.put("\\bi would\\b", "you would");
        this.replacements.put("\\bi've\\b", "you have");
        this.replacements.put("\\bi have\\b", "you have");
        this.replacements.put("\\bi'll\\b", "you will");
        this.replacements.put("\\bi will\\b", "you will");
        this.replacements.put("\\bmy\\b", "your");
        this.replacements.put("\\byou are\\b", "I am");
        this.replacements.put("\\bare you\\b", "");
        this.replacements.put("\\bare\\b", "am");
        this.replacements.put("\\byou've\\b", "I have");
        this.replacements.put("\\byou'll\\b", "I will");
        this.replacements.put("\\byour\\b", "my");
        this.replacements.put("\\byours\\b", "mine");
        //this.replacements.put("\\byou\\b", "me");
        this.replacements.put("\\bme\\b", "you");

        // Dialogmuster füllen
        this.patterns.put("I need (.*)", new String[] {
            "Why do you need %%?",
            "Would it really help you to get %%?",
            "Are you sure you need %%?",
        });

        this.patterns.put("Why don\\'?t you ([^\\?]*)\\??", new String[] {
            "Do you really think I don't %%?",
            "Perhaps eventually I will %%.",
            "Do you really want me to %%?",
        });

        this.patterns.put("Why can\\'?t I ([^\\?]*)\\??", new String[] {
            "Do you think you should be able to %%?",
            "If you could %%, what would you do?",
            "I don't know -- why can't you %%?",
            "Have you really tried?",
        });

        this.patterns.put("I can\\'?t (.*)", new String[] {
            "How do you know you can't %%?",
            "Perhaps you could %% if you tried.",
            "What would it take for you to %%?",
        });

        this.patterns.put("I am (.*)", new String[] {
            "Did you come to me because you are %%?",
            "How long have you been %%?",
            "How do you feel about being %%?",
        });

        this.patterns.put("I\\'?m (.*)", new String[] {
            "How does being %% make you feel?",
            "Do you enjoy being %%?",
            "Why do you tell me you're %%?",
            "Why do you think you're %%?",
        });

        this.patterns.put("Are you (.*)", new String[] {
            "Why does it matter whether I am %%?",
            "Would you prefer it if I were not %%?",
            "Perhaps you believe I am %%.",
            "I may be %% -- what do you think?",
        });

        this.patterns.put("What (.*)", new String[] {
            "Why do you ask?",
            "How would an answer to that help you?",
            "What do you think?",
        });

        this.patterns.put("How (.*)", new String[] {
            "How do you suppose?",
            "Perhaps you can answer your own question.",
            "What is it you're really asking?",
        });

        this.patterns.put("Because (.*)", new String[] {
            "Is that the real reason?",
            "What other reasons come to mind?",
            "Does that reason apply to anything else?",
            "If %%, what else must be true?",
        });

        this.patterns.put("(.*)\\bsorry\\b(.*)", new String[] {
            "There are many times when no apology is needed.",
            "What feelings do you have when you apologize?",
        });

        this.patterns.put("(Hello)|(Hi)(.*)", new String[] {
            "Hello... I'm glad you could drop by today.",
            "Hi there... how are you today?",
            "Hello, how are you feeling today?",
        });

        this.patterns.put("I think (.*)", new String[] {
            "Do you doubt %%?",
            "Do you really think so?",
            "But you're not sure %%?",
        });

        this.patterns.put("(.*)\\bfriend\\b(.*)", new String[] {
            "Tell me more about your friends.",
            "When you think of a friend, what comes to mind?",
            "Why don't you tell me about a childhood friend?",
        });

        this.patterns.put("Yes", new String[] {
            "You seem quite sure.",
            "OK, but can you elaborate a bit?",
        });

        this.patterns.put("(.*)\\bcomputer\\b(.*)", new String[] {
            "Are you really talking about me?",
            "Does it seem strange to talk to a computer?",
            "How do computers make you feel?",
            "Do you feel threatened by computers?",
        });

        this.patterns.put("Is it (.*)", new String[] {
            "Do you think it is %%?",
            "Perhaps it's %% -- what do you think?",
            "If it were %%, what would you do?",
            "It could well be that %%.",
        });

        this.patterns.put("It is (.*)", new String[] {
            "You seem very certain.",
            "If I told you that it probably isn't %%, what would you feel?",
        });

        this.patterns.put("Can you ([^\\?]*)\\??", new String[] {
            "What makes you think I can't %%?",
            "If I could %%, then what?",
            "Why do you ask if I can %%?",
        });

        this.patterns.put("Can I ([^\\?]*)\\??", new String[] {
            "Perhaps you don't want to %%.",
            "Do you want to be able to %%?",
            "If you could %%, would you?",
        });

        this.patterns.put("You are (.*)", new String[] {
            "Why do you think I am %%?",
            "Does it please you to think that I'm %%?",
            "Perhaps you would like me to be %%.",
            "Perhaps you're really talking about yourself?",
        });

        this.patterns.put("You\\'?re (.*)", new String[] {
            "Why do you say I am %%?",
            "Why do you think I am %%?",
            "Are we talking about you, or me?",
        });

        this.patterns.put("I don\\'?t (.*)", new String[] {
            "Don't you really %%?",
            "Why don't you %%?",
            "Do you want to %%?",
        });

        this.patterns.put("I feel (.*)", new String[] {
            "Good, tell me more about these feelings.",
            "Do you often feel %%?",
            "When do you usually feel %%?",
            "When you feel %%, what do you do?",
        });

        this.patterns.put("I have (.*)", new String[] {
            "Why do you tell me that you've %%?",
            "Have you really %%?",
            "Now that you have %%, what will you do next?",
        });

        this.patterns.put("I would (.*)", new String[] {
            "Could you explain why you would %%?",
            "Why would you %%?",
            "Who else knows that you would %%?",
        });

        this.patterns.put("Is there (.*)", new String[] {
            "Do you think there is %%?",
            "It's likely that there is %%.",
            "Would you like there to be %%?",
        });

        this.patterns.put("My (.*)", new String[] {
            "I see, your %%.",
            "Why do you say that your %%?",
            "When your %%, how do you feel?",
        });

        this.patterns.put("You (.*)", new String[] {
            "We should be discussing you, not me.",
            "Why do you say that about me?",
            "Why do you care whether I %%?",
        });

        this.patterns.put("Why (.*)", new String[] {
            "Why don't you tell me the reason why %%?",
            "Why do you think %%?",
        });

        this.patterns.put("I want (.*)", new String[] {
            "What would it mean to you if you got %%?",
            "Why do you want %%?",
            "What would you do if you got %%?",
            "If you got %%, then what would you do?",
        });

        this.patterns.put("(.*)\\bmother\\b(.*)", new String[] {
            "Tell me more about your mother.",
            "What was your relationship with your mother like?",
            "How do you feel about your mother?",
            "How does this relate to your feelings today?",
            "Good family relations are important.",
        });

        this.patterns.put("(.*)\\bfather\\b(.*)", new String[] {
            "Tell me more about your father.",
            "How did your father make you feel?",
            "How do you feel about your father?",
            "Does your relationship with your father relate to your feelings today?",
            "Do you have trouble showing affection with your family?",
        });

        this.patterns.put("(.*)\\bchild\\b(.*)", new String[] {
            "Did you have close friends as a child?",
            "What is your favorite childhood memory?",
            "Do you remember any dreams or nightmares from childhood?",
            "Did the other children sometimes tease you?",
            "How do you think your childhood experiences relate to your feelings today?",
        });

        this.patterns.put("(.*)\\?", new String[] {
            "Why do you ask that?",
            "Please consider whether you can answer your own question.",
            "Perhaps the answer lies within yourself?",
            "Why don't you tell me?",
        });

        this.patterns.put("(.*\\b)((quit)|(bye)|(byebye)|(bye-bye)|(goodbye)|(good-bye)|(cu))(\\b.*)", new String[] {
            "Thank you for talking with me.",
            "Good-bye.",
            "Thank you, that will be $150.  Have a good day!",
        });

        this.patterns.put("(.*)", new String[] {
            "Please tell me more.",
            "Let's change focus a bit... Tell me about your family.",
            "Can you elaborate on that?",
            "Why do you say that %%?",
            "I see.",
            "Very interesting.",
            "%%.",
            "I see.  And what does that tell you?",
            "How does that make you feel?",
            "How do you feel when you say that?",
        });
    }

    /**
     * Diese Methode liefert eine zufällige Begrüßungsnachricht, mit der sich
     * Eliza vorstellt. Als Parameter muss der angezeigte Benutzername des
     * Bots übergeben werden.
     */
    public String getFirstMessage(String username) {
        int i = (int) Math.floor(Math.random() * this.idleMessages.length);
        String msg = this.firstMessages[i];
        return msg.replace("%%", username);
    }

    /**
     * Diese Methode liefert eine zufällige Nachricht, die anzeigt, das Eliza
     * sicht langweilt.
     */
    public String getIdleMessage() {
        int i = (int) Math.floor(Math.random() * this.idleMessages.length);
        return this.idleMessages[i];
    }

    /**
     * Diese Methode nimmt einen Satz vom Anwender entgegen und erzeugt eine
     * Antwort darauf.
     */
    public String talk(String request) {
        String input = request.trim();
        String output = "";

        for (String regexp : this.patterns.keySet()) {
            Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (!matcher.matches()) { continue; }

            for (String regexp1 : this.replacements.keySet()) {
                Pattern replace = Pattern.compile(regexp1, Pattern.CASE_INSENSITIVE);
                String newWord = this.replacements.get(regexp1);
                input = replace.matcher(input).replaceAll(newWord);
            }

            if (input.endsWith(".") || input.endsWith(",") || input.endsWith("!") || input.endsWith("?")) {
                input = input.substring(0, input.length() - 1);
            }

            String[] answers = this.patterns.get(regexp);
            int i = (int) Math.floor(Math.random() * answers.length);
            output = answers[i].replace("%%", input);
            break;
        }

        if (output.endsWith("?.")) {
            output = output.substring(0, output.length() - 2) + ".";
        } else if (output.endsWith("??")) {
            output = output.substring(0, output.length() - 2) + "?";
        }

        output = output.toLowerCase();
        return output;
    }
}
