package cdioil.feedbackmonkey.application;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import cdioil.feedbackmonkey.R;
import cdioil.feedbackmonkey.restful.utils.xml.ReviewXMLService;
import cdioil.feedbackmonkey.utils.ToastNotification;

/**
 * Activity responsible for listing a question and its options.
 */
public class QuestionActivity extends AppCompatActivity implements OnAnswerListener {

    /**
     * TextView holding the current question.
     */
    private TextView questionTextView;

    /**
     * User's authentication token.
     * NOTE: This attribute is important since it allows for multiple answers in a single session.
     */
    private String authenticationToken;

    /**
     * Progress bar to indicate how far the user is in the survey.
     */
    private ProgressBar progressBar;

    /**
     * Integer that indicates the progress the user has made in %
     */
    private int progressMade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        questionTextView = findViewById(R.id.question);
        progressBar = findViewById(R.id.answerSurveyProgressBar);

        if (getIntent().getExtras() != null) {
            authenticationToken = getIntent().getExtras().getString("authenticationToken");
        } else {
            ToastNotification.show(this, "Não tem uma sessão");
            return;
        }

        loadQuestionInfo(false);
    }

    private void loadQuestionInfo(boolean onBackPressed) {
        Bundle currentQuestionBundle = ReviewXMLService.instance().getCurrentQuestionBundle();
        questionTextView.setText(currentQuestionBundle.getString("questionText"));

        String currentQuestionType = currentQuestionBundle.getString("currentQuestionType");

        if (currentQuestionType == null) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        configureActivityAnimations(onBackPressed, fragmentTransaction);
        Fragment newFragment = null;

        if (currentQuestionType.compareTo("B") == 0) {
            newFragment = BinaryQuestionFragment.newInstance();
        } else if (currentQuestionType.compareTo("Q") == 0) {
            newFragment = QuantitativeQuestionFragment.newInstance();
        } else if (currentQuestionType.compareTo("MC") == 0) {
            newFragment = MultipleChoiceQuestionFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }

    /**
     * Configures the activity's animations for the question text view and the fragment transaction
     *
     * @param onBackPressed       boolean to know whether the user is pressing the back button or not
     * @param fragmentTransaction fragment transaction that is going to happen to show the previous or
     *                            the next question to the user
     */
    private void configureActivityAnimations(boolean onBackPressed, FragmentTransaction fragmentTransaction) {
        if (onBackPressed) {
            Animation textViewAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_right);
            textViewAnimation.reset();
            questionTextView.clearAnimation();
            questionTextView.startAnimation(textViewAnimation);
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            Animation textViewAnimation = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
            textViewAnimation.reset();
            questionTextView.clearAnimation();
            questionTextView.startAnimation(textViewAnimation);
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    /**
     * Builds an AlertDialog and starts a SubmitSuggestionActivity.
     */
    private void submitSuggestion() {
        runOnUiThread(() -> {
            AlertDialog.Builder wantToSubmitSuggestionDialog =
                    new AlertDialog.Builder(QuestionActivity.this);
            wantToSubmitSuggestionDialog.setTitle("Submeter Sugestão");
            wantToSubmitSuggestionDialog.setMessage("Quer submeter uma sugestão acerca do item avaliado?");
            wantToSubmitSuggestionDialog.setIcon(R.drawable.ic_info_black_18dp);
            wantToSubmitSuggestionDialog.setPositiveButton("Sim", (dialog, which) -> {

                Intent submitSuggestionIntent = new Intent(QuestionActivity.this, SubmitSuggestionActivity.class);
                submitSuggestionIntent.putExtra("authenticationToken", authenticationToken);
                startActivity(submitSuggestionIntent);
                finish();
            });
            wantToSubmitSuggestionDialog.setNegativeButton("Não", (dialog, which) -> {
//                Intent skipSuggestionIntent = new Intent(QuestionActivity.this, MainMenuActivity.class);
//                skipSuggestionIntent.putExtra("authenticationToken", authenticationToken);
//                startActivity(skipSuggestionIntent);
                finish();
            });
            final AlertDialog alertDialog = wantToSubmitSuggestionDialog.create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3d3d3d"));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3d3d3d"));
            });
            alertDialog.show();
        });
    }

    @Override
    public void onQuestionAnswered(String answer) {
        try {
            ReviewXMLService xmlService = ReviewXMLService.instance();
            boolean canAnswerNextQuestion = xmlService.saveAnswer(answer);
            progressMade = progressMade + 25;
            progressBar.setProgress(progressMade);
            if (!canAnswerNextQuestion) {
                submitSuggestion();
            } else {
                loadQuestionInfo(false);
            }

        } catch (ParserConfigurationException | IOException | TransformerException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            boolean canUndo = ReviewXMLService.instance().undoAnswer();
            progressMade = progressMade - 25;
            progressBar.setProgress(progressMade);
            if (canUndo) {
                loadQuestionInfo(true);
            } else {
                super.onBackPressed();
            }

        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
