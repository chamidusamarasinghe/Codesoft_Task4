import java.util.Scanner;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

///////////////////////////////////////////////////
class QuisQuestion
{
    private String question;
    private List<String> options;
    private int correctAns;

    public QuisQuestion(String question, List<String> options, int correctAns)
    {
        this.question = question;
        this.options = options;
        this.correctAns = correctAns;
    }

    public String getQuestion()
    {
        return question;
    }

    public int getCorrectAns()
    {
        return correctAns;
    }

    public List<String> getOption()
    {
        return options;
    }
}
//////////////////////////////////////////////////
class Quis
{

    Scanner sc =new Scanner(System.in);

    private int score;
    private List<QuisQuestion> questions;
    private List<Boolean> results;
    

    public Quis(List<QuisQuestion> questions)
    {
        this.questions = questions;
        this.score = 0;
        this.results = new ArrayList<>(Collections.nCopies(questions.size(), false));
    }

    public void quisStart()
    {
        for (int i = 0; i < questions.size(); i++)
        {
            boolean answered = askQuestion(i);
            if (!answered)
            {
                System.out.println("Time up!!!!!!");
            }
        }
        displayResult();
    }

    private boolean askQuestion(int questionIndex)
    {
        QuisQuestion question = questions.get(questionIndex);

        System.out.println(question.getQuestion());
        List<String> options = question.getOption();

        for (int i = 0; i < options.size(); i++)
        {
            System.out.println((i + 1) + ": " + options.get(i));
        }

        final boolean[] answered = {false};

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Future<?> future = executor.submit(() ->
        {
            int userAnswer = sc.nextInt();

            if (userAnswer == question.getCorrectAns())
            {
                System.out.println("Correct!!!!");
                score++;
                results.set(questionIndex, true);
            }
            else
            {
                System.out.println("Wrong!!!! The correct answer : " + question.getCorrectAns());
            }
            answered[0] = true;
        });

        executor.schedule(() ->
        {
            future.cancel(true);
            System.out.println("Time is up for this question!");
        }, 10, TimeUnit.SECONDS);

        try
        {
            future.get();
        }
        catch(InterruptedException | ExecutionException | CancellationException e)
        {
            System.out.println("No answer provided in time.");
        }
        finally
        {
            executor.shutdown();
        }
        return answered[0];
    }

    private void displayResult()
    {
        System.out.println("\nQuiz completed!!!!! final score is: " + score + "/" + questions.size());

        for (int i = 0; i < questions.size(); i++)
        {
            QuisQuestion question = questions.get(i);
            System.out.println((i + 1) + ". " + question.getQuestion());

            if (results.get(i))
            {
                System.out.println("answer: Correct");
            }
            else
            {
                System.out.println("answer: Wrong!!!!! Correct answer : " + question.getCorrectAns());
            }
        }
    }
}
/////////////////////////////////////////////
public class QuisApplication {
    public static void main(String[] args) {
        List<QuisQuestion> questions = new ArrayList<>();
        questions.add(new QuisQuestion("Which language uses System.out.println()?", Arrays.asList("1. Java", "2. C#", "3. Html", "4. C++"), 1));
        questions.add(new QuisQuestion("What is the capital city of Sri Lanka ?", Arrays.asList("1. Gampaha", "2. Colombo", "3. Sri Jayawardanapura", "4. Kandy"), 3));
        questions.add(new QuisQuestion("What is the longest river in Sri Lanka ?", Arrays.asList("1. Kelani", "2. Kalu", "3. Mahaveli", "4. Walave"), 3));
        questions.add(new QuisQuestion("What is the equestion of 100/5", Arrays.asList("1. 10", "2. 25", "3. 5", "4. 20"), 4));
        questions.add(new QuisQuestion("What is the main of Sri Lanka", Arrays.asList("1. Vollyball", "2. Cricket", "3. Football", "4. Rugbby"), 1));

        Quis quiz = new Quis(questions);
        quiz.quisStart();
    }
}
