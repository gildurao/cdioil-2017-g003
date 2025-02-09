package cdioil.backoffice.application;

import cdioil.application.utils.AnswerProbabilityReader;
import cdioil.application.utils.AnswerProbabilityReaderFactory;
import cdioil.domain.Question;
import cdioil.domain.QuestionOption;
import cdioil.domain.Review;
import cdioil.domain.Survey;
import cdioil.persistence.impl.ReviewRepositoryImpl;
import cdioil.persistence.impl.SurveyRepositoryImpl;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controller Class
 *
 * Stress Tests the application by loading hundreds of survey reviews on runtime
 */
public class LoadAnswersController {
    /**
     * Constant that represents the number of threads that are available to execute the stress test
     */
    private static final int THREAD_NUMBER = (Runtime.getRuntime().availableProcessors()*2)+1;

    /**
     * Loads generated survey reviews into the application in runtime.
     *
     * @param filename
     * @param numAnswers
     * @return time (in ms) that took to load
     * @throws java.lang.InterruptedException Throws InterruptedException if 
     * a spawned thread was interrupted by an external source
     */
    public long performStressTest(String filename, int numAnswers) throws InterruptedException {

        AnswerProbabilityReader reader = AnswerProbabilityReaderFactory.create(new File(filename));

        Map<String, List<Double>> distributions = reader.readProbabilities();

        SurveyRepositoryImpl surveyRepositoryImpl = new SurveyRepositoryImpl();

        Survey survey = surveyRepositoryImpl.findAllActiveSurveys().get(1);

        final long startTime = System.currentTimeMillis();

        /*Note: For the time being, the Questions stored within the Review are 
        still referencing those that have already been persisted. Ideally these should be copies of the Questions.*/
        Thread[] threads=new Thread[THREAD_NUMBER];
        for (int t = 0; t < THREAD_NUMBER; t++) {
            threads[t]= new Thread(stressTestWithThreads(THREAD_NUMBER,survey,numAnswers,distributions));
            threads[t].start();
        }
        for(int t=0;t<THREAD_NUMBER;t++)threads[t].join();
        final long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
    /**
     * Runs the stress test via a certain number of threads
     * @param threadNumber Integer with the thread number going to execute the stress test
     * @param survey Survey with the survey that is going to be answered
     * @param numAnswers Integer with the total number of reviews being executed 
     * @param distributions Map with the stress test distributions
     * @return Runnable with the runable thread being executed
     */
    private Runnable stressTestWithThreads(int threadNumber, Survey survey, int numAnswers,
            Map<String, List<Double>> distributions) {
        Runnable stressTestThreads = () -> {
            ReviewRepositoryImpl repo=new ReviewRepositoryImpl();
            Random randomNumber = new Random();
            Review currentReview;
            double generatedNumber;
            Question currentQuestion;
            int realNumberAnswers = numAnswers / threadNumber;
            for (int i = 0; i < realNumberAnswers; i++) {
                currentReview = new Review(survey);
                while (!currentReview.isFinished()) {
                    generatedNumber = randomNumber.nextDouble();
                    currentQuestion = currentReview.getCurrentQuestion();
                    List<QuestionOption> currentQuestionOptions = currentQuestion.getOptionList();

                    List<Double> probabilities = distributions.get(currentQuestion.getQuestionID());

                    double infLimit = 0;
                    for (int j = 0; j < currentQuestionOptions.size(); j++) {
                        double supLimit = probabilities.get(j) + infLimit;

                        if (generatedNumber > infLimit && generatedNumber <= supLimit) {
                            QuestionOption option = currentQuestionOptions.get(j);
                            currentReview.answerQuestion(option);
                            break;
                        }

                        infLimit = supLimit;
                    }
                }
                repo.add(currentReview);
            }
        };
        return stressTestThreads;
    }
}
